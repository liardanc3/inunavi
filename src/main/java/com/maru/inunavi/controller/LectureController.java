package com.maru.inunavi.controller;

import com.maru.inunavi.entity.Lecture;
import com.maru.inunavi.service.LectureService;
import com.maru.inunavi.service.NaviService;
import com.maru.inunavi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.aspectj.bridge.MessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class LectureController {

    private final LectureService _LectureService;
    private final NaviService _NaviService;
    private final UserService _UserService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @GetMapping("allLecture")
    @ResponseBody
    public List<Lecture> allLecture(){
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ip = req.getHeader("X-Forwarded-For");
        if(ip==null) ip=req.getRemoteAddr();
        logger.info("allLecture("+ip+")");
        return _LectureService.allLecture();
    }

    @GetMapping("master/updateLecture")
    @ResponseBody
    public List<Lecture> updateLecture(){
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ip = req.getHeader("X-Forwarded-For");
        if(ip==null) ip=req.getRemoteAddr();
        logger.info("updateLecture("+ip+")");
        return _LectureService.updateLecture();
    }

    @GetMapping("getTimeTableInfo")
    @ResponseBody
    public HashMap<String,List<LectureService.TimeTableInfo>> getTimeTableInfo(){
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ip = req.getHeader("X-Forwarded-For");
        logger.info("getTimeTableInfo("+ip+")");
        return _LectureService.getTimeTableInfo();
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
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ip = req.getHeader("X-Forwarded-For");
        if(ip==null) ip=req.getRemoteAddr();
        logger.info("selectLecture("+ip+")");
        List<Lecture> LL = _LectureService.selectLecture(main_keyword,keyword_option,major_option,cse_option,sort_option,grade_option,category_option,score_option);
        HashMap<String,List<Lecture>> result = new HashMap<>();
        result.put("response",LL);
        return result;
    }
}
