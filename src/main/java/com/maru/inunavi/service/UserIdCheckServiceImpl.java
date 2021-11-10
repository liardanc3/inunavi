package com.maru.inunavi.service;

import com.maru.inunavi.dao.UserDao;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class UserIdCheckServiceImpl implements Service{

    public Object execute(HttpServletRequest request){
        UserDao uDao = new UserDao();
        Map<String, String> json = new HashMap<>();
        String id = request.getParameter("id");
        json.put("id", id);
        if(uDao.idCheck(id)) json.put("success", "true");
        else json.put("success", "false");
        return json;
    }

}
