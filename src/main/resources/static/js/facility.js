function sendData() {
    var xml = '<requestdto> <name>'+$('#name').val()+'</name></requestdto>';
    $.ajax({
        url: '/gateway/send',
        type: "POST",
        dataType: "xml",
        contentType: "application/xml",
        data: xml,
        success: function (response) {
            console.log(response);
        }
    });
}