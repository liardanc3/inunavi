package com.maru.inunavi.recommend.controller;

import com.maru.inunavi.aspect.annotation.Log;
import com.maru.inunavi.recommend.domain.dto.RecommendDto;
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
@RequiredArgsConstructor
public class RecommendController {

    private final RecommendService recommendService;

    @Log
    @PostMapping("/getRecommendLecture")
    public Map<String, List<RecommendDto>> getRecommendLecture(String email){
        return Map.of("response", recommendService.getRecommendLecture(email));
    }

    @Log
    @GetMapping("/admin/updateRecommend")
    public void updateRecommend(){
        recommendService.resetRecommends();
    }
}
