package com.maru.inunavi.lecture.controller;

import com.maru.inunavi.aspect.annotation.Log;
import com.maru.inunavi.aspect.annotation.ParamReplace;
import com.maru.inunavi.aspect.annotation.SnakeToCamel;
import com.maru.inunavi.lecture.domain.dto.SearchFilter;
import com.maru.inunavi.lecture.domain.dto.SelectLectureDto;
import com.maru.inunavi.lecture.domain.dto.TimeTableInfoDto;
import com.maru.inunavi.lecture.domain.entity.Lecture;
import com.maru.inunavi.lecture.service.LectureService;
import com.maru.inunavi.recommend.service.RecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class LectureController {

    private final LectureService lectureService;
    private final RecommendService recommendService;

    /**
     * [ADMIN] Find all lecture
     * @return {@code List<Lecture>}
     */
    @Log
    @GetMapping("/lecture")
    public List<Lecture> findLectures(){
        return lectureService.findLectures();
    }

    /**
     * [ADMIN] Update lecture table and reset recommend table<b>
     */
    @Log
    @GetMapping("/admin/newSemester")
    public String newSemester(){
        lectureService.updateLectures();
        recommendService.resetRecommends();
        return "success";
    }

    /**
     * Provide semester info
     */
    @Log
    @GetMapping("/getTimeTableInfo")
    public Map<String, TimeTableInfoDto> getTimeTableInfo(){
        return Map.of("response", lectureService.getTimeTableInfo());
    }

    /**
     * Search lectures with condition
     */
    @Log
    @SnakeToCamel
    @ParamReplace(before = "\"", after = "")
    @GetMapping("/selectLecture")
    public Map<String, List<SelectLectureDto>> selectLecture(@ModelAttribute SearchFilter searchFilter){
        return Map.of("response", lectureService.selectLecture(searchFilter));
    }
}
