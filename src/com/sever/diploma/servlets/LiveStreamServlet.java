package com.sever.diploma.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.sever.diploma.dao.ClassroomDao;
import com.sever.diploma.helpers.AuthenticationHelper;

public class LiveStreamServlet extends HttpServlet {

	private static final long serialVersionUID = -1406689842382291934L;

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		// authenticate user
		if (!AuthenticationHelper.userLoggedIn()) {
			RequestDispatcher rd = getServletContext().getRequestDispatcher(
					"/login.jsp");
			rd.forward(req, resp);
			return;
		}
		if (req.getParameter("classroom") == null) {
			// redirect
			RequestDispatcher rd = getServletContext().getRequestDispatcher(
					"/liveFilter");
			rd.forward(req, resp);
			return;
		}
		if (req.getParameter("action") != null
				&& req.getParameter("action").equals("remove")) {
			removeStreamUser(req.getParameter("channelKey"));
		} else if (req.getParameter("action") != null
				&& req.getParameter("action").equals("start")) {
			// create new channel
			ChannelService channelService = ChannelServiceFactory
					.getChannelService();
			UserService userService = UserServiceFactory.getUserService();

			String channelKey = userService.getCurrentUser().getUserId()
					+ UUID.randomUUID().toString();
			String token = channelService.createChannel(channelKey);
			// token and channelKey are passed to client because we need them
			// when user unloads liveStream page
			req.setAttribute("channelKey", channelKey);
			req.setAttribute("token", token);
			// passing classroom to liveStream to filter IP addresses
			long classroomId = Long.parseLong(req.getParameter("classroom"));
			req.setAttribute("classroom",
					ClassroomDao.INSTANCE.getById(classroomId));
			// type of diplay
			String displayType = req.getParameter("display-type");
			String cheatCount = req.getParameter("cheatCount");
			int cheatLimit = 6;
			if (displayType.equals("statusbar")) {
				try {
					cheatLimit = Integer.parseInt(cheatCount);
				} catch (NumberFormatException e) {
				}
			}
			req.setAttribute("displayType", displayType);
			req.setAttribute("cheatLimit", cheatLimit);
			// other ip users
			req.setAttribute("otherUsers", req.getParameter("otherUsers") != null);

			saveStreamUser(channelKey);
			// redirect
			RequestDispatcher rd = getServletContext().getRequestDispatcher(
					"/liveStream.jsp");
			rd.forward(req, resp);
		}
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		// Recieve cheat
		String cheatType = req.getParameter("cheatType");
		String ipv4 = req.getParameter("ipv4");
		String ipv6 = req.getParameter("ipv6");
		String studentName = req.getParameter("studentName");
		String moodleId = req.getParameter("moodleId");
		sendMessageToAllUsers("{\"cheatType\":\"" + cheatType
				+ "\", \"ipv4\":\"" + ipv4 + "\", \"ipv6\":\"" + ipv6
				+ "\", \"studentName\":\"" + studentName
				+ "\", \"moodleId\":\"" + moodleId + "\"}");
	}

	/**
	 * Adds user to list of users that are listening for cheats.
	 */
	@SuppressWarnings("unchecked")
	private void saveStreamUser(String channelKey) {
		ServletContext sc = getServletContext();
		ArrayList<String> streamUsers = (ArrayList<String>) sc
				.getAttribute("streamUsers");
		if (streamUsers == null)
			streamUsers = new ArrayList<String>();
		streamUsers.add(channelKey);
		sc.setAttribute("streamUsers", streamUsers);
	}

	/**
	 * Deletes userId from list of users that are listening on cheats.
	 */
	@SuppressWarnings("unchecked")
	private void removeStreamUser(String channelKey) {
		ServletContext sc = getServletContext();
		ArrayList<String> streamUsers = (ArrayList<String>) sc
				.getAttribute("streamUsers");
		if (streamUsers == null)
			return;
		int index = streamUsers.indexOf(channelKey);
		if (index > -1)
			streamUsers.remove(index);
	}

	@SuppressWarnings("unchecked")
	private void sendMessageToAllUsers(String message) {
		ChannelService channelService = ChannelServiceFactory
				.getChannelService();
		ServletContext sc = getServletContext();
		if (sc == null)
			return;
		ArrayList<String> streamUsers = (ArrayList<String>) sc
				.getAttribute("streamUsers");
		if (streamUsers == null)
			return;
		for (String channelKey : streamUsers) {
			channelService.sendMessage(new ChannelMessage(channelKey, message));
		}
	}
}
