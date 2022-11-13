import os,re,subprocess,datetime,glob,shutil,argparse
from lxml import etree as ET
import DownloadCamsreGrib

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="This script generates the CAMSRE files for a period",  # main description for help
                                     epilog='Usage samples : \n\tpython CAMSRE_Ingestion.py -strt \'2021-01-01\' -stp \'2021-01-31\' -o CAMSRE_Generation/ \n\n',
                                     formatter_class=argparse.RawTextHelpFormatter)  # displayed after help
    parser.add_argument("-start", "--start",
                        help="Date of the start of the CAMSRE generation",
                        required=True)
    parser.add_argument("-stop", "--stop",
                        help="Date of the end of the CAMSRE generation",
                        required=True)
    parser.add_argument("-wd", "--workingDir",
                        help="Working directory of the script",
                        required=True)
    args = parser.parse_args()

    # Timestamps of every grib files that need to appear in the final DBL file
    timestampsToAddToDBL = [0,3,6,9,12,15,18,21,24,27]

    startDate = datetime.datetime.strptime(args.start, "%Y-%m-%d")
    stopDate = datetime.datetime.strptime(args.stop, "%Y-%m-%d")

    workingDir = args.workingDir

    # Pattern of the main grib files downloaded
    rawGribNamePattern = 'r%s.grib'
  
    # Launching downloads
    stopDateToDownload = stopDate
    # We modifiy the end date of download to take into account all the timeStamps we want into the DBL archive
    if timestampsToAddToDBL[-1] > 24:
        stopDateToDownload = stopDateToDownload + datetime.timedelta(hours=timestampsToAddToDBL[-1] - 24)
    DownloadCamsreGrib.downloadCamsreGribForLargePeriodInParallel(startDate, stopDateToDownload, workingDir, rawGribNamePattern)

    outputDir = os.path.join(workingDir, "Output_CAMSRE")
    os.makedirs(outputDir, exist_ok=True)

    # Directory used to extract all the grib files contained in the main grib file
    # downloaded
    initialGribExtractionDir = os.path.join(workingDir, 'initialGribExtraction')
    os.makedirs(initialGribExtractionDir, exist_ok=True)

    # Open HDR template file
    template_filename_cams = os.path.join(os.path.dirname(os.path.realpath(__file__)),"hdr_template_cams.xml")
    print("template hdr _cams: "+template_filename_cams)
    my_namespaces_cams = dict([node for _, node in ET.iterparse(template_filename_cams, events = ['start-ns'])])
    tree_hdr_cams = ET.parse(template_filename_cams)
    root_hdr_cams = tree_hdr_cams.getroot()
    fixed_header_hdr_cams = root_hdr_cams.find("Fixed_Header",namespaces=my_namespaces_cams)

    for file in os.listdir(workingDir):
        # Iterate over all files of the current directory
    
        if re.fullmatch(rawGribNamePattern % '\\d{1}', file):
            # The file is a main grib file downloaded

            # Split the grib into pieces to recompose afterward
            gribPattern = 'z_cams_c_ecmf_[dataDate]_prod_an_sfc_[time]_[shortName].grib'
        
            # Launch the split
            process_4 = subprocess.run(["grib_copy",
                                        os.path.join(workingDir, file),
                                        os.path.join(initialGribExtractionDir,
                                        gribPattern)])
        
            if process_4.returncode != 0:
                # The command to extract grib files from the main one has failed
                print("Failed to cut grib for request 4")
                exit(1)

    print("Initial CAMS data retrieved")

    # Date used to loop over every day between the start and the end period given in parameters
    work_date_pyt = startDate

    # Directory where the final grib files will be stored
    CAMS_working_dir = os.path.join(workingDir, 'ReworkedExtractedGribFiles')
    os.makedirs(CAMS_working_dir, exist_ok=True)

    #
    # Loop from beginning of the period to the end of it
    #
    while work_date_pyt <= stopDate:

        # Files that will be part of the DBL archive
        files_to_tar = []

        #
        # Taking into account the timestamps that are wanted int the final DBL file
        #
        for timestamp in timestampsToAddToDBL:
            # Adding the timestamp to the current date of grib to generate a new date
            # whose associated grib will be searched among the initially extracted grib files
            cur_date_pyt = work_date_pyt + datetime.timedelta(hours=timestamp)
            # Find the grib file among the initially extracted grib files
            file_to_search = "z_cams_c_ecmf_"+cur_date_pyt.strftime("%Y%m%d")+"_prod_an_sfc_"+cur_date_pyt.strftime("%H")+"*.grib"
            req_cams_grib_file = glob.glob(os.path.join(initialGribExtractionDir, file_to_search))
            if len(req_cams_grib_file) != 11:
                # The grib file has not been found
                print("Error on getting the requested cams file for date " + file_to_search)
                print(req_cams_grib_file)
                exit(1)

            # The file has been found
            for c in req_cams_grib_file:
                # Generate the new grib file name to create the GRIB output files
                grib_output_filename = "z_cams_c_ecmf_"+work_date_pyt.strftime("%Y%m%d%H%M%S")+"_prod_an_sfc_"+"{:03d}".format(timestamp)+c[str(c).rfind("_"):]
                # Agglomerate both files
                shutil.copyfile(c,os.path.join(CAMS_working_dir,grib_output_filename))
                # Add the file to the grib files that need to be part of the final DBL archive
                files_to_tar.append(grib_output_filename)

        # Validity of the final CAMSRE file
        valid_start = (work_date_pyt).strftime("%Y%m%dT%H%M%S")
        # Number of hours that the file covers : it is set to 30 to match the already ingested
        # CAMSRE on base.
        dataValidityLength = 30
        valid_stop = (work_date_pyt + datetime.timedelta(hours=dataValidityLength)).strftime("%Y%m%dT%H%M%S")

        #
        # Create HDR file
        #
        hdr_filename = "S2__OPER_AUX_CAMSRE_ADG__"+work_date_pyt.strftime("%Y%m%dT%H%M%S")+"_V"+valid_start+"_"+valid_stop
        print(hdr_filename)
        valid_start_xml = (work_date_pyt).strftime("UTC=%Y-%m-%dT%H:%M:%S")
        valid_stop_xml = (work_date_pyt + datetime.timedelta(hours=36)).strftime("UTC=%Y-%m-%dT%H:%M:%S")
        crea_time_xml = work_date_pyt.strftime("UTC=%Y-%m-%dT%H:%M:%S")
        fixed_header_hdr_cams.find("File_Name", my_namespaces_cams).text = hdr_filename
        fixed_header_hdr_cams.find("Validity_Period",my_namespaces_cams).find("Validity_Start",my_namespaces_cams).text = valid_start_xml
        fixed_header_hdr_cams.find("Validity_Period", my_namespaces_cams).find("Validity_Stop",
                                                                    my_namespaces_cams).text = valid_stop_xml
        fixed_header_hdr_cams.find("Source", my_namespaces_cams).find("Creation_Date",
                                                                        my_namespaces_cams).text = valid_stop_xml
        print(fixed_header_hdr_cams.find("File_Name",my_namespaces_cams).text)
        print(fixed_header_hdr_cams.find("Validity_Period", my_namespaces_cams).find("Validity_Start",my_namespaces_cams).text)
        print("Starting HDR file write : " + os.path.join(workingDir, hdr_filename + ".HDR"))
        tree_hdr_cams.write(os.path.join(workingDir,hdr_filename+".HDR"),encoding="UTF-8")
        print("Starting DBL file write : " + os.path.join(workingDir, hdr_filename + ".DBL"))
      
        # Create DBL
        process_dbl = subprocess.run(["tar", "cf",
                                      os.path.join(workingDir,hdr_filename+".DBL"),
                                      "-C",
                                      CAMS_working_dir]+files_to_tar)
        if process_dbl.returncode != 0:
            print("Failed tar the DBL for date "+cur_date_pyt.strftime("%Y-%m-%dT%H:%M:%S"))
            exit(1)

        # Create TGZ
        process_tgz = subprocess.run(["tar", "czf",
                                      os.path.join(workingDir, hdr_filename + ".TGZ"), "-C", workingDir, hdr_filename+".DBL",
                                      hdr_filename+".HDR"])
        if process_tgz.returncode != 0:
            print("Failed tar the DBL for date " + cur_date_pyt.strftime("%Y-%m-%dT%H:%M:%S"))
            exit(1)
        shutil.move(os.path.join(workingDir, hdr_filename + ".TGZ"),os.path.join(outputDir, hdr_filename + ".TGZ"))
        print("TGZ file wrote : " + os.path.join(outputDir, hdr_filename + ".TGZ"))
      
        # Iteration of the current day
        work_date_pyt = work_date_pyt + datetime.timedelta(days=1)