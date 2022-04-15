package com.maru.inunavi.service;

import com.maru.inunavi.entity.Lecture;
import com.maru.inunavi.entity.UserInfo;
import com.maru.inunavi.entity.UserLecture;
import com.maru.inunavi.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserInfoRepository _UserInfoRepository;
    private final UserLectureRepository _UserLectureRepository;
    private final AllLectureRepository _AllLectureRepository;
    private final RecommendRepository _RecommendRepository;

    @Autowired
    private JavaMailSender javaMailSender;

    public Map<String, String> signUp(String email, String password, String major){
        PasswordEncoder passwordencoder = new BCryptPasswordEncoder();
        Map<String, String> json = new HashMap<>();
        json.put("email", email);
        if (_UserInfoRepository.findByEmail(email) == null){
            _UserInfoRepository.save(new UserInfo(email,passwordencoder.encode(password), major));
            json.put("success", "true");
        }else{
            json.put("success","false");
        }
        return json;
    }

    public Map<String, String> login(String email, String password) {
        PasswordEncoder passwordencoder = new BCryptPasswordEncoder();
        Map<String, String> json = new HashMap<>();
        json.put("email", email);
        UserInfo _UserInfo = _UserInfoRepository.findByEmail(email);
        if (_UserInfo==null) {
            json.put("success", "false");
            json.put("message", "아이디가 틀림");
        }
        else if (!passwordencoder.matches(password, _UserInfo.getPassword())){
            json.put("success", "false");
            json.put("message", "비밀번호 오류");
        } else {
            json.put("success", "true");
            json.put("major", _UserInfo.getMajor());
            json.put("message", "로그인 성공");
        }

        return json;
    }

    public Map<String, String> idCheck(String email){
        Map<String, String> json = new HashMap<>();
        json.put("email", email);
        if(_UserInfoRepository.findByEmail(email) != null)
            json.put("success", "true");
        else json.put("success", "false");
        return json;
    }

    public Map<String, String> AddLecture(String email, String lectureId) {

        Map<String, String> json = new HashMap<>();

        // lectureId -> lectureIdx
        int lectureIdx = _AllLectureRepository.findByLectureId(lectureId).getId();

        json.put("email", email);
        if(_UserLectureRepository.findByUserEmailAndLectureIdx(email,lectureIdx) == null){
            _UserLectureRepository.save(new UserLecture(email, lectureIdx));
            json.put("success", "true");
            updateRecommendTable(email,lectureIdx,true);
        }else{
            json.put("success", "false");
        }
        return json;
    }

    public Map<String, String> deleteLecture(String email, String lectureID){
        Map<String, String> json = new HashMap<>();

        int lectureIdx = _AllLectureRepository.findByLectureId(lectureID).getId();

        json.put("email", email);
        UserLecture _UserLecture = _UserLectureRepository.findByUserEmailAndLectureIdx(email, lectureIdx) ;
        if(_UserLecture != null){
            json.put("success", "true");
            updateRecommendTable(email,lectureIdx,false);
            _UserLectureRepository.delete(_UserLecture);
        }
        else {
            json.put("success", "false");
        }
        return json;
    }

    public Map<String, List<Map<String, String>>> showMyLecture(String email) {
        Map<String, List<Map<String, String>>> json = new HashMap<>();
        List<UserLecture> _UL = _UserLectureRepository.findAllByEmail(email);
        List<Map<String, String>> _LAL = new ArrayList<>();
        for (int i = 0; i < _UL.size(); i++) {
            Map<String, String> retMap = new HashMap<>();
            Lecture now = _AllLectureRepository.getById(_UL.get(i).getLectureIdx());
            retMap.put("id", Integer.toString(now.getId()));
            retMap.put("department", now.getDepartment());
            retMap.put("grade", now.getGrade());
            retMap.put("category", now.getCategory());
            retMap.put("number", now.getNumber());
            retMap.put("lecturename", now.getLecturename());
            retMap.put("professor", now.getProfessor());
            retMap.put("classroom_raw", now.getClassroom_raw());
            retMap.put("classtime_raw", now.getClasstime_raw());
            String __CLASSROOM__ = now.getClassroom();
            if(__CLASSROOM__.length()>2)
                retMap.put("classroom",__CLASSROOM__.substring(0,__CLASSROOM__.length()-1));
            else retMap.put("classroom",__CLASSROOM__);
            retMap.put("classtime", now.getClasstime());
            retMap.put("how", now.getHow());
            retMap.put("point", now.getPoint());

            String __CLASSTIME__ = now.getClasstime();
            String __RETCLASSTIME__ = "";
            if (__CLASSTIME__.equals("-")) {
                retMap.put("realTime", "-");
                _LAL.add(retMap);
            }
            else {
                __CLASSTIME__ = __CLASSTIME__.replaceAll(",", "-");
                StringTokenizer st = new StringTokenizer(__CLASSTIME__);
                while (st.hasMoreTokens()) {
                    int start = Integer.parseInt(st.nextToken("-"));
                    int end = Integer.parseInt(st.nextToken("-"));

                    int dayOfWeek = start / 48;
                    int startHour = (start % 48) / 2;
                    int startHalf = (start % 2);
                    int endHour = (end % 48) / 2 + 1;
                    int endHalf = ~(end % 2);

                    switch (dayOfWeek) {
                        case 0:
                            __RETCLASSTIME__ += "월 ";
                            break;
                        case 1:
                            __RETCLASSTIME__ += "화 ";
                            break;
                        case 2:
                            __RETCLASSTIME__ += "수 ";
                            break;
                        case 3:
                            __RETCLASSTIME__ += "목 ";
                            break;
                        case 4:
                            __RETCLASSTIME__ += "금 ";
                            break;
                        case 5:
                            __RETCLASSTIME__ += "토 ";
                            break;
                        case 6:
                            __RETCLASSTIME__ += "일 ";
                            break;
                    }

                    __RETCLASSTIME__ += Integer.toString(startHour);
                    __RETCLASSTIME__ += ":";
                    __RETCLASSTIME__ += startHalf == 1 ? "30 - " : "00 - ";
                    __RETCLASSTIME__ += Integer.toString(endHour);
                    __RETCLASSTIME__ += ":";
                    __RETCLASSTIME__ += endHalf == 1 ? "30, " : "00, ";
                }
                __RETCLASSTIME__ = __RETCLASSTIME__.substring(0, __RETCLASSTIME__.length() - 2);
                retMap.put("realTime", __RETCLASSTIME__);
                _LAL.add(retMap);
            }
        }
        json.put("response", _LAL);
        return json;
    }

    public Map<String, String> verify(String email){
        Map<String, String> json = new HashMap<>();
        String code = "";
        int[][] d = {{48, 10}, {65, 26}, {97, 26}};
        for(int i=0;i<6;i++){
            int j = (int) (Math.random() * 3);
            code += (char) ((int) (Math.random() * d[j][1] + d[j][0]));
        }
        try{
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setTo(email);
            simpleMailMessage.setSubject("verify");
            simpleMailMessage.setText(code);
            this.javaMailSender.send(simpleMailMessage);
            json.put("success", "true");
            json.put("code", code);
        }catch (Exception E){
            json.put("messege", E.getMessage());
            json.put("success", "false");
        }

        return json;

    }

    public Map<String, String> updatePassword(String email, String password){
        PasswordEncoder passwordencoder = new BCryptPasswordEncoder();
        Map<String, String> json = new HashMap<>();
        json.put("email", email);
        UserInfo _UserInfo = _UserInfoRepository.findByEmail(email);
        if (_UserInfo != null){
            _UserInfoRepository.save(new UserInfo(email, passwordencoder.encode(password), _UserInfo.getMajor()));
            json.put("success", "true");
        }else{
            json.put("success","false");
        }
        return json;
    }

    public Map<String, String> updateMajor(String email, String major){
        Map<String, String> json = new HashMap<>();
        json.put("email", email);
        UserInfo _UserInfo = _UserInfoRepository.findByEmail(email);
        if (_UserInfo != null){
            _UserInfoRepository.save(new UserInfo(email, _UserInfo.getPassword(), major));
            json.put("success", "true");
        }else{
            json.put("success","false");
        }
        return json;
    }

    public Map<String, String> delete(String email, String password){
        PasswordEncoder passwordencoder = new BCryptPasswordEncoder();
        Map<String, String> json = new HashMap<>();
        json.put("email", email);
        UserInfo _UserInfo = _UserInfoRepository.findByEmail(email);
        if (_UserInfo == null){
            json.put("success", "false");
        }else if (!passwordencoder.matches(password, _UserInfo.getPassword())){
            json.put("success","false");
        }else{
            _UserInfoRepository.delete(_UserInfo);
            List<UserLecture> userLectureList = _UserLectureRepository.findAllByEmail(email);
            for(int i=0; i<userLectureList.size(); i++){
                updateRecommendTable(email,userLectureList.get(i).getLectureIdx(),false);
                _UserLectureRepository.delete(userLectureList.get(i));
            }
            json.put("success","true");
        }
        return json;
    }

    public void updateRecommendTable(String email, int lectureIdx, boolean add){
        List<UserLecture> userLectureList = _UserLectureRepository.findAllByEmail(email);

        // 유저가 듣는 수업의 인덱스 리스트
        List<Integer> userLectureIdxList = new ArrayList<>();
        for(int i=0; i<userLectureList.size(); i++)
            userLectureIdxList.add(userLectureList.get(i).getLectureIdx());

        // 유사도행렬 업데이트
        int len = (int) _AllLectureRepository.count();
        int[][] similarityArr = new int[len+1][len+1];
        for(int i=0; i<userLectureIdxList.size(); i++){
            int idx = userLectureIdxList.get(i);
            String similarityString = _RecommendRepository.getById(idx).getSimilarityString();
            StringTokenizer st = new StringTokenizer(similarityString);
            for(int j=1; j<=len; j++){
                String s = st.nextToken(",");
                int similarityPoint = Integer.parseInt(s);
                similarityArr[idx][j] += similarityPoint;
            }
            if(idx==lectureIdx) continue;

            // 추가면 +1, 삭제면 -1
            similarityArr[idx][lectureIdx] += add ? 1 : -1;
            similarityArr[lectureIdx][idx] += add ? 1 : -1;
        }

        // recommendTable 업데이트
        for(int i=0; i<userLectureIdxList.size(); i++){
            int idx = userLectureIdxList.get(i);
            String similarityString = "";
            for(int j=1; j<=len; j++)
                similarityString += Integer.toString(similarityArr[idx][j]) + ",";
            _RecommendRepository.updateSimilarityString(idx,similarityString);
        }
    }

    public List<UserInfo> memberList() {
        return _UserInfoRepository.findAll();
    }
}
