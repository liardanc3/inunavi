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
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String major = request.getParameter("major");
        return _UserService.resister(email, password, major);
    }

    @RequestMapping(value = "/insert/class", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> insertClass(HttpServletRequest request) { // ?id=&class_id=
        String email = request.getParameter("email");
        String classId = request.getParameter("class_id");
        return _UserService.AddLecture(email, classId);
    }

    @RequestMapping(value = "/delete/class", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> deleteClass(HttpServletRequest request) { // ?id=&class_id=
        String email = request.getParameter("email");
        String classId = request.getParameter("class_id");
        return _UserService.deleteLecture(email, classId);
    }

    @RequestMapping(value = "/select/class", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, ArrayList<Lecture>> selectClass(HttpServletRequest request) {
        String email = request.getParameter("email");
        return _UserService.showMyLecture(email);
    }

    @RequestMapping(value = "/check/id", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, String> idCheck(HttpServletRequest request) {
        String email = request.getParameter("email");
        return _UserService.idCheck(email);
    }

    @RequestMapping(value = "/selectAll", method = RequestMethod.GET)
    @ResponseBody
    public List<UserInfo> showAll() {
        return _UserService.showAll();
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> login(HttpServletRequest request) {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        return _UserService.login(email, password);
    }

    @RequestMapping(value = "/verify", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> verify(HttpServletRequest request) {
        String email = request.getParameter("email");
        return _UserService.verify(email);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> update(HttpServletRequest request) {
        String email = request.getParameter("email");
        String password = request.getParameter("newPassword");
        return _UserService.updatePassword(email, password);
    }

    @RequestMapping(value = "/update2", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> update2(HttpServletRequest request) {
        String email = request.getParameter("email");
        String major = request.getParameter("newMajor");
        return _UserService.updateMajor(email, major);
    }

    @RequestMapping(value = "/quit", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> delete(HttpServletRequest request) {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        return _UserService.delete(email, password);
    }

}
