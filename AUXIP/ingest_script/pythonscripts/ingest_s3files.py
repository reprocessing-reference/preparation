# coding=utf-8


import argparse
import csv
from datetime import datetime 
import hashlib
import json
import os
import re
import copy
import uuid
import glob

from sqlalchemy import create_engine
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker

engine = create_engine('postgresql://auxip:**auxip**@172.18.0.2:5432/auxipdb')
Session = sessionmaker(bind=engine)

Base = declarative_base()

session = Session()



from sqlalchemy import Column, String, Integer, Date, BigInteger,Float,DateTime,ForeignKey,PrimaryKeyConstraint,Sequence
from sqlalchemy.dialects.postgresql import UUID
from sqlalchemy.orm import relationship



attributes_id_seq = Sequence('attributes_id_seq')


class Product(Base):
    __tablename__ = 'product'

    id = Column(UUID(as_uuid=True), primary_key=True,default=uuid.uuid4, unique=True)
    name = Column(String)
    contenttype = Column(String)
    contentlength = Column(BigInteger)
    origindate = Column(DateTime)
    publicationdate = Column(DateTime)
    evictiondate = Column(DateTime)
    start = Column(DateTime)
    stop = Column(DateTime)


    def __init__(self,name,content_type,content_length,origin_date,publication_date,eviction_date):
        self.name = name
        self.contenttype = content_type
        self.contentlength = content_length
        self.origindate = origin_date
        self.publicationdate = publication_date
        self.evictiondate = eviction_date
    
c = 10000
def attributes_next():
    global c
    c = c + 1
    return c

class IntegerAttribute(Base):
    __tablename__ = 'integerattribute'

    id = Column(BigInteger,primary_key=True,default=attributes_next )
    product_id = Column(UUID(as_uuid=True), ForeignKey('product.id'))
    product = relationship("Product", backref="integer_attributes")

    name = Column(String)
    valuetype = Column(String)
    value = Column(BigInteger)


    def __init__(self,name,value_type,value):
        self.name = name
        self.value_type = value_type
        self.value = value
        


class DoubleAttribute(Base):
    __tablename__ = 'doubleattribute'

    id = Column(BigInteger,primary_key=True,default=attributes_next )
    product_id = Column(UUID(as_uuid=True), ForeignKey('product.id'),primary_key=False)
    product = relationship("Product", backref="double_attributes")

    name = Column(String)
    valuetype = Column(String)
    value = Column(Float)

    def __init__(self,name,value_type,value):
        self.name = name
        self.value_type = value_type
        self.value = value
        

class StringAttribute(Base):
    __tablename__ = 'stringattribute'

    id = Column(BigInteger,primary_key=True,default=attributes_next )
    product_id = Column(UUID(as_uuid=True), ForeignKey('product.id'),primary_key=False)
    product = relationship("Product", backref="string_attributes")

    name = Column(String)
    valuetype = Column(String)
    value = Column(String)

    def __init__(self,name,value_type,value):
        self.name = name
        self.value_type = value_type
        self.value = value


class DateTimeOffsetAttribute(Base):
    __tablename__ = 'datetimeoffsetattribute'

    id = Column(BigInteger,primary_key=True,default=attributes_next )
    product_id = Column(UUID(as_uuid=True), ForeignKey('product.id'),primary_key=False)
    product = relationship("Product", backref="datetime_offset_attributes")

    name = Column(String)
    valuetype = Column(String)
    value = Column(DateTime)    



    def __init__(self,name,value_type,value):
        self.name = name
        self.value_type = value_type
        self.value = value
        



def md5(fname):
    hash_md5 = hashlib.md5()
    with open(fname, "rb") as f:
        for chunk in iter(lambda: f.read(524288), b""):
            hash_md5.update(chunk)
    return hash_md5.hexdigest()

def parse_filename(file_name):
    # OLCI
    odata_datetime_format = "%Y-%m-%dT%H:%M:%S.%fZ"
    # start_dt = datetime.strftime( datetime.strptime(file_name[16:16+15], "%Y%m%dT%H%M%S"),odata_datetime_format)
    # stop_dt  = datetime.strftime( datetime.strptime(file_name[32:32+15], "%Y%m%dT%H%M%S"), odata_datetime_format)
    # creation_dt  = datetime.strftime( datetime.strptime(file_name[48:48+15], "%Y%m%dT%H%M%S"), odata_datetime_format)

    start_dt = datetime.strptime(file_name[16:16+15], "%Y%m%dT%H%M%S") 
    stop_dt  = datetime.strptime(file_name[32:32+15], "%Y%m%dT%H%M%S")
    creation_dt  = datetime.strptime(file_name[48:48+15], "%Y%m%dT%H%M%S")
    
    return (start_dt,stop_dt,creation_dt)

    # "S3A_OL_1_EFR____20160614T101206_20160614T101506_20160614T120248_0180_005_179_2339_SVL_O_NR_001.SEN3"



