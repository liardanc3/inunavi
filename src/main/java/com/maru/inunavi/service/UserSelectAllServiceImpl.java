package com.maru.inunavi.service;

import com.maru.inunavi.dao.UserDao;

import javax.servlet.http.HttpServletRequest;

public class UserSelectAllServiceImpl implements Service {
    public Object execute(HttpServletRequest request){
        UserDao uDao = new UserDao();
        return uDao.selectAll();
    }
}
