package com.maru.inunavi.service;

import com.maru.inunavi.dao.UserDao;
import com.maru.inunavi.entity.Lecture;
import com.maru.inunavi.entity.user.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserServiceImpl implements UserService {

    private UserDao uDao;

    public UserServiceImpl(){
        this.uDao = new UserDao();
    }

    public ArrayList<User> showAll(){
        return this.uDao.selectAll();
    }

    public Map<String, String> resister(String id, String password, String name, String email){
        Map<String, String> json = new HashMap<>();
        json.put("id", id);
        if(uDao.insert(id, password, name, email))
            json.put("success", "true");
        else json.put("success", "false");
        return json;
    }

    public Map<String, String> login(String id, String password) {
        Map<String, String> json = new HashMap<>();
        json.put("id", id);
        int res = uDao.login(id, password);
        if(res == 1){
            json.put("success", "true");
            json.put("message", "로그인 성공");
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

    public Map<String, String> idCheck(String id){
        Map<String, String> json = new HashMap<>();
        json.put("id", id);
        if(uDao.idCheck(id)) json.put("success", "true");
        else json.put("success", "false");
        return json;
    }

    public Map<String, String> addLecture(String id, String classId){
        Map<String, String> json = new HashMap<>();
        json.put("id", id);
        uDao.insertClass(id, classId);
        json.put("success", "true");
        return json;
    }

    public Map<String, String> deleteLecture(String id, String classId){
        Map<String, String> json = new HashMap<>();
        json.put("id", id);
        uDao.deleteClass(id, classId);
        json.put("success", "true");
        return json;
    }

    public Map<String, ArrayList<Lecture>> showMyLecture(String id) {
        Map<String, ArrayList<Lecture>> json = new HashMap<>();
        json.put("response", uDao.selectClass(id));
        return json;
    }
}
