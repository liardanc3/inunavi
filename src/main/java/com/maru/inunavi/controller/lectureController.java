package com.maru.inunavi.controller;

import com.maru.inunavi.entity.lecture;
import com.maru.inunavi.service.lectureService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class lectureController{
    //sex
    private final lectureService _lectureService;

    public lectureController(lectureService _lectureService){
        this._lectureService=_lectureService;
    }

    @GetMapping("allLecture")
    @ResponseBody
    public List<lecture> allLecture(){
        System.out.println("sex");
        return _lectureService.allLecture();
    }

    @GetMapping("updateLecture")
    @ResponseBody
    public List<lecture> updateLecture(){
        return _lectureService.updateLecture();
    }
}
