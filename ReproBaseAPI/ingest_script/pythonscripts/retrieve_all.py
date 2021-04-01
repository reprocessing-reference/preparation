import argparse
import datetime
import json
import os

import requests
from pip._vendor.requests import auth
from requests.auth import HTTPBasicAuth

odata_datetime_format = "%Y-%m-%dT%H:%M:%SZ[GMT]"
odata_datetime_nosec_format = "%Y-%m-%dT%H:%MZ[GMT]"
url = "https://reprocessing-preparation.ml/reprocessing.svc/AuxFiles"
login="admin"
password = "%HEl$1698OgDa%L"

def send_request(request, log, passwd):
    headers = {'Content-type': 'application/json'}
    resp = requests.get(request, auth=HTTPBasicAuth(log, passwd),headers=headers)
    if resp.status_code != 200:
        raise Exception("Bad return code for request: "+request)
    res = resp.json()
    resp.close()
    return res


def main():
    parser = argparse.ArgumentParser(description="",  # main description for help
            epilog='Beta', formatter_class=argparse.RawTextHelpFormatter)  # displayed after help
    parser.add_argument("-o", "--output",
                        help="output json folder",
                        required=True)

    args = parser.parse_args()
    request = url + "/$count"
    print(request)
    result = send_request(request,login,password)
    print(result)
    nb_elem = result["value"]
    print(nb_elem)
    step=10000
    for i in range(0,nb_elem,step):
        start = i
        stop = i + step
        request = url + "?$filter=startswith(FullName,'S1')&$expand=AuxType&$orderby=ValidityStart asc&$top="+str(step)+"&$skip="+str(start)
        print(request)
        result = send_request(request, login, password)
        for f in result["value"]:
            # Write down
            with open(os.path.join(args.output, os.path.splitext(f["FullName"])[0] + ".json"), 'w') as json_file:
                json.dump(f, json_file)





if __name__ == "__main__":
    main()
