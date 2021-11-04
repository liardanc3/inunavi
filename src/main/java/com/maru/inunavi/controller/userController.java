package com.maru.inunavi.controller;

import com.maru.inunavi.entity.user;
import com.maru.inunavi.service.userService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class userController {

    private final userService _userService;

    public userController(userService _userService){
        this._userService = _userService;
    }

    @GetMapping("/allUser")
    @ResponseBody
    public List<user> allUser() {
        return _userService.allUser();
    }

}
