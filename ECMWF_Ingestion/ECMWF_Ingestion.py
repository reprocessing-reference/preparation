#!/bin/python
# -*- coding: utf-8 -*-

import argparse
import os,re,subprocess,datetime,glob,shutil
import request_generator
import ecmwfapi
from lxml import etree as ET


def save_to_xml_file(root_node, xml_filepath):
    print("Writing %s", xml_filepath)
    tree = ET.ElementTree(root_node)

    with open(xml_filepath, "w") as fi:
        fi.write(ET.tostring(tree, xml_declaration=True, method="xml",
                            encoding="UTF-8").decode())
        fi.close()
    print("%s Created", xml_filepath)


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="This script generates the ECMXF files for a period",  # main description for help
            epilog='Usage samples : \n\tpython ECMWF_Ingestion.py -u username -pw password \n\n', formatter_class=argparse.RawTextHelpFormatter)  # displayed after help
    parser.add_argument("-k", "--key",
                        help="Mars user key",
                        required=True)
    parser.add_argument("-u", "--url",
                        help="Mars user url",
                        required=True)
    parser.add_argument("-m", "--email",
                        help="Mars user email",
                        required=True)
    parser.add_argument("-w", "--working",
                        help="Working folder",
                        required=True)
    parser.add_argument("-o", "--output",
                        help="Output folder",
                        required=True)
    parser.add_argument("-s", "--startdate",
                        help="starting date",
                        required=True)
    parser.add_argument("-e", "--enddate",
                        help="ending date",
                        required=True)
    args = parser.parse_args()

    pattern_date = re.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}")
    if not re.search(pattern_date,args.startdate):
        print("The start date doesn't respect the pattern 2015-06-23")
        exit(1)
    if not re.search(pattern_date,args.enddate):
        print("The end date doesn't respect the pattern 2015-06-23")
        exit(1)

    start_date_pyt = datetime.datetime.strptime(args.startdate, "%Y-%m-%d")
    end_date_pyt = datetime.datetime.strptime(args.enddate, "%Y-%m-%d")

    #Open HDR template file
    template_filename = os.path.join(os.path.dirname(os.path.realpath(__file__)),"hdr_template.xml")
    print("template hdr : "+template_filename)
    my_namespaces = dict([node for _, node in ET.iterparse(template_filename, events = ['start-ns'])])
    tree_hdr = ET.parse(template_filename)
    root_hdr = tree_hdr.getroot()
    fixed_header_hdr = root_hdr.find("Fixed_Header",namespaces=my_namespaces)
    #Open HDR template file
    template_filename_cams = os.path.join(os.path.dirname(os.path.realpath(__file__)),"hdr_template_cams.xml")
    print("template hdr _cams: "+template_filename_cams)
    my_namespaces_cams = dict([node for _, node in ET.iterparse(template_filename_cams, events = ['start-ns'])])
    tree_hdr_cams = ET.parse(template_filename_cams)
    root_hdr_cams = tree_hdr_cams.getroot()
    fixed_header_hdr_cams = root_hdr_cams.find("Fixed_Header",namespaces=my_namespaces)

    #Surface
    print("Starting at surface ECMWF handling")
    request_1 = request_generator.RequestGenerator()
    request_1.param_list = ["206.128","137.128","151.128","165.128","166.128"]
    request_1_filename = os.path.join(args.working, "request1.req")
    request_1_target = os.path.join(args.working, "request1.grib")
    request_1.target = request_1_target
    request_1.date_begin = args.startdate
    request_1.date_end = args.enddate
    request_1.write_to_file(request_1_filename)
    with open(request_1_filename) as f:
        data = f.read()
        #Exec request
        c = ecmwfapi.ECMWFService('mars',key=args.key,url=args.url,email=args.email)
        c.execute(data, request_1_target)
    #test if grib is here
    if not os.path.exists(request_1_target):
        print("Output grib for request 1 is not available")
        exit(1)
    #Split the grib into pieces to recompose afterword
    request_1_split_folder = os.path.join(args.working, "request_1_split")
    os.makedirs(request_1_split_folder,exist_ok=True)
    process_1 = subprocess.run(["grib_copy",request_1_target,os.path.join(request_1_split_folder,"[dataDate]-[time]-[step]")])
    if process_1.returncode != 0:
        print("Failed to cut grib for request 1")
        exit(1)
    #At level
    print("Starting at level ECMWF handling")
    request_2 = request_generator.RequestGenerator()
    request_2.param_list = ["157.128"]
    request_2.levtype = "pl"
    request_2_filename = os.path.join(args.working, "request2.req")
    request_2_target = os.path.join(args.working, "request2.grib")
    request_2.target = request_2_target
    request_2.date_begin = args.startdate
    request_2.date_end = args.enddate
    request_2.write_to_file(request_2_filename)
    with open(request_2_filename) as f:
        data = f.read()
        #Exec request
        c = ecmwfapi.ECMWFService('mars',key=args.key,url=args.url,email=args.email)
        c.execute(data, request_2_target)

    # test if grib is here
    if not os.path.exists(request_2_target):
        print("Output grib for request 2 is not available")
        exit(1)
    #Split the grib into pieces to recompose afterword
    request_2_split_folder = os.path.join(args.working, "request_2_split")
    os.makedirs(request_2_split_folder, exist_ok=True)
    process_2 = subprocess.run(["grib_copy",request_2_target,os.path.join(request_2_split_folder,"[dataDate]-[time]-[step]")])
    if process_2.returncode != 0:
       print("Failed to cut grib for request 2")
       exit(1)
    print("ECMWF Datas retrieved")
    #Put in each output files
    print("Starting output files creation")
    start_date_pyt = datetime.datetime.strptime(args.startdate, "%Y-%m-%d")
    end_date_pyt = datetime.datetime.strptime(args.enddate, "%Y-%m-%d")
    work_date_pyt = start_date_pyt
    ECMWF_working_dir = os.path.join(args.working,"ECMWF_Files")
    os.makedirs(ECMWF_working_dir,exist_ok=True)
    while work_date_pyt <= end_date_pyt:
        for t in request_1.time_list:
            delta_time = datetime.datetime.strptime(t, "%H:%M:%S")
            cur_date_pyt = work_date_pyt.replace(hour=delta_time.hour, minute=delta_time.minute)
            files_to_tar = []
            for s in request_1.step:
                delta_hour = datetime.timedelta(hours=int(s))
                cur_date_pyt_step = cur_date_pyt + delta_hour
                print(cur_date_pyt_step)
                # find request 1 grib file
                file_to_search = cur_date_pyt.strftime("%Y%m%d-%H%M") + "-" + s
                req_1_grib_file = glob.glob(os.path.join(request_1_split_folder, file_to_search))
                if len(req_1_grib_file) != 1:
                    print("Error on getting request1 file for date " + file_to_search)
                    exit(1)
                req_2_grib_file = glob.glob(os.path.join(request_2_split_folder, file_to_search))
                if len(req_2_grib_file) != 1:
                    print("Error on getting request2 file for date " + file_to_search)
                    exit(1)
                #Create the GRIB output files
                grib_output_filename = "S2D"+cur_date_pyt.strftime("%Y%m%d%H%M")+cur_date_pyt_step.strftime("%Y%m%d%H%M")+"1"
                #Agglomerate both files
                shutil.copyfile(req_1_grib_file[0],os.path.join(ECMWF_working_dir,grib_output_filename))
                with open(os.path.join(ECMWF_working_dir,grib_output_filename),"ab") as first, open(req_2_grib_file[0],"rb") as second:
                    first.write(second.read())
                files_to_tar.append(grib_output_filename)
            #create HDR file
            valid_start = (cur_date_pyt+datetime.timedelta(hours=int(request_1.step[0]))).strftime("%Y%m%dT%H%M%S")
            valid_stop = (cur_date_pyt + datetime.timedelta(hours=int(request_1.step[-1])+3)).strftime("%Y%m%dT%H%M%S")
            hdr_filename = "S2__OPER_AUX_ECMWFD_ADG__"+cur_date_pyt.strftime("%Y%m%dT%H%M%S")+"_V"+valid_start+"_"+valid_stop
            print(hdr_filename)
            valid_start_xml = (cur_date_pyt + datetime.timedelta(hours=int(request_1.step[0]))).strftime("UTC=%Y-%m-%dT%H:%M:%S")
            valid_stop_xml = (cur_date_pyt + datetime.timedelta(hours=int(request_1.step[-1])+3)).strftime("UTC=%Y-%m-%dT%H:%M:%S")
            crea_time_xml = cur_date_pyt.strftime("UTC=%Y-%m-%dT%H:%M:%S")
            fixed_header_hdr.find("File_Name", my_namespaces).text = hdr_filename
            fixed_header_hdr.find("Validity_Period",my_namespaces).find("Validity_Start",my_namespaces).text = valid_start_xml
            fixed_header_hdr.find("Validity_Period", my_namespaces).find("Validity_Stop",
                                                                         my_namespaces).text = valid_stop_xml
            fixed_header_hdr.find("Source", my_namespaces).find("Creation_Date",
                                                                         my_namespaces).text = valid_stop_xml
            print("Starting HDR file write : "+os.path.join(args.working,hdr_filename+".HDR"))
            tree_hdr.write(os.path.join(args.working,hdr_filename+".HDR"),encoding="UTF-8")
            # Create DBL
            print("Starting DBL file write : " + os.path.join(args.working, hdr_filename + ".DBL"))
            process_dbl = subprocess.run(
                ["tar", "cf", os.path.join(args.working,hdr_filename+".DBL"), "-C", ECMWF_working_dir]+files_to_tar)
            if process_dbl.returncode != 0:
              print("Failed tar the DBL for date "+cur_date_pyt.strftime("%Y-%m-%dT%H:%M:%S"))
              exit(1)
            # Create TGZ
            process_tgz = subprocess.run(
                ["tar", "czf", os.path.join(args.working, hdr_filename + ".TGZ"), "-C", args.working, hdr_filename+".DBL",
                 hdr_filename+".HDR"])
            if process_tgz.returncode != 0:
                print("Failed tar the DBL for date " + cur_date_pyt.strftime("%Y-%m-%dT%H:%M:%S"))
                exit(1)
            shutil.move(os.path.join(args.working, hdr_filename + ".TGZ"),os.path.join(args.output, hdr_filename + ".TGZ"))
            print("TGZ file wrote : "+os.path.join(args.output, hdr_filename + ".TGZ"))
            os.remove(os.path.join(args.working,hdr_filename+".DBL"))
            os.remove(os.path.join(args.working,hdr_filename+".HDR"))
        work_date_pyt = work_date_pyt + datetime.timedelta(days=1)

    #CAMS
    print("Starting CAMS data handling")
    request_3 = request_generator.RequestGenerator()
    request_3.param_list = ["aod550","z","ssaod550","duaod550","omaod550","bcaod550","suaod550","aod469","aod670","aod865","aod1240"]
    request_3.domain = None
    request_3.dataset = "cams_nrealtime"
    request_3.grid = "0.4/0.4"
    request_3.classid = "mc"
    request_3.time_list = ["00:00:00","06:00:00","12:00:00","18:00:00"]
    request_3.type = "an"
    request_3.step = None
    request_3_filename = os.path.join(args.working, "request3.req")
    request_3_target = os.path.join(args.working, "request3.grib")
    request_3.target = request_3_target
    request_3.date_begin = args.startdate
    request_3.date_end = args.enddate
    request_3.write_to_file(request_3_filename)
    with open(request_3_filename) as f:
       data = f.read()
       #Exec request
       c = ecmwfapi.ECMWFService('mars',key=args.key,url=args.url,email=args.email)
       c.execute(data, request_3_target)
    #Split the grib into pieces to recompose afterword
    request_3_split_folder = os.path.join(args.working, "request_3_split")
    os.makedirs(request_3_split_folder, exist_ok=True)
    process_3 = subprocess.run(["grib_copy", request_3_target, os.path.join(request_3_split_folder,
                                                                          "z_cams_c_ecmf_[dataDate]_prod_an_sfc_[time]_[shortName].grib")])
    if process_3.returncode != 0:
      print("Failed to cut grib for request 3")
      exit(1)
    request_4 = request_3
    request_4.time_list = ["00:00:00","06:00:00"]
    request_4.date_begin = (end_date_pyt+datetime.timedelta(days=1)).strftime("%Y-%m-%d")
    request_4.date_end = None
    request_4_filename = os.path.join(args.working, "request4.req")
    request_4_target = os.path.join(args.working, "request4.grib")
    request_4.write_to_file(request_4_filename)
    with open(request_4_filename) as f:
       data = f.read()
       #Exec request
       c = ecmwfapi.ECMWFService('mars',key=args.key,url=args.url,email=args.email)
       c.execute(data, request_4_target)
    #test if grib is here
    if not os.path.exists(request_4_target):
       print("Output grib for request 4 is not available")
       exit(1)
    #Split the grib into pieces to recompose afterword
    process_4 = subprocess.run(["grib_copy", request_4_target, os.path.join(request_3_split_folder,
                                                                            "z_cams_c_ecmf_[dataDate]_prod_an_sfc_[time]_[shortName].grib")])
    if process_4.returncode != 0:
      print("Failed to cut grib for request 4")
      exit(1)

    print("CAMS Datas retrieved")
    #Put in each output files
    work_date_pyt = start_date_pyt
    CAMS_working_dir = os.path.join(args.working, "CAMS_Files")
    os.makedirs(CAMS_working_dir, exist_ok=True)
    while work_date_pyt <= end_date_pyt:
        files_to_tar = []
        for t in [0,6,12,18,24,30]:
            cur_date_pyt = work_date_pyt + datetime.timedelta(hours=t)
            # find request 1 grib file
            file_to_search = "z_cams_c_ecmf_"+cur_date_pyt.strftime("%Y%m%d")+"_prod_an_sfc_"+cur_date_pyt.strftime("%H")+"*.grib"
            req_cams_grib_file = glob.glob(os.path.join(request_3_split_folder, file_to_search))
            if len(req_cams_grib_file) != 11:
                print("Error on getting request cams file for date " + file_to_search)
                print(req_cams_grib_file)
                exit(1)
            for c in req_cams_grib_file:
                #Create the GRIB output files
                grib_output_filename = "z_cams_c_ecmf_"+work_date_pyt.strftime("%Y%m%d")+"_prod_an_sfc_"+"{:03d}".format(t)+c[str(c).rfind("_"):]
                #Agglomerate both files
                shutil.copyfile(c,os.path.join(CAMS_working_dir,grib_output_filename))
                files_to_tar.append(grib_output_filename)
        #create HDR file
        valid_start = (work_date_pyt).strftime("%Y%m%dT%H%M%S")
        valid_stop = (work_date_pyt + datetime.timedelta(hours=36)).strftime("%Y%m%dT%H%M%S")
        hdr_filename = "S2__OPER_AUX_CAMSAN_ADG__"+work_date_pyt.strftime("%Y%m%dT%H%M%S")+"_V"+valid_start+"_"+valid_stop
        print(hdr_filename)
        valid_start_xml = (work_date_pyt).strftime("UTC=%Y-%m-%dT%H:%M:%S")
        valid_stop_xml = (work_date_pyt + datetime.timedelta(hours=36)).strftime("UTC=%Y-%m-%dT%H:%M:%S")
        crea_time_xml = work_date_pyt.strftime("UTC=%Y-%m-%dT%H:%M:%S")
        fixed_header_hdr_cams.find("File_Name", my_namespaces).text = hdr_filename
        fixed_header_hdr_cams.find("Validity_Period",my_namespaces).find("Validity_Start",my_namespaces).text = valid_start_xml
        fixed_header_hdr_cams.find("Validity_Period", my_namespaces).find("Validity_Stop",
                                                                     my_namespaces).text = valid_stop_xml
        fixed_header_hdr_cams.find("Source", my_namespaces).find("Creation_Date",
                                                                         my_namespaces).text = valid_stop_xml
        print(fixed_header_hdr_cams.find("File_Name",my_namespaces).text)
        print(fixed_header_hdr_cams.find("Validity_Period", my_namespaces).find("Validity_Start",my_namespaces).text)
        print("Starting HDR file write : " + os.path.join(args.working, hdr_filename + ".HDR"))
        tree_hdr_cams.write(os.path.join(args.working,hdr_filename+".HDR"),encoding="UTF-8")
        print("Starting DBL file write : " + os.path.join(args.working, hdr_filename + ".DBL"))
        # Create DBL
        process_dbl = subprocess.run(
             ["tar", "cf", os.path.join(args.working,hdr_filename+".DBL"), "-C", CAMS_working_dir]+files_to_tar)
        if process_dbl.returncode != 0:
          print("Failed tar the DBL for date "+cur_date_pyt.strftime("%Y-%m-%dT%H:%M:%S"))
          exit(1)
        # Create TGZ
        process_tgz = subprocess.run(
               ["tar", "czf", os.path.join(args.working, hdr_filename + ".TGZ"), "-C", args.working, hdr_filename+".DBL",
                hdr_filename+".HDR"])
        if process_tgz.returncode != 0:
           print("Failed tar the DBL for date " + cur_date_pyt.strftime("%Y-%m-%dT%H:%M:%S"))
           exit(1)
        shutil.move(os.path.join(args.working, hdr_filename + ".TGZ"),os.path.join(args.output, hdr_filename + ".TGZ"))
        print("TGZ file wrote : " + os.path.join(args.output, hdr_filename + ".TGZ"))
        work_date_pyt = work_date_pyt + datetime.timedelta(days=1)
