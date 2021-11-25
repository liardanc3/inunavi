package com.maru.inunavi.controller;

import com.maru.inunavi.entity.Constant;
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

    private Service service;

    @Autowired
    public void setTemplate(DataSource dataSource){
        Constant.template = new JdbcTemplate(dataSource);
    }

    @RequestMapping("/form")
    public String home() {
        //System.out.println("sex");
        return "signupForm";
    }

    @RequestMapping("/loginform")
    public String loginForm() {
        //System.out.println("sex");
        return "loginForm";
    }

    @RequestMapping("/session")
    @ResponseBody
    public String session(HttpServletRequest request) {
        return (String)request.getSession().getAttribute("id");
    }

    @RequestMapping(value = "/insert", method = RequestMethod.POST) // ?id=&name=&password=&email=
    @ResponseBody
    public Map<String, String> insert(HttpServletRequest request) {
        service = new UserInsertServiceImpl();
        return (Map<String, String>) service.execute(request);
    }

    @RequestMapping("/insert/class")
    @ResponseBody
    public Map<String, String> insertClass(HttpServletRequest request) { // ?id=&class_id=
        service = new UserInsertClassServiceImpl();
        return (Map<String, String>) service.execute(request);
    }

    @RequestMapping("/delete/class")
    @ResponseBody
    public Map<String, String> deleteClass(HttpServletRequest request) { // ?id=&class_id=
        service = new UserDeleteClassServiceImpl();
        return (Map<String, String>) service.execute(request);
    }

    @RequestMapping("/select/class")
    @ResponseBody
    public ArrayList<ClassList> selectClass(HttpServletRequest request) {
        service = new UserSelectClassServiceImpl();
        return (ArrayList<ClassList>) service.execute(request);
    }

    @RequestMapping(value = "/check/id", method = RequestMethod.GET) // ?id=
    @ResponseBody
    public Map<String, String> idCheck(HttpServletRequest request) {
        service = new UserIdCheckServiceImpl();
        return (Map<String, String>) service.execute(request);
    }

    @RequestMapping(value = "/selectAll", method = RequestMethod.GET)
    @ResponseBody
    public ArrayList<User> selectAll(HttpServletRequest request) {
        service = new UserSelectAllServiceImpl();
        return (ArrayList<User>) service.execute(request);
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST) // ?id=&password=
    @ResponseBody
    public Map<String, String> login(HttpServletRequest request) {
        service = new UserLoginServiceImpl();
        return (Map<String, String>) service.execute(request);
    }
}
