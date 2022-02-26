package com.maru.inunavi.service;

import com.maru.inunavi.entity.Lecture;
import com.maru.inunavi.entity.RecommendTable;
import com.maru.inunavi.entity.UserInfo;
import com.maru.inunavi.entity.UserLecture;
import com.maru.inunavi.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RecommendService {

    private final NaviRepository _NaviRepository;
    private final UserInfoRepository _UserInfoRepository;
    private final UserLectureRepository _UserLectureRepository;
    private final AllLectureRepository _AllLectureRepository;
    private final NodePathRepository _NodePathRepository;
    private final PlaceRepository _PlaceRepository;
    private final RecommendTableRepository _RecommendTableRepository;

    public List<RecommendTable> updateRecommendTable(){

        List<Lecture> lectureList = _AllLectureRepository.findAll();
        List<UserInfo> userInfoList = _UserInfoRepository.findAll();

        // 수업 개수 + 유사도행렬
        int len = lectureList.size();
        int[][] similarityArr = new int[len+1][len+1];

        // 유저 수만큼 반복
        for(int i=0; i<userInfoList.size(); i++){
            // 유저 이메일
            String userEmail = userInfoList.get(i).getEmail();

            // 유저 수강정보 리스트
            List<UserLecture> userLectureList = _UserLectureRepository.findAllByEmail(userEmail);

            // 유저 수강정보 리스트 벡터추출
            List<Integer> userLectureIdxList = new ArrayList<>();
            for(int j=0; j<userLectureList.size(); j++)
                userLectureIdxList.add(userLectureList.get(j).getLectureIdx());

            // 유사도행렬 추가
            for(int j=0; j<userLectureIdxList.size()-1; j++){
                int now = userLectureIdxList.get(j);
                for(int k=j+1; k<userLectureIdxList.size(); k++){
                    if(j==k) continue;

                    int next = userLectureIdxList.get(k);
                    similarityArr[now][next]++;
                    similarityArr[next][now]++;
                }
            }
        }
    }

    public Map<String, List<Lecture>> getRecommendLecture(String email) {
        Map<String, List<Lecture>> retMap = new HashMap<>();

        return retMap;
    }
}
