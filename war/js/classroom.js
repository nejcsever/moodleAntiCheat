/* JSON for classroom name, width, height and seats*/
var classroomData = {
	"name" : "",
	"colCount" : 0,
	"rowCount" : 0,
	"seats" : []
};

/* Variable which is activated if edit button is clicked (to determine wether to check for existance or not) */
var editingSeat = false;

/* Generates grid for classroom seats. */
var generateGrid = function(e) {
	
	if (!isGridFormValid()) {
		return false;
	}
	
	var classroomName = $("#name").val();
	$('#classroom-name').html(classroomName);
	$('#classroom-name').show();
	$('#add-classroom-form').show();
	$('#grid-form').slideUp(function() {
		
		var colCount = $("input[name='col']").val();
		var rowCount = $("input[name='row']").val();
		var elementWidth = 100 / colCount;
		classroomData.name = classroomName;
		classroomData.colCount = parseInt(colCount);
		classroomData.rowCount = parseInt(rowCount);
		
		var table = $('<table>');
		for ( var i = 0; i < rowCount; i++) {
			var tableRow = $('<tr>');
			for ( var j = 0; j < colCount; j++) {
				var tableCol = $('<td>');
				tableCol.append('<div id="' + i + '-' + j + '" class="seat"><p class="seat-index">Vrsta: ' + (i + 1) + ', Sedež:' + (j + 1) + '</p><p class="empty-seat-text">Prosto</p><button class="btn btn-mini add-seat-button">Dodaj sedež</button></div>');
				tableRow.append(tableCol);
			}
			table.append(tableRow)
		}
		$('#grid').append(table);
		
		$('#grid').slideDown(500);
	});
	e.preventDefault();
};

/* Validates inputs before generating grid */
function isGridFormValid() {
	/* Check empty classroom name */
	if (!$("#name").val()) {
		return false;
	}
	if (!isNumber($("#col").val())) {
		return false;
	}
	if (!isNumber($("#row").val())) {
		return false;
	}
	
	return true;
}

function isNumber(n) {
  return !isNaN(parseFloat(n)) && isFinite(n);
}

var saveSeat = function(e) {
	
	var ipv4 = $('input[name="ipv4"]').val();
	var ipv6 = $('input[name="ipv6"]').val();
	var col = parseInt($('input[name="col"]').attr('value'));
	var row = parseInt($('input[name="row"]').attr('value'));
	
	if (editingSeat) {
		removeSeat(row, col);
	}
	
	classroomData.seats.push({"row" : row, "col" : col, "ipv4" : ipv4, "ipv6" : ipv6});
	$('#dialog').dialog('close');
	
	/* Update seat text. */
	$('div#' + row + '-' + col).children('.add-seat-button').after('<button class="btn btn-mini btn-danger delete-seat-button"><i class="icon-remove"></i></button>');
	$('div#' + row + '-' + col).children('.add-seat-button').after('<button class="btn btn-mini edit-seat-button"><i class="icon-edit"></i> Edit</button>');
	$('div#' + row + '-' + col).children('.add-seat-button').remove();
	
	
	var seatTextElement = $('div#' + row + '-' + col).children((editingSeat) ? '.seat-text' : '.empty-seat-text');
	seatTextElement.switchClass('empty-seat-text', 'seat-text');
	var seatText = "";
	if (ipv4 != "") {
		seatText += '<small><strong>IPv4: </strong></small><span class="ipv4-value">' + ipv4 + '</span><br />';
	} else {
		seatText += '<br />';
	}
	if (ipv6 != "") {
		seatText += '<small><strong>IPv6: </strong></small><span class="ipv6-value">' + ipv6 + '</span>';
	} else {
		seatText += '<br />';
	}
	seatTextElement.html(seatText);
	$('div#' + row + '-' + col).addClass('seat-full');
	
	e.preventDefault();
};

function removeSeat(row, col) {
	$.each(classroomData.seats, function(i,obj) {
	  if (obj.row === row && obj.col === col) {
		  classroomData.seats.splice(i, 1); // remove element
		  return false;
	  }
	});  
}

$(function() {
	$("#dialog").dialog({
		autoOpen: false,
		resizable: false,
		modal: true,
		close: function() {
			$('#dialog-form').find(':input').each(function() {
				$(this).val('');
			});
		}
	});
});
var saveClassroom = function() {
	$('#add-classroom-form button').hide();
	$('#classroom-loading').fadeIn();
	$.ajax({
		type : "POST",
		url : "createClassroom",
		data : {'classroomData' : JSON.stringify(classroomData)},
		dataType: "json",
		async: false
	}).done(function() {
		window.location = "/classroomList";
		return false;
	}).fail(function() {
		window.location = "/classroomList";
		return false;
	});
	return false;
}

/* DOCUMENT READY */
$(document).ready(function() {
	$('#classroom-loading').hide();
	/* Hiding grid and classroom name on page load. */
	$('#grid').hide();
	$('#classroom-name').hide();
	$('#add-classroom-form').hide();
	
	/* ADD SEAT BUTTON */
	$('#grid').on('click', '.add-seat-button', function() {
		editingSeat = false;
		/* Set column and row position in hidden input fields. Needed for saving seat data later. */
		var rowCol = $(this).parent().attr('id').split("-");
		$('input[name="row"]').attr('value', rowCol[0]);
		$('input[name="col"]').attr('value', rowCol[1]);
		
		$("#dialog").dialog('open');
	});
	
	/* EDIT SEAT BUTTON */
	$('#grid').on('click', '.edit-seat-button', function() {
		editingSeat = true;
		/* Set column and row position in hidden input fields. Needed for saving seat data later. */
		var rowCol = $(this).parent().attr('id').split("-");
		var ipv4 = $(this).parent().find('.ipv4-value').text();
		var ipv6 = $(this).parent().find('.ipv6-value').text();
		$('input[name="row"]').attr('value', rowCol[0]);
		$('input[name="col"]').attr('value', rowCol[1]);
		$('input[name="ipv4"]').val(ipv4);
		$('input[name="ipv6"]').val(ipv6);
		
		$("#dialog").dialog('open');
	});
	
	/* DELETE SEAT BUTTON */
	$('#grid').on('click', '.delete-seat-button', function() {
		editingSeat = true;
		/* Set column and row position in hidden input fields. Needed for saving seat data later. */
		var rowCol = $(this).parent().attr('id').split("-");
		var row = parseInt(rowCol[0]);
		var col = parseInt(rowCol[1]);
		removeSeat(row, col);
		var seatIndex = $(this).parent().find('.seat-index');
		/* Style seat removal */
		$('div#' + row + '-' + col).removeClass('seat-full');
		seatIndex.nextAll().remove();
		seatIndex.after('<p class="empty-seat-text">Prosto</p><button class="btn btn-mini add-seat-button">Dodaj sedež</button>');
	});
	
	$('#grid-button').click(generateGrid);
	
	$('#btn-save-seat').click(saveSeat);
	
	$('#add-classroom-form').submit(saveClassroom);
});