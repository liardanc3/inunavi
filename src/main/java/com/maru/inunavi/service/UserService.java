package com.maru.inunavi.service;

import com.maru.inunavi.entity.Lecture;
import com.maru.inunavi.entity.UserInfo;
import com.maru.inunavi.entity.UserLecture;
import com.maru.inunavi.repository.AllLectureRepository;
import com.maru.inunavi.repository.NaviRepository;
import com.maru.inunavi.repository.UserInfoRepository;
import com.maru.inunavi.repository.UserLectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    public List<UserInfo> showAll(){
        return _UserInfoRepository.findAll();
    }

    public Map<String, String> resister(String id, String password, String name, String email){
        Map<String, String> json = new HashMap<>();
        json.put("id", id);
        UserInfo _UserInfo = new UserInfo(id,password,name,email);
        try{
            _UserInfoRepository.findById(id);
            json.put("success","false");
        }
        catch(Exception E){
            _UserInfoRepository.save(_UserInfo);
            json.put("success", "true");
        }
        return json;
    }

    public Map<String, String> login(String id, String password) {
        Map<String, String> json = new HashMap<>();
        json.put("id", id);
        UserInfo _UserInfo = _UserInfoRepository.findByIdAndUserPW(id,password);
        if (_UserInfo==null) {
            json.put("success", "false");
            json.put("message", "로그인 실패");
        }
        else {
            json.put("success", "true");
            json.put("message", "로그인 성공");
        }
        return json;
    }

    public Map<String, String> idCheck(String id){
        Map<String, String> json = new HashMap<>();
        json.put("id", id);
        if(_UserInfoRepository.findById(id) != null)
            json.put("success", "true");
        else json.put("success", "false");
        return json;
    }

    public Map<String, String> AddLecture(String userID, String lectureID) {
        Map<String, String> json = new HashMap<>();
        json.put("id", userID);
        UserLecture _UserLecture = new UserLecture(userID,lectureID);
        try{
            UserLecture _OverlapLecture = _UserLectureRepository.findByUserIDAndLectureID(userID,lectureID);
            json.put("success", "false");
        }catch(Exception e){
            _UserLectureRepository.save(_UserLecture);
            json.put("success", "true");
        }
        return json;
    }

    public Map<String, String> deleteLecture(String UserID, String LectureID){
        Map<String, String> json = new HashMap<>();
        json.put("id", UserID);
        try{
            UserLecture _UserLecture = _UserLectureRepository.findByUserIDAndLectureID(UserID, LectureID);
            _UserLectureRepository.delete(_UserLecture);
            json.put("success", "true");
        }
        catch (Exception E) {
            json.put("success", "false");
        }
        return json;
    }

    public Map<String, ArrayList<Lecture>> showMyLecture(String id) {
        Map<String, ArrayList<Lecture>> json = new HashMap<>();
        List<UserLecture> _UL = _UserLectureRepository.findAllByUserID(id);
        ArrayList<Lecture> _LAL = new ArrayList<Lecture>();
        for(int i=0; i<_UL.size(); i++){
            try{
                _LAL.add(_AllLectureRepository.findByLectureID(_UL.get(i).getLectureID()));
            }catch (Exception E) {
                continue;
            }
        }
        json.put("response", _LAL);
        return json;
    }
}
