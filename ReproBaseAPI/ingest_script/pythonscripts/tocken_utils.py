#!/bin/python
# -*- coding: utf-8 -*-

import sys
import os
import argparse
import uuid as UUID
import datetime 
import time
import requests

def get_token_info(user,password,service="auxip", mode="dev"):
    headers = {'Content-Type': 'application/x-www-form-urlencoded'}
    data = {"username":user, "password":password,"client_id":service,"grant_type":"password"}
    if mode == "dev":
        token_endpoint = "https://dev.reprocessing-preparation.ml/auth/realms/%s/protocol/openid-connect/token" % service
    else:
        token_endpoint = "https://reprocessing-auxiliary.copernicus.eu/auth/realms/%s/protocol/openid-connect/token" % service

    print(token_endpoint)
    response = requests.post(token_endpoint,data=data,headers=headers)
    return response.json()

def refresh_token_info(token_info,timer,service="auxip"):
    # access_token expires_in 900 seconds (15 minutes) 

    if timer < 900 :
        # access_token is still valid
        return token_info
    else:
        headers = {'Content-Type': 'application/x-www-form-urlencoded'}
        data = {"refresh_token":token_info['refresh_token'],"client_id":"%s" % service,"grant_type":"refresh_token"}
        token_endpoint = "https://reprocessing-auxiliary.copernicus.eu/auth/realms/%s/protocol/openid-connect/token" % service
        response = requests.post(token_endpoint,data=data,headers=headers)
        return response.json() 
