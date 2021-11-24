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

    //Æó±â
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

    @GetMapping("selectLecture")
    @ResponseBody
    public List<Lecture> selectLecture(@RequestParam(value = "main_keyword") String main_keyword,
                                       @RequestParam(value = "keyword_option") String keyword_option,
                                       @RequestParam(value = "major_option") String major_option,
                                       @RequestParam(value = "cse_option") String cse_option,
                                       @RequestParam(value = "sort_option") String sort_option,
                                       @RequestParam(value = "grade_option") String grade_option,
                                       @RequestParam(value = "category_option") String category_option,
                                       @RequestParam(value = "score_option") String score_option){
        return _LectureService.selectLecture(main_keyword,keyword_option,major_option,cse_option,sort_option,grade_option,category_option,score_option);
    }
}
