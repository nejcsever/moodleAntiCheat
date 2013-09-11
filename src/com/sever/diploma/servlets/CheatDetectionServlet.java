package com.sever.diploma.servlets;

import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

public class CheatDetectionServlet extends HttpServlet {

	private static final long serialVersionUID = 7775737133121533837L;

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {

		String action = req.getParameter("action");
		if (action != null) {
			processCheat(req, resp);
		}
	}

	/**
	 * Gets parameters from client request and processes them into a valid
	 * entities in datastore.
	 */
	@SuppressWarnings("unchecked")
	private void processCheat(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// JSON Object with all data to be send to queue
		JSONObject jsonData = new JSONObject();
		
		/* Handling student */
		String studentName = req.getParameter("studentName");
		long moodleId = -1;
		try {
			moodleId = Long.parseLong(req.getParameter("studentMoodleId"));
		} catch (NumberFormatException e) {
			// still stored -1 in moogleId -> OK
		}
		jsonData.put("studentMoodleId", moodleId);
		jsonData.put("studentName", studentName);
		
		/* Handling quiz */
		String quizName = req.getParameter("quizName");
		String quizSubject = req.getParameter("quizSubject");
		String quizUrl = req.getParameter("quizUrl");
		jsonData.put("quizName", quizName);
		jsonData.put("quizSubject", quizSubject);
		jsonData.put("quizUrl", quizUrl);
		
		/* Handling Attempt */
		String browserName = getBrowserName(req);
		Date timestamp = new Date(Long.parseLong(req.getParameter("timestamp")));
		String stringTimeLeft = req.getParameter("timeLeft");
		// Handling endTime for Attempt
		boolean timeLimited = stringTimeLeft != null
				&& !stringTimeLeft.equals("");
		Date endTime = new Date(Long.MAX_VALUE);
		if (timeLimited) { // Time limited quiz
			long timeLeft = parseTimeString(stringTimeLeft);
			endTime = new Date(timestamp.getTime() + timeLeft);
		}
		jsonData.put("timestamp", timestamp.getTime());
		jsonData.put("endTime", endTime.getTime());
		jsonData.put("timeLimited", timeLimited);
		jsonData.put("browserName", browserName);
		
		/* Handling cheat */
		String ipv4 = null, ipv6 = null;
		String requestIp = getIpAddress(req);
		if (isIpv6(requestIp))
			ipv6 = requestIp;
		else
			ipv4 = requestIp;

		String cheatType = req.getParameter("cheatType");
		String additionalData = req.getParameter("additionalData") == null || req.getParameter("additionalData").equals("") ? null : req.getParameter("additionalData");
		jsonData.put("ipv4", ipv4);
		jsonData.put("ipv6", ipv6);
		jsonData.put("cheatType", cheatType);
		jsonData.put("additionalData", additionalData);
		
		/* Adding cheat to task queue which will add it to datastore - important for writing rate and conflicts */
		addCheatToQueue(jsonData);
		
		/* Notify all live listeners about cheat */
		RequestDispatcher rd = getServletContext().getRequestDispatcher(
				"/liveStream?action=cheat&cheatType=" + cheatType + "&ipv4="
						+ ipv4 + "&ipv6=" + ipv6 + "&studentName="
						+ studentName + "&moodleId=" + moodleId);
		rd.include(req, resp);
	}

	/**
	 * Adds cheat to Google app engine task queue which will add it in datastore at defined rate.
	 */
	private void addCheatToQueue(JSONObject data) {
		Queue queue = QueueFactory.getQueue("cheat-queue");
		TaskOptions task = TaskOptions.Builder.withUrl("/addCheat");
		task.param("data", data.toJSONString());
		queue.add(task);
	}

	/**
	 * Gets milliseconds from time in string.
	 * 
	 * @param time
	 *            - String in format HH:mm:ss
	 * @retur	n time in milliseconds
	 */
	private long parseTimeString(String time) {
		String[] elements = time.split(":");
		return ((24 * Integer.parseInt(elements[0]) + Integer
				.parseInt(elements[1])) * 60 + Integer.parseInt(elements[2])) * 1000;
	}

	public static boolean isIpv6(String ip) {
		InetAddress address;
		try {
			address = InetAddress.getByName(ip);
		} catch (UnknownHostException e) {
			return false;
		}
		return address instanceof Inet6Address;
	}

	// Source:
	// http://stackoverflow.com/questions/4678797/how-do-i-get-the-remote-address-of-a-client-in-servlet
	public static String getIpAddress(HttpServletRequest req) {
		String ip = req.getHeader("X-Forwarded-For");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = req.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = req.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = req.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = req.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = req.getRemoteAddr();
		}
		return ip;
	}

	public static String getBrowserName(HttpServletRequest req) {
		String userAgent = req.getHeader("user-agent");

		String browserName = "unknown";

		if (userAgent.contains("Seamonkey")) {
			browserName = "seamonkey";
		} else if (userAgent.contains("Chromium")) {
			browserName = "chromium";
		} else if (userAgent.contains("Opera")) {
			browserName = "opera";
		} else if (userAgent.contains("MSIE")) {
			browserName = "ie";
		} else if (userAgent.contains("Firefox")
				&& !userAgent.contains("Seamonkey")) {
			browserName = "firefox";
		} else if (userAgent.contains("Chrome")
				&& !userAgent.contains("Chromium")) {
			browserName = "chrome";
		} else if (userAgent.contains("Safari")
				&& (!userAgent.contains("Chrome") || !userAgent
						.contains("Chromium"))) {
			browserName = "safari";
		}

		return (browserName == null) ? "Unknown browser" : browserName;
	}
}
