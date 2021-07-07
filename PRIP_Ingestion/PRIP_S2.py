import requests
from requests.auth import HTTPBasicAuth


def prip_list(user, password, type):
    file_list = []
    headers = {'Content-type': 'application/json'}
    request = "https://prip.s2pdgs.com/odata/v1/Products?$filter=contains(Name,'"+type+"')"
    response = requests.get(request, auth=HTTPBasicAuth(user, password),headers=headers)
    if response.status_code != 200:
        if response is not None:
            if response.status_code == 200:
                for f in response.json()["value"]:
                    ID = f["ID"]
                    file_list.append(ID)
    return file_list



'''
def prip_download(identifier,access_token,output_folder):
    try:
        headers = {'Content-Type': 'application/json','Authorization' : 'Bearer %s' % access_token }
        auxip_endpoint = "https://reprocessing-auxiliary.copernicus.eu/auxip.svc/Products?$filter=contains(Name,'%s')" % file_name

        response = None
        if contains != "*":
            if contains in file_name and exclude not in contains:
                response = requests.get(auxip_endpoint,headers=headers)
        else:
            if exclude not in file_name:
                response = requests.get(auxip_endpoint,headers=headers)

        if response is not None:
            if response.status_code == 200:
                if len(response.json()["value"]) > 0 :
                    ID = response.json()["value"][0]["ID"]
                    length = float(response.json()["value"][0]["ContentLength"])
                    slength = "%.03f MB" % (length*1.e-6) if  length < 1.e9 else "%.03f GB" % (length*1.e-9)
                    # get ID and size of the product
                    print( "\nDownloading %s : %s" % (file_name,slength) )
                    with open(output_folder +"/"+file_name,"w") as fid:
                        start = time.clock()
                        product_response = requests.get("https://reprocessing-auxiliary.copernicus.eu/auxip.svc/Products(%s)/$value" % ID ,headers=headers,stream=True)
                        total_length = int(product_response.headers.get('content-length'))
                        if total_length is None: # no content length header
                            fid.write(product_response.content)
                        else:
                            dl = 0
                            for data in product_response.iter_content(chunk_size=4096):
                                dl += len(data)
                                fid.write(data)
                                done = int(50 * dl / total_length)
                                sys.stdout.write("\r[%s%s] %s bps" % ('=' * done, ' ' * (50-done), dl//(time.clock() - start)))
                                sys.stdout.flush()
                                # fid.write(product_response.content)
                        fid.close()
                else:
                    print( "%s : Not Found by the Auxip service" % file_name )
            else:
                print (response.json())
                # print(response.json()["error"]["message"])

    except Exception as e:
        print(e)

'''