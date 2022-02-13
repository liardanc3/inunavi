package com.maru.inunavi.controller;

import com.maru.inunavi.entity.Lecture;
import com.maru.inunavi.service.LectureService;
import com.maru.inunavi.service.NaviService;
import com.maru.inunavi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class LectureController {

    private final LectureService _LectureService;
    private final NaviService _NaviService;
    private final UserService _UserService;

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

    @GetMapping("selectLecture")
    @ResponseBody
    public HashMap<String,List<Lecture>> selectLecture(@RequestParam(value = "main_keyword") String main_keyword,
                                                       @RequestParam(value = "keyword_option") String keyword_option,
                                                       @RequestParam(value = "major_option") String major_option,
                                                       @RequestParam(value = "cse_option") String cse_option,
                                                       @RequestParam(value = "sort_option") String sort_option,
                                                       @RequestParam(value = "grade_option") String grade_option,
                                                       @RequestParam(value = "category_option") String category_option,
                                                       @RequestParam(value = "score_option") String score_option){
        List<Lecture> LL = _LectureService.selectLecture(main_keyword,keyword_option,major_option,cse_option,sort_option,grade_option,category_option,score_option);
        HashMap<String,List<Lecture>> result = new HashMap<>();
        result.put("response",LL);
        return result;
    }
}
