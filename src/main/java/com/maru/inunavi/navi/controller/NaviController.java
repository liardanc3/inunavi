package com.maru.inunavi.navi.controller;

import com.maru.inunavi.aspect.annotation.Log;
import com.maru.inunavi.aspect.annotation.ParamReplace;
import com.maru.inunavi.navi.domain.dto.PathDto;
import com.maru.inunavi.navi.domain.dto.RouteInfo;
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

    @Log
    @GetMapping("/admin/newMap")
    public String newMap(){
        naviService.updateNavi();
        naviService.updatePlace();
        return "ok";
    }

    @Log
    @ParamReplace(before = "\"", after = "")
    @GetMapping("/getRootLive")
    public Map<String, List<PathDto>> getRouteLive(@ModelAttribute RouteInfo routeInfo){
        return Map.of("response", List.of(naviService.getRouteLive(routeInfo)));
    }

    @PostMapping("/getNextPlace")
    public Map<String,String> getNextPlace(@RequestParam(value = "email") String email){
        return naviService.getNextPlace(email);
    }

    @GetMapping("/placeSearchList")
    public Map<String, List<Map<String,String>>> placeSearchList(@RequestParam(value = "searchKeyword") String searchKeyword,
                                                                 @RequestParam(value = "myLocation") String myLocation){

        return naviService.placeSearchList(searchKeyword, myLocation);
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
