import requests
from requests.auth import HTTPBasicAuth
import time,sys,os
from ingestion.lib.auxip import get_latest_of_type


def prip_list(user, password, auxip_token, base_url, type_list, mode="prod"):
    file_list = []
    headers = {'Content-type': 'application/json'}
    if len(type_list) == 0:
        return file_list
    latest_pub_date = get_latest_of_type(access_token=auxip_token,aux_type_list=type_list,mode=mode)
    if latest_pub_date is not None:
        request = base_url+"Products?$orderby=PublicationDate desc&$filter=PublicationDate gt "+latest_pub_date+" and (contains(Name,'"+type_list[0]+"')"
    else:
        print("No file available in auxip for types : ")
        print(type_list)
        return file_list
    for idx in range(1,len(type_list)):
        request = request + " or contains(Name,'"+type_list[idx]+"')"
    request = request + ")"

    step=200
    for i in range(0,100000,step):
        start = i
        stop = i + step
        request_top= request+"&$top="+str(step)+"&$skip="+str(start)
        print("Request : "+request_top)
        response = requests.get(request_top, auth=HTTPBasicAuth(user, password),headers=headers,verify=False)
        if response is not None:
            if response.status_code == 200:
                print("Number of element found : "+str(len(response.json()["value"])))
                if len(response.json()["value"]) == 0:
                    break
                #print(response.json())
                for f in response.json()["value"]:
                    ID = f["Id"]
                    file_list.append((ID,f["Name"]))
            else:
                raise Exception("Error on request code : "+str(response.status_code))
    return file_list


def prip_download(id, name,user, password,base_url,output_folder):
    try:
        headers = {'Content-type': 'application/json'}
        # get ID and size of the product
        print( "\nDownloading %s " % (name) )
        id_folder = output_folder
        os.makedirs(id_folder,exist_ok=True)
        file_path = os.path.join(id_folder, name)
        with open(file_path,"wb") as fid:
            start = time.perf_counter()
            product_response = requests.get(base_url+"Products(%s)/$value" % id ,auth=HTTPBasicAuth(user, password),
                                            headers=headers,stream=True,verify=False)
            if product_response.status_code == 404:
                raise Exception("Not found on the server : "+base_url+"/Products(%s)/$value" % id)
            total_length = int(product_response.headers.get('content-length'))
            if total_length is None or total_length == 0: # no content length header
                 fid.write(product_response.content)
            else:
                dl = 0
                print("Starting download")
                for data in product_response.iter_content(chunk_size=4096):
                    print("len "+str(len(data)))
                    dl += len(data)
                    fid.write(data)
                    done = int(50 * dl / total_length)
                    #sys.stdout.write("\r[%s%s] %s bps" % ('=' * done, ' ' * (50-done), dl//(time.perf_counter() - start)))
                    #sys.stdout.flush()
                    # fid.write(product_response.content)
                print("Download Done")
            fid.close()
    except Exception as e:
        print(e)
        raise e
