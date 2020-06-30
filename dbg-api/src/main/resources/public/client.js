
var id = new URLSearchParams(window.location.search).get('id');

var host = "tabletopster.com/game";
host = "localhost:8080";

const client = new StompJs.Client({
	brokerURL : "ws://"+host+"/sock",
	debug : function(str) {
		//console.log(str);
	},
	reconnectDelay : 5000,
	heartbeatIncoming : 4000,
	heartbeatOutgoing : 4000
});
client.onConnect = function(frame) {
	var subscription = client.subscribe("/out/game/"+id+"/board", function(message) {
		console.log(JSON.stringify(message));
		document.getElementById("theBoard").src = "http://"+host+"/papi/gameimage/"+id+"?" + new Date().getTime();
//	    if (message.body) {
//	      alert("got message with body " + message.body);
//	    } else {
//	      alert("got empty message");
//	    }
	});
};
document.getElementById("theBoard").src = "http://"+host+"/papi/gameimage/"+id+"?" + new Date().getTime();
client.activate();
