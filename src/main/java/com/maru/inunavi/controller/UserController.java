package com.maru.inunavi.controller;

import com.maru.inunavi.entity.Constant;
import com.maru.inunavi.entity.Lecture;
import com.maru.inunavi.entity.user.ClassList;
import com.maru.inunavi.entity.user.User;
import com.maru.inunavi.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/form")
    public String home() {
        //System.out.println("sex");
        return "signupForm";
    }

    @RequestMapping("/loginform")
    public String loginForm() {

        return "loginForm";
    }

    @RequestMapping("/session")
    @ResponseBody
    public String session(HttpServletRequest request) {
        return (String)request.getSession().getAttribute("id");
    }

    @RequestMapping(value = "/insert", method = RequestMethod.POST) // ?id=&name=&password=&email=
    @ResponseBody
    public Map<String, String> Resister(HttpServletRequest request) {
        String id = request.getParameter("id");
        String password = request.getParameter("password");
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        return userService.resister(id, password, name, email);
    }

    @RequestMapping(value = "/insert/class", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> insertClass(HttpServletRequest request) { // ?id=&class_id=
        String id = request.getParameter("id");
        String classId = request.getParameter("class_id");
        return userService.addLecture(id, classId);
    }

    @RequestMapping(value = "/delete/class", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> deleteClass(HttpServletRequest request) { // ?id=&class_id=
        String id = request.getParameter("id");
        String classId = request.getParameter("class_id");
        return userService.deleteLecture(id, classId);
    }

    @RequestMapping("/select/class")
    @ResponseBody
    public Map<String, ArrayList<Lecture>> selectClass(HttpServletRequest request) {
        String id = request.getParameter("id");
        return userService.showMyLecture(id);
    }

    @RequestMapping(value = "/check/id", method = RequestMethod.GET) // ?id=
    @ResponseBody
    public Map<String, String> idCheck(HttpServletRequest request) {
        String id = request.getParameter("id");
        return userService.idCheck(id);
    }

    @RequestMapping(value = "/selectAll", method = RequestMethod.GET)
    @ResponseBody
    public ArrayList<User> showAll() {
        return userService.showAll();
    }


    @RequestMapping(value = "/login", method = RequestMethod.POST) // ?id=&password=
    @ResponseBody
    public Map<String, String> login(HttpServletRequest request) {
        String id = request.getParameter("id");
        String password = request.getParameter("password");
        return userService.login(id, password);
    }

}
