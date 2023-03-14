import argparse
import datetime
import json
import os
import time

import requests
url_dev = "https://dev.reprocessing-preparation.ml/reprocessing.svc/AuxFiles"
url_prod = "https://reprocessing-auxiliary.copernicus.eu/reprocessing.svc/AuxFiles"
import tocken_utils

odata_datetime_format = "%Y-%m-%dT%H:%M:%SZ[GMT]"
odata_datetime_nosec_format = "%Y-%m-%dT%H:%MZ[GMT]"


def send_request(request, access_token):
    headers = {'Content-type': 'application/json', 'Authorization': 'Bearer %s' % access_token}
    try:
        resp = requests.get(request, headers=headers)
    except Exception as e:
        time.sleep(1)
        resp = requests.get(request, headers=headers)

    if resp.status_code != 200:
        print(resp.status_code)
        print(resp.text)
        raise Exception("Bad return code for request: " + request)
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
    parser.add_argument("-o", "--output",
                        help="output json folder",
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
    parser.add_argument("-t", "--type",
                        help="type to search",
                        required=True)

    args = parser.parse_args()

    if args.mode == "dev":
        url = url_dev
    else:
        url = url_prod

    token_info = tocken_utils.get_token_info(args.user, args.password, service="reprocessing-preparation",
                                             mode=args.mode)
    print("Token: " + token_info['access_token'])
    request = url + "/$count"
    print(request)
    result = send_request(request,token_info['access_token'])
    print(result)
    nb_elem = result["value"]
    print(nb_elem)
    step=1000
    for i in range(0,nb_elem,step):
        start = i
        stop = i + step
        request = url + "?$filter=contains(FullName,\'"+args.type+"')&$expand=AuxType&$orderby=ValidityStart asc&$top="+str(step)+"&$skip="+str(start)
        print(request)
        result = send_request(request, token_info['access_token'])
        if len(result["value"]) == 0 :
            break
        for f in result["value"]:
            # Write down
            with open(os.path.join(args.output, os.path.splitext(f["FullName"])[0] + ".json"), 'w') as json_file:
                json.dump(f, json_file)





if __name__ == "__main__":
    main()
