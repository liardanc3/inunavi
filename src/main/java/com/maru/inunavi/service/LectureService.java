package com.maru.inunavi.service;

import com.maru.inunavi.entity.Lecture;
import com.maru.inunavi.entity.UserLecture;
import com.maru.inunavi.repository.AllLectureRepository;
import com.maru.inunavi.repository.UserLectureRepository;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringTokenizer;

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
            file = new File("src/main/resources/ALLLECTURE.csv");
            FileReader fileReader = new FileReader(file);
            BufferedReader bufReader = new BufferedReader(fileReader);

            String line = "", tmp = "";
            line = bufReader.readLine();
            while ((line = bufReader.readLine()) != null) {

                // csv로 읽은 행 데이터 List에 넣어서 통채로 생성자로
                List<String> csv = new ArrayList<>();
                StringTokenizer s = new StringTokenizer(line);

                // 학과(부),학년,이수구분,학수번호,교과목명,담당교수,강의실,시간표,수업방법관리,학점
                for (int i = 1; i <= 12 && s.hasMoreTokens(); i++) {
                    tmp = "";
                    tmp += s.nextToken(",");
                    System.out.println(tmp);

                    // A,B열 스킵
                    if (i <= 2) continue;
                    if(tmp.charAt(0) == '"'){
                        while (tmp.charAt(tmp.length() - 1) != '"') {
                            tmp += ",";
                            tmp += s.nextToken(",");
                        }
                    }
                    csv.add(tmp);
                }
                String _classroom = "", _classtime="";
                String room = csv.get(6), time=csv.get(7);



                System.out.println(csv);
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
