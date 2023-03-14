#Start http listener :

cd AUXIP_OLINGO/src/test/python
python3 main.py

#Send requests to service
curl -X POST -H "Content-Type: application/json" -d @sub.json '<serviceurl>/auxipv2.svc/Subscriptions'

curl -X POST -H "Content-Type: application/json" -d @product.json '<serviceurl>/auxipv2.svc/Products'

curl -X DELETE '<serviceurl>/auxipv2.svc/Products(7fc282fc-4a00-417f-b77c-4e851440105a)'


