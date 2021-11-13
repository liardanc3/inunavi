package com.maru.inunavi.controller;

import com.maru.inunavi.entity.Lecture;
import com.maru.inunavi.entity.UserLecture;
import com.maru.inunavi.service.LectureService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LectureController {

    private final LectureService _LectureService;

    public LectureController(LectureService _LectureService){
        this._LectureService=_LectureService;
    }

    @GetMapping("allLecture")
    @ResponseBody
    public List<Lecture> allLecture(){
        return _LectureService.allLecture();
    }

    @GetMapping("updateLecture")
    @ResponseBody
    public List<Lecture> updateLecture(){
        return _LectureService.updateLecture();
    }

    @GetMapping("addLecture")
    @ResponseBody
    public boolean addLecture(@RequestParam(value = "userID") String userID,
                              @RequestParam(value = "lectureID") String lectureID){
        return _LectureService.addLecture(userID,lectureID);
    }

}
