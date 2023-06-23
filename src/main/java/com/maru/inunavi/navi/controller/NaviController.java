package com.maru.inunavi.navi.controller;

import com.maru.inunavi.lecture.service.LectureService;
import com.maru.inunavi.navi.domain.entity.Navi;
import com.maru.inunavi.navi.domain.entity.NodePath;
import com.maru.inunavi.navi.domain.entity.Place;
import com.maru.inunavi.navi.service.NaviService;
import com.maru.inunavi.user.service.UserService;
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
@Slf4j
public class NaviController {

    private final LectureService lectureService;
    private final NaviService naviService;
    private final UserService userService;

    @GetMapping("/admin/updateNavi")
    @ResponseBody
    public List<Navi> updateNavi(){
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ip = req.getHeader("X-Forwarded-For");
        if(ip==null) ip=req.getRemoteAddr();
        log.info("updateNavi("+ip+")");
        return naviService.updateNavi2();
    }

    @GetMapping("/admin/updatePlace")
    @ResponseBody
    public List<Place> updatePlace(){
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ip = req.getHeader("X-Forwarded-For");
        if(ip==null) ip=req.getRemoteAddr();
        log.info("updatePlace("+ip+")");
        return naviService.updatePlace();
    }

    @GetMapping("/getRootLive")
    @ResponseBody
    public Map<String, List<NodePath>> getRootLive(@RequestParam(value = "startPlaceCode") String startPlaceCode,
                                              @RequestParam(value = "endPlaceCode") String endPlaceCode,
                                              @RequestParam(value = "startLocation") String startLocation,
                                              @RequestParam(value = "endLocation") String endLocation){
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ip = req.getHeader("X-Forwarded-For");
        if(ip==null) ip=req.getRemoteAddr();
        log.info("getRootLive("+ip+")");
        return naviService.getRootLive(startPlaceCode,endPlaceCode,startLocation,endLocation);
    }

    @GetMapping("/AstargetRootLive")
    @ResponseBody
    public Map<String, List<NodePath>> AstargetRootLive(@RequestParam(value = "startPlaceCode") String startPlaceCode,
                                                   @RequestParam(value = "endPlaceCode") String endPlaceCode,
                                                   @RequestParam(value = "startLocation") String startLocation,
                                                   @RequestParam(value = "endLocation") String endLocation){
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ip = req.getHeader("X-Forwarded-For");
        if(ip==null) ip=req.getRemoteAddr();
        log.info("AstargetRootLive("+ip+")");
        return naviService.AstarGetRootLive(startPlaceCode,endPlaceCode,startLocation,endLocation);
    }

    @PostMapping("/getNextPlace")
    @ResponseBody
    public Map<String,String> getNextPlace(@RequestParam(value = "email") String email){
        log.info("getNextPlace("+email+")");
        return naviService.getNextPlace(email);
    }

    @GetMapping("/placeSearchList")
    @ResponseBody
    public Map<String, List<Map<String,String>>> placeSearchList(@RequestParam(value = "searchKeyword") String searchKeyword,
                                                                 @RequestParam(value = "myLocation") String myLocation){
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ip = req.getHeader("X-Forwarded-For");
        if(ip==null) ip=req.getRemoteAddr();
        log.info("placeSearchList("+ip+")");
        return naviService.placeSearchList(searchKeyword, myLocation);
    }

    @PostMapping("/getOverviewRoot")
    @ResponseBody
    public Map<String, List<Map<String, String>>> getOverviewRoot(@RequestParam(value = "email") String email){
        log.info("getOverviewRoot("+email+")");
        return naviService.getOverviewRoot(email);
    }

    @PostMapping("/getAnalysisResult")
    @ResponseBody
    public Map<String, String> getAnalysisResult(@RequestParam("email") String email){
        log.info("getAnalysisResult("+email+")");
        return naviService.getAnalysisResult(email);
    }
}
