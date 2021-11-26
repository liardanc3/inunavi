package com.maru.inunavi.service;

import com.maru.inunavi.dao.UserDao;
import com.maru.inunavi.entity.Lecture;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserSelectClassServiceImpl implements Service {
    public Object execute(HttpServletRequest request) {
        UserDao uDao = new UserDao();
        String id = request.getParameter("id");
        Map<String, ArrayList<Lecture>> json = new HashMap<>();
        json.put("response", uDao.selectClass(id));
        return json;
    }
}
