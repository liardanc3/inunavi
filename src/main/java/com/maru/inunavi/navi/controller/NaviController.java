package com.maru.inunavi.navi.controller;

import com.maru.inunavi.aspect.annotation.Log;
import com.maru.inunavi.aspect.annotation.ParamReplace;
import com.maru.inunavi.navi.domain.dto.*;
import com.maru.inunavi.navi.domain.entity.Path;
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
     * Update navi and place info
     * @return
     */
    @Log
    @GetMapping("/admin/newMap")
    public String newMap(){
        naviService.updateNavi();
        naviService.updatePlace();
        return "ok";
    }

    /**
     * Provide route info in real time
     */
    @Log
    @ParamReplace(before = "\"", after = "")
    @GetMapping("/getRootLive")
    public Map<String, List<PathDto>> getRouteLive(@ModelAttribute RouteInfo routeInfo){
        return Map.of("response", List.of(naviService.getRouteLive(routeInfo)));
    }

    /**
     * Provide the next class location based on the user's timetable
     */
    @Log
    @PostMapping("/getNextPlace")
    public NextPlaceDto getNextPlace(String email){
        return naviService.getNextPlace(email);
    }

    /**
     * Provide place based on filter
     */
    @Log
    @ParamReplace(before = "\"", after = "")
    @GetMapping("/placeSearchList")
    public Map<String, List<PlaceDto>> searchPlace(PlaceSearchFilter placeSearchFilter){
        return Map.of("response", naviService.searchPlace(placeSearchFilter));
    }

    @PostMapping("/getOverviewRoot")
    public Map<String, List<Map<String, String>>> getOverviewRoot(@RequestParam(value = "email") String email){
        return naviService.getOverviewRoot(email);
    }

    @PostMapping("/getAnalysisResult")
    public Map<String, String> getAnalysisResult(@RequestParam("email") String email){
        return naviService.getAnalysisResult(email);
    }
}
