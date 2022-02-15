var hiddenContact = true
var emailIsValid = function (email) {
  return /\S+@\S+\.\S+/.test(email)
}
var success = function() {
    $("#success-alert").fadeTo(2000, 500).slideUp(500, function() {
        $("#success-alert").slideUp(1500);
    })
}
var error = function(message) {
    $("#error_id")[0].innerText = message
    $("#error-alert").fadeTo(2000, 500).slideUp(500, function() {
        $("#error-alert").slideUp(3000);
    })
}
var sendEmail = function() {
    var email = $("#email")[0].value
    var comments = $("#comments")[0].value
    if (!emailIsValid(email)) {
        error("Email is not valid")
        return
    }
    if (comments.length ==0) {
        error("No comment to send")
        return
    }
    $("#email")[0].value=''
    $("#comments")[0].value=''
}
var toggleContact = function() {
    console.log("toggleContact",hiddenContact)
    if (hiddenContact == true) {
        $("#contact_div").slideDown(1000)
    } else {
        $("#contact_div").slideUp(1000)
    }
    hiddenContact = !hiddenContact
}	
var init_type_text = function() {
    $('.type-text' ).each( function() {
        var items = $( this ).attr( 'title' ) + ';' + $( this ).text();
        $( this ).empty().attr( 'title', '' ).teletype( {
            text: $.map( items.split( ';' ), $.trim ),
            typeDelay: 10,
            cursor: '▋', 
            delay: 3000,
            preserve: true,
            loop: 1
        } );
    } )
}

$(document).ready(function() {
    $("#success-alert").hide()
    $("#error-alert").hide()
    $("#contact_div").hide()
    init_type_text()
})