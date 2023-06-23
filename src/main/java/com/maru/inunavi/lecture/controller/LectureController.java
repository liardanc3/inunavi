package com.maru.inunavi.lecture.controller;

import com.maru.inunavi.lecture.domain.entity.Lecture;
import com.maru.inunavi.lecture.service.LectureService;
import com.maru.inunavi.navi.service.NaviService;
import com.maru.inunavi.recommend.service.RecommendService;
import com.maru.inunavi.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class LectureController {

    private final LectureService lectureService;
    private final NaviService naviService;
    private final UserService userService;
    private final RecommendService recommendService;

    @GetMapping("/allLecture")
    @ResponseBody
    public List<Lecture> allLecture(){
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ip = req.getHeader("X-Forwarded-For");
        if(ip==null) ip=req.getRemoteAddr();
        log.info("allLecture("+ip+")");
        return lectureService.allLecture();
    }

    @GetMapping("/admin/newSemester")
    @ResponseBody
    public List<Lecture> newSemester(){
        lectureService.deleteAllUserLecture();
        ArrayList<Lecture> ret = (ArrayList<Lecture>) updateLecture();
        recommendService.updateRecommend();
        return ret;
    }

    @GetMapping("/admin/updateLecture")
    @ResponseBody
    public List<Lecture> updateLecture(){
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ip = req.getHeader("X-Forwarded-For");
        if(ip==null) ip=req.getRemoteAddr();
        log.info("updateLecture("+ip+")");
        return lectureService.updateLecture();
    }

    @GetMapping("/getTimeTableInfo")
    @ResponseBody
    public HashMap<String,List<LectureService.TimeTableInfo>> getTimeTableInfo(){
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ip = req.getHeader("X-Forwarded-For");
        log.info("getTimeTableInfo("+ip+")");
        return lectureService.getTimeTableInfo();
    }

    @GetMapping("/selectLecture")
    @ResponseBody
    public Map<String,List<Map<String, String>>> selectLecture(@RequestParam(value = "main_keyword") String main_keyword,
                                                       @RequestParam(value = "keyword_option") String keyword_option,
                                                       @RequestParam(value = "major_option") String major_option,
                                                       @RequestParam(value = "cse_option") String cse_option,
                                                       @RequestParam(value = "sort_option") String sort_option,
                                                       @RequestParam(value = "grade_option") String grade_option,
                                                       @RequestParam(value = "category_option") String category_option,
                                                       @RequestParam(value = "score_option") String score_option){


        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ip = req.getHeader("X-Forwarded-For");
        if(ip==null) ip=req.getRemoteAddr();
        log.info("selectLecture("+ip+")");


        List<Map<String,String>> LL = lectureService.selectLecture(main_keyword,keyword_option,major_option,cse_option,sort_option,grade_option,category_option,score_option);
        Map<String, List<Map<String, String>>> result = new HashMap<>();
        result.put("response",LL);
        return result;
    }
}
