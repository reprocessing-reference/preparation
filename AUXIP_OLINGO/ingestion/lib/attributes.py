
# coding=utf-8

import datetime
import hashlib
import json
import os
import re
import copy
import shutil
import sys

import xml.etree.ElementTree as ET

def getValueByName(root_node,attribute_name):
    for elt in root_node:
        if attribute_name in elt.tag:
            return elt.text
    
    return None


def getNodeByName(root_node,node_name):
    for elt in root_node:
        if node_name in elt.tag:
            return elt
    
    return None

def getNodeByID(metadat_section,ID):
    for elt in metadat_section:
        if ID == elt.get('ID'):
            return elt
    
    return None


def md5(fname):
    hash_md5 = hashlib.md5()
    with open(fname, "rb") as f:
        for chunk in iter(lambda: f.read(524288), b""):
            hash_md5.update(chunk)
    return hash_md5.hexdigest()


def get_attributes(path_to_aux_data_file):
    odata_datetime_format = "%Y-%m-%dT%H:%M:%S.%fZ"

    try:
        file_path = path_to_aux_data_file
        filename_zip  = os.path.basename(path_to_aux_data_file)
        satellite = filename_zip[:2]

        # compute the md5 checksum
        checksum = md5(file_path)
        
        if satellite == 'S1':
            if '.zip' in filename_zip:
                filename = filename_zip.split('.zip')[0]
            else:
                filename = filename_zip.split('.TGZ')[0]

            extension = filename.split('.')[1]

# ==========================================================================================
#                      S1 .SAFE FILES
# ==========================================================================================
            if extension == 'SAFE':
                xml_file = "%s/manifest.safe" % filename
                unzip_command = "unzip -qq %s %s" % (file_path,xml_file)
                if '.TGZ' in filename_zip :
                    unzip_command = "tar xzf %s %s" % (file_path,xml_file)

                try:
                    os.system( unzip_command )
                except Exception as e:
                    print(e)

                tree = ET.parse( xml_file )
                root = tree.getroot()

                metadataSection = getNodeByName(root,'metadataSection')

                processing = getNodeByID(metadataSection,'processing')
                platform = getNodeByID(metadataSection,'platform')

                generalProductInformation = getNodeByID(metadataSection,'generalProductInformation')
                if generalProductInformation is None: # for AUX_PP1,AUX_PP2,AUX_CAL,AUX_INS
                    generalProductInformation = getNodeByID(metadataSection,'standAloneProductInformation')

                processing = getNodeByName(getNodeByName(processing,'metadataWrap'),'xmlData')
                generalProductInformation = getNodeByName(getNodeByName(generalProductInformation,'metadataWrap'),'xmlData')
                platform = getNodeByName(getNodeByName(platform,'metadataWrap'),'xmlData')
                
                beginningDateTime = getValueByName( generalProductInformation[0],'validity') 
                product_type = getValueByName(generalProductInformation[0], 'auxProductType' )

                if product_type in ['AUX_ICE','AUX_WAV','AUX_WND']:
                    start_dt = datetime.datetime.strptime(beginningDateTime, "%Y-%m-%dT%H:%M:%S.%f")
                    stop_dt = start_dt + datetime.timedelta(days=1)
                    endingDateTime = datetime.datetime.strftime(stop_dt, odata_datetime_format)
                else :
                    endingDateTime = "2100-01-01T00:00:00"

                attributes = {
                    "md5" : checksum,
                    "length" : os.path.getsize(file_path) ,
                    "productType": product_type , 
                    "platformShortName": getValueByName(platform[0], 'familyName' ) , 
                    "platformSerialIdentifier": getValueByName(platform[0], 'number' ) ,
                    "processingDate": processing[0].get('start'), 
                    "beginningDateTime": beginningDateTime, 
                    "endingDateTime": endingDateTime , 
                    "processingCenter": processing[0][0].get('site'), 
                }

                # try to get processorName / processorVersion
                # these attributes are missing in some .SAFE files ( may be are missing in all of them )
                try:
                    processorName = getNodeByName(processing[0][0],'software').get('name')
                    processorVersion = getNodeByName(processing[0][0],'software').get('version')

                    attributes["processorName"] = processorName
                    attributes["processorVersion"] = processorName

                except Exception as e:
                    pass

# ==========================================================================================
#                      S1 .EOF FILES
# ==========================================================================================
            else:
                xml_file = filename
                unzip_command = "unzip %s" % file_path

                os.system( unzip_command )
                tree = ET.parse(xml_file)
                fixed_header = tree.getroot()[0][0]
                source_node = getNodeByName(fixed_header,'Source')
                validity_period = getNodeByName(fixed_header,'Validity_Period')
                attributes = {
                    "md5" : checksum,
                    "length" : os.path.getsize(file_path) ,
                    "productType": getValueByName(fixed_header, 'File_Type' ) , 
                    "platformShortName": getValueByName(fixed_header, 'Mission' ) , 
                    "platformSerialIdentifier": getValueByName(fixed_header, 'Mission' ) ,
                    "processingDate": getValueByName( source_node,'Creation_Date').split('UTC=')[1], 
                    "beginningDateTime": getValueByName( validity_period,'Validity_Start').split('UTC=')[1], 
                    "endingDateTime": getValueByName( validity_period,'Validity_Stop').split('UTC=')[1], 
                    "processingCenter": getValueByName( source_node,'System'),  
                    "processorName": getValueByName( source_node,'Creator'),
                    "processorVersion": getValueByName( source_node,'Creator_Version') 
                }

            # with open(os.path.join(filename_zip + ".json"), 'w') as json_file:
            #     json.dump(attributes, json_file)

            os.remove(xml_file)
            shutil.rmtree(filename, ignore_errors=True)
            
