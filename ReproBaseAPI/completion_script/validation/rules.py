import requests
from openpyxl import load_workbook
from datetime import datetime 
from requests import Request, Session

import xml.etree.ElementTree as ET


s = Session()
s.auth = ('user', '*K7KzTrZWhC2zkc')


wb = load_workbook("./data/RRPP-TN-0003-CS_all_AUX.xlsx")
sheet_all = wb['all']
sheet_rules_selection = wb['selection rules']

product_types = [pt.value for pt in sheet_all["A"][1:]]
corresponding_rules = [rule.value for rule in sheet_all["G"][1:]]


# 
rules_names = [rule.value for rule in sheet_rules_selection["A"][1:]]
rules_ids = [id.value for id in sheet_rules_selection["C"][1:]]

rules_dict = {}
for i in range( len(rules_names)):
    name = rules_names[i]
    if name :
        rules_dict[name.strip()] = int(rules_ids[i])


product_types_dict = {}
for i in range( len(product_types)):
    ptype = product_types[i]
    rule = corresponding_rules[i]
    if ptype and rule :
        # print(ptype)
        product_types_dict[ptype.strip()] = int(rule)



raw = 1
for product_type in product_types:
    
    if product_type : 
        product_type = product_type.strip()
        try:
            request = "https://reprocessing-preparation.ml/reprocessing.svc/AuxTypes('%s')" % product_type
            r = s.get(request)

            root = ET.fromstring(r.content)
            rule_name = root[11][0][6].text.strip()
            if rules_dict[rule_name] == product_types_dict[product_type]:
                print( "%s : Expected rule : %d  matching ===> OK " % (product_type, product_types_dict[product_type] ) )
            else:
                print( "%s : Expected rule : %d  matching ===> KO found rule : %d " % (product_type, product_types_dict[product_type] ,rules_dict[rule_name]) )

        except Exception as e:
            # print(e)
            # print(r.content)
            if product_type in product_types_dict:
                print( "%s : Expected rule : %d  matching ===> ? " % (product_type, product_types_dict[product_type]) )

