package com.maru.inunavi.controller;

import com.maru.inunavi.entity.Lecture;
import com.maru.inunavi.entity.UserInfo;
import com.maru.inunavi.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final LectureService _LectureService;
    private final NaviService _NaviService;
    private final UserService _UserService;

    @RequestMapping("/session")
    @ResponseBody
    public String session(HttpServletRequest request) {
        return (String)request.getSession().getAttribute("id");
    }

    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> Resister(HttpServletRequest request) {
        String id = request.getParameter("id");
        String password = request.getParameter("password");
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        return _UserService.resister(id, password, name, email);
    }

    @RequestMapping(value = "/insert/class", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> insertClass(HttpServletRequest request) { // ?id=&class_id=
        String id = request.getParameter("id");
        String classId = request.getParameter("class_id");
        return _UserService.AddLecture(id, classId);
    }

    @RequestMapping(value = "/delete/class", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> deleteClass(HttpServletRequest request) { // ?id=&class_id=
        String id = request.getParameter("id");
        String classId = request.getParameter("class_id");
        return _UserService.deleteLecture(id, classId);
    }

    @RequestMapping("/select/class")
    @ResponseBody
    public Map<String, ArrayList<Lecture>> selectClass(HttpServletRequest request) {
        String id = request.getParameter("id");
        return _UserService.showMyLecture(id);
    }

    @RequestMapping(value = "/check/id", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, String> idCheck(HttpServletRequest request) {
        String id = request.getParameter("id");
        return _UserService.idCheck(id);
    }

    @RequestMapping(value = "/selectAll", method = RequestMethod.GET)
    @ResponseBody
    public List<UserInfo> showAll() {
        return _UserService.showAll();
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> login(HttpServletRequest request) {
        String id = request.getParameter("id");
        String password = request.getParameter("password");
        return _UserService.login(id, password);
    }

}
