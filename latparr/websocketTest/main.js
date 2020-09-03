var wsUri = "wss://echo.websocket.org/";
const connection = new WebSocket(wsUri);

connection.onopen = () => {
    console.log("open");
}
let yourConn;
connection.onmessage = message => {
    var data = JSON.parse(message.data)
    console.log(data);
    switch (data.type) {
        case 'start':
            StartConnection();
            break;

        case 'candidate':
            yourConn.addIceCandidate(new RTCIceCandidate(data.candidate));
            console.log(data.candidate);
            break;
        case "offer":
            handleOffer(data.offer, data.name);
            break;
        case "answer":
            yourConn.setRemoteDescription(new RTCSessionDescription(data.answer));
            break;

        default:
            break;
    }
}

connection.onclose = () => {
    console.log("close");
}

btnTest.addEventListener('click', () => {
    send({ type: 'start' })
})

function StartConnection() {
    var configuration = {
        "iceServers": [{ "url": "stun:stun2.1.google.com:19302" }]
    };

    yourConn = new RTCPeerConnection(configuration);
    console.log("connection", yourConn);
    yourConn.setLocalDescription()
        // Setup ice handling
    yourConn.onicecandidate = event => {
        if (event.candidate) {
            send({
                type: "candidate",
                candidate: event.candidate
            });
        }
    };
    //creating data channel 
    dataChannel = yourConn.createDataChannel("channel");

    dataChannel.onerror = function(error) {
        console.log("Ooops...error:", error);
    };

    //when we receive a message from the other peer, display it on the screen 
    dataChannel.onmessage = function(event) {
        //  chatArea.innerHTML += connectedUser + ": " + event.data + "<br />";
    };

    dataChannel.onclose = function() {
        console.log("data channel is closed");
    };

    yourConn.createOffer(function(offer) {
        send({
            type: "offer",
            offer: offer
        });
        yourConn.setLocalDescription(offer);
    }, function(error) {
        alert("Error when creating an offer");
    });

}

function send(message) {
    //attach the other peer username to our messages
    // if (connectedUser) {
    //     message.name = connectedUser;
    // }
    message.name = "khmo";
    connection.send(JSON.stringify(message));

};

function handleOffer(offer, name) {
    connectedUser = name;
    yourConn.setRemoteDescription(new RTCSessionDescription(offer));

    //create an answer to an offer 
    yourConn.createAnswer(function(answer) {
        yourConn.setLocalDescription(answer);
        send({
            type: "answer",
            answer: answer
        });
    }, function(error) {
        alert("Error when creating an answer");
    });

};