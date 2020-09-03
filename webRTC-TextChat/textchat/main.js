const name = prompt("What's your name?");


if (!location.hash) {
    location.hash = Math.floor(Math.random() * 0xFFFFFF).toString(16);
}
const chatHash = location.hash.substring(1);


const drone = new ScaleDrone('4ozCuDmpg2PRpiVg');

const roomName = 'observable-' + chatHash;


let room;

let receiveBuffer = [];
var fileName;


const configuration = {
    iceServers: [{
        url: 'stun:stun.l.google.com:19302'
    }]
};
// RTCPeerConnection
let pc;
// RTCDataChannel
var dataChannel;
// Wait for Scaledrone signalling server to connect
drone.on('open', error => {
    if (error) {
        return console.error(error);
    }
    room = drone.subscribe(roomName);
    room.on('open', error => {
        if (error) {
            return console.error(error);
        }
        console.log('Connected to signaling server');
    });

    room.on('members', members => {
        console.log(members.length)
        if (members.length >= 3) {
            return alert('The room is full');
        }
        const isOfferer = members.length === 2;
        startWebRTC(isOfferer);
    });
});

function sendSignalingMessage(message) {
    drone.publish({
        room: roomName,
        message
    });
}

function startWebRTC(isOfferer) {
    console.log('Starting WebRTC in as', isOfferer ? 'offerer' : 'waiter');
    pc = new RTCPeerConnection(configuration);

    pc.onicecandidate = event => {
        if (event.candidate) {
            sendSignalingMessage({ 'candidate': event.candidate });
        }
    };



    if (isOfferer) {
        pc.onnegotiationneeded = () => {
            pc.createOffer(localDescCreated, error => console.error(error));
        }
        dataChannel = pc.createDataChannel('chat');
        setupDataChannel();
    } else {
        // If user is not the offerer let wait for a data channel
        pc.ondatachannel = event => {
            dataChannel = event.channel;
            setupDataChannel();
        }
    }

    startListentingToSignals();
}

function startListentingToSignals() {

    room.on('data', (message, client) => {

        if (client.id === drone.clientId) {
            return;
        }
        if (message.sdp) {
            pc.setRemoteDescription(new RTCSessionDescription(message.sdp), () => {
                console.log('pc.remoteDescription.type', pc.remoteDescription.type);

                if (pc.remoteDescription.type === 'offer') {
                    console.log('Answering offer');
                    pc.createAnswer(localDescCreated, error => console.error(error));
                }
            }, );
        } else if (message.candidate) {
            // Add the new ICE candidate to our connections remote description
            pc.addIceCandidate(new RTCIceCandidate(message.candidate));
        }
    });
}

function localDescCreated(desc) {
    pc.setLocalDescription(
        desc,
        () => sendSignalingMessage({ 'sdp': pc.localDescription }),
        error => console.error(error)
    );
}


function setupDataChannel() {
    checkDataChannelState();

    dataChannel.onopen = checkDataChannelState;
    dataChannel.onclose = checkDataChannelState;
    dataChannel.onmessage = event => {
        insertMessageToDOM(JSON.parse(event.data), false);
    }
}




function checkDataChannelState() {
    console.log('WebRTC channel state is:', dataChannel.readyState);
    if (dataChannel.readyState === 'open') {
        insertMessageToDOM({ content: 'WebRTC data channel is now open' });
    }
}


function insertMessageToDOM(options, isFromMe) {

    var receivedBuffers = [];
    receivedBuffers.push(options);
    console.log(options);
    console.log(receivedBuffers);

    const template = document.querySelector('template[data-template="message"]');
    const nameEl = template.content.querySelector('.message__name');

    if (options.name) {
        nameEl.innerText = options.name;
    }
    template.content.querySelector('.message__bubble').innerText = options.message;
    var intFile = [];

    console.log(options.file);

    //console.log(intFile);
    if (options.file != null) {

        // const dowmloadfile = template.content.querySelector('.download__file');
        // const b64 = options.file.split(',');
        // const type = b64[0].split(':');
        // const filetype = type[1].split(";");
        // const received = new Blob([b64[1]], { type: filetype[0] });
        // console.log("received", received);
        // dowmloadfile.href = URL.createObjectURL(received);
        // console.log("donlodahref", dowmloadfile.href);
        // dowmloadfile.download = options.fileName1;
        // dowmloadfile.textContent =
        //     `Click to download ${options.fileName1}`;
        // dowmloadfile.style.display = 'block';
    }
    const clone = document.importNode(template.content, true);
    const messageEl = clone.querySelector('.message');
    if (isFromMe) {
        messageEl.classList.add('message--mine');
    } else {
        messageEl.classList.add('message--theirs');
    }

    const messagesEl = document.querySelector('.messages');
    messagesEl.appendChild(clone);

    // Scroll to bottom
    messagesEl.scrollTop = messagesEl.scrollHeight - messagesEl.clientHeight;




}


const chunkSize = 20000;
let offset = 0;

const form = document.querySelector('form');
var fileInput = document.querySelector('input[type="file"]');
fileInput.addEventListener('change', handleFileInputChange, false);
var file;
var arrayfile = null;
async function handleFileInputChange() {
    file = fileInput.files[0];


    if (!file) {
        console.log('No file chosen');
    } else {
        var reader = new FileReader()
        reader.onload = function() {
            arrayfile = reader.result;

        }
        fileName = file.name;

        reader.readAsArrayBuffer(file);

        // reader.addEventListener('load', e => {
        //     //  console.log(e.target.result);
        //     //dataChannel.send(JSON.stringify({ slicefile: e.target.result }));
        //     arrayfile = e.target.result;
        //     offset += e.target.result.byteLength;
        //     if (offset < file.size) {
        //         readSlice(offset);
        //     }
        // });
        // const readSlice = o => {
        //     //       console.log('readSlice ', o);
        //     const slice = file.slice(offset, o + chunkSize);
        //     reader.readAsArrayBuffer(slice);
        // };
        // readSlice(0);

    }

}
form.addEventListener('submit', () => {
    const input = document.querySelector('input[type="text"]');
    const message = input.value;
    input.value = "";
    fileInput.value = "";
    console.log(intFile);
    var intFile = new Int8Array(arrayfile);
    // console.log(intFile);
    const data = {
        name,
        message: message,
        fileName1: fileName,
        file: intFile
    };

    dataChannel.send(JSON.stringify(data));
    insertMessageToDOM(data, true);

});