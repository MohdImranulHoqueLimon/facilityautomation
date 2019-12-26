function openDoor(field) {
    var y = $(field).find(".thumb");
    var x = y.attr("class");

    if (y.hasClass("thumbOpened")) {
        y.removeClass("thumbOpened");
    } else {
        $(".thumb").removeClass("thumbOpened");
        y.addClass("thumbOpened");
    }
}

function evacuateManually() {
    var xml = '<requestdto> <evacuate>true</evacuate></requestdto>';
    $.ajax({
        url: '/gateway/security',
        type: "POST",
        dataType: "xml",
        contentType: "application/xml",
        data: xml,
        success: function (xmlResponse) {

        }
    });
}

function confirmIdentity() {
    var xml = '<requestdto> <evacuate>false</evacuate> <pin>' + $('#pin').val() + '</pin> </requestdto>';
    $.ajax({
        url: '/gateway/security',
        type: "POST",
        dataType: "xml",
        contentType: "application/xml",
        data: xml,
        success: function (xmlResponse) {

        }
    });
}

var previousState;

function getAllData() {
    $.ajax({
        url: '/gateway/get-all-data',
        type: "GET",
        dataType: "xml",
        contentType: "application/xml",
        success: function (xmlResponse) {

            var xmlToJson = $.xml2json(xmlResponse);
            var lightSensorState = xmlToJson.lightSensorState;
            var securitySensorState = xmlToJson.securitySensorState;

            if(previousState != xmlResponse) {

                openDoor('.perspective');
                previousState = xmlResponse;

                if (lightSensorState.on == 'true') {
                    $('#toggle').prop("checked", true);
                }
                if (lightSensorState.on == 'false') {
                    $('#toggle').prop("checked", false);
                }

                $('#total-entry').html(securitySensorState.totalEntered);
                $('#total-exit').html(securitySensorState.totalExited);
                $('#total-people-inside').html(securitySensorState.totalPeople);

                /*setTimeout(function () {
                    openDoor('.perspective');
                }, 1500);*/
            }

        }
    });
}

setInterval(getAllData, 1000);