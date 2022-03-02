package com.maru.inunavi.controller;

import com.maru.inunavi.entity.Lecture;
import com.maru.inunavi.entity.Recommend;
import com.maru.inunavi.service.RecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class RecommendController {

    private final RecommendService _RecommendService;

    @PostMapping("getRecommendLecture")
    @ResponseBody
    public Map<String, List<Lecture>> getRecommendLecture(@RequestParam("email") String email){
        return _RecommendService.getRecommendLecture(email);
    }

    @GetMapping("updateRecommend")
    @ResponseBody
    public List<Recommend> updateRecommend(){
        return _RecommendService.updateRecommend();
    }
}
