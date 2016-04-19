function run(wsUrl) {
    console.log(wsUrl);
    var webSocket = new WebSocket(wsUrl);
    webSocket.onmessage = function (event) {
        console.log(event.data);
    }
}