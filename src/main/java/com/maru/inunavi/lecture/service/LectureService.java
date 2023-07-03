package com.maru.inunavi.lecture.service;

import com.maru.inunavi.lecture.domain.dto.LectureSearchFilter;
import com.maru.inunavi.lecture.domain.dto.SelectLectureDto;
import com.maru.inunavi.lecture.domain.dto.TimeTableInfoDto;
import com.maru.inunavi.lecture.domain.entity.Lecture;
import com.maru.inunavi.lecture.repository.LectureRepository;
import com.maru.inunavi.user.domain.entity.User;
import com.maru.inunavi.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LectureService {

    private final ObjectProvider<LectureService> lectureServiceProvider;
    private final LectureRepository lectureRepository;
    private final UserRepository userRepository;

    private LectureService lectureService;

    @PostConstruct
    void init(){
        lectureService = lectureServiceProvider.getObject();
    }

    /**
     * Find all lecture
     * @return {@code List<Lecture>}
     */
    public List<Lecture> findLectures(){
        return lectureRepository.findAll();
    }

    /**
     * Update lecture table<b>
     * @return {@code List<Lecture>}
     */
    @Transactional
    @SneakyThrows
    public List<Lecture> updateLectures() {

        userRepository.findAll().forEach(User::removeLectures);

        lectureRepository.deleteAll();
        lectureRepository.deleteINCREMENT();

        InputStream inputStream = new ClassPathResource("lecture.txt").getInputStream();
        BufferedReader buffer = new BufferedReader(new InputStreamReader(inputStream));

        String line = "";
        while ((line = buffer.readLine()) != null) {

            StringTokenizer lineToken = new StringTokenizer(line);

            lineToken.nextToken("\t");
            lineToken.nextToken("\t");

            String department = lineToken.nextToken("\t");
            String grade = lineToken.nextToken("\t");
            String category = lineToken.nextToken("\t");
            String number = lineToken.nextToken("\t");
            String lectureName = lineToken.nextToken("\t");

            lineToken.nextToken("\t");

            String professor = lineToken.nextToken("\t");
            String classRoomRaw = lineToken.nextToken("\t");
            String classTimeRaw = lineToken.nextToken("\t");
            String point = lineToken.nextToken("\t");

            String[] parseResult = parseTime(classTimeRaw);
            String classTime = parseResult[0];
            String classRoom = parseResult[1];

            String placeCode = classRoom.equals("-") ? "-" :
                    Arrays.stream(classRoomRaw.split(","))
                            .sequential()
                            .reduce((classRoomToken, result) ->
                                    result + "," + classRoomToken.split("-")[1].split(" ")[0]
                            )
                            .get();


            lectureRepository.save(
                    Lecture.builder()
                            .department(department)
                            .grade(grade)
                            .category(category)
                            .number(number)
                            .lectureName(lectureName)
                            .professor(professor)
                            .classRoomRaw(classRoomRaw)
                            .classTimeRaw(classTimeRaw)
                            .classRoom(classRoom)
                            .classTime(classTime)
                            .how("-")
                            .point(point)
                            .placeCode(placeCode)
                            .build()
            );
        }

        return lectureRepository.findAll();
    }

    /**
     * Parse raw time text<b>
     * @param classTimeRaw
     * @return {@code String[]{classTime, classRoom}}
     */
    public String[] parseTime(String classTimeRaw){
        StringBuilder classTime = new StringBuilder();
        StringBuilder classRoom = new StringBuilder();

        if(classTimeRaw.charAt(0) == '-'){
            classTime = new StringBuilder("-");
            classRoom = new StringBuilder("-");
        }
        else {
            StringTokenizer classTimeRawToken = new StringTokenizer(classTimeRaw);

            int dayStartTime = 0;
            int dayEndTime = 0;

            while(classTimeRawToken.hasMoreTokens()) {

                classTimeRawToken.nextToken("[");
                if(!classTimeRawToken.hasMoreTokens())
                    break;

                String room = classTimeRawToken.nextToken(":");
                if(room.length()<3)
                    break;
                room = room.substring(1) + ",";

                if(!classTimeRawToken.hasMoreTokens())
                    break;

                String restToken = classTimeRawToken.nextToken("]");
                if(restToken.charAt(0)==':')
                    restToken = restToken.substring(1);
                
                int classStartTime = 0;
                int classEndTime = 0;
                int timeCnt = 0;

                StringTokenizer timeRangeToken = new StringTokenizer(restToken);
                while(timeRangeToken.hasMoreTokens()){
                    String timeRange = timeRangeToken.nextToken(",");

                    int idx = 0;
                    if(timeRange.charAt(idx)=='('){
                        classStartTime = dayStartTime;
                        classEndTime = dayEndTime;
                    }
                    else{
                        switch (timeRange.charAt(0)){
                            case '월' : classStartTime=16; classEndTime=16; break;
                            case '화' : classStartTime=64; classEndTime=64; break;
                            case '수' : classStartTime=112; classEndTime=112; break;
                            case '목' : classStartTime=160; classEndTime=160; break;
                            case '금' : classStartTime=208; classEndTime=208; break;
                            case '토' : classStartTime=256; classEndTime=256; break;
                        }
                        idx++;
                    }

                    if(timeRange.charAt(idx + 1) == '야'){
                        classStartTime += 18;

                        dayStartTime = classStartTime;

                        classStartTime += 2 * (timeRange.charAt(idx + 2) - '0');
                        classEndTime += 18;

                        dayEndTime = classEndTime;
                    }

                    else{
                        dayStartTime = classStartTime;

                        classStartTime += (2 * (timeRange.charAt(idx + 1) - '0'));
                        if(timeRange.charAt(idx+2) == 'B'){
                            classStartTime += 1;
                        }

                        dayEndTime = classEndTime;

                        if(timeRange.contains("야")){
                            classEndTime += 18;
                        }
                    }

                    int isHalfHour = timeRange.charAt(timeRange.length() - 2) == 'A' ? 1 : 0;
                    int extraTime = timeRange.charAt(timeRange.length() - 2 - isHalfHour) - '0';
                    classEndTime += 2 * extraTime + 1 - isHalfHour;

                    classTime.append(classStartTime).append('-').append(classEndTime).append(',');
                    timeCnt++;
                }
                
                while(timeCnt-- > 0){
                    classRoom.append(room);
                }
            }
        }

        return new String[]{classTime.toString(), classRoom.toString()};
    }

    /**
     * Provide semester info
     * @return {@code TimeTableInfo}
     */
    public TimeTableInfoDto getTimeTableInfo(){

        String majors = lectureRepository.findDistinctMajors("교양", "교직", "일선", "군사학")
                .parallelStream()
                .reduce((result, department) -> result + "," + department)
                .map(result -> result + ",전체")
                .orElseThrow(NoSuchElementException::new);

        String basicGenerals = lectureRepository.findDistinctBasicGenerals("교양필수", "전학년", "교양")
               .parallelStream()
               .reduce((result, department) -> result + "," + department)
               .map(result -> result + ",전체")
               .orElseThrow(NoSuchElementException::new);

        String categories = lectureRepository.findDistinctCategory()
               .parallelStream()
               .reduce((result, department) -> result + "," + department)
               .map(result -> result + ",전체")
               .orElseThrow(NoSuchElementException::new);

        LocalDate currentDate = LocalDate.now();

        String year = Integer.toString(currentDate.getYear());

        int month = currentDate.getMonthValue();
        String semester = month <= 2 ? "겨울" : month <= 6 ? "1" : month <= 8 ? "여름" : "2";

        return TimeTableInfoDto.builder()
                .majors(majors)
                .basicGenerals(basicGenerals)
                .categories(categories)
                .year(year)
                .semester(semester)
                .build();
    }

    /**
     * Provide filtered lecture list
     * @param lectureSearchFilter
     * @return {@code List<SelectLectureDto>}
     */
    public List<SelectLectureDto> selectLecture(LectureSearchFilter lectureSearchFilter) {
        return lectureRepository.findBySearchFilter(lectureSearchFilter);
    }
}