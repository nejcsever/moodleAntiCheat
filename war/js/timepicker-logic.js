$(document).ready(function() {
	var currentDate = new Date();
	$('#dateFrom').datetimepicker({
		timeFormat: 'HH:mm',
		dateFormat: 'dd.mm.yy,',
		hour: currentDate.getHours() - 2,
		minute: currentDate.getMinutes()
	});
	$('#dateTo').datetimepicker({
		timeFormat: 'HH:mm',
		dateFormat: 'dd.mm.yy,',
		hour: currentDate.getHours(),
		minute: currentDate.getMinutes()
	});
});