package inunavi.controller;

import inunavi.entity.user;
import inunavi.service.userService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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
