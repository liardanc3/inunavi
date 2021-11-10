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

            String _classroom = "";
            String _classtime = "";

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

                    // I열 수업장소
                    if(i==9){
                        // 다중 강의실
                        if(tmp.charAt(0)=='"')
                            tmp += s.nextToken(",");

                    }
                    // J열 수업시간, 수업장소
                    if(i==10 && tmp.charAt(0)=='"'){
                        while(tmp.charAt(tmp.length()-1)!='"')
                            tmp += s.nextToken(",");
                    }

                    csv.add(tmp);
                }

                System.out.println(csv);
                lecture _lecture = new lecture(csv,_classroom,_classtime);
                _lectureRepository.save(_lecture);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return _lectureRepository.findAll();
    }
    // 요일 및 시간 정수화
    public String Daytime2Int(String daytime) {

        // 요일 시간 없을 때
        if(daytime.equals("-")){
            return daytime;
        }

        // 시간 초기값 설정(30분 분할, 월요일 오전 9시 기준 0, 화요일 오전 9시 = 48)
        final char [] dayARR = {'월','화','수','목','금','토'};
        int t = 0; int start=99999; int end=0;
        for(int i=0; i<7; i++){
            if(dayARR[i]==daytime.charAt(0)){
                t = i*47+18;
                break;
            }
        }
        // 야간여부 확인
        boolean flag = false;
        for(int i=0; i<daytime.length(); i++){
            if(daytime.charAt(i)=='야'){
                flag=true;
                break;
            }
        }
        if(flag) t+=18;

        // 정수화
        String _daytime = daytime.substring(1,daytime.length()-1);
        StringTokenizer s = new StringTokenizer(_daytime);
        while(s.hasMoreTokens()){
            String ss = s.nextToken(" -야");
            System.out.println(ss);

            // 숫자만 입력된 경우 (7 8 9 등)
            if(ss.length()==1){
                int now = Integer.parseInt(ss);
                start = Math.min(start,t+(now-1)*2);
                end = Math.max(end,t+(now-1)*2+1);
            }

            // 섞인 경우 (8A 8B 등)
            else{
                int now = Integer.parseInt(ss.substring(0,1));
                start = Math.min(start,t+(now-1)*2+1);
                end = Math.max(end,t+(now-1)*2);
            }
        }

        // ex "0,5" -> (월 1 2 3)
        String _start = Integer.toString(start);
        String _end = Integer.toString(end);
        return _start + "," + _end;
    }
}
