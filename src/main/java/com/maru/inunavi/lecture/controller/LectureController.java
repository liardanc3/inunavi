package com.maru.inunavi.lecture.controller;

import com.maru.inunavi.aspect.annotation.Log;
import com.maru.inunavi.aspect.annotation.ReplaceParameter;
import com.maru.inunavi.aspect.annotation.SnakeToCamel;
import com.maru.inunavi.lecture.domain.dto.LectureSearchFilter;
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
     * [ADMIN] API to find all Lecture
     * @return A List of Lecture
     */
    @Log
    @GetMapping("/lecture")
    public List<Lecture> findLectures(){
        return lectureService.findLectures();
    }

    /**
     * [ADMIN] API to update lecture table and reset recommend table
     */
    @Log
    @GetMapping("/admin/newSemester")
    public List<Lecture> newSemester(){
        lectureService.updateLectures();
        recommendService.resetRecommends();
        return lectureService.findLectures();
    }

    /**
     * API to Provide semester info
     */
    @Log
    @GetMapping("/getTimeTableInfo")
    public Map<String, TimeTableInfoDto> getTimeTableInfo(){
        return Map.of("response", lectureService.getTimeTableInfo());
    }

    /**
     * API to search lectures with condition
     */
    @Log
    @SnakeToCamel
    @ReplaceParameter(before = "\"", after = "")
    @GetMapping("/selectLecture")
    public Map<String, List<SelectLectureDto>> selectLecture(@ModelAttribute LectureSearchFilter lectureSearchFilter){
        return Map.of("response", lectureService.selectLecture(lectureSearchFilter));
    }
}
