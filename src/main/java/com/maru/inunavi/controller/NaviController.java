package com.maru.inunavi.controller;

import com.maru.inunavi.entity.Navi;
import com.maru.inunavi.entity.NodePath;
import com.maru.inunavi.entity.Place;
import com.maru.inunavi.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.Node;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class NaviController {

    private final LectureService _LectureService;
    private final NaviService _NaviService;
    private final UserService _UserService;

    @GetMapping("updateNavi")
    @ResponseBody
    public List<Navi> updateNavi(){ return _NaviService.updateNavi(); }

    @GetMapping("updatePlace")
    @ResponseBody
    public List<Place> updatePlace(){ return _NaviService.updatePlace(); }

    @GetMapping("getRootLive")
    @ResponseBody
    public Map<String, List<NodePath>> getRootLive(@RequestParam(value = "startPlaceCode") String startPlaceCode,
                                              @RequestParam(value = "endPlaceCode") String endPlaceCode,
                                              @RequestParam(value = "startLocation") String startLocation,
                                              @RequestParam(value = "endLocation") String endLocation){
        return _NaviService.getRootLive(startPlaceCode,endPlaceCode,startLocation,endLocation);
    }

    @PostMapping("getNextPlace")
    @ResponseBody
    public Map<String,String> getNextPlace(@RequestParam(value = "email") String email){
        return _NaviService.getNextPlace(email);
    }

    @GetMapping("placeSearchList")
    @ResponseBody
    public Map<String, List<Map<String,String>>> placeSearchList(@RequestParam(value = "searchKeyword") String searchKeyword,
                                                                 @RequestParam(value = "myLocation") String myLocation){
        return _NaviService.placeSearchList(searchKeyword, myLocation);
    }

}
