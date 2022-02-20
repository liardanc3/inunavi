package com.maru.inunavi.service;

import com.maru.inunavi.entity.Lecture;
import com.maru.inunavi.entity.UserInfo;
import com.maru.inunavi.entity.UserLecture;
import com.maru.inunavi.repository.AllLectureRepository;
import com.maru.inunavi.repository.NaviRepository;
import com.maru.inunavi.repository.UserInfoRepository;
import com.maru.inunavi.repository.UserLectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final NaviRepository _NaviRepository;
    private final UserInfoRepository _UserInfoRepository;
    private final UserLectureRepository _UserLectureRepository;
    private final AllLectureRepository _AllLectureRepository;

    @Autowired
    private JavaMailSender javaMailSender;

    public List<UserInfo> showAll(){
        return _UserInfoRepository.findAll();
    }

    public Map<String, String> resister(String email, String password, String major){
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

    public Map<String, String> AddLecture(String email, String lectureID) {
        Map<String, String> json = new HashMap<>();
        json.put("email", email);
        if(_UserLectureRepository.findByUserEmailAndLectureID(email,lectureID) == null){
            _UserLectureRepository.save(new UserLecture(email, lectureID));
            json.put("success", "true");
        }else{
            json.put("success", "false");
        }
        return json;
    }

    public Map<String, String> deleteLecture(String email, String lectureID){
        Map<String, String> json = new HashMap<>();
        json.put("email", email);
        UserLecture _UserLecture = _UserLectureRepository.findByUserEmailAndLectureID(email, lectureID) ;
        if(_UserLecture != null){
            _UserLectureRepository.delete(_UserLecture);
            json.put("success", "true");
        }
        else {
            json.put("success", "false");
        }
        return json;
    }

    public Map<String, ArrayList<Lecture>> showMyLecture(String email) {
        Map<String, ArrayList<Lecture>> json = new HashMap<>();
        List<UserLecture> _UL = _UserLectureRepository.findAllByUserEmail(email);
        ArrayList<Lecture> _LAL = new ArrayList<Lecture>();
        for(int i=0; i<_UL.size(); i++){
            _LAL.add(_AllLectureRepository.findByLectureID(_UL.get(i).getLectureId()));
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
            json.put("success","true");
        }
        return json;
    }
}