# ==========================================================================================
#                      S2  FILES
# ==========================================================================================
        elif satellite == 'S2':
            filename = filename_zip.split('.')[0]
            
            product_type = filename[9:19]
            if product_type in ['AUX_ECMWFD','AUX_UT1UTC']:
                hdr_file = "%s/%s.HDR" % (filename,filename)
                tar_command = "tar xzf %s %s" % (file_path,hdr_file)
            else:
                hdr_file = "%s.HDR" % filename
                tar_command = "tar xzf %s %s" % (file_path,hdr_file)

            os.system( tar_command )
            tree = ET.parse(hdr_file)
            root = tree.getroot()

            if root.tag == 'Earth_Explorer_File':# pug in S2 Files 
                root = root[0]

            product_type = getValueByName(root[0], 'File_Type' )

            if product_type in ['GIP_VIEDIR', 'GIP_R2EQOG', 'GIP_R2DEFI', 'GIP_R2WAFI', 'GIP_R2L2NC', 'GIP_R2DENT', 'GIP_R2DECT' , 'GIP_R2EOB2']:
                # add band to product_type
                band = filename.split('_')[-1].split('.')[0]
                product_type = product_type + '_' + band


            source_node = getNodeByName(root[0],'Source')
            validity_period = getNodeByName(root[0],'Validity_Period')
            attributes = {
                "md5" : checksum,
                "length" : os.path.getsize(file_path) ,
                "productType": product_type , 
                "platformShortName": getValueByName(root[0], 'Mission' ) , 
                "platformSerialIdentifier": getValueByName(root[0], 'Mission' ) ,
                "processingDate": getValueByName( source_node,'Creation_Date').split('UTC=')[1], 
                "beginningDateTime": getValueByName( validity_period,'Validity_Start').split('UTC=')[1], 
                "endingDateTime": getValueByName( validity_period,'Validity_Stop').split('UTC=')[1], 
                "processingCenter": getValueByName( source_node,'System'),  
                "processorName": getValueByName( source_node,'Creator'),
                "processorVersion": getValueByName( source_node,'Creator_Version') 
            }

            # with open(os.path.join(filename_zip + ".json"), 'w') as json_file:
            #     json.dump(attributes, json_file)

            os.remove(hdr_file)
            shutil.rmtree(filename, ignore_errors=True)
            

# ==========================================================================================
#                      S3 FILES
# ==========================================================================================
        else: # S3
            
            filename = filename_zip.split('.zip')[0]
            manifest_file =  "%s/xfdumanifest.xml" % filename
            unzip_command = "unzip %s %s" % (file_path,manifest_file)
            
            os.system( unzip_command )
            tree = ET.parse(manifest_file)
            root = tree.getroot()

            metadataSection = getNodeByName(root,'metadataSection')

            processing = getNodeByID(metadataSection,'processing')
            generalProductInformation = getNodeByID(metadataSection,'generalProductInformation')

            processing = getNodeByName(getNodeByName(processing,'metadataWrap'),'xmlData')[0]
            generalProductInformation = getNodeByName(getNodeByName(generalProductInformation,'metadataWrap'),'xmlData')[0]

            facility = getNodeByName( processing,'facility')
                    
            attributes = {
                "md5" : checksum,
                "length" : os.path.getsize(file_path) ,
                "productType": getValueByName(generalProductInformation, 'fileType' ) , 
                "timeliness": getValueByName(generalProductInformation, 'timeliness' ) , 
                "platformShortName": getValueByName(generalProductInformation, 'familyName' ) , 
                "platformSerialIdentifier": getValueByName(generalProductInformation, 'number' ) ,
                "processingDate": getValueByName( generalProductInformation,'creationTime'), 
                "beginningDateTime": getValueByName( generalProductInformation,'validityStartTime'), 
                "endingDateTime": getValueByName( generalProductInformation,'validityStopTime'), 
                "processingCenter": facility.get('site') , 
                "processorName": getNodeByName( facility,'software').get('name') ,
                "processorVersion": getNodeByName( facility,'software').get('version') 
            }

            # with open(os.path.join(filename_zip + ".json"), 'w') as json_file:
            #     json.dump(attributes, json_file)

            shutil.rmtree(filename, ignore_errors=True)

    except Exception as e:
        print( e )
        exc_type, exc_obj, exc_tb = sys.exc_info()
        fname = os.path.split(exc_tb.tb_frame.f_code.co_filename)[1]
        print(exc_type, fname, exc_tb.tb_lineno)
        attributes = None
        # shutil.rmtree(filename, ignore_errors=True)

    return attributes
                