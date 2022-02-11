/**
 * index.js
 *                  simple page to manage the availabity of AUXIP services
 * 
 *  author: FLD(CS)
 *  date  : 2022-02-10
 * 
 * Parameters :
 *           target : 'prod'(default) or 'dev'
 *                    specify the server to manage
 *           service : '1','2','3'
 *                     specify the service to manage :
 *                          '1' : reprocessing.svc
 *                          '2' : auxip.svc (not yet implemented)
 *                          '3' : rdb.svc (not yet implemented)
 *           display : if present, always display the result in a web page (return status 200)
 *                     if not present, return status 200 when OK, and status 404 when KO
 */

const express = require('express')
const app = express()
const port = 3000

var XMLHttpRequest = require("xmlhttprequest").XMLHttpRequest;

var urlToken = "/auth/realms/reprocessing-preparation/protocol/openid-connect/token"
var urlAuxTypes = "/reprocessing.svc/AuxTypes"

var prodParams = {
    host: "https://reprocessing-auxiliary.copernicus.eu",
    dataToken: "grant_type=password&username=static&password=AkGu795Fp&client_id=reprocessing-preparation"
}

var devParams = {
    host: "https://dev.reprocessing-preparation.ml",
    dataToken: "grant_type=password&username=frederic.ligeard@csgroup.eu&password=NsxtxfE96nfGKvR&client_id=reprocessing-preparation"
}

var getHost = function(target) {
    if (target == "prod") {
        return prodParams.host
    } else if (target == "dev") {
        return devParams.host
    } else return null
}

var getDataToken = function(target) {
    if (target == "prod") {
        return prodParams.dataToken
    } else if (target == "dev") {
        return devParams.dataToken
    } else return null
}

var getToken = function(res,params) {
    console.log("getToken()")
    return new Promise((successCallback, failureCallback) => {
        var xhr = new XMLHttpRequest()
        xhr.open("POST", getHost(params.target)+urlToken, true)
        xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded") // Very important, not application/json
        xhr.onreadystatechange = function () {
            if ( (xhr.readyState === 4 ) && (xhr.status === 200)) {
                var json = JSON.parse(xhr.responseText)
                var token = json.access_token
                successCallback( {"token":token, "res":res, "params":params } )
            } else if ( (xhr.readyState === 4) ) {
                failureCallback({
                    "source" : "getToken", 
                    "status" : xhr.status,
                    "message": JSON.parse(xhr.responseText),
                    "res"    : res,
                    "params" : params
                })     
            }
        };
        xhr.send(getDataToken(params.target))
    })
}

var getAuxTypes = function(result) {
    console.log("getAuxTypes()")
    return new Promise((successCallback, failureCallback) => {
        var xhr = new XMLHttpRequest()
        xhr.open("GET", getHost(result.params.target)+urlAuxTypes, true)
        var bearer="Bearer "+result.token

        //xhr.setRequestHeader("Content-Type", "text/plain")
        xhr.setRequestHeader("Content-Type", "application/json")
        xhr.setRequestHeader("Authorization", bearer)
        xhr.onreadystatechange = function () {
            if ( (xhr.readyState === 4 ) && (xhr.status === 200)) {
                successCallback( { "res":result.res, "params":result.params })
            } else if (xhr.readyState === 4 ) { 
                failureCallback({
                    "source": "getAuxType", 
                    "status" : xhr.status,
                    "message": JSON.parse(xhr.responseText),
                    "res": result.res,
                    "params" : result.params
                })
            }
        };
        xhr.send()
    })
}

var launchCheck = function(res,params) {
    return new Promise((successCallback, failureCallback) => {
        if (params.service == "1") {
            // Check first service : reprocessing.svc
            getToken(res,params)
            .then( result => getAuxTypes(result),result => failureCallback(result))
            .then( result => successCallback(result),result => failureCallback(result))
        }
    })
}

var ok = function(result) {
    result.res.send('The server <b>' + result.params.target + '</b> is <font color="green"><b>alive</b></color> :-)')
    result.res.status(200).end()
}

var ko = function(result) {
    var display = ""
    if ((result.message) && (result.message.error) && (result.message.error.code)) {
        display = "From "+result.source+" / Error "+result.message.error.code+" / "+result.message.error.message+")"
    } else {
        display = "From "+result.source+" / Error "+result.status+" / "+result.message.error +"(" + result.message.error_description + ")"
    }

    if (result.params.display == true) {
        result.res.status(200).send("The server <b>" + result.params.target + "</b> is <font color=red><b>DOWN</b></font><br>"+display)
        result.res.end()
    } else {
        result.res.status(404).end()
    }

}

app.get('/', (req, res) => {

    // get and check parameters
    var target = req.query.target
    var service = req.query.service
    var display = false

    if (target == null) {
        target = "prod"
    } else {
        if ( (target !== "prod") && (target !== "dev") ) {
            console.log("bad value for target")
            res.send("Only 'prod' or 'dev' are allowed for target parameter")
            res.end()
            return
        }
    }

    if (service == null) {
        service = "1"
    } else {
        if ( (service !== "1") && (service !== "2") && (service !== "3")) {
            console.log("bad value for service")
            res.send("Only '1','2' or '3' are allowed for service parameter")
            res.end()
            return
        }

    }
    if ( req.query.display != null) {
        display = true
    }
    console.log("Keep alive : target = ["+target+"] service = ["+service+"] display = ["+display+"]")

    launchCheck(res,{"target":target,"service":service,"display":display})
        .then( ok, ko)
})
  
app.listen(port, () => {
    console.log(`Keep alive started on ${port}`)
})
 