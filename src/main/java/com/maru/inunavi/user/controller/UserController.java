package com.maru.inunavi.user.controller;

import com.maru.inunavi.aspect.annotation.Log;
import com.maru.inunavi.aspect.annotation.SnakeToCamel;
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
     * API to register a new user. It handles user registration by taking email, password, and major as input.
     */
    @Log
    @PostMapping("/user/insert")
    public UpdateDto signUp(String email, String password, String major) {
        return userService.signUp(email, password, major);
    }

    /**
     * API to retrieve the user's class information.
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

    /**
     * API to update password
     */
    @Log
    @PostMapping("/user/update")
    public UpdateDto updatePassword(String email, String newPassword) {
        return userService.updatePassword(email, newPassword);
    }

    /**
     * API to update major
     */
    @Log
    @PostMapping("/user/update2")
    public UpdateDto updateMajor(String email, String newMajor) {
        return userService.updateMajor(email, newMajor);
    }

    /**
     * API to delete user
     */
    @Log
    @PostMapping("/user/quit")
    public UpdateDto deleteUser(String email, String password) {
        return userService.deleteUser(email, password);
    }

    /**
     * API to insert class
     */
    @Log
    @SnakeToCamel
    @PostMapping("/user/insert/class")
    public UpdateDto insertClass(String email, String classId) {
        return userService.insertLecture(email, classId);
    }

    /**
     * API to delete class
     */
    @Log
    @SnakeToCamel
    @PostMapping("/user/delete/class")
    public UpdateDto deleteClass(String email, String classId) {
        return userService.deleteLecture(email, classId);
    }

}
