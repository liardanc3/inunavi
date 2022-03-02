package com.maru.inunavi.controller;

import com.maru.inunavi.entity.Lecture;
import com.maru.inunavi.entity.Recommend;
import com.maru.inunavi.service.RecommendService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class RecommendController {

    private final RecommendService _RecommendService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @PostMapping("getRecommendLecture")
    @ResponseBody
    public Map<String, List<Lecture>> getRecommendLecture(@RequestParam("email") String email){
        logger.info("getRecommendLecture("+email+")");
        return _RecommendService.getRecommendLecture(email);
    }

    @GetMapping("master/updateRecommend")
    @ResponseBody
    public List<Recommend> updateRecommend(){
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ip = req.getHeader("X-Forwarded-For");
        if(ip==null) ip=req.getRemoteAddr();
        logger.info("updateRecommend("+ip+")");
        return _RecommendService.updateRecommend();
    }
}
