package com.sever.diploma.helpers;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.sever.diploma.dao.UserDao;

public class AuthenticationHelper {
	public static boolean userLoggedIn() {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();

		return user != null && UserDao.INSTANCE.exists(user.getEmail());
	}
}
