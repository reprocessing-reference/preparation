USER=$1
PASSWORD=$2


RAWTKN=$(curl -s -X POST \
    -H "Content-Type: application/x-www-form-urlencoded" \
    -d "username="$USER \
    -d "password="$PASSWORD \
    -d 'grant_type=password' \
    -d "client_id=reprocessing-preparation" \
    https://reprocessing-auxiliary.copernicus.eu/auth/realms/reprocessing-preparation/protocol/openid-connect/token \
    |jq . )

TOKEN=$(echo $RAWTKN | jq -r '.access_token')
echo "Token to be used to access to our service :  ${TOKEN} "
