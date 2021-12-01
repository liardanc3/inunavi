package com.maru.inunavi.controller;

import com.maru.inunavi.entity.Constant;
import com.maru.inunavi.entity.navi.Graph;
import com.maru.inunavi.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("/navi")
public class NaviController {

    @Autowired
    private NaviService naviService;

    @RequestMapping("/")
    public String home() {
        return "index";
    }

    @RequestMapping("/load")
    public @ResponseBody Map<String, Object> showLoad(HttpServletRequest request) { // ?start=&end=
        int start = Integer.parseInt(request.getParameter("start"));
        int end = Integer.parseInt(request.getParameter("end"));
        return naviService.showLoad(start, end);
    }

}
