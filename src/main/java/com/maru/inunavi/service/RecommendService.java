package com.maru.inunavi.service;

import com.maru.inunavi.entity.Lecture;
import com.maru.inunavi.entity.RecommendTable;
import com.maru.inunavi.entity.UserInfo;
import com.maru.inunavi.entity.UserLecture;
import com.maru.inunavi.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RecommendService {

    private final UserInfoRepository _UserInfoRepository;
    private final UserLectureRepository _UserLectureRepository;
    private final AllLectureRepository _AllLectureRepository;
    private final RecommendTableRepository _RecommendTableRepository;

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
            String similarityString = _RecommendTableRepository.getById(idx).getSimilarityString();
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
            _RecommendTableRepository.updateSimilarityString(idx,similarityString);
        }
    }

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

        // recommendTable 업데이트
        _RecommendTableRepository.deleteINCREMENT();
        _RecommendTableRepository.deleteAll();

        List<RecommendTable> recommendTableList = new ArrayList<>();
        for(int i=1; i<=len; i++){
            String similarityString = "";
            for(int j=1; j<=len; j++)
                similarityString += Integer.toString(similarityArr[i][j])+",";

            RecommendTable _RecommendTable = new RecommendTable(similarityString);
            recommendTableList.add(_RecommendTable);
        }

        _RecommendTableRepository.saveAll(recommendTableList);
        return _RecommendTableRepository.findAll();
    }

    class Pair implements Comparable<Pair>{
        private int idx;
        private int similarityPoint;

        Pair(int idx, int similarityPoint){
            this.idx=idx;
            this.similarityPoint= similarityPoint;
        }

        public int compareTo(Pair p){
            return this.similarityPoint < p.similarityPoint ? 1 : -1;
        }
    }
    public Map<String, List<Lecture>> getRecommendLecture(String email) {
        Map<String, List<Lecture>> retMap = new HashMap<>();

        // 유저가 듣는 수업 확인
        List<UserLecture> userLectureList = _UserLectureRepository.findAllByEmail(email);
        if(userLectureList.size()==0){
            List<Lecture> retList = new ArrayList<>();
            retMap.put("response",retList);
            return retMap;
        }

        // 유저 전공확인
        String userMajor = _UserInfoRepository.findByEmail(email).getMajor();

        // 유저 기존 수업시간 확인
        int[] userClassTime = new int[336];

        // 유사도배열 할당
        int len = (int) _AllLectureRepository.count();
        int[] similarityArr = new int[len+1];

        for(int i=0; i<userLectureList.size(); i++){

            // 유사도배열 값 추가
            String similarityString = _RecommendTableRepository
                    .getById(userLectureList.get(i).getLectureIdx())
                    .getSimilarityString();
            StringTokenizer st = new StringTokenizer(similarityString);
            for(int j=1; j<=len; j++){
                String s = st.nextToken(",");
                similarityArr[j] += Integer.parseInt(s);
            }

            // 기존 수업시간 추가
            String classTime = _AllLectureRepository
                    .getById((long) userLectureList.get(i).getLectureIdx())
                    .getClasstime();
            classTime.replaceAll("-",",");
            st = new StringTokenizer(classTime);
            while(st.hasMoreTokens()){
                int time = Integer.parseInt(st.nextToken(","));
                userClassTime[time] = 1;
            }
        }

        // 유사도 높은순 정렬
        PriorityQueue<Pair> pq = new PriorityQueue<>();
        for(int i=1; i<=len; i++)
            pq.add(new Pair(i,similarityArr[i]));

        // 상위 4개 추출
        List<Lecture> recommendLectureList = new ArrayList<>();
        while(!pq.isEmpty() && recommendLectureList.size() < 4){
            int lectureIdx = pq.peek().idx;
            int similarityPoint = pq.peek().similarityPoint;
            pq.remove();
            if(similarityPoint==0) continue;

            Lecture _Lecture = _AllLectureRepository.getById((long) lectureIdx);
            String major = _Lecture.getDepartment();
            String classTime = _Lecture.getClasstime();
            classTime.replaceAll(",","-");

            // 이미 듣고있는수업 체크
            boolean flag = false;
            for(int i=0; i<userLectureList.size() && !flag; i++){
                if(userLectureList.get(i).getLectureIdx() == lectureIdx)
                    flag = true;
            }
            if(flag) continue;

            // 수업시간 중복 체크
            if(classTime.equals("-")) continue;
            StringTokenizer st = new StringTokenizer(classTime);
            while(st.hasMoreTokens() && !flag){
                int start = Integer.parseInt(st.nextToken("-"));
                int end = Integer.parseInt(st.nextToken("-"));

                for(int i=start; i<=end && !flag; i++)
                    flag = userClassTime[i]==1 ? true : false;
            }
            if(flag) continue;

            // 타과전공 체크
            if(!major.equals("교양") && !major.equals("일선") && !major.equals(userMajor))
                continue;

            recommendLectureList.add(_Lecture);
        }

        retMap.put("response",recommendLectureList);
        return retMap;
    }
}
