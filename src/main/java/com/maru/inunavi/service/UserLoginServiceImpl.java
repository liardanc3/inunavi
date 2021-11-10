package com.maru.inunavi.service;

import com.maru.inunavi.dao.UserDao;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

public class UserLoginServiceImpl implements Service{
    @Override
    public Object execute(HttpServletRequest request) {
        UserDao uDao = new UserDao();
        Map<String, String> json = new HashMap<>();
        String id = request.getParameter("id");
        json.put("id", id);
        HttpSession session = request.getSession();
        int res = uDao.login(id, request.getParameter("password"));
        if(res == 1){
            json.put("success", "true");
            json.put("message", "로그인 성공");
            session.setAttribute("id", id);
        }else{
            json.put("success", "false");
            if(res == 0){
                json.put("message", "비밀번호 불일치");
            }else{
                json.put("message", "아이디가 없음");
            }
        }
        return json;
    }
}
