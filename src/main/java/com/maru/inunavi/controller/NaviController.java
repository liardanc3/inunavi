package com.maru.inunavi.controller;

import com.maru.inunavi.entity.Navi;
import com.maru.inunavi.entity.NodePath;
import com.maru.inunavi.entity.Place;
import com.maru.inunavi.service.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
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
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @GetMapping("/admin/updateNavi")
    @ResponseBody
    public List<Navi> updateNavi(){
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ip = req.getHeader("X-Forwarded-For");
        if(ip==null) ip=req.getRemoteAddr();
        logger.info("updateNavi("+ip+")");
        return _NaviService.updateNavi2();
    }

    @GetMapping("/admin/updatePlace")
    @ResponseBody
    public List<Place> updatePlace(){
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ip = req.getHeader("X-Forwarded-For");
        if(ip==null) ip=req.getRemoteAddr();
        logger.info("updatePlace("+ip+")");
        return _NaviService.updatePlace();
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
        logger.info("getRootLive("+ip+")");
        return _NaviService.getRootLive(startPlaceCode,endPlaceCode,startLocation,endLocation);
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
        logger.info("AstargetRootLive("+ip+")");
        return _NaviService.AstarGetRootLive(startPlaceCode,endPlaceCode,startLocation,endLocation);
    }

    @PostMapping("/getNextPlace")
    @ResponseBody
    public Map<String,String> getNextPlace(@RequestParam(value = "email") String email){
        logger.info("getNextPlace("+email+")");
        return _NaviService.getNextPlace(email);
    }

    @GetMapping("/placeSearchList")
    @ResponseBody
    public Map<String, List<Map<String,String>>> placeSearchList(@RequestParam(value = "searchKeyword") String searchKeyword,
                                                                 @RequestParam(value = "myLocation") String myLocation){
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ip = req.getHeader("X-Forwarded-For");
        if(ip==null) ip=req.getRemoteAddr();
        logger.info("placeSearchList("+ip+")");
        return _NaviService.placeSearchList(searchKeyword, myLocation);
    }

    @PostMapping("/getOverviewRoot")
    @ResponseBody
    public Map<String, List<Map<String, String>>> getOverviewRoot(@RequestParam(value = "email") String email){
        logger.info("getOverviewRoot("+email+")");
        return _NaviService.getOverviewRoot(email);
    }

    @PostMapping("/getAnalysisResult")
    @ResponseBody
    public Map<String, String> getAnalysisResult(@RequestParam("email") String email){
        logger.info("getAnalysisResult("+email+")");
        return _NaviService.getAnalysisResult(email);
    }
}
