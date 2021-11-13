package com.maru.inunavi.service;

import com.maru.inunavi.dao.UserDao;

import javax.servlet.http.HttpServletRequest;

public class UserSelectClassServiceImpl implements Service {
    public Object execute(HttpServletRequest request) {
        UserDao uDao = new UserDao();
        String id = request.getParameter("id");
        return uDao.selectClass(id);
    }
}
