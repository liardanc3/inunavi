package com.maru.inunavi.service;

import com.maru.inunavi.entity.Lecture;
import com.maru.inunavi.entity.UserLecture;
import com.maru.inunavi.repository.AllLectureRepository;
import com.maru.inunavi.repository.UserLectureRepository;
import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.AutoPopulatingList;

import java.io.*;
import java.util.*;

import static org.apache.commons.io.FileUtils.copyInputStreamToFile;

@Service
public class LectureService {
    private final AllLectureRepository _AllLectureRepository;
    private final UserLectureRepository _UserLectureRepository;
    public LectureService(AllLectureRepository _AllLectureRepository, UserLectureRepository _UserLectureRepository) {
        this._AllLectureRepository = _AllLectureRepository;
        this._UserLectureRepository = _UserLectureRepository;
    }

    //-------------------------------------------//

    public List<Lecture> allLecture(){
        return _AllLectureRepository.findAll();

    }

    public List<Lecture> updateLecture() {

        // 기존 테이블 삭제 후 순번 초기화
        _AllLectureRepository.deleteAll();
        _AllLectureRepository.deleteINCREMENT();

        List<Lecture> LL = new ArrayList<Lecture>();

        // csv파일 읽어서 DB에 수업정보 업데이트
        try {
            // 배포용 경로
            InputStream inputStream = new ClassPathResource("ALLLECTURE2.txt").getInputStream();
            File file =File.createTempFile("ALLLECTURE2",".txt");
            try {
                FileUtils.copyInputStreamToFile(inputStream, file);
            } finally {
                IOUtils.closeQuietly(inputStream);
            }
            FileReader fileReader = new FileReader(file);
            BufferedReader bufReader = new BufferedReader(fileReader);

            String line = "";
            line = bufReader.readLine();
            while ((line = bufReader.readLine()) != null) {

                // tsv로 읽은 행 데이터 List에 넣어서 통채로 생성자로
                List<String> csv = new ArrayList<>();
                StringTokenizer s = new StringTokenizer(line);

                // 학과(부),학년,이수구분,학수번호,교과목명,담당교수,강의실,시간표,수업방법관리,학점
                for (int i = 1; i <= 12 && s.hasMoreTokens(); i++) {
                    String tmp = s.nextToken("\t");
                    if(i<=2) continue;
                    csv.add(tmp);
                }

                String _classroom = "", _classtime="";
                String classroom_raw = csv.get(6), classtime_raw=csv.get(7);

                // classroom 정제(폐기)
                /*if(classroom_raw.charAt(0) != '-'){
                    StringTokenizer room = new StringTokenizer(classroom_raw);
                    while(room.hasMoreTokens()){
                        String tmp = room.nextToken("[");
                        if(!room.hasMoreTokens()) break;
                        tmp = room.nextToken("]");
                        _classroom += tmp.substring(1,tmp.length());
                        _classroom += ",";
                        room_cnt++;
                    }
                } else _classroom = classroom_raw;*/
                // classtime 정제
                if(classtime_raw.charAt(0) != '-'){
                    StringTokenizer time = new StringTokenizer(classtime_raw);
                    int init_start=0, init_end=0;
                    while(time.hasMoreTokens()){
                        time.nextToken("[");
                        if(!time.hasMoreTokens()) break;
                        String _room = time.nextToken(":");
                        if(_room.length()<3) break;
                        _room = _room.substring(1,_room.length()) + ",";
                        if(!time.hasMoreTokens()) break;
                        String tmp = time.nextToken("]");
                        if(tmp.charAt(0)==':')
                            tmp = tmp.substring(1,tmp.length());
                        int start=0, end=0, time_cnt=0;

                        // time_sep = 화(8B-9);
                        StringTokenizer time_sep = new StringTokenizer(tmp);
                        while(time_sep.hasMoreTokens()){
                            String ttmp = time_sep.nextToken(",");
                            int idx=0;
                            if(ttmp.charAt(idx)=='('){
                                start=init_start;
                                end=init_end;
                            }
                            else{
                                switch (ttmp.charAt(0)){
                                    case '월': start+=16; end+=16; break;
                                    case '화': start+=64; end+=64; break;
                                    case '수': start+=112; end+=112; break;
                                    case '목': start+=160; end+=160; break;
                                    case '금': start+=208; end+=208; break;
                                    case '토': start+=256; end+=256; break;
                                }
                                init_start=start; init_end=end;
                                idx++;
                            }

                            // 야간
                            if(ttmp.charAt(idx+1)=='야'){
                                start+=18;
                                init_start = start;
                                int int2Str = ttmp.charAt(idx+2) - '0';
                                start += 2*int2Str;

                                end+=18;
                                init_end = end;
                                if(ttmp.charAt(ttmp.length()-2) == 'A'){
                                    int2Str = ttmp.charAt(ttmp.length()-3) - '0';
                                    end += 2*int2Str;
                                }
                                else {
                                    int2Str = ttmp.charAt(ttmp.length() - 2) - '0';
                                    end += 2 * int2Str + 1;
                                }
                                _classtime += Integer.toString(start) + '-' + Integer.toString(end) + ',';
                                time_cnt++;
                            }

                            // 주간
                            else{
                                init_start = start;
                                init_end = end;

                                int int2Str = ttmp.charAt(idx+1) - '0';
                                start += (2*int2Str);
                                if(ttmp.charAt(idx+2) == 'B')
                                    start += 1;

                                if(ttmp.charAt(ttmp.length()-2) == 'A'){
                                    int2Str = ttmp.charAt(ttmp.length()-3) - '0';
                                    end += 2 * int2Str;
                                }
                                else {
                                    int2Str = ttmp.charAt(ttmp.length()-2) - '0';
                                    end += 2 * int2Str + 1;
                                }
                                _classtime += Integer.toString(start) + '-' + Integer.toString(end) + ',';
                                time_cnt++;
                            }
                        }
                        for(int i=0; i<time_cnt; i++){
                            _classroom+=_room;
                        }
                    }
                } else _classtime = classtime_raw;

                Lecture _lecture = new Lecture(csv, _classroom, _classtime);
                LL.add(_lecture);

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        _AllLectureRepository.saveAll(LL);
        return _AllLectureRepository.findAll();
    }

    public boolean addLecture(String userID, String lectureID) {
        UserLecture _UserLecture = new UserLecture(userID,lectureID);
        // 중복체크
        try{
            UserLecture _OverlapLecture = _UserLectureRepository.findByUserIDAndLectureID(userID,lectureID);
            String name = _OverlapLecture.getLectureID();
            System.out.println("Already done");
            return false;
        }catch(Exception e){
            _UserLectureRepository.save(_UserLecture);
            return true;
        }
    }

    public List<Lecture> selectLecture(String main_keyword, String keyword_option, String major_option, String cse_option, String sort_option, String grade_option, String category_option, String score_option) {
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
        List<Lecture> result = new ArrayList<Lecture>();

        if(sort_option.equals("과목코드"))
            tmp = _AllLectureRepository.findAllByOrderByNumberAsc();
        else if(sort_option.equals("과목명"))
            tmp = _AllLectureRepository.findAllByOrderByLecturenameAsc();
        else tmp = _AllLectureRepository.findAll();

        for(int i=0; i<tmp.size(); i++){
            Lecture now = tmp.get(i);

            // main_keyword, keyword_option
            if(main_keyword!=""){
                if(keyword_option.equals("전체")){
                    if(!now.getLecturename().contains(main_keyword) && !now.getProfessor().contains(main_keyword) &&
                            !now.getNumber().contains(main_keyword)) continue;
                }
                if(keyword_option.equals("과목명") && !now.getLecturename().contains(main_keyword))
                    continue;
                else if(keyword_option.equals("교수명") && !now.getProfessor().contains(main_keyword))
                    continue;
                else if(keyword_option.equals("과목코드") && !now.getNumber().contains(main_keyword))
                    continue;
            }

            if(now.getLecturename().contains("그래픽스"))
                System.out.println("point1");

            // major_option
            if(!major_option.equals("전체") && !now.getDepartment().equals(major_option))
                continue;
            if(now.getLecturename().contains("그래픽스"))
                System.out.println("point2");
            // cse_option
            if(!cse_option.equals("전체") && !cse_option.equals(now.getCategory()))
                continue;
            if(now.getLecturename().contains("그래픽스"))
                System.out.println("point3");
            // grade_option
            StringTokenizer gst = new StringTokenizer(grade_option);
            int grade_check[] = new int[]{1,0,0,0,0};
            if(now.getLecturename().contains("그래픽스"))
                System.out.println(grade_option);
            while(!grade_option.equals("전체") && gst.hasMoreTokens()){
                String tok = gst.nextToken(", ");
                if(tok.charAt(0)=='1') grade_check[1]++;
                else if(tok.charAt(0)=='2') grade_check[2]++;
                else if(tok.charAt(0)=='3') grade_check[3]++;
                else if(tok.charAt(0)=='4') grade_check[4]++;
            }
            if(!grade_option.equals("전체") && grade_check[now.getGrade().charAt(0)-'0'] != 1)
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
            int score_check[] = new int[]{0,0,0,0,0,0,0};
            while(sst.hasMoreTokens()){
                String tok = sst.nextToken(", ");
                if(tok.charAt(0)=='1') score_check[1]++;
                else if(tok.charAt(0)=='2') score_check[2]++;
                else if(tok.charAt(0)=='3') score_check[3]++;
                else if(tok.charAt(0)=='5') score_check[5]++;
                else if(tok.charAt(0)=='6') score_check[6]++;
            }
            if(!score_option.equals("전체") && score_check[Integer.parseInt(now.getPoint())] == 0)
                continue;
            if(now.getLecturename().contains("그래픽스"))
                System.out.println("point6");
            result.add(now);

        }

       /*// sort_option
        if(sort_option=="과목코드")
            Arrays.sort(result,cmpNumber);
        if(sort_option=="과목명")
            Arrays.sort(result,cmpLectureName);*/
        return result;
    }
}
