socket = io.connect("http://localhost:8080", {transports: ['websocket']});

startJS();

function startJS() {


    const Start = "        <h5>Type Here: </h5>\n" +
        "        <label>\n" +
        "            <input type=\"text\" placeholder=\"Enter Message\" id=\"typedMessage\">\n" +
        "        </label>\n" +
        "        <button onclick=\"sendMessage();\">Send</button>\n" +
        "        <div id = \"message\">\n" +
        "        </div>";

    socket.on("JoiningMessage", function (event) {
        const messageID = document.getElementById("message");
        const message = messageID.innerHTML;
        messageID.innerHTML = message + "</br>" + "<h3>" + event + "<h3>";
    });

    socket.on("StartChat", function (event) {
        document.getElementById("start").innerHTML = Start

    });

    socket.on("messageReceived", function (event) {
        const messageID = document.getElementById("message");
        const message = messageID.innerHTML;
        messageID.innerHTML = message + "</br>" + event;
    });
}

function sendMessage() {
    const message = document.getElementById("typedMessage").value;
    document.getElementById("typedMessage").value = "";
    if(message !== "") {
        socket.emit("sendMessage", message);
    }
}

function sendUsername() {
    const username = document.getElementById("Username").value;
    if(username !== "") {
        socket.emit("addUserName", username);
        socket.emit("UserJoined")
    }
}