package com.maru.inunavi.controller;

import com.maru.inunavi.entity.Lecture;
import com.maru.inunavi.service.LectureService;
import com.maru.inunavi.service.NaviService;
import com.maru.inunavi.service.RecommendService;
import com.maru.inunavi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class RecommendController {

    private final RecommendService _RecommendService;

    @GetMapping("getRecommendLecture")
    @ResponseBody
    public Map<String, List<Lecture>> getRecommendLecture(@RequestParam("email") String email){
        return _RecommendService.getRecommendLecture(email);
    }
}
