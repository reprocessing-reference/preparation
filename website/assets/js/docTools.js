var getDivTitle = function(doc) {
    var newDiv = $( "<div class='row justify-content-center font-weight-bold'></div>" )
    var title= $("<h2><a href='"+doc.link+"' target='_blank'>"+doc.title+"<a></h2>")
    newDiv.append(title)
    return newDiv
}
var getDivDate = function(doc) {
    var newDiv = $( "<div class='row justify-content-center font-weight-bold'></div>" )
    var date = $("<h6><i>("+doc.date+")</i></h6>")
    newDiv.append(date)
    return newDiv
}
var getDivDesc = function(doc) {
    var newDiv = $( "<div class='row justify-content-center font-weight-bold'></div>" )
    var desc = $("<h6>"+doc.desc+"</h6>")
    newDiv.append(desc)
    return newDiv
}
var getDivComment = function(doc) {
    var newDiv = $( "<div class='row justify-content-center font-weight-bold' style='vertical-align: middle'></div>" )
    var title = $("<p>Comment :&nbsp;&nbsp;&nbsp;</p>")
    var inputText = $("<textarea id='comment"+doc.num+"' rows=1 cols=80></textarea>")
    var blank = $("<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</p>")
    var sendButton = $("<input type='button' id='btn"+doc.num+"' onclick='sendComment("+doc.num+")' value='Send'></input>")
    newDiv.append(title,inputText,blank,sendButton)
    return newDiv
}
var getEmptyDiv = function(doc) {
    var newDiv = $( "<div class='row justify-content-center font-weight-bold'><p>&nbsp;</p></div>" )
    return newDiv
}
var sendComment = function(num) {
    
    var docName = docList.docs[num].title
    var docComment = $("#comment"+num)[0].value

    alert("TODO: Envoi du commentaire \n"+docComment+"\n sur le fichier "+docName)

}

var generateDocList = function() {
    var i=0;
    docList.docs.forEach(doc => {
        doc.num = i
        $("#list_id").append(getDivTitle(doc))
        $("#list_id").append(getDivDate(doc))
        $("#list_id").append(getDivDesc(doc))
        $("#list_id").append(getDivComment(doc))
        $("#list_id").append(getEmptyDiv(doc))
        i++
    })
}