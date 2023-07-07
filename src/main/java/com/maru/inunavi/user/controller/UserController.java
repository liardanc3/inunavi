package com.maru.inunavi.user.controller;

import com.maru.inunavi.aspect.annotation.Log;
import com.maru.inunavi.lecture.domain.dto.FormattedTimeDto;
import com.maru.inunavi.user.domain.dto.LoginResultDto;
import com.maru.inunavi.user.domain.dto.UpdateDto;
import com.maru.inunavi.user.domain.dto.VerifyDto;
import com.maru.inunavi.user.domain.entity.User;
import com.maru.inunavi.user.service.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * [ADMIN] API to retrieve a list of all members. It is used to fetch the list of
     * @return
     */
    @Log
    @GetMapping("/admin/memberList")
    public List<User> memberList(){
        return userService.memberList();
    }

    /**
     * API to register a new user. It handles user registration by taking email, password, and major as input.
     */
    @Log
    @PostMapping("/user/insert")
    public UpdateDto signUp(String email, String password, String major) {
        return userService.signUp(email, password, major);
    }

    /**
     * API to retrieve the user's class information.
     * <p>It fetches the list of user's classes based on the provided email.
     */
    @Log
    @PostMapping("/user/select/class")
    public Map<String, List<FormattedTimeDto>> selectClass(String email) {
        return Map.of("response", userService.userLectureList(email));
    }

    /**
     * API to check id is available.
     */
    @Log
    @GetMapping("/user/check/id")
    public UpdateDto idCheck(String email) {
        return userService.idCheck(email);
    }

    /**
     * API to login
     */
    @Log
    @PostMapping("/user/login")
    public LoginResultDto login(String email, String password) {
        return userService.login(email, password);
    }

    /**
     * API to send mail to update password when forgot password
     */
    @Log
    @PostMapping("/user/verify")
    public VerifyDto verify(String email) {
        return userService.verify(email);
    }

    @Log
    @PostMapping("/user/update")
    public UpdateDto update(String email, String newPassword) {
        return userService.updatePassword(email, newPassword);
    }


    @PostMapping("/user/insert/class")
    public Map<String, String> insertClass(HttpServletRequest request) { // ?id=&class_id=
        return userService.AddLecture(email, classId);
    }

    @RequestMapping(value = "/user/delete/class", method = RequestMethod.POST)
    public Map<String, String> deleteClass(HttpServletRequest request) { // ?id=&class_id=
        String email = request.getParameter("email");
        String classId = request.getParameter("class_id");
        log.info("deleteClass("+email+" / "+classId+")");
        return userService.deleteLecture(email, classId);
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

    @GetMapping("/user/session")
    public String session(HttpServletRequest request) {
        return (String) request.getSession().getAttribute("id");
    }

}
