import argparse
import datetime
import json
import requests
from pip._vendor.requests import auth
from requests.auth import HTTPBasicAuth

odata_datetime_format = "%Y-%m-%dT%H:%M:%SZ[GMT]"
odata_datetime_nosec_format = "%Y-%m-%dT%H:%MZ[GMT]"
url = "https://reprocessing-preparation.ml/reprocessing.svc/AuxFiles"
login="admin"
password = "***PASSWORD***"

def send_request(request, jsonload, log, passwd):
    headers = {'Content-type': 'application/json'}
    resp = requests.put(request, auth=HTTPBasicAuth(log, passwd),headers=headers, json=jsonload)
    if resp.status_code != 200:
        print(resp.status_code)
        print(resp.text)
        raise Exception("Bad return code for request: "+request)
    res = resp.json()
    resp.close()
    return res


def main():
    parser = argparse.ArgumentParser(description="",  # main description for help
            epilog='Beta', formatter_class=argparse.RawTextHelpFormatter)  # displayed after help
    parser.add_argument("-i", "--input",
                        help="input json file",
                        required=True)

    args = parser.parse_args()

    template_base = None
    with open(args.input) as f:
        json_base = json.load(f)

    request = url + "(" + json_base["Id"]+")"
    print(request)
    result = send_request(request,json_base,login,password)
    print(result)




if __name__ == "__main__":
    main()
