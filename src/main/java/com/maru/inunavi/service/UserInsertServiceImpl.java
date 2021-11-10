package com.maru.inunavi.service;

import com.maru.inunavi.dao.UserDao;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class UserInsertServiceImpl implements Service{

    public Object execute(HttpServletRequest request){
        UserDao uDao = new UserDao();
        Map<String, String> json = new HashMap<>();
        String id = request.getParameter("id");
        json.put("id", id);
        if(uDao.insert(id, request.getParameter("password"), request.getParameter("name"), request.getParameter("email")))
            json.put("success", "true");
        return json;
    }

}
