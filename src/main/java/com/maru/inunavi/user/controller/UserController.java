package com.maru.inunavi.user.controller;

import com.maru.inunavi.user.domain.dto.SignUpDto;
import com.maru.inunavi.user.domain.entity.User;
import com.maru.inunavi.user.service.UserService;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/admin/memberList")
    public List<User> memberList(){
        return userService.memberList();
    }

    @GetMapping("/user/session")
    public String session(HttpServletRequest request) {
        return (String) request.getSession().getAttribute("id");
    }

    @PostMapping(value = "/user/insert")
    public SignUpDto signUp(String email, String password, String major) {
        return userService.signUp(email, password, major);
    }

    @RequestMapping(value = "/user/insert/class", method = RequestMethod.POST)
    public Map<String, String> insertClass(HttpServletRequest request) { // ?id=&class_id=
        String email = request.getParameter("email");
        String classId = request.getParameter("class_id");
        log.info("insertClass("+email+" / "+classId+")");
        return userService.AddLecture(email, classId);
    }

    @RequestMapping(value = "/user/delete/class", method = RequestMethod.POST)
    public Map<String, String> deleteClass(HttpServletRequest request) { // ?id=&class_id=
        String email = request.getParameter("email");
        String classId = request.getParameter("class_id");
        log.info("deleteClass("+email+" / "+classId+")");
        return userService.deleteLecture(email, classId);
    }

    @PostMapping("/user/select/class")
    public Map<String, List<Map<String, String>>> selectClass(HttpServletRequest request) {
        String email = request.getParameter("email");
        log.info("selectClass("+email+")");
        return userService.showMyLecture(email);
    }

    @GetMapping(value = "/user/check/id")
    public Map<String, String> idCheck(HttpServletRequest request) {
        String email = request.getParameter("email");
        log.info("idCheck("+email+")");
        return userService.idCheck(email);
    }

    @RequestMapping(value = "/user/login", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> login(HttpServletRequest request) {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        log.info("login("+email + ")");
        return userService.login(email, password);
    }

    @RequestMapping(value = "/user/verify", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> verify(HttpServletRequest request) {
        String email = request.getParameter("email");
        log.info("verify("+email+")");
        return userService.verify(email);
    }

    @RequestMapping(value = "/user/update", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> update(HttpServletRequest request) {
        String email = request.getParameter("email");
        String password = request.getParameter("newPassword");
        log.info("updatePassword("+email+")");
        return userService.updatePassword(email, password);
    }

    @RequestMapping(value = "/user/update2", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> update2(HttpServletRequest request) {
        String email = request.getParameter("email");
        String major = request.getParameter("newMajor");
        log.info("updateMajor("+email+" / "+major+")");
        return userService.updateMajor(email, major);
    }

    @RequestMapping(value = "/user/quit", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> delete(HttpServletRequest request) {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        log.info("delete("+email+")");
        return userService.delete(email, password);
    }
}
