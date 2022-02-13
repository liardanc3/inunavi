package com.maru.inunavi.controller;

import com.maru.inunavi.entity.Navi;
import com.maru.inunavi.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
}
