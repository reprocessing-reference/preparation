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
        working_list = []
        report.write("Number of result tested : "+str(len(result['value']))+"\n")
        if len(result['value']) == 0 :
            report.write("ERROR : no file found !!!!!\n")
        for f in result['value']:
            start_date_str = f['SensingTimeApplicationStart']
            stop_date_str = f['SensingTimeApplicationStop']
            if len(start_date_str) == 22:
                start_dt = datetime.datetime.strptime(start_date_str, odata_datetime_nosec_format)
            else:
                start_dt = datetime.datetime.strptime(start_date_str, odata_datetime_format)
            if len(stop_date_str) == 22:
                stop_dt = datetime.datetime.strptime(stop_date_str, odata_datetime_nosec_format)
            else:
                stop_dt = datetime.datetime.strptime(stop_date_str, odata_datetime_format)
            working_list.append((start_dt, stop_dt, f))
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
        found = 0
        idx = 0
        report.write("#### Testing steps start ####\n")
        #test if there is only one file for a given date using the step
        while work_dt < stop_dt:
            idx = idx - found
            found = 0
            found_list = []
            while idx < len(working_list):
                #The file validity is valid for the tested date
                if working_list[idx][0] <= work_dt and working_list[idx][1] > work_dt:
                    found = found + 1
                    found_list.append(working_list[idx][2]["FullName"]+ " : "+str(working_list[idx][0])+ " / "+str(working_list[idx][1]))
                #The file is after the tested date, finishing test
                if working_list[idx][0] > work_dt:
                    if found == 0:
                        report.write("No file found for date :"+datetime.datetime.strftime(work_dt, odata_datetime_format)+"\n")
                    if found > 1:
                        report.write("More than one file found for date :"+datetime.datetime.strftime(work_dt, odata_datetime_format)+"\n")
                        for l in found_list:
                            report.write(" - "+l+"\n")
                    break
                idx = idx + 1
            try:
                work_dt = work_dt + datetime.timedelta(hours=int(args.step))
            except OverflowError as e:
                work_dt = stop_dt
        report.write("#### Testing steps stop ####\n")
        report.write("#### Testing that file have more than the step duration starts ####\n")
        for f in working_list:
            if f[1] - f[0] > datetime.timedelta(hours=int(args.step)):
                report.write("File has an extend more than the step : " + f[2]['FullName'] +" : " +str(f[0])+" / " +str(f[1])+"\n")
        report.write("#### Testing that file have more than the step duration stop ####\n")
        report.write("#### Testing overlaps/gaps start ####\n")
        # test if there is only one file for a given date using the step
        idx = 1
        while idx < len(working_list):
            # The file validity is valid for the tested date
            if working_list[idx-1][1] > working_list[idx][0]:
                report.write("overlap between file  :" + working_list[idx-1][2]['FullName'] + " and file : "+working_list[idx][2]['FullName']+"\n")
            if working_list[idx-1][1] < working_list[idx][0]:
                report.write("gap between file  :" + working_list[idx-1][2]['FullName'] + " and file : "+working_list[idx][2]['FullName']+"\n")
            idx = idx + 1
        report.write("#### Testing overlaps/gaps stop ####\n")

        report.write("##### Report end ########\n")




if __name__ == "__main__":
    main()
