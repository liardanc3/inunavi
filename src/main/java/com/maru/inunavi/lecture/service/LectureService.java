package com.maru.inunavi.lecture.service;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.maru.inunavi.aop.log.Log;
import com.maru.inunavi.lecture.domain.dto.TimeTableInfo;
import com.maru.inunavi.lecture.domain.dto.TimeTableInfoDto;
import com.maru.inunavi.lecture.domain.entity.Lecture;
import com.maru.inunavi.lecture.repository.LectureRepository;
import com.maru.inunavi.user.domain.entity.User;
import com.maru.inunavi.user.repository.UserLectureTableRepository;
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
    @Log
    @Transactional
    @SneakyThrows
    public void updateLectures() {

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
                            .build()
            );
        }
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
                            case '월' -> { classStartTime=16; classEndTime=16; }
                            case '화' -> { classStartTime=64; classEndTime=64; }
                            case '수' -> { classStartTime=112; classEndTime=112; }
                            case '목' -> { classStartTime=160; classEndTime=160; }
                            case '금' -> { classStartTime=208; classEndTime=208; }
                            case '토' -> { classStartTime=256; classEndTime=256; }
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
    public TimeTableInfo getTimeTableInfo(){

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

        return TimeTableInfo.builder()
                .majors(majors)
                .basicGenerals(basicGenerals)
                .categories(categories)
                .year(year)
                .semester(semester)
                .build();
    }

    public List<Map<String, String>> selectLecture(String main_keyword, String keyword_option, String major_option, String cse_option, String sort_option, String grade_option, String category_option, String score_option) {
        main_keyword = main_keyword.substring(1,main_keyword.length()-1);
        keyword_option = keyword_option.substring(1,keyword_option.length()-1);
        major_option = major_option.substring(1,major_option.length()-1);
        cse_option = cse_option.substring(1,cse_option.length()-1);
        sort_option = sort_option.substring(1,sort_option.length()-1);
        grade_option = grade_option.substring(1,grade_option.length()-1);
        category_option = category_option.substring(1,category_option.length()-1);
        score_option = score_option.substring(1,score_option.length()-1);

        // sort_option
        List<Lecture> tmp = new ArrayList<Lecture>();
        List<Map<String, String>> result = new ArrayList<>();
        if(sort_option.equals("과목코드"))
            tmp = lectureRepository.findAllByOrderByNumberAsc();
        else if(sort_option.equals("과목명"))
            tmp = lectureRepository.findAllByOrderByLectureNameAsc();
        else
            tmp = lectureRepository.findAll();

        for(int i=0; i<tmp.size(); i++){
            Lecture now = tmp.get(i);

            if(!main_keyword.equals("")){
                if(keyword_option.equals("전체")){
                    if(!now.getLectureName().toUpperCase().contains(main_keyword.toUpperCase())
                            && !now.getProfessor().contains(main_keyword) && !now.getNumber().contains(main_keyword)) continue;
                }
                if(keyword_option.equals("과목명") && !now.getLectureName().toUpperCase().contains(main_keyword.toUpperCase()))
                    continue;
                else if(keyword_option.equals("교수명") && !now.getProfessor().contains(main_keyword))
                    continue;
                else if(keyword_option.equals("과목코드") && !now.getNumber().contains(main_keyword))
                    continue;
            }

            // major_option
            if(!major_option.equals("전체") && !now.getDepartment().equals(major_option))
                continue;

            // cse_option
            if(!cse_option.equals("전체")) {
                if(!now.getCategory().equals("교양필수"))
                    continue;
                if (!cse_option.equals("기타") && !now.getLectureName().contains(cse_option))
                    continue;
                if (cse_option.equals("기타") && !major_option.equals("전체") && !now.getDepartment().equals(major_option))
                    continue;
            }

            // grade_option
            StringTokenizer gst = new StringTokenizer(grade_option);
            int grade_check[] = new int[]{1,0,0,0,0};
            while(!grade_option.equals("전체") && gst.hasMoreTokens()){
                String tok = gst.nextToken(", ");
                if(tok.charAt(0)=='1') grade_check[1]++;
                else if(tok.charAt(0)=='2') grade_check[2]++;
                else if(tok.charAt(0)=='3') grade_check[3]++;
                else if(tok.charAt(0)=='4') grade_check[4]++;
            }

            if(!grade_option.equals("전체") && (!now.getGrade().equals("전학년")  && grade_check[now.getGrade().charAt(0)-'0'] != 1))
                continue;

            // category_option
            StringTokenizer cst = new StringTokenizer(category_option);
            // 교선, 교필, 교직, 군사, 기초과학, 일선, 전기, 전선, 전필
            HashMap<String, Boolean> category_check = new HashMap<String, Boolean>();
            while(cst.hasMoreTokens()){
                String tok = cst.nextToken(", ");
                if(tok.equals("교양선택")) category_check.put("교양선택",true);
                else if(tok.equals("교양필수")) category_check.put("교양필수",true);
                else if(tok.equals("교직")) category_check.put("교직",true);
                else if(tok.equals("군사학")) category_check.put("군사학",true);
                else if(tok.equals("기초과학")) category_check.put("기초과학",true);
                else if(tok.equals("일반선택")) category_check.put("일반선택",true);
                else if(tok.equals("전공기초")) category_check.put("전공기초",true);
                else if(tok.equals("전공선택")) category_check.put("전공선택",true);
                else if(tok.equals("전공필수")) category_check.put("전공필수",true);
            }
            if(!category_option.equals("전체") && !category_check.containsKey(now.getCategory()))
                continue;

            // score_option
            StringTokenizer sst = new StringTokenizer(score_option);
            int score_check[] = new int[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
            while(!score_option.equals("전체") && sst.hasMoreTokens())
                score_check[Integer.parseInt(sst.nextToken(", ").substring(0,1))]++;
            if(!score_option.equals("전체") && score_check[Integer.parseInt(now.getPoint())] == 0)
                continue;

            Map<String, String> retMap = new HashMap<>();
            retMap.put("id",Long.toString(now.getId()));
            retMap.put("department",now.getDepartment());
            retMap.put("grade",now.getGrade());
            retMap.put("category",now.getCategory());
            retMap.put("number",now.getNumber());
            retMap.put("lecturename",now.getLectureName());
            retMap.put("professor",now.getProfessor());
            retMap.put("classRoomRaw",now.getClassRoomRaw());
            retMap.put("classTimeRaw",now.getClassTimeRaw());
            String _classRoom__ = now.getClassRoom();
            if(_classRoom__.length()>2)
                retMap.put("classroom",_classRoom__.substring(0,_classRoom__.length()-1));
            else retMap.put("classroom",_classRoom__);
            retMap.put("classTime",now.getClassTime());
            retMap.put("how",now.getHow());
            retMap.put("point",now.getPoint());

            String _classTime__ = now.getClassTime();
            String __RETclassTime__ = "";
            if(_classTime__.equals("-"))
                retMap.put("realTime","-");
            else{
                _classTime__ = _classTime__.replaceAll(",","-");
                StringTokenizer st = new StringTokenizer(_classTime__);
                while(st.hasMoreTokens()){
                    int start = Integer.parseInt(st.nextToken("-"));
                    int end = Integer.parseInt(st.nextToken("-"));

                    int dayOfWeek = start/48;
                    int startHour = (start%48)/2;
                    int startHalf = (start%2);
                    int endHour = (end%48)/2 + 1;
                    int endHalf = ~(end%2);

                    switch(dayOfWeek){
                        case 0: __RETclassTime__+="월 "; break;
                        case 1: __RETclassTime__+="화 "; break;
                        case 2: __RETclassTime__+="수 "; break;
                        case 3: __RETclassTime__+="목 "; break;
                        case 4: __RETclassTime__+="금 "; break;
                        case 5: __RETclassTime__+="토 "; break;
                        case 6: __RETclassTime__+="일 "; break;
                    }

                    __RETclassTime__ += Integer.toString(startHour);
                    __RETclassTime__ += ":";
                    __RETclassTime__ += startHalf==1 ? "30 - " : "00 - ";
                    __RETclassTime__ += Integer.toString(endHour);
                    __RETclassTime__ += ":";
                    __RETclassTime__ += endHalf==1 ? "30, " : "00, ";
                }
                __RETclassTime__ = __RETclassTime__.substring(0,__RETclassTime__.length()-2);
                retMap.put("realTime",__RETclassTime__);
            }
            result.add(retMap);
        }
        return result;
    }
}