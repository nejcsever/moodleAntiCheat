$(document).ready(function() {
	// hide loading gifs
	$('#quiz-name-loading').hide();
	$('#student-name-loading').hide();
	if ($('#subject').find(":selected").text() == "")
		$('#quizName').prop('disabled', 'disabled');
	if ($('#quizName').find(":selected").text() == "")
		$('#studentName').prop('disabled', 'disabled');
	
	// on subject change
	$('form [name="subject"]').change(function() {
		// remove all from list below and show loading gif
		$('#quizName').prop('disabled', 'disabled');
		$('#studentName').prop('disabled', 'disabled');
		$('#quiz-name-loading').show();
		$('#quizName').empty();
		$('#studentName').empty();
		
		var data =  { 'action' : 'getQuizzes', 'subject' : $('select[name="subject"]').val() }
		sendFiltersToServer(data, function(result) {
			$('#quizName').append('<option value=""></option>');
			for (var i = 0; i < result.resultData.length; i++) {
				$('#quizName').append('<option value="'+ result.resultData[i] +'">'+ result.resultData[i] +'</option>');
			}
			$('#quiz-name-loading').hide();
			$('#quizName').prop('disabled', false);
		});
	});
	// on quiz name change
	$('form [name="quizName"]').change(function() {
		// remove all from list below and show loading gif
		$('#studentName').prop('disabled', 'disabled');
		$('#student-name-loading').show();
		$('#studentName').empty();
		
		var data =  { 'action' : 'getStudents', 'quizName' : $('select[name="quizName"]').val() }
		sendFiltersToServer(data, function(result) {
			$('#studentName').append('<option value=""></option>');
			for (var i = 0; i < result.resultData.length; i++) {
				$('#studentName').append('<option value="'+ result.resultData[i] +'">'+ result.resultData[i] +'</option>');
			}
			$('#student-name-loading').hide();
			$('#studentName').prop('disabled', false);
		});
	});
});

function sendFiltersToServer(data, callback) {
	$.ajax({
		type : "POST",
		url : "attemptFilter",
		dataType : "json",
		data : data
	}).done(function( result ) {
		callback(result)
	}).fail(function() {
	});
}