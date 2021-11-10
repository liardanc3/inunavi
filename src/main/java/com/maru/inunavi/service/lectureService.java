package com.maru.inunavi.service;

import com.maru.inunavi.entity.lecture;
import com.maru.inunavi.repository.lectureRepository;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

@Service
public class lectureService {
    private final lectureRepository _lectureRepository;

    public lectureService(lectureRepository _lectureRepository){
        this._lectureRepository = _lectureRepository;
    }

    public List<lecture> allLecture(){
        return _lectureRepository.findAll();
    }

    public List<lecture> updateLecture(){

        // 기존 테이블 삭제 후 순번 초기화
        _lectureRepository.deleteAll();
        _lectureRepository.deleteINCREMENT();

        // csv파일 읽어서 DB에 수업정보 업데이트
        try{
            File file;
            file = new File("src/main/resources/ALLLECTURE.csv");
            FileReader fileReader = new FileReader(file);
            BufferedReader bufReader = new BufferedReader(fileReader);

            String line = "", tmp= "";
            line = bufReader.readLine();
            while((line = bufReader.readLine()) != null){

                // csv로 읽은 행 데이터 List에 넣어서 통채로 생성자로
                List<String> csv = new ArrayList<>();

                StringTokenizer s = new StringTokenizer(line);

                // 학과(부),학년,이수구분,학수번호,교과목명,담당교수,강의실,시간표,수업방법관리,학점
                for(int i=1; i<=12 && s.hasMoreTokens(); i++){
                    tmp = "";
                    tmp += s.nextToken(",");
                    System.out.println(tmp);

                    // A,B열 스킵
                    if(i<=2) continue;

                    // I열 스킵
                    if(i==9){
                        // 다중 강의실
                        if(tmp.charAt(0)=='"'){
                            s.nextToken(",");
                        }
                        continue;
                    }

                    // 수업시간
                    if(i==10){

                    }

                    csv.add(tmp);
                }
                System.out.println(csv);
                lecture _lecture = new lecture(csv);
                _lectureRepository.save(_lecture);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return _lectureRepository.findAll();
    }

}
