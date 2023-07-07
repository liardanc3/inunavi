package com.maru.inunavi.recommend.controller;

import com.maru.inunavi.aspect.annotation.Log;
import com.maru.inunavi.lecture.domain.dto.FormattedTimeDto;
import com.maru.inunavi.recommend.service.RecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class RecommendController {

    private final RecommendService recommendService;

    /**
     * API to get recommended lectures for a user.
     */
    @Log
    @PostMapping("/getRecommendLecture")
    public Map<String, List<FormattedTimeDto>> getRecommendLecture(String email) {
        return Map.of("response", recommendService.getRecommendLecture(email));
    }

    /**
     * API to update the recommendations.
     */
    @Log
    @GetMapping("/admin/updateRecommend")
    public void updateRecommend() {
        recommendService.resetRecommends();
    }
}
