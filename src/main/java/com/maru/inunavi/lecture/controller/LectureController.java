package com.maru.inunavi.lecture.controller;

import com.maru.inunavi.aop.log.Log;
import com.maru.inunavi.lecture.domain.entity.Lecture;
import com.maru.inunavi.lecture.service.LectureService;
import com.maru.inunavi.recommend.service.RecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class LectureController {

    private final LectureService lectureService;
    private final RecommendService recommendService;

    @Log
    @GetMapping("/lecture")
    public List<Lecture> findLectures(){
        return lectureService.findLectures();
    }

    @Log
    @GetMapping("/admin/newSemester")
    public List<Lecture> newSemester(){
        lectureService.deleteAllUserLecture();
        List<Lecture> ret = lectureService.updateLecture();
        recommendService.updateRecommend();
        return ret;
    }

    @Log
    @GetMapping("/getTimeTableInfo")
    public HashMap<String,List<LectureService.TimeTableInfo>> getTimeTableInfo(){
        return lectureService.getTimeTableInfo();
    }

    @Log
    @GetMapping("/selectLecture")
    public Map<String,List<Map<String, String>>> selectLecture(@RequestParam(value = "main_keyword") String main_keyword,
                                                       @RequestParam(value = "keyword_option") String keyword_option,
                                                       @RequestParam(value = "major_option") String major_option,
                                                       @RequestParam(value = "cse_option") String cse_option,
                                                       @RequestParam(value = "sort_option") String sort_option,
                                                       @RequestParam(value = "grade_option") String grade_option,
                                                       @RequestParam(value = "category_option") String category_option,
                                                       @RequestParam(value = "score_option") String score_option){

        List<Map<String,String>> LL = lectureService.selectLecture(main_keyword,keyword_option,major_option,cse_option,sort_option,grade_option,category_option,score_option);
        Map<String, List<Map<String, String>>> result = new HashMap<>();
        result.put("response",LL);
        return result;
    }
}
