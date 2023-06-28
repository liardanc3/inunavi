package com.maru.inunavi.navi.controller;

import com.maru.inunavi.aspect.annotation.Log;
import com.maru.inunavi.navi.domain.entity.Navi;
import com.maru.inunavi.navi.domain.entity.NodePath;
import com.maru.inunavi.navi.domain.entity.Place;
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
    @GetMapping("/getRootLive")
    public Map<String, List<NodePath>> getRootLive(@RequestParam(value = "startPlaceCode") String startPlaceCode,
                                              @RequestParam(value = "endPlaceCode") String endPlaceCode,
                                              @RequestParam(value = "startLocation") String startLocation,
                                              @RequestParam(value = "endLocation") String endLocation){
        return naviService.getRootLive(startPlaceCode,endPlaceCode,startLocation,endLocation);
    }

    @GetMapping("/AstargetRootLive")
    public Map<String, List<NodePath>> AstargetRootLive(@RequestParam(value = "startPlaceCode") String startPlaceCode,
                                                   @RequestParam(value = "endPlaceCode") String endPlaceCode,
                                                   @RequestParam(value = "startLocation") String startLocation,
                                                   @RequestParam(value = "endLocation") String endLocation){

        return naviService.AstarGetRootLive(startPlaceCode,endPlaceCode,startLocation,endLocation);
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
