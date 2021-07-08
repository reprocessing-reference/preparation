#!/bin/python
# -*- coding: utf-8 -*-

import sys
import os
import numpy as np
import argparse
import uuid as UUID
import datetime 
import threading
import time

from lib.auxip import post_to_auxip,get_token_info,refresh_token_info
from lib.wasabi import upload_to_wasabi , generate_wasabi_listing

OK = 0

class myThread (threading.Thread):
   def __init__(self,id,path_to_mc, token_info,listing,mode):
      threading.Thread.__init__(self)
      self.id = id
      self.listing = listing
      self.token_info = token_info
      self.mode = mode
      self.path_to_mc = path_to_mc
   def run(self):
      upload_and_post(self.id,self.path_to_mc,self.token_info,self.listing,self.mode)


def upload_and_post(thread_id,path_to_mc,token_info,listing,mode="dev"):

    with open("report_thread_%d.txt" % thread_id,"w") as report:

        timer_start = time.time()
        access_token = token_info['access_token']
        for path_to_auxfile in listing:
            # Generate the uuid for this aux data
            uuid = str(UUID.uuid4())
            # upload it to wasabi
            if upload_to_wasabi(path_to_mc,path_to_auxfile,uuid,mode) == OK:

                print("%s ==> uploaded to wasabi successfully with : %s " % (path_to_auxfile,uuid) )

                # refesh token if necessary 
                timer_stop = time.time()
                elapsed_seconds = timer_stop - timer_start
                token_info = refresh_token_info(token_info,elapsed_seconds)
                if access_token != token_info['access_token']:
                    timer_start = time.time()
                    access_token = token_info['access_token']

                # do a post to auxip.svc if the upload to wasabi is OK
                if post_to_auxip(access_token,path_to_auxfile,uuid,mode) == OK:
                    message = "%s : %s\tupload_to_wasabi : OK post_to_auxip : OK\n" % (path_to_auxfile,uuid)
                else:
                    message = "%s : %s\tupload_to_wasabi : OK post_to_auxip : KO\n" % (path_to_auxfile,uuid)
            else:
                message = "%s : %s\tupload_to_wasabi : KO post_to_auxip : NO VALID UUID\n" % (path_to_auxfile,uuid)

            report.write(message)
                 


if __name__ == "__main__": 
    parser = argparse.ArgumentParser(description="",  # main description for help
            epilog='Beta', formatter_class=argparse.RawTextHelpFormatter)  # displayed after help
    parser.add_argument("-i", "--input",
                        help="input folder were to look for new auxiliary data files",
                       required=True)
    parser.add_argument("-w", "--wasabi",
                        help="wasabi listing generated using './mc ls --recursive auxip_s3/auxip >> wasabi.listing'",
                        default="tobe_generated",
                       required=False)
    parser.add_argument("-s", "--n_sublist",
                        help="split the main list to N sub-listings in order to run the uploadings in parallel, " \
                        "be aware to the machine memory usage",
                        default=1,
                        required=False)
    parser.add_argument("-u", "--user",
                        help="Auxip user with reporting role",
                        required=True)
    parser.add_argument("-pw", "--password",
                        help="User password ",
                        required=True)
    parser.add_argument("-mc", "--path_to_mc",
                        help="Path to mc program",
                        default="/data/mc",
                        required=False)
    parser.add_argument("-m", "--mode",
                        help="dev or prod",
                        default="dev",
                        required=False)                    
    args = parser.parse_args()

    # =======================================================================
    #               CREATE AUXILIARY DATA FILES LISTING WITHOUT DUPLICATION
    # =======================================================================
    auxiliary_data_files = {}
    for root,folders,files in os.walk(args.input):
        for name in files:
            # auxiliary_data_files.append(os.path.join(root,name))
            auxiliary_data_files[name] = os.path.join(root,name)

    # =======================================================================
    #               READ / OR GENERATE WASABI LISTING
    # =======================================================================
    wasabi_listing = []
    wasabi_listing_dict = {}
    if args.wasabi == "tobe_generated":
        # generating the wasabi listing
        print("Getting the already uploaded aux data files listing from wasabi ....")
        print("This may take a while ....")
        wasabi_listing = generate_wasabi_listing(args.path_to_mc)

    else:
        with open(args.wasabi) as fid:
            lines = fid.readlines()
            for line in lines:
                # egnore .txt files 
                if '.txt' not in line:
                    spl = line.split('B ')
                    uuid = spl[1].split('/')[0].strip()
                    
                    try:    
                        file_name = spl[1].split('/')[1].split('\n')[0].strip()
                        wasabi_listing.append(file_name)
                        wasabi_listing_dict[file_name] = uuid
                    except Exception as e:
                        print(e,spl,line)
                        exc_type, exc_obj, exc_tb = sys.exc_info()
                        fname = os.path.split(exc_tb.tb_frame.f_code.co_filename)[1]
                        print(exc_type, fname, exc_tb.tb_lineno)
                        pass

    # =======================================================================
    #               EGNORE ALREADY UPLOADED AUX FILES 
    # =======================================================================
    auxiliary_data_filenames = [name for name in auxiliary_data_files.keys()]
    nas_array = np.array( auxiliary_data_filenames )
    wasabi_array = np.array( wasabi_listing )
    already_uploaded = np.isin(nas_array,wasabi_array)

    # get token_info
    timer_start = time.time()
    token_info = get_token_info(args.user,args.password,args.mode)
    access_token = token_info['access_token']

    if args.n_sublist == 1:
        with open("report.txt","w") as report:
            for i in range ( len(already_uploaded) ):
                if not already_uploaded[i]:

                    auxiliary_data_file = auxiliary_data_files[auxiliary_data_filenames[i]]
                    # Generate the uuid for this aux data
                    uuid = str(UUID.uuid4())
                    # upload it to wasabi
                    if upload_to_wasabi(args.path_to_mc,auxiliary_data_file,uuid,args.mode) == OK:
                        # refesh token if necessary 
                        timer_stop = time.time()
                        elapsed_seconds = timer_stop - timer_start
                        token_info = refresh_token_info(token_info,elapsed_seconds)
                        if access_token != token_info['access_token']:
                            timer_start = time.time()
                            access_token = token_info['access_token']

                        # do a post to auxip.svc if the upload to wasabi is OK
                        if post_to_auxip(access_token,auxiliary_data_file,uuid,args.mode) == OK:
                            message = "%s : %s\tupload_to_wasabi : OK post_to_auxip : OK\n" % (auxiliary_data_file,uuid)
                        else:
                            message = "%s : %s\tupload_to_wasabi : OK post_to_auxip : KO\n" % (auxiliary_data_file,uuid)
                    else:
                        message = "%s : %s\tupload_to_wasabi : KO post_to_auxip : NO VALID UUID\n" % (auxiliary_data_file,uuid)

                    report.write(message)
            report.close()
    else:
        # Create listings
        not_yet_uploaded = []
        for i in range ( len(already_uploaded) ):
            if not already_uploaded[i]:
                not_yet_uploaded.append(auxiliary_data_files[auxiliary_data_filenames[i]])

        nthread = int(args.n_sublist)
        N = len(not_yet_uploaded)/nthread
        if len(not_yet_uploaded) > 0:
            for i in range(nthread):
                listing = not_yet_uploaded[i*N:(i+1)*N]
                if i == (nthread-1):
                    listing = listing + not_yet_uploaded[nthread*N:]

                thread = myThread(i+1,args.path_to_mc,token_info,listing,args.mode)
                thread.start()
        else:
            print("No new auxiliary data file found in the input folder")
        


