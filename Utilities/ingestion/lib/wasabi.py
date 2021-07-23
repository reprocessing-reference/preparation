import os
import subprocess

# upload auxiliaray data file to wasabi
def upload_to_wasabi(path_to_mc,auxiliary_data_file,uuid,mode="dev"):
    file_name = os.path.basename(auxiliary_data_file)    
    upload_command = [path_to_mc ,
                      "cp",
                      auxiliary_data_file, "auxip_s3/auxip/%s/%s" % (uuid,file_name)]
    if mode == "dev" :
        print( "mc command => %s \n" % upload_command )
        return 0
    else:
        return subprocess.call( upload_command )

# Generate a listing of already uploaded files
def generate_wasabi_listing(path_to_mc):
    upload_command = [path_to_mc ,
                      "ls",
                      "--recursive", "auxip_s3/auxip"]

    text_listing = subprocess.check_output( upload_command )
    listing = text_listing.decode("ascii").split('\n')
    wasabi_listing = []

    for line in listing:
        if '.txt' not in line:
            try:
                file_name = line.split('B ')[1].split('/')[1].split('\n')[0].strip()
                wasabi_listing.append(file_name)
            except Exception as e:
                pass

    return wasabi_listing