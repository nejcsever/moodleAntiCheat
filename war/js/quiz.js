/* CONSTANTS */
var JQUERY_URL = "https://diploma-sever.appspot.com/js/jquery.js";
var CHEAT_SCRIPT_URL = "https://diploma-sever.appspot.com/js/cheatDetection.js";

function generateQuizScript() {
	var quizName = $('input[name="name"]').val();
	var quizSubject = $('input[name="subject"]').val();
	var quizUrl = $('input[name="url"]').val();
	
	var generatedScript = "<div><style type=\"text/css\">@media print {body{display: none;}}</style>" +
	"<script type=\"text/javascript\" src=\"" + JQUERY_URL + "\"></script>" +
	"<script type=\"text/javascript\">" +
	"var quizName=\'" + quizName + "\';" +
	"var quizSubject=\'" + quizSubject + "\';" +
	"var quizUrl=\'" + quizUrl + "\';" +
	"</script><script type=\"text/javascript\" src=\"" + CHEAT_SCRIPT_URL + "\"></script></div>"
	
	$('#code-area').val(generatedScript);
	$('.code-container').slideDown();
}

$(document).ready(function() {
	
	/* Hide lements */
	$('.code-container').hide();
	
	$('#quiz-form').submit(function() {
		generateQuizScript();
		return false;
	});
});