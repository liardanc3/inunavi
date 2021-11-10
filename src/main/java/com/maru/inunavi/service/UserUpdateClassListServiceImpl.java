package com.maru.inunavi.service;

import com.maru.inunavi.dao.UserDao;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class UserUpdateClassListServiceImpl implements Service{

    public Object execute(HttpServletRequest request) {
        UserDao uDao = new UserDao();
        Map<String, String> json = new HashMap<>();
        String id = request.getParameter("id");
        json.put("id", id);
        if(uDao.updateClassList(request.getParameter("id"), request.getParameter("class_list")))
            json.put("success", "true");
        return json;
    }
}
