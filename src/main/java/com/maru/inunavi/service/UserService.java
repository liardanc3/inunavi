package com.maru.inunavi.service;

import com.maru.inunavi.entity.Lecture;
import com.maru.inunavi.entity.user.User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Map;

@Service
public interface UserService {

    public ArrayList<User> showAll();

    public Map<String, String> resister(String id, String password, String name, String email);

    public Map<String, String> login(String id, String password);

    public Map<String, String> idCheck(String id);

    public Map<String, String> addLecture(String id, String classId);

    public Map<String, String> deleteLecture(String id, String classId);

    public Map<String, ArrayList<Lecture>> showMyLecture(String id);
}
