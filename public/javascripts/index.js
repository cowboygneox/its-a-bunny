var calculationHistory = [];

function run(divId, expressionId, submitId, errorId, postUrl, wsUrl) {
    var webSocket = new WebSocket(wsUrl);

    webSocket.onmessage = function (event) {
        var json = JSON.parse(event.data);
        if (json.allHistory) {
            calculationHistory = json.allHistory;
            $("#" + divId).html(formatHistoryTable());
        } else if (json.history) {
            if (calculationHistory.len > 9) {
                calculationHistory = [json.history].concat(calculationHistory.arr.splice(calculationHistory.len - 2));
            } else {
                calculationHistory = [json.history].concat(calculationHistory);
            }
            console.log(calculationHistory);
            $("#" + divId).html(formatHistoryTable());
        }
    }

    $("#" + expressionId).focus(function(event) {
        $("#" + errorId).html("");
    });

    function submit(event) {
        var text = $("#" + expressionId).val();
        var json = {expression: text}

        function finishedPost(event) {
            if (event.status != 202) {
                $("#" + expressionId).blur();

                $("#" + errorId).html(
                    '<br />' +
                    '<div class="panel panel-danger">' +
                        '<div class="panel-heading">' +
                            '<h3 class="panel-title">Evaluation Failed</h3>' +
                        '</div>' +
                        '<div class="panel-body">' + event.responseJSON.error + '</div>' +
                    '</div>'
                );
            }
        }

        $.ajax({
            type: "POST",
            url:  postUrl,
            data: JSON.stringify(json),
            complete: finishedPost,
            contentType: "application/json",
            dataType: "json"
        });
    }

    $("#" + submitId).click(submit);

    $("form").keypress(function (e) {
        if (e.which == 13) {
            submit(e);
            return false;
        }
    });
}

function formatHistoryTable() {
    var rows = $.map(calculationHistory, function(history) {
        return '<tr>' +
            '<td>' + history.expression + ' = ' + history.result + '</td>' +
        '</tr>';
    });

    return '<table class="table">' +
        rows.join('') +
    '</table>';
}