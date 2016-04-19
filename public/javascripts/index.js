function run(divId, wsUrl) {
    console.log(wsUrl);
    var webSocket = new WebSocket(wsUrl);

    var history = [];

    webSocket.onmessage = function (event) {
        var json = JSON.parse(event.data);
        console.log(json);
        if (json.allHistory) {
            history = json.allHistory;
            console.log(json.allHistory);
            $("#" + divId).html(JSON.stringify(history));
        }
    }
}