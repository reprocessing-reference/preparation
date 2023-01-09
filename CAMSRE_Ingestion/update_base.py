import argparse
import datetime
import glob
import json
import time
import os
import requests
from ingestion.lib.auxip import get_token_info

odata_datetime_format = "%Y-%m-%dT%H:%M:%SZ[GMT]"
odata_datetime_nosec_format = "%Y-%m-%dT%H:%MZ[GMT]"
url_dev = "https://dev.reprocessing-auxiliary.copernicus.eu/reprocessing.svc"
url_prod = "https://reprocessing-auxiliary.copernicus.eu/reprocessing.svc"

def send_request(request, jsonload, access_token):
    headers = {'Content-type': 'application/json','Authorization' : 'Bearer %s' % access_token}
    try:
        resp = requests.put(request, headers=headers, json=jsonload)
    except Exception as e:
        time.sleep(1)
        resp = requests.put(request, headers=headers, json=jsonload)

    if resp.status_code != 200:
        print(resp.status_code)
        print(resp.text)
        raise Exception("Bad return code for request: "+request)
    try:
        res = resp.json()
    except Exception as e:
        print(resp)
        print(resp.text)
        raise
    finally:
        resp.close()
    return res


def main():
    parser = argparse.ArgumentParser(description="",  # main description for help
            epilog='Beta', formatter_class=argparse.RawTextHelpFormatter)  # displayed after help
    parser.add_argument("-i", "--input",
                        help="input json file",
                        required=True)
    parser.add_argument("-u", "--user",
                        help="Auxip user with reporting role",
                        required=True)
    parser.add_argument("-pw", "--password",
                        help="User password ",
                        required=True)
    parser.add_argument("-m", "--mode",
                        help="dev or prod",
                        default="dev",
                        required=False)
    args = parser.parse_args()
    token_info = get_token_info(args.user, args.password, args.mode)
    access_token = token_info['access_token']
    template_base = None

    fileUploadError=[]

    if args.mode == "dev":
        url = url_dev
    else:
        url = url_prod
        
    if os.path.isfile(args.input):
        with open(args.input) as f:
            json_base = json.load(f)
        if "Id" in json_base:
            request = url + "/AuxFiles(" + json_base["Id"]+")"
        else:
            request = url + "/AuxTypes('" + json_base["LongName"]+"')"
        print(request)
        result = send_request(request,json_base,token_info['access_token'])
        print(result)
    else:
        timer_start = time.time()
        for j in glob.glob(os.path.join(args.input,"*.json")):
            # refesh token if necessary
            timer_stop = time.time()
            elapsed_seconds = timer_stop - timer_start
            if elapsed_seconds > 300 :
                token_info = get_token_info(args.user, args.password, mode=args.mode)
            with open(j) as f:
                json_base = json.load(f)
            if "Id" in json_base:
                request = url + "/AuxFiles(" + json_base["Id"] + ")"
            else:
                request = url + "/AuxTypes('" + json_base["LongName"] + "')"
            print(request)
            try:
                result = send_request(request, json_base, token_info['access_token'])
                print(result)
            except Exception as e:
                print(e)
                print("Le fichier {0} n'a pas été uploadé sur reprobase".format(j))
                fileUploadError.append(j)
    if len(fileUploadError) > 0:
        print("Une erreur s'est produite pour l'upload des fichiers suivants :")       
        for f in fileUploadError:
            print(f)


if __name__ == "__main__":
    main()
