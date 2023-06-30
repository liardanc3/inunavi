package com.maru.inunavi.user.service;

import com.maru.inunavi.aspect.annotation.Log;
import com.maru.inunavi.lecture.domain.entity.Lecture;
import com.maru.inunavi.lecture.repository.LectureRepository;
import com.maru.inunavi.recommend.repository.RecommendRepository;
import com.maru.inunavi.user.domain.dto.SignUpDto;
import com.maru.inunavi.user.domain.entity.User;
import com.maru.inunavi.user.domain.entity.UserLectureTable;
import com.maru.inunavi.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final LectureRepository LectureRepository;
    private final RecommendRepository recommendRepository;

    private final JavaMailSender javaMailSender;

    @Log("[{}] singUp")
    public SignUpDto signUp(String email, String password, String major){
        if (userRepository.findByEmail(email) == null){
            User savedUser = userRepository.save(
                    User.builder()
                            .email(email)
                            .password(new BCryptPasswordEncoder().encode(password))
                            .major(major)
                            .build()
            );
            return new SignUpDto(savedUser);
        }
        return new SignUpDto(email);
    }

    public Map<String, String> login(String email, String password) {
        PasswordEncoder passwordencoder = new BCryptPasswordEncoder();
        Map<String, String> json = new HashMap<>();
        json.put("email", email);
        User user = userRepository.findByEmail(email);
        if (user ==null) {
            json.put("success", "false");
            json.put("message", "아이디가 틀림");
        }
        else if (!passwordencoder.matches(password, user.getPassword())){
            json.put("success", "false");
            json.put("message", "비밀번호 오류");
        } else {
            json.put("success", "true");
            json.put("major", user.getMajor());
            json.put("message", "로그인 성공");
        }

        return json;
    }

    public Map<String, String> idCheck(String email){
        Map<String, String> json = new HashMap<>();
        json.put("email", email);
        if(userRepository.findByEmail(email) != null)
            json.put("success", "true");
        else json.put("success", "false");
        return json;
    }

    @Transactional
    public Map<String, String> AddLecture(String email, String lectureId) {

        Map<String, String> json = new HashMap<>();

        // lectureId -> lectureIdx
        Long lectureIdx = LectureRepository.findByNumber(lectureId).getId();

        json.put("email", email);
        if(userLectureTableRepository.findByEmailAndLectureIdx(email, Math.toIntExact(lectureIdx)) == null){
            userLectureTableRepository.save(new UserLectureTable(email, Math.toIntExact(lectureIdx)));
            json.put("success", "true");
            updateRecommendTable(email, Math.toIntExact(lectureIdx),true);
        }else{
            json.put("success", "false");
        }

        User user = userRepository.findByEmail(email);
        Lecture lecture = LectureRepository.findByNumber(lectureId);
        System.out.println("user.getId() = " + user.getId());
        System.out.println("lecture.getId() = " + lecture.getId());
        lecture.getUsers().add(user);
        user.getLectures().add(lecture);
        return json;
    }

    public Map<String, String> deleteLecture(String email, String lectureID){
        Map<String, String> json = new HashMap<>();

        Long lectureIdx = LectureRepository.findByNumber(lectureID).getId();

        json.put("email", email);
        UserLectureTable userLectureTable = userLectureTableRepository.findByEmailAndLectureIdx(email, Math.toIntExact(lectureIdx)) ;
        if(userLectureTable != null){
            json.put("success", "true");
            updateRecommendTable(email, Math.toIntExact(lectureIdx),false);
            userLectureTableRepository.delete(userLectureTable);
        }
        else {
            json.put("success", "false");
        }
        return json;
    }

    public Map<String, List<Map<String, String>>> showMyLecture(String email) {
        Map<String, List<Map<String, String>>> json = new HashMap<>();
        List<UserLectureTable> _UL = userLectureTableRepository.findAllByEmail(email);
        List<Map<String, String>> _LAL = new ArrayList<>();
        for (int i = 0; i < _UL.size(); i++) {
            Map<String, String> retMap = new HashMap<>();
            Lecture now = LectureRepository.getById(_UL.get(i).getLectureIdx());
            retMap.put("id", Long.toString(now.getId()));
            retMap.put("department", now.getDepartment());
            retMap.put("grade", now.getGrade());
            retMap.put("category", now.getCategory());
            retMap.put("number", now.getNumber());
            retMap.put("lecturename", now.getLectureName());
            retMap.put("professor", now.getProfessor());
            retMap.put("ClassRoomRaw", now.getClassRoomRaw());
            retMap.put("classTime_raw", now.getClassTimeRaw());
            String __ClassRoom__ = now.getClassRoom();
            if(__ClassRoom__.length()>2)
                retMap.put("ClassRoom",__ClassRoom__.substring(0,__ClassRoom__.length()-1));
            else retMap.put("ClassRoom",__ClassRoom__);
            retMap.put("classTime", now.getClassTime());
            retMap.put("how", now.getHow());
            retMap.put("point", now.getPoint());

            String __classTime__ = now.getClassTime();
            String __RETclassTime__ = "";
            if (__classTime__.equals("-")) {
                retMap.put("realTime", "-");
                _LAL.add(retMap);
            }
            else {
                __classTime__ = __classTime__.replaceAll(",", "-");
                StringTokenizer st = new StringTokenizer(__classTime__);
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
                            __RETclassTime__ += "월 ";
                            break;
                        case 1:
                            __RETclassTime__ += "화 ";
                            break;
                        case 2:
                            __RETclassTime__ += "수 ";
                            break;
                        case 3:
                            __RETclassTime__ += "목 ";
                            break;
                        case 4:
                            __RETclassTime__ += "금 ";
                            break;
                        case 5:
                            __RETclassTime__ += "토 ";
                            break;
                        case 6:
                            __RETclassTime__ += "일 ";
                            break;
                    }

                    __RETclassTime__ += Integer.toString(startHour);
                    __RETclassTime__ += ":";
                    __RETclassTime__ += startHalf == 1 ? "30 - " : "00 - ";
                    __RETclassTime__ += Integer.toString(endHour);
                    __RETclassTime__ += ":";
                    __RETclassTime__ += endHalf == 1 ? "30, " : "00, ";
                }
                __RETclassTime__ = __RETclassTime__.substring(0, __RETclassTime__.length() - 2);
                retMap.put("realTime", __RETclassTime__);
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
        User user = userRepository.findByEmail(email);
        if (user != null){
            userRepository.save(
                    User.builder()
                            .email(email)
                            .password(passwordencoder.encode((password)))
                            .major(user.getMajor())
                            .build()
            );
            json.put("success", "true");
        }else{
            json.put("success","false");
        }
        return json;
    }

    public Map<String, String> updateMajor(String email, String major){
        Map<String, String> json = new HashMap<>();
        json.put("email", email);
        User user = userRepository.findByEmail(email);
        if (user != null){
            userRepository.save(
                    User.builder()
                            .email(email)
                            .password(user.getPassword())
                            .major(major)
                            .build()
            );
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
        User user = userRepository.findByEmail(email);
        if (user == null){
            json.put("success", "false");
        }else if (!passwordencoder.matches(password, user.getPassword())){
            json.put("success","false");
        }else{
            userRepository.delete(user);
            List<UserLectureTable> userLectureTableList = userLectureTableRepository.findAllByEmail(email);
            for(int i = 0; i< userLectureTableList.size(); i++){
                updateRecommendTable(email, userLectureTableList.get(i).getLectureIdx(),false);
                userLectureTableRepository.delete(userLectureTableList.get(i));
            }
            json.put("success","true");
        }
        return json;
    }

    public void updateRecommendTable(String email, int lectureIdx, boolean add){
        List<UserLectureTable> userLectureTableList = userLectureTableRepository.findAllByEmail(email);

        // 유저가 듣는 수업의 인덱스 리스트
        List<Integer> userLectureIdxList = new ArrayList<>();
        for(int i = 0; i< userLectureTableList.size(); i++)
            userLectureIdxList.add(userLectureTableList.get(i).getLectureIdx());

        // 유사도행렬 업데이트
        int len = (int) LectureRepository.count();
        int[][] similarityArr = new int[len+1][len+1];
        for(int i=0; i<userLectureIdxList.size(); i++){
            int idx = userLectureIdxList.get(i);
            String similarityString = recommendRepository.getById(idx).getSimilarity();
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
            recommendRepository.updateSimilarityString(idx,similarityString);
        }
    }

    public List<User> memberList() {
        return userRepository.findAll();
    }
}
