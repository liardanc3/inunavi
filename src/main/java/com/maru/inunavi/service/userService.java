package com.maru.inunavi.service;

import com.maru.inunavi.entity.user;
import com.maru.inunavi.object.userJoin;
import com.maru.inunavi.repository.userRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class userService {
    private final userRepository _userRepository;

    public userService(userRepository _userRepository){
        this._userRepository = _userRepository;
    }

    public userJoin join(user _user) {
        _userRepository.save(_user);
        userJoin _userJoin = new userJoin("True",_user.getUserID());
        return _userJoin;
    }

    public List<user> allUser(){
        List<user> _allUser = _userRepository.findAll();
        return _allUser;
    }


}
