package com.maru.inunavi.lecture.controller;

import com.maru.inunavi.aspect.annotation.Log;
import com.maru.inunavi.aspect.annotation.SnakeToCamel;
import com.maru.inunavi.lecture.domain.dto.SearchFilter;
import com.maru.inunavi.lecture.domain.dto.TimeTableInfoDto;
import com.maru.inunavi.lecture.domain.entity.Lecture;
import com.maru.inunavi.lecture.service.LectureService;
import com.maru.inunavi.recommend.service.RecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class LectureController {

    private final LectureService lectureService;
    private final RecommendService recommendService;

    /**
     * [ADMIN] find all lecture
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
    public TimeTableInfoDto getTimeTableInfo(){
        return new TimeTableInfoDto(lectureService.getTimeTableInfo());
    }

    /**
     * search lectures with condition
     */
    @Log
    @SnakeToCamel
    @GetMapping("/selectLecture")
    public Map<String,List<Map<String, String>>> selectLecture(SearchFilter searchFilter){
        System.out.println("searchFilter = " + searchFilter);

        Map<String, List<Map<String, String>>> result = new HashMap<>();
        return result;
    }
}
