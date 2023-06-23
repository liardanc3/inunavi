package com.maru.inunavi.recommend.controller;

import com.maru.inunavi.recommend.domain.entity.Recommend;
import com.maru.inunavi.recommend.service.RecommendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
public class RecommendController {

    private final RecommendService recommendService;

    @PostMapping("/getRecommendLecture")
    @ResponseBody
    public Map<String, List<Map<String, String>>> getRecommendLecture(@RequestParam("email") String email){
        log.info("getRecommendLecture("+email+")");
        return recommendService.getRecommendLecture(email);
    }

    @GetMapping("/admin/updateRecommend")
    @ResponseBody
    public List<Recommend> updateRecommend(){
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ip = req.getHeader("X-Forwarded-For");
        if(ip==null) ip=req.getRemoteAddr();
        log.info("updateRecommend("+ip+")");
        return recommendService.updateRecommend();
    }
}
