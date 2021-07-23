import argparse
import datetime
import json
import requests
from pip._vendor.requests import auth
from requests.auth import HTTPBasicAuth
import tocken_utils
odata_datetime_format = "%Y-%m-%dT%H:%M:%SZ[GMT]"
odata_datetime_nosec_format = "%Y-%m-%dT%H:%MZ[GMT]"
url_dev = "https://dev.reprocessing-preparation.ml/reprocessing.svc"
url_prod = "https://reprocessing-preparation.ml/reprocessing.svc"
login="admin"
password = "***PASSWORD***"

def send_request(request, jsonload, access_token):
    headers = {'Content-type': 'application/json','Authorization' : 'Bearer %s' % access_token}
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
                        help="Auxip and Reprobase username,\nYour attention please ! : \nThis script assumes that you have the same account for both services Auxip.svc and Reprobase.svc \n(This supposition will be automatically satisfied in the next release of Reprocessing Preparation Package)",
                        required=True)
    parser.add_argument("-pw", "--password",
                        help="User password ",
                        required=True)
    parser.add_argument("-m", "--mode",
                        help="mode dev/prod ",
                        required=False,
                        default="dev")


    args = parser.parse_args()

    template_base = None
    with open(args.input) as f:
        json_base = json.load(f)

    if args.mode == "dev":
        url = url_dev
    else:
        url = url_prod
        
    if "Id" in json_base:
        request = url + "/AuxFiles(" + json_base["Id"]+")"
    else:
        request = url + "/AuxTypes('" + json_base["LongName"]+"')"
    print(request)
    token_info = tocken_utils.get_token_info(args.user,args.password,service="reprocessing-preparation",mode=args.mode)
    print("Token: "+token_info['access_token'])
    
    result = send_request(request,json_base,token_info['access_token'])
    print(result)




if __name__ == "__main__":
    main()
