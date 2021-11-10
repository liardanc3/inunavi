package com.maru.inunavi.controller;

import com.maru.inunavi.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("/navi")
public class NaviController {

    private Service service;

    @RequestMapping("/")
    public String home() {
        //System.out.println("sex");
        return "index";
    }

    @RequestMapping("/load")
    public @ResponseBody Map<String, Object> load(HttpServletRequest request) {
        System.out.println("sex");
        service = new NaviServiceImpl();
        Map<String, Object> json = (Map<String, Object>) service.execute(request);
        //Map<String, ArrayList<Map<String, Integer>>> json = new HashMap<String, ArrayList<Map<String, Integer>>>();
        //json.put("load", res);
        return json;
    }

}
