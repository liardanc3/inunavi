package com.maru.inunavi.service;

import com.maru.inunavi.entity.Lecture;
import com.maru.inunavi.entity.UserLecture;
import com.maru.inunavi.repository.AllLectureRepository;
import com.maru.inunavi.repository.UserLectureRepository;
import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

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
}
