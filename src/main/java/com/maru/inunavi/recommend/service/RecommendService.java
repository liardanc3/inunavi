package com.maru.inunavi.recommend.service;

import com.maru.inunavi.lecture.domain.entity.Lecture;
import com.maru.inunavi.lecture.repository.LectureRepository;
import com.maru.inunavi.recommend.repository.RecommendRepository;
import com.maru.inunavi.recommend.domain.entity.Recommend;
import com.maru.inunavi.user.domain.entity.User;
import com.maru.inunavi.user.domain.entity.UserLectureTable;
import com.maru.inunavi.user.repository.UserRepository;
import com.maru.inunavi.user.repository.UserLectureTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendService {

    private final UserRepository userRepository;
    private final UserLectureTableRepository userLectureTableRepository;
    private final LectureRepository lectureRepository;
    private final RecommendRepository recommendRepository;

    /**
     * Reset recommend table
     */
    @Transactional
    public void resetRecommends() {
        recommendRepository.deleteAll();
        recommendRepository.deleteINCREMENT();

        StringBuilder similarity = new StringBuilder();
        long count = lectureRepository.count();
        while (count-- > 0) {
            similarity.append("0,");
        }

        lectureRepository.findAll()
                .forEach(lecture ->
                        recommendRepository.save(
                                Recommend.builder()
                                        .similarity(similarity.toString())
                                        .build()
                        )
                );
    }

    public List<Recommend> updateRecommend1(){

        recommendRepository.deleteAll();
        recommendRepository.deleteINCREMENT();

        List<Lecture> lectureList = lectureRepository.findAll();
        List<User> userList = userRepository.findAll();

        // 수업 개수 + 유사도행렬
        int len = lectureList.size();
        int[][] similarityArr = new int[len+1][len+1];

        // 유저 수만큼 반복
        for (User user : userList) {
            // 유저 이메일
            String userEmail = user.getEmail();

            // 유저 수강정보 리스트
            List<UserLectureTable> userLectureTableList = userLectureTableRepository.findAllByEmail(userEmail);

            // 유저 수강정보 리스트 벡터추출
            List<Integer> userLectureIdxList = new ArrayList<>();
            for (UserLectureTable userLectureTable : userLectureTableList)
                userLectureIdxList.add(userLectureTable.getLectureIdx());

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
        List<UserLectureTable> userLectureTableList = userLectureTableRepository.findAllByEmail(email);
        if(userLectureTableList.size()==0){
            retMap.put("response",recommendLectureList);
            return retMap;
        }

        // 유저 전공확인
        String userMajor = userRepository.findByEmail(email).getMajor();

        // 유저 기존 수업시간 확인
        int[] userclassTime = new int[336];

        // 유사도배열 할당
        int len = (int) lectureRepository.count();
        int[] similarityArr = new int[len+1];
        for (UserLectureTable userLectureTable : userLectureTableList) {

            // 유사도배열 값 추가
            String similarityString = recommendRepository
                    .getById(userLectureTable.getLectureIdx())
                    .getSimilarity();
            StringTokenizer st = new StringTokenizer(similarityString);
            for (int j = 1; j <= len; j++) {
                String s = st.nextToken(",");
                similarityArr[j] += Integer.parseInt(s);
            }

            // 기존 수업시간 추가
            Lecture lecture = lectureRepository.getById(userLectureTable.getLectureIdx());
            String classTime = lecture.getClassTime();

            classTime = classTime.replaceAll(",", "-");

            if(!classTime.equals("-")){
                st = new StringTokenizer(classTime);
                while (st.hasMoreTokens()) {
                    int _start = Integer.parseInt(st.nextToken("-"));
                    int _end = Integer.parseInt(st.nextToken("-"));
                    for(int time=_start; time<=_end; time++)
                        userclassTime[time] = 1;
                }
            }
        }

        // 유사도 높은순 정렬
        PriorityQueue<Pair> pq = new PriorityQueue<>();
        for(int i=1; i<=len; i++){
            if(similarityArr[i] < 1) continue;
            Lecture lecture = lectureRepository.getById(i);
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

            Lecture lecture = lectureRepository.getById(lectureIdx);
            String major = lecture.getDepartment();

            String classTime = lecture.getClassTime();
            String className = lecture.getLectureName();
            classTime = classTime.replaceAll(",","-");

            // 이미 듣고있는수업 + 이미 듣고있는 수업명 동일한지 체크
            boolean flag = false;
            for(int i = 0; i< userLectureTableList.size() && !flag; i++){
                if(userLectureTableList.get(i).getLectureIdx() == lectureIdx)
                    flag = true;
                if(className.equals(lectureRepository
                        .getById(userLectureTableList.get(i)
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
                    flag = (userclassTime[i] == 1);
            }
            if(flag) continue;

            // 타과전공 체크
            if(!major.equals("교양") && !major.equals("일선") && !major.equals(userMajor))
                continue;

            Map<String, String> rretMap = new HashMap<>();
            rretMap.put("id", Long.toString(lecture.getId()));
            rretMap.put("department", lecture.getDepartment());
            rretMap.put("grade", lecture.getGrade());
            rretMap.put("category", lecture.getCategory());
            rretMap.put("number", lecture.getNumber());
            rretMap.put("lecturename", lecture.getLectureName());
            rretMap.put("professor", lecture.getProfessor());
            rretMap.put("ClassRoomRaw", lecture.getClassRoomRaw());
            rretMap.put("classTimeRaw", lecture.getClassTimeRaw());
            String __ClassRoom__ = lecture.getClassRoom();
            if(__ClassRoom__.length()>2)
                rretMap.put("ClassRoom",__ClassRoom__.substring(0,__ClassRoom__.length()-1));
            else rretMap.put("ClassRoom",__ClassRoom__);
            rretMap.put("classTime", lecture.getClassTime());
            rretMap.put("how", lecture.getHow());
            rretMap.put("point", lecture.getPoint());

            String __classTime__ = lecture.getClassTime();
            String __RETclassTime__ = "";
            if (__classTime__.equals("-")) {
                rretMap.put("realTime", "-");
                recommendLectureList.add(rretMap);
            }
            else {
                __classTime__ = __classTime__.replaceAll(",", "-");
                StringTokenizer sst = new StringTokenizer(__classTime__);
                while (sst.hasMoreTokens()) {
                    int start = Integer.parseInt(sst.nextToken("-"));
                    int end = Integer.parseInt(sst.nextToken("-"));

                    int dayOfWeek = start / 48;
                    int startHour = (start % 48) / 2;
                    int startHalf = (start % 2);
                    int endHour = (end % 48) / 2 + 1;
                    int endHalf = ~(end % 2);

                    switch (dayOfWeek) {
                        case 0: __RETclassTime__ += "월 ";break;
                        case 1: __RETclassTime__ += "화 ";break;
                        case 2: __RETclassTime__ += "수 ";break;
                        case 3: __RETclassTime__ += "목 ";break;
                        case 4: __RETclassTime__ += "금 ";break;
                        case 5: __RETclassTime__ += "토 ";break;
                        case 6: __RETclassTime__ += "일 ";break;
                    }

                    __RETclassTime__ += Integer.toString(startHour);
                    __RETclassTime__ += ":";
                    __RETclassTime__ += startHalf == 1 ? "30 - " : "00 - ";
                    __RETclassTime__ += Integer.toString(endHour);
                    __RETclassTime__ += ":";
                    __RETclassTime__ += endHalf == 1 ? "30, " : "00, ";
                }
                __RETclassTime__ = __RETclassTime__.substring(0, __RETclassTime__.length() - 2);
                rretMap.put("realTime", __RETclassTime__);
                recommendLectureList.add(rretMap);
            }
        }
        retMap.put("response",recommendLectureList);
        return retMap;
    }
}
