import argparse
import datetime

import requests
from pip._vendor.requests import auth
from requests.auth import HTTPBasicAuth

odata_datetime_format = "%Y-%m-%dT%H:%M:%SZ"
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
    parser.add_argument("-d", "--startdate",
                        help="starting date to search",
                        required=True)
    parser.add_argument("-e", "--enddate",
                        help="ending date to search",
                        required=True)
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


    work_dt = datetime.datetime.strptime(args.startdate, "%Y%m%dT%H%M%S")
    stop_dt = datetime.datetime.strptime(args.enddate, "%Y%m%dT%H%M%S")
    with open(args.output, mode='w') as report:
        report.write("##### Report start : type : " + args.type + " , startdate :  " + args.startdate +
                     " , enddate :  " + args.enddate + " , step :  " + args.step + " hours ########\n")
        while work_dt < stop_dt:
            print("Tested date :"+datetime.datetime.strftime(work_dt, odata_datetime_format))
            work_dt = work_dt + datetime.timedelta(hours=int(args.step))
            work_odata = datetime.datetime.strftime(work_dt, odata_datetime_format)
            request = url + "?$filter=contains(FullName,'"+args.type+"') and SensingTimeApplicationStart lt "+work_odata+\
                      " and SensingTimeApplicationStop gt "+work_odata+" and startswith(FullName,'"+args.mission+"')&$top=10"
            result = send_request(request,login,password)
            if "value" not in result.keys():
                raise Exception("Result is wrong for request "+request)
            if len(result["value"]) > 1:
                report.write(work_odata + " has "+str(len(result["value"]))+" value while only one expected\n")
            if len(result["value"]) == 0 :
                report.write(work_odata + " has NO value while only one expected\n")
        report.write("##### Report end ########\n")




if __name__ == "__main__":
    main()
