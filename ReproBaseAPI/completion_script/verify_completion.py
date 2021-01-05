import argparse
import datetime

import requests
from pip._vendor.requests import auth
from requests.auth import HTTPBasicAuth

odata_datetime_format = "%Y-%m-%dT%H:%M:%SZ[GMT]"
odata_datetime_nosec_format = "%Y-%m-%dT%H:%MZ[GMT]"
url = "https://reprocessing-preparation.ml/reprocessing.svc/AuxFiles"
login="user"
password = "*K7KzTrZWhC2zkc"

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
    parser.add_argument("-s", "--step",
                        help="step of the search in hours",
                        required=True)
    parser.add_argument("-t", "--type",
                        help="type to search",
                        required=True)
    parser.add_argument("-m", "--mission",
                        help="Satellite trigram",
                        required=True)
    parser.add_argument(
            "-o",
            "--output",
            help="Output data directory (report file): ex report.txt'",
            required=True)

    args = parser.parse_args()

    with open(args.output, mode='w') as report:
        report.write("##### Report start : type : " + args.type + " , step :  " + args.step + " hours ########\n")
        request = url + "?$orderby=SensingTimeApplicationStart asc&$filter=contains(FullName,'"+args.type+"') and startswith(FullName,'"+args.mission+"')"
        result = send_request(request,login,password)
        print(str(len(result['value'])))
        start_date_str = result['value'][0]['SensingTimeApplicationStart']
        stop_date_str = result['value'][-1]['SensingTimeApplicationStop']
        if len(start_date_str) == 22:
            start_dt = datetime.datetime.strptime(start_date_str, odata_datetime_nosec_format)
        else:
            start_dt = datetime.datetime.strptime(start_date_str, odata_datetime_format)
        if len(stop_date_str) == 22:
            stop_dt = datetime.datetime.strptime(stop_date_str, odata_datetime_nosec_format)
        else:
            stop_dt = datetime.datetime.strptime(stop_date_str, odata_datetime_format)
        report.write("Start date : "+start_date_str+"\n")
        report.write("Stop date : " + stop_date_str + "\n")
        work_dt = start_dt
        while work_dt < stop_dt:
            print("Tested date :"+datetime.datetime.strftime(work_dt, odata_datetime_format))
            work_dt = work_dt + datetime.timedelta(hours=int(args.step))


        report.write("##### Report end ########\n")




if __name__ == "__main__":
    main()
