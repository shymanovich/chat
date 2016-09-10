var appState = {
	mainUrl : 'http://localhost:8080/chat'
};

function defaultErrorHandler(msg) {
	console.error(msg);
	alert(msg);
}

function isError(text) {
	if(text == "")
		return false;

	try {
		var obj = JSON.parse(text);
	} catch(ex) {
		return true;
	}
	return !!obj.error;
}

function get(url, continueWith) {
	ajax('GET', url, null, continueWith);
}

function post(url, data, continueWith) {
	ajax('POST', url, data, function () {
		document.getElementById('msg_cnt').innerHTML = '';
		chat();
	});
}

function put(url, data, continueWith) {
	ajax('PUT', url, data, continueWith);
}

function deleteMsg(url, data, continueWith) {
	ajax("DELETE", url, data, continueWith);
}

function ajax(method, url, data, continueWith)
{
	var xhr = new XMLHttpRequest();
	xhr.open(method || 'GET', url, true);

	xhr.onload = function() {
		if(xhr.readyState != 4)
			return;

		if(xhr.status != 200) {
			defaultErrorHandler('Error on the server side, response ' + xhr.status);
			return;
		}

		if(isError(xhr.responseText)) {
			defaultErrorHandler('Error on the server side, response ' + xhr.responseText);
			return;
		}

		continueWith(xhr.responseText);
	};

	xhr.ontimeout = function() {
		defaultErrorHandler('Server timed out!');
	};

	xhr.onerror = function(e) {
		var errMsg = 'Server connection error!\n' +
			'\n' +
			'Check if \n' +
			'- server is active\n' +
			'- server sends header "Access-Control-Allow-Origin:*"';

		defaultErrorHandler(errMsg);
	};

	xhr.send(data);
}

function chat() {
	var url = appState.mainUrl;
	get(url, function(responseText) {
		console.assert(responseText != null);
		var response = JSON.parse(responseText);
		createAllMessages(response.messages);
	});
}