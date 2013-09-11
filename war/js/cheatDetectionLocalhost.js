var DETECTION_SERVLET = '/cheatDetection';

var studentName = "unknown";
var studentMoodleId = "unknown";
var quizName, quizSubject, quizUrl; // Quiz data is generated when user generates code for quiz
var attemptMode = true; // wheather student is reviewing his quiz results or solving an attempt
var timeLimitedTimerRunning = true;

$(document).ready(function() {
	/* Checking if attempt or review */
	attemptMode = $('body#page-mod-quiz-attempt').length;
	
	/* Here we get student's name and moodle id. */
	studentName = $('.logininfo').children('a').first().text();
	var studentURL = $('.logininfo').children('a').first().attr('href');
	studentMoodleId = decodeURI((RegExp(name + '=' + '(.+?)(&|$)').exec(studentURL)||[,null])[1] );
	
	sendActionToServer('info', 'ready');
	
	/* Disabling printing website */
	var style = document.createElement('style');
	style.type = 'text/css';
	style.innerHTML = '@media print {body{display: none;}}';
	document.getElementsByTagName('head')[0].appendChild(style);
	
	/* Start counting 2 seconds, for #quiz-time-left. Must wait 2 seconds, because #quiz-time-left is always visible and time is set dynamically */
	setTimeout(function() {
		timeLimitedTimerRunning = false;
	}, 2000);
});

/* When website is closed. */
$(window).on('beforeunload', function() {
	sendActionToServer('info', 'unload');
});

/* Posts activity on server. */
function sendActionToServer(action, cheatType, additionalData) {
	/* Post to server only if in attempt mode */
	if (!attemptMode)
		return;
	
	var timestamp = new Date().getTime();
	// delay posting while timer is running (differences between infinite and time limeted quizzes)
	if (timeLimitedTimerRunning) {
		var interval = setInterval(function() {
			timeLeft = $('#quiz-time-left').text();
			if (timeLeft != "" || !timeLimitedTimerRunning) { 
				$.ajax({
					type: 'POST',
					url: DETECTION_SERVLET,
					dataType : 'xml',
					data: {'action' : action, 'timestamp' : timestamp, 'timeLeft' : timeLeft, 'cheatType' : cheatType,'studentName' : studentName, 'studentMoodleId' : studentMoodleId, 'quizName' : quizName, 'quizSubject' : quizSubject, 'quizUrl' : quizUrl, 'additionalData' : additionalData}
				});
				clearInterval(interval);
			}
		}, 500);
	} else {
		var timeLeft = ( $('#quiz-time-left').length > 0 ) ? $('#quiz-time-left').text() : undefined;
		$.ajax({
			type: 'POST',
			url: DETECTION_SERVLET,
			dataType : 'xml',
			data: {'action' : action, 'timestamp' : timestamp, 'timeLeft' : timeLeft, 'cheatType' : cheatType,'studentName' : studentName, 'studentMoodleId' : studentMoodleId, 'quizName' : quizName, 'quizSubject' : quizSubject, 'quizUrl' : quizUrl, 'additionalData' : additionalData}
		});
	}
}

/* Gets text which is currently highlighted on webpage (without html tags). */
function getHighlightedText() {
	var text = '';
    if (window.getSelection)
        text = window.getSelection().toString();
    else if (document.selection && document.selection.type != "Control")
        text = document.selection.createRange().text;
    return text;
}

/* Disabled right click */
$(document).bind('contextmenu', function() {
	sendActionToServer('cheat', 'rightClick', getHighlightedText());
    return false;
});

/* Lost focus with window */
$(window).bind('blur', function() {
	sendActionToServer('cheat', 'focus');
});

/* Check printscreen button */
$(window).keyup(function(e) {
	var keyCode = e.keyCode||e.charCode; 
	if (keyCode == 44) {
		sendActionToServer('cheat', 'prntScrn');
		e.preventDefault();
	}
});

/* Ctrl combinations */
var ctrlPressed = false;
var forbiddenCtrlKeys = ['A', 'C', 'X', 'V', 'S', 'U'];
var additionalDataKeys = ['C', 'V', 'X']
$(document).keydown(function(e){
	if(e.ctrlKey || e.metaKey)
		ctrlPressed=true;
	
	var keyPressed = String.fromCharCode(e.keyCode);
	var forbiddenKeyIndex = $.inArray(keyPressed, forbiddenCtrlKeys);
	if(ctrlPressed && forbiddenKeyIndex > -1) {
		var ctrlType = 'CTRL+' + forbiddenCtrlKeys[forbiddenKeyIndex];
		var hasAdditionalData = $.inArray(keyPressed, additionalDataKeys) > -1;
		sendActionToServer('cheat', ctrlType, hasAdditionalData ? getHighlightedText() : undefined);
		return false;
	}
}).keyup(function(e) {
	if(e.ctrlKey || e.metaKey) ctrlPressed = false;
});