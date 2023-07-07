package com.maru.inunavi.navi.controller;

import com.maru.inunavi.aspect.annotation.Log;
import com.maru.inunavi.aspect.annotation.ReplaceParameter;
import com.maru.inunavi.navi.domain.dto.*;
import com.maru.inunavi.navi.service.NaviService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class NaviController {

    private final NaviService naviService;

    /**
     * API to update the navigation and place information.
     */
    @Log
    @GetMapping("/admin/newMap")
    public String newMap() {
        naviService.updateNavi();
        naviService.updatePlace();
        return "ok";
    }

    /**
     * API to get the live route information.
     */
    @Log
    @ReplaceParameter(before = "\"", after = "")
    @GetMapping("/getRootLive")
    public Map<String, List<PathDto>> getRouteLive(@ModelAttribute RouteInfo routeInfo) {
        return Map.of("response", List.of(naviService.getRouteLive(routeInfo)));
    }

    /**
     * API to get the next class location based on the user's timetable.
     */
    @Log
    @PostMapping("/getNextPlace")
    public NextPlaceDto getNextPlace(String email) {
        return naviService.getNextPlace(email);
    }

    /**
     * API to search places based on the filter.
     */
    @Log
    @ReplaceParameter(before = "\"", after = "")
    @GetMapping("/placeSearchList")
    public Map<String, List<PlaceDto>> searchPlace(PlaceSearchFilter placeSearchFilter) {
        return Map.of("response", naviService.searchPlace(placeSearchFilter));
    }

    /**
     * API to get the week overview.
     */
    @Log
    @PostMapping("/getOverviewRoot")
    public Map<String, List<RouteOverviewDto>> getOverview(String email) {
        return Map.of("response", naviService.getOverview(email));
    }

    /**
     * API to get the result of week analysis.
     */
    @Log
    @PostMapping("/getAnalysisResult")
    public AnalysisDto getAnalysisResult(String email) {
        return naviService.getAnalysisResult(email);
    }
}
