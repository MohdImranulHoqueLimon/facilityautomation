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

function startSmoking() {
    var xml = '<smokeRequestDto> <fire>true</fire> </smokeRequestDto>';
    $.ajax({
        url: '/gateway/start-fire',
        type: "POST",
        dataType: "xml",
        contentType: "application/xml",
        data: xml,
        success: function (xmlResponse) {

        }
    });
}

var previousEntry = 0;
var previousExit = 0;
var previousTotalPeople = 0;

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
            var alaramSensorState = xmlToJson.alaramSensorState;
            var smokeDetectorSensorState = xmlToJson.smokeDetectorSensorState;
            var fireSprinklerState = xmlToJson.fireSprinklerState;

            console.log("smoke = " + smokeDetectorSensorState.smoke);
            console.log("fire sprinke = " + fireSprinklerState.on);

            if(smokeDetectorSensorState.smoke == 'true') {
                $('#emulation-section').addClass('fire-background');
            } else {
                $('#emulation-section').removeClass('fire-background');
            }

            if(fireSprinklerState.on == 'true') {
                $('#sprinkler').show();
            } else {
                $('#sprinkler').hide();
            }

            var currentTotalEntry = securitySensorState.totalEntered;
            var currentTotalExit = securitySensorState.totalExited;
            var currentTotalPeople = securitySensorState.totalPeople;

            var audioSelector = document.getElementById("myAudio");
            if (alaramSensorState.on == 'true') {
                $('#danger-alarm').attr('src', '/images/danger-alarm-on.png');
                audioSelector.play();
            } else {
                $('#danger-alarm').attr('src', '/images/danger-alarm-off.png');
                if (audioSelector != null) {
                    audioSelector.pause();
                }
            }

            if (currentTotalEntry != previousEntry || currentTotalExit != previousExit || currentTotalPeople != previousTotalPeople) {
                openDoor('.perspective');
                previousEntry = currentTotalEntry;
                previousExit = currentTotalExit;
                previousTotalPeople = currentTotalPeople;

                setTimeout(function () {
                    openDoor('.perspective');
                }, 1000);

                if (lightSensorState.on == 'true') {
                    lightSelector.addClass("active");
                }
                if (lightSensorState.on == 'false') {
                    lightSelector.removeClass("active");
                }
            }

            $('#total-entry').html(currentTotalEntry);
            $('#total-exit').html(currentTotalExit);
            $('#total-people-inside').html(currentTotalPeople);

        }
    });
}

setInterval(getAllData, 2000);

var lightSelector = $('.c-lightbulb__top');
var lightSwitchSelector = $('#light-switch');

lightSwitchSelector.click(function () {
    lightSelector.toggleClass("active");
});