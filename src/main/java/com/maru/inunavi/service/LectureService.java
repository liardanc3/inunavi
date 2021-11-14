package com.maru.inunavi.service;

import com.maru.inunavi.entity.Lecture;
import com.maru.inunavi.entity.UserLecture;
import com.maru.inunavi.repository.AllLectureRepository;
import com.maru.inunavi.repository.UserLectureRepository;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.io.*;
import java.util.*;

@Service
public class LectureService {

    private final AllLectureRepository _AllLectureRepository;
    private final UserLectureRepository _UserLectureRepository;

    public LectureService(AllLectureRepository _AllLectureRepository, UserLectureRepository _UserLectureRepository) {
        this._AllLectureRepository = _AllLectureRepository;
        this._UserLectureRepository = _UserLectureRepository;
    }

    public List<Lecture> allLecture(){
        return _AllLectureRepository.findAll();
    }
    public List<Lecture> updateLecture() {

        // 기존 테이블 삭제 후 순번 초기화
        _AllLectureRepository.deleteAll();
        _AllLectureRepository.deleteINCREMENT();

        // csv파일 읽어서 DB에 수업정보 업데이트
        try {
            File file;
            file = new File("src/main/resources/ALLLECTURE2.txt");
            FileReader fileReader = new FileReader(file);
            BufferedReader bufReader = new BufferedReader(fileReader);

            String line = "";
            line = bufReader.readLine();
            while ((line = bufReader.readLine()) != null) {

                // csv로 읽은 행 데이터 List에 넣어서 통채로 생성자로
                List<String> csv = new ArrayList<>();
                StringTokenizer s = new StringTokenizer(line);

                // 학과(부),학년,이수구분,학수번호,교과목명,담당교수,강의실,시간표,수업방법관리,학점
                for (int i = 1; i <= 12 && s.hasMoreTokens(); i++) {
                    String tmp = s.nextToken("\t");
                    if(i<=2) continue;

                    csv.add(tmp);
                }


                System.out.println(csv);

                String _classroom = "", _classtime="";
                String classroom_raw = csv.get(6), classtime_raw=csv.get(7);
                int room_cnt=0, time_cnt=0;

                // classroom 정제
                if(classroom_raw.charAt(0) != '-'){
                    StringTokenizer room = new StringTokenizer(classroom_raw);
                    while(room.hasMoreTokens()){
                        String tmp = room.nextToken("[");
                        if(!room.hasMoreTokens()) break;
                        tmp = room.nextToken("]");
                        _classroom += tmp.substring(1,tmp.length());
                        _classroom += ",";
                        room_cnt++;
                    }
                } else _classroom = classroom_raw;

                // classtime 정제
                if(classtime_raw.charAt(0) != '-'){
                    StringTokenizer time = new StringTokenizer(classtime_raw);
                    while(time.hasMoreTokens()){
                        String tmp = time.nextToken(":");
                    }
                } else _classtime = classtime_raw;

                Lecture _lecture = new Lecture(csv, _classroom, _classtime);
                _AllLectureRepository.save(_lecture);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

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
}
