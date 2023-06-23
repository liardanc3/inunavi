package com.maru.inunavi.recommend.service;

import com.maru.inunavi.lecture.domain.entity.Lecture;
import com.maru.inunavi.lecture.repository.LectureRepository;
import com.maru.inunavi.recommend.repository.RecommendRepository;
import com.maru.inunavi.recommend.domain.entity.Recommend;
import com.maru.inunavi.user.domain.entity.UserInfo;
import com.maru.inunavi.user.domain.entity.UserLecture;
import com.maru.inunavi.user.repository.UserInfoRepository;
import com.maru.inunavi.user.repository.UserLectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RecommendService {

    private final UserInfoRepository userInfoRepository;
    private final UserLectureRepository userLectureRepository;
    private final LectureRepository LectureRepository;
    private final RecommendRepository recommendRepository;

    public List<Recommend> updateRecommend(){

        recommendRepository.deleteAll();
        recommendRepository.deleteINCREMENT();

        List<Lecture> lectureList = LectureRepository.findAll();
        List<UserInfo> userInfoList = userInfoRepository.findAll();

        // 수업 개수 + 유사도행렬
        int len = lectureList.size();
        int[][] similarityArr = new int[len+1][len+1];

        // 유저 수만큼 반복
        for (UserInfo userInfo : userInfoList) {
            // 유저 이메일
            String userEmail = userInfo.getEmail();

            // 유저 수강정보 리스트
            List<UserLecture> userLectureList = userLectureRepository.findAllByEmail(userEmail);

            // 유저 수강정보 리스트 벡터추출
            List<Integer> userLectureIdxList = new ArrayList<>();
            for (UserLecture userLecture : userLectureList)
                userLectureIdxList.add(userLecture.getLectureIdx());

            // 유사도행렬 추가
            for (int j = 0; j < userLectureIdxList.size() - 1; j++) {
                int now = userLectureIdxList.get(j);
                for (int k = j + 1; k < userLectureIdxList.size(); k++) {
                    if (j == k) continue;

                    int next = userLectureIdxList.get(k);
                    similarityArr[now][next]++;
                    similarityArr[next][now]++;
                }
            }
        }

        // recommendTable 업데이트

        List<Recommend> recommendList = new ArrayList<>();
        for(int i=1; i<=len; i++){
            String similarityString = "";
            for(int j=1; j<=len; j++)
                similarityString += Integer.toString(similarityArr[i][j])+",";

            Recommend recommend = new Recommend(similarityString);
            recommendList.add(recommend);
        }

        recommendRepository.saveAll(recommendList);
        return recommendRepository.findAll();
    }

    public class Pair implements Comparable<Pair>{
        public int idx;
        public int similarityPoint;

        Pair(int idx, int similarityPoint){
            this.idx=idx;
            this.similarityPoint= similarityPoint;
        }

        public int compareTo(Pair p){
            return this.similarityPoint < p.similarityPoint ? 1 : -1;
        }
    }
    public Map<String, List<Map<String, String>>> getRecommendLecture(String email) {
        Map<String, List<Map<String, String>>> retMap = new HashMap<>();

        List<Map<String, String>> recommendLectureList = new ArrayList<>();

        // 유저가 듣는 수업 확인
        List<UserLecture> userLectureList = userLectureRepository.findAllByEmail(email);
        if(userLectureList.size()==0){
            retMap.put("response",recommendLectureList);
            return retMap;
        }

        // 유저 전공확인
        String userMajor = userInfoRepository.findByEmail(email).getMajor();

        // 유저 기존 수업시간 확인
        int[] userClassTime = new int[336];

        // 유사도배열 할당
        int len = (int) LectureRepository.count();
        int[] similarityArr = new int[len+1];
        for (UserLecture userLecture : userLectureList) {

            // 유사도배열 값 추가
            String similarityString = recommendRepository
                    .getById(userLecture.getLectureIdx())
                    .getSimilarityString();
            StringTokenizer st = new StringTokenizer(similarityString);
            for (int j = 1; j <= len; j++) {
                String s = st.nextToken(",");
                similarityArr[j] += Integer.parseInt(s);
            }

            // 기존 수업시간 추가
            Lecture lecture = LectureRepository.getById(userLecture.getLectureIdx());
            String classTime = lecture.getClasstime();

            classTime = classTime.replaceAll(",", "-");

            if(!classTime.equals("-")){
                st = new StringTokenizer(classTime);
                while (st.hasMoreTokens()) {
                    int _start = Integer.parseInt(st.nextToken("-"));
                    int _end = Integer.parseInt(st.nextToken("-"));
                    for(int time=_start; time<=_end; time++)
                        userClassTime[time] = 1;
                }
            }
        }

        // 유사도 높은순 정렬
        PriorityQueue<Pair> pq = new PriorityQueue<>();
        for(int i=1; i<=len; i++){
            if(similarityArr[i] < 1) continue;
            Lecture lecture = LectureRepository.getById(i);
            if(lecture.getDepartment().equals(userMajor))
                pq.add(new Pair(i,similarityArr[i]/2));
            else pq.add(new Pair(i,similarityArr[i]));
        }


        // 상위 4개 추출
        while(!pq.isEmpty() && recommendLectureList.size() < 4){
            int lectureIdx = pq.peek().idx;
            int similarityPoint = pq.peek().similarityPoint;
            pq.remove();
            if(similarityPoint==0) continue;

            Lecture lecture = LectureRepository.getById(lectureIdx);
            String major = lecture.getDepartment();

            String classTime = lecture.getClasstime();
            String className = lecture.getLectureName();
            classTime = classTime.replaceAll(",","-");

            // 이미 듣고있는수업 + 이미 듣고있는 수업명 동일한지 체크
            boolean flag = false;
            for(int i=0; i<userLectureList.size() && !flag; i++){
                if(userLectureList.get(i).getLectureIdx() == lectureIdx)
                    flag = true;
                if(className.equals(LectureRepository
                        .getById(userLectureList.get(i)
                                .getLectureIdx()).getLectureName()))
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
                    flag = (userClassTime[i] == 1);
            }
            if(flag) continue;

            // 타과전공 체크
            if(!major.equals("교양") && !major.equals("일선") && !major.equals(userMajor))
                continue;

            Map<String, String> rretMap = new HashMap<>();
            rretMap.put("id", Integer.toString(lecture.getId()));
            rretMap.put("department", lecture.getDepartment());
            rretMap.put("grade", lecture.getGrade());
            rretMap.put("category", lecture.getCategory());
            rretMap.put("number", lecture.getNumber());
            rretMap.put("lecturename", lecture.getLectureName());
            rretMap.put("professor", lecture.getProfessor());
            rretMap.put("classroom_raw", lecture.getClassroom_raw());
            rretMap.put("classtime_raw", lecture.getClasstime_raw());
            String __CLASSROOM__ = lecture.getClassroom();
            if(__CLASSROOM__.length()>2)
                rretMap.put("classroom",__CLASSROOM__.substring(0,__CLASSROOM__.length()-1));
            else rretMap.put("classroom",__CLASSROOM__);
            rretMap.put("classtime", lecture.getClasstime());
            rretMap.put("how", lecture.getHow());
            rretMap.put("point", lecture.getPoint());

            String __CLASSTIME__ = lecture.getClasstime();
            String __RETCLASSTIME__ = "";
            if (__CLASSTIME__.equals("-")) {
                rretMap.put("realTime", "-");
                recommendLectureList.add(rretMap);
            }
            else {
                __CLASSTIME__ = __CLASSTIME__.replaceAll(",", "-");
                StringTokenizer sst = new StringTokenizer(__CLASSTIME__);
                while (sst.hasMoreTokens()) {
                    int start = Integer.parseInt(sst.nextToken("-"));
                    int end = Integer.parseInt(sst.nextToken("-"));

                    int dayOfWeek = start / 48;
                    int startHour = (start % 48) / 2;
                    int startHalf = (start % 2);
                    int endHour = (end % 48) / 2 + 1;
                    int endHalf = ~(end % 2);

                    switch (dayOfWeek) {
                        case 0: __RETCLASSTIME__ += "월 ";break;
                        case 1: __RETCLASSTIME__ += "화 ";break;
                        case 2: __RETCLASSTIME__ += "수 ";break;
                        case 3: __RETCLASSTIME__ += "목 ";break;
                        case 4: __RETCLASSTIME__ += "금 ";break;
                        case 5: __RETCLASSTIME__ += "토 ";break;
                        case 6: __RETCLASSTIME__ += "일 ";break;
                    }

                    __RETCLASSTIME__ += Integer.toString(startHour);
                    __RETCLASSTIME__ += ":";
                    __RETCLASSTIME__ += startHalf == 1 ? "30 - " : "00 - ";
                    __RETCLASSTIME__ += Integer.toString(endHour);
                    __RETCLASSTIME__ += ":";
                    __RETCLASSTIME__ += endHalf == 1 ? "30, " : "00, ";
                }
                __RETCLASSTIME__ = __RETCLASSTIME__.substring(0, __RETCLASSTIME__.length() - 2);
                rretMap.put("realTime", __RETCLASSTIME__);
                recommendLectureList.add(rretMap);
            }
        }
        retMap.put("response",recommendLectureList);
        return retMap;
    }
}
