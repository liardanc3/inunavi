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

    public Map<String, String> resister(String email, String password){
        PasswordEncoder passwordencoder = new BCryptPasswordEncoder();
        Map<String, String> json = new HashMap<>();
        json.put("email", email);
        UserInfo _UserInfo = new UserInfo(email,passwordencoder.encode(password));
        if (_UserInfoRepository.findByEmail(email) == null){
            _UserInfoRepository.save(_UserInfo);
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
            json.put("message", "로그인 실패");
        }
        else if (!passwordencoder.matches(password, _UserInfo.getPassword())){
            json.put("success", "false");
            json.put("message", "로그인 실패2");
        } else {
            json.put("success", "true");
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
        UserLecture _UserLecture = new UserLecture(email,lectureID);
        try{
            UserLecture _OverlapLecture = _UserLectureRepository.findByUserEmailAndLectureID(email,lectureID);
            json.put("success", "false");
        }catch(Exception e){
            _UserLectureRepository.save(_UserLecture);
            json.put("success", "true");
        }
        return json;
    }

    public Map<String, String> deleteLecture(String email, String LectureID){
        Map<String, String> json = new HashMap<>();
        json.put("id", email);
        try{
            UserLecture _UserLecture = _UserLectureRepository.findByUserEmailAndLectureID(email, LectureID);
            _UserLectureRepository.delete(_UserLecture);
            json.put("success", "true");
        }
        catch (Exception E) {
            json.put("success", "false");
        }
        return json;
    }

    public Map<String, ArrayList<Lecture>> showMyLecture(String email) {
        Map<String, ArrayList<Lecture>> json = new HashMap<>();
        List<UserLecture> _UL = _UserLectureRepository.findAllByUserEmail(email);
        ArrayList<Lecture> _LAL = new ArrayList<Lecture>();
        for(int i=0; i<_UL.size(); i++){
            try{
                _LAL.add(_AllLectureRepository.findByLectureID(_UL.get(i).getLectureId()));
            }catch (Exception E) {
                continue;
            }
        }
        json.put("response", _LAL);
        return json;
    }

    public Map<String, String> verify(String email, String code){
        Map<String, String> json = new HashMap<>();
        try{
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setTo(email);
            simpleMailMessage.setSubject("verify");
            simpleMailMessage.setText(code);
            this.javaMailSender.send(simpleMailMessage);
            json.put("success", "true");
        }catch (Exception E){
            json.put("messege", E.getMessage());
            json.put("success", "false");
        }

        return json;

    }

    public Map<String, String> update(String email, String password){
        PasswordEncoder passwordencoder = new BCryptPasswordEncoder();
        Map<String, String> json = new HashMap<>();
        json.put("email", email);
        UserInfo _UserInfo = new UserInfo(email,passwordencoder.encode(password));
        if (_UserInfoRepository.findByEmail(email) != null){
            _UserInfoRepository.save(_UserInfo);
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
