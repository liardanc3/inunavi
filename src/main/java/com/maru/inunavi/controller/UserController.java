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
        return _UserService.resister(email, password);
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

    @RequestMapping("/select/class")
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
        String code = request.getParameter("verificationCode");
        return _UserService.verify(email, code);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> update(HttpServletRequest request) {
        String email = request.getParameter("email");
        String password = request.getParameter("newPassword");
        return _UserService.update(email, password);
    }

    @RequestMapping(value = "/quit", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> delete(HttpServletRequest request) {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        return _UserService.delete(email, password);
    }

}
