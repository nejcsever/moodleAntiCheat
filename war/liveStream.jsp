<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ page import="com.sever.diploma.entities.*"%>
<%@ page import="java.util.List"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Goljufije v živo</title>

<link rel="stylesheet" type="text/css" href="css/bootstrap.min.css">
<link rel="stylesheet" type="text/css" href="css/mainStyle.css">

<script type="text/javascript" src="js/jquery.js"></script>
<script type="text/javascript" src="js/bootstrap.min.js"></script>
<script type="text/javascript" src="/_ah/channel/jsapi"></script>
<script type="text/javascript">
// clock
function startTime() {
		var today=new Date();
		var h=today.getHours();
		var m=today.getMinutes();
		// add a zero in front of numbers<10
		m=checkTime(m);
		document.getElementById('time').innerHTML=h+":"+m;
		t=setTimeout(function(){startTime()},1000);
	}
	
	function checkTime(i) {
		if (i<10){
			i="0" + i;
		}
	return i;
}
</script>
<% // JAVASCRIPT for channelAPI is at the end of body tag. %>
<% Classroom classroom = (Classroom) request.getAttribute("classroom"); %>
<% List<Seat> seats = classroom.getSeats(); %>
<% int cheatLimit = (Integer) request.getAttribute("cheatLimit"); %>
<% String displayType = (String) request.getAttribute("displayType"); %>
<% boolean otherUsers = (Boolean) request.getAttribute("otherUsers"); %>
</head>
<body onload="startTime()">
	<jsp:include page="page-elements/authentication.jsp">
		<jsp:param name="hideLogout" value="true"/>
	</jsp:include>
	<div class="wood-container">
		<h3 class="text-center no-margin"><%= classroom.getName() %></h3>
		<p class="text-center"><i class="icon-time"></i></p>
		<div id="time" class="text-center"></div>
		<div id="classroom-layout"></div>
		<% if (otherUsers) {%>
		<hr />
		<div id="other-users">
			<table></table>
		</div>
		<%}%>
	</div>
	
	<script type="text/javascript">
		var rowCount = <%= classroom.getRowCount() %>;
	    var columnCount = <%= classroom.getColumnCount() %>;
		var displayType = '<%=displayType%>';
		var MAX_CHEATS = <%=cheatLimit%>;
		var otherUsersEnabled = <%=otherUsers%>;
		var otherUsers = {}; // key=moodleId, value=[row, column, cheatCount]
		
		function getSeatContent(row, col) {
			if (displayType == "hangman") {
				return '<div id="seat-'+ row +'-' + col +'" class="hangman hangman-0"></div>';
			} else if (displayType == "statusbar") {
				return '<div id="seat-'+ row +'-' + col +'" class="statusbar"><span class="statusbar-progress"><p class="live-cheat-count">0</p></span></div>';
			} else {
				return '';
			}
		}
		
		function changeSeatStyle(row, column, cheatCount) {
			if (displayType == "hangman") {
				// change hangman image
				var hangmanObj = $('#seat-'+ row +'-'+ column);
				var parent = hangmanObj.parent();
				parent.fadeOut('fast', function() {
					hangmanObj.removeClass('hangman-' + (cheatCount - 1));
					hangmanObj.addClass('hangman-' + cheatCount);
					parent.fadeIn('fast');
				});
			} else if (displayType == "statusbar") {
				var statusbarObj = $('#seat-'+ row +'-'+ column);
				var progressObj = statusbarObj.find('.statusbar-progress');
				progressObj.width(cheatCount / MAX_CHEATS * 100 + '%');
				progressObj.css("border-right", "1px solid black");
				progressObj.find(".live-cheat-count").text(cheatCount);
			}
		}	
		
		// checks if cheat is severe
		function isCheatSevere(cheat) {
			var notSevereCheats = ["ready", "unload"];
			if (notSevereCheats.indexOf(cheat) > -1)
				return false;
			return true;
		}
		
		// handles cheats from ip-s that are not in classroom
		function handleOtherUser(cheat) {
			var otherUsersCount = Object.keys(otherUsers).length;
			var col = otherUsersCount % columnCount;
			var row = Math.floor(otherUsersCount / columnCount) + rowCount; // rows begin with rowCount on...
			if (otherUsers[cheat.moodleId] == undefined) {
				otherUsers[cheat.moodleId] = [row, col, 0];
				var tdObj = $('<td id="'+ row +'-'+ col +'"><div class="seat-live"><p class="student-name">'+ cheat.studentName +'</p>'+ getSeatContent(row, col) +'</div></td>');
				if (otherUsersCount % rowCount == 0) {
					$('#other-users table').append($('<tr>'));
				}
				$('#other-users table tr').last().append(tdObj);
			} else {
				row = otherUsers[cheat.moodleId][0];
				col = otherUsers[cheat.moodleId][1];
			}
			if (isCheatSevere(cheat.cheatType) && otherUsers[cheat.moodleId][2] < MAX_CHEATS) {
				var cheatCount = ++otherUsers[cheat.moodleId][2];
				changeSeatStyle(row, col, cheatCount);
			}
		}
		
		// manage recieved cheat
		var onMessage = function(msg) {
			cheat = JSON.parse(msg.data);
			var row = -1;
			var column = -1;
			// check if ip matches classroom seats
			if (cheat.ipv4 != "null" && cheat.ipv4 in ipv4List) {
				row = ipv4List[cheat.ipv4][0];
				column = ipv4List[cheat.ipv4][1];
			} else if (cheat.ipv6 != "null" && cheat.ipv6 in ipv6List) {
				row = ipv6List[cheat.ipv6][0];
				column = ipv6List[cheat.ipv6][1];
			}
			// matching ip
			if (row != -1 && column != -1) {
				var seatMoodleId = seats[row + '-' + column][0];
				// display name
				if (cheat.moodleId != seatMoodleId) {
					seats[row + '-' + column][0] = parseInt(cheat.moodleId);
					var nameObj = $('#'+ row +'-'+ column +' .student-name');
					nameObj.fadeOut('fast', function() {
						nameObj.html(cheat.studentName);
						nameObj.fadeIn();
					});
				}
				if (isCheatSevere(cheat.cheatType) && seats[row + '-' + column][1] < MAX_CHEATS) {
					var cheatCount = ++seats[row + '-' + column][1];
					changeSeatStyle(row, column, cheatCount);
				}
			} else if (otherUsersEnabled) { // users outside classroom
				handleOtherUser(cheat);
			}
		}
		
	    // generating classroom layout
	    var table = document.createElement('table');
	    for (var i = 0; i < rowCount; i++) {
	    	var tr = document.createElement('tr');
	    	for (var j = 0; j < columnCount; j++) {
	    		tr.innerHTML += '<td id="' + i + '-' + j + '"></td>';
	    	}
	    	table.appendChild(tr);
	    }
	    document.getElementById('classroom-layout').appendChild(table);
	    
		// HANDLING SEATS
	    // initialization of ip hash maps of seats in classroom
	    var ipv4List = {}; // key=ipv4, value=[row, col]
	    var ipv6List = {}; // key=ipv6, value=[row, col]
	    var seats = {}; // key= row-column, value=[moodleId, cheatCount]
	    <% for (Seat s : seats) { %>
	    	var row = <%= s.getRow() %>;
	    	var col = <%= s.getColumn() %>;
	    	seats[row +'-' + col] = [-1, 0];
	    	$('#'+ row +'-' + col).append('<div class="seat-live"><p class="student-name">Prazen sedež</p>'+ getSeatContent(row, col) +'</div>');
	    	<% if (s.getIpv4() != null && !s.getIpv4().equals("")) { %>
	    		ipv4List['<%= s.getIpv4() %>'] = [row,col];
	    	<% } %>
	    	<% if (s.getIpv6() != null && !s.getIpv6().equals("")) { %>
	    		ipv6List['<%= s.getIpv6() %>'] = [row,col];
	    	<% } %>
	    <% } %>
	 	
		// INITIALIZATION of Google ChannelAPI
		<% String token = (String) request.getAttribute("token");%>
		channel = new goog.appengine.Channel('<%= token %>');
	    socket = channel.open();
	    socket.onopen = function() {
	    };
	    socket.onmessage = onMessage;
	    socket.onerror = function() {
	    	alert("Prišlo je do napake zajema goljufij v živo.");
	    };
	    socket.onclose = function() {};
	    
	    // Removing channel
	    $(window).on('unload', function() {
	    	$.ajax({
				type : "GET",
				url : "liveStream",
				data : {'action' : 'remove', 'channelKey' : '<%= request.getAttribute("channelKey") %>'},
				dataType: "json",
				async: false
			});
	    });
	</script>
</body>
</html>