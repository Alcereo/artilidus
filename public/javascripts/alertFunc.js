function ShowAlertInfo(text) {

    var alert = $("#main_alert_info");
    var alert_content = $("#alert_info_content");

    alert_content.append("<p>" + text + "<p>");
    alert.removeClass("hidden");

}

function ShowAlertSuccess(text) {

    var alert = $("#main_alert-success");
    var alert_content = $("#alert-success_content");

    alert_content.append("<p>" + text + "<p>");
    alert.removeClass("hidden");

}

function ShowAlertDanger(text) {

    var alert = $("#main_alert-danger");
    var alert_content = $("#alert-danger_content");

    alert_content.append("<p>" + text + "<p>");
    alert.removeClass("hidden");

}

$(function () {

    $("#alert-info_close").on("click", function () {
        var alert = $("#main_alert_info");
        var alert_content = $("#alert_info_content");

        alert.addClass("hidden");
        alert_content.empty();
    });

    $("#alert-success_close").on("click", function () {
        var alert = $("#main_alert-success");
        var alert_content = $("#alert-success_content");

        alert.addClass("hidden");
        alert_content.empty();
    });

    $("#alert-danger_close").on("click", function () {
        var alert = $("#main_alert-danger");
        var alert_content = $("#alert-danger_content");

        alert.addClass("hidden");
        alert_content.empty();
    });

})