def main(attributes_json_folder):
    
    json_attributes = glob.glob(attributes_json_folder + "/*.json")
    
    aux_types =[]
    for att in json_attributes:
        
        file_name = os.path.basename(att)

        n = len("S3__AX___LWM_AX_")
        if file_name[:n] not in aux_types:
            aux_types.append( file_name[:n] )
            
            start_dt,stop_dt,creation_dt = parse_filename(file_name)

            fid = open(att)
            
            attributes = json.load( fid )
            file_name = file_name.split(".json")[0]

            product = Product(name=file_name +".zip",content_type="",content_length=124535,origin_date=creation_dt,publication_date=datetime.utcnow(),eviction_date=datetime.utcnow())
            product.start = start_dt
            product.stop = stop_dt

            session.add(product)

            for at in attributes:
                if "Date" in at:
                    attribute = DateTimeOffsetAttribute(name=at,value_type="DateTimeOffset",value=attributes[at])
                else:
                    attribute = StringAttribute(name=at,value_type="String",value=attributes[at])

                attribute.product = product
                session.add(attribute)
            
            print( att )
            session.commit()



        

if __name__ == "__main__":

    attributes_json_folder="/home/naceur/workspace/extract_attributes/attributes/acri"
    main(attributes_json_folder)

    

    # p = Product(name = "S3A_OL_1_EFR____20160614T101206_20160614T101506_20160614T120248_0180_005_179_2339_SVL_O_NR_001.SEN3",
    #                content_type = "",content_length=4256874,origin_date=datetime.datetime.utcnow(),publication_date=datetime.datetime.utcnow(),eviction_date=datetime.datetime.utcnow())

    

    # ia = IntegerAttribute(name="Sensor",value_type="int64",value=2)
    # ia.product = p

    # session.add(p)
    # session.add(ia)
    # session.commit()

    

    # products = session.query(Product).all()

    s_attributes = session.query(StringAttribute).filter(StringAttribute.name =="productType").filter(StringAttribute.value == ("SY_2_ACLMAX")).all()
    # s_attributes = session.query(StringAttribute).filter(StringAttribute.name =="productType").filter(StringAttribute.value == ("OL_1_EFR___")).all()


    request1 = session.query(Product).join(StringAttribute).filter( StringAttribute.name == "platformShortName").filter( StringAttribute.value == "SENTINEL-3")
    request2 = session.query(Product).join(DateTimeOffsetAttribute).filter( DateTimeOffsetAttribute.name == "processingDate").filter( DateTimeOffsetAttribute.value >= "2019-04-18T00:00:00.000Z" )

    products = request1.union(request2).all()

    print str(request1.union(request2))

    # # querytext = "select p from product p where p.id in ( select attribute.product_id from stringattribute attribute join attribute.product pp where attribute.name = 'productType' and  attribute.value = 'AUX_ECMWFD')  or p.id in ( select pp.id from stringattribute attribute join attribute.product pp where attribute.name = 'platformShortName' and  attribute.value = 'SENTINEL-2' ) "
    # querytext = "select p from product p where p.id in ( select pp.id from product pp join stringattribute on pp.id = stringattribute.product_id where attribute.name = 'productType' and  attribute.value = 'AUX_ECMWFD' )"
    
    # querytext = "select p from product p where p.id in ( select pp.id from product pp join stringattribute on pp.id = stringattribute.product_id  attribute where attribute.name = 'productType' and  attribute.value = 'AUX_ECMWFD' )  or p.id in ( select pp.id from product pp join pp.string_attributes attribute where attribute.name = 'platformShortName' and  attribute.value = 'SENTINEL-2' ) "
    # products = session.execute(querytext)

    for p in products:
        print p.name , " : " , p.id
        for a in p.datetime_offset_attributes:
            print a.name,a.value

    # for a in s_attributes:
    #     # print(a)
    #     print(a.name,a.value,a.product.name)
