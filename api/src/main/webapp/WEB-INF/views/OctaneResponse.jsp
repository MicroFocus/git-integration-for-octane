<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Git integration for Octane</title>
    <meta charset="UTF-8">

    <style>
        header {
            width: 100%;
            height: 56px;
            background-color: #0079ef;
            color: white;
            line-height: 56px;
            font-weight: bold;
            font-size: 25px;
            margin-bottom: 20px;
        }

        body {
            margin: 0;
            padding: 0;
            font-family: "MetricWeb-Medium", Calibri, sans-serif;
            text-align: center;
            background-color: #f6f6f6;
        }

        .border-b {
            border-bottom: 1px solid #dddddd;
            margin-bottom: 10px;
        }

        .title-text {
            font-weight: bold;
            font-size: 22px;
            padding: 5px;
        }


        .text {
            color: #555555;
            font-size: 18px;
        }

        .aboveFooter {
            position: absolute;
            bottom: 30px;
            width: 100%;
        }

        .text-small{
            color: #555555;
            font-size: 16px;
        }

        footer {
            position: absolute;
            bottom: 0;
            width: 100%;
            line-height: 30px;
            background-color: #dddddd;
            height: 30px;
        }


    </style>

    <script>

        <%--Seconds after which the window will close--%>
        var closeWindowPeriod = 4;
        var cookieName = "GitIntegrationForOctane-autoCloseResponseCookie";

        <%--posts a message to octane to close the dialog window--%>
        function closeOctane() {
            var message = {
                event_name: 'octane_close_dialog',
                shared_space: '${sharedSpace}',
                workspace: '${workSpace}',
                data: {
                    dialog_id: "${dialogId}",
                    refresh: true
                }
            };
            window.parent.postMessage(message, '*');
        }

        window.onload = function () {
            updateAutoCloseState();
        };

        <%-- set a cookie with the name cname and value cvalue --%>
        function setCookie(cname, cvalue) {
            document.cookie = cname + "=" + cvalue + ";path=/";
        }

        <%--  get the value of the cookie with the name cname --%>
        function getCookie(cname) {
            var name = cname + "=";
            var decodedCookie = decodeURIComponent(document.cookie);
            var ca = decodedCookie.split(';');
            for (var i = 0; i < ca.length; i++) {
                var c = ca[i];
                while (c.charAt(0) === ' ') {
                    c = c.substring(1);
                }
                if (c.indexOf(name) === 0) {
                    return c.substring(name.length, c.length);
                }
            }
            return "";
        }

        <%--delete the cookie with the name cname--%>
        function deleteCookie(cname) {
            document.cookie = cname + "=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";

        }

        <%--set the cookie to auto close the dialog based on the checkbox --%>
        function setAutoCloseCookie() {
            var checkBox = document.getElementById("autoClose");
            if (checkBox.checked === true) {
                setCookie(cookieName, "true");
            } else {
                deleteCookie(cookieName);
            }
            updateAutoCloseState();

        }

        var countdownTimer;
        var closeWindowTimeout;

        <%--displays the bottom closing text and sets the dialog closing timer based on the state of the cookie--%>
        function updateAutoCloseState() {
            var text = document.getElementById("autoClose-timer");
            var checkBox = document.getElementById("autoClose");
            var seconds = closeWindowPeriod;

            if (getCookie(cookieName) === "true") {
                checkBox.checked = true;
                document.getElementById("timer").innerHTML = seconds;
                text.style.display = "block";
                countdownTimer = setInterval(function () {
                    seconds = seconds - 1;
                    document.getElementById("timer").innerHTML = seconds;
                    if (seconds === 0) {
                        clearInterval(countdownTimer);
                    }
                }, 1000);
                closeWindowTimeout = setTimeout(closeOctane, seconds * 1000);
            } else {
                if (countdownTimer)
                    clearInterval(countdownTimer);

                if (closeWindowTimeout)
                    clearTimeout(closeWindowTimeout);
                text.style.display = "none";
                checkBox.checked = false;
            }

        }

    </script>
</head>
<body>
<header>Git Integration for Octane</header>

<div class="text">
    <div class="large border-b title-text">A request was sent for ${ids.size()} ${ids.size()>1?"items":"item"}!</div>
    <div>
        <p>The request might take a while to complete
            <c:if test="${requestType.equals('pull-requests')}">
                and the related pull requests will soon be available in the detailed view of the
                selected ${ids.size()>1?"items":"item"}.
            </c:if>
        </p>
        <c:if test="${requestType.equals('pull-requests')}">
            <p>An entity refresh might be needed to display the results in the UDF that was configured in the properties
                file.</p>
        </c:if>
    </div>
    <div class="aboveFooter">
        <label for="autoClose">Close this window automatically next time: </label>
        <input type="checkbox"
               id="autoClose"
               onclick="setAutoCloseCookie()">
    </div>
</div>

<footer class="text-small" id="autoClose-timer" style="display:none">This message will automatically close in: <span
        id="timer"></span> seconds
</footer>
</body>
</html>
