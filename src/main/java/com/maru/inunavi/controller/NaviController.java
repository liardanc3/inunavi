package com.maru.inunavi.controller;

import com.maru.inunavi.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/navi")
public class NaviController {

    // ---- required args ---- //
    private final LectureService _LectureService;
    private final NaviService _NaviService;
    private final UserService _UserService;
    // ----------------------- //

}
