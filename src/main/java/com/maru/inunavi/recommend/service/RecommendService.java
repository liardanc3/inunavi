package com.maru.inunavi.recommend.service;

import com.maru.inunavi.lecture.domain.dto.FormattedTimeDto;
import com.maru.inunavi.lecture.domain.entity.Lecture;
import com.maru.inunavi.lecture.repository.LectureRepository;
import com.maru.inunavi.recommend.repository.RecommendRepository;
import com.maru.inunavi.recommend.domain.entity.Recommend;
import com.maru.inunavi.user.domain.entity.User;
import com.maru.inunavi.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendService {

    private final UserRepository userRepository;
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

    /**
     * Retrieves recommend lecture
     * @param email
     * @return A List of FormattedTimeDto
     */
    public List<FormattedTimeDto> getRecommendLecture(String email) {
        return userRepository.findLecturesByEmail(email)
                .map(lectureList -> {

                    class Pair implements Comparable<Pair>{

                        public int id;
                        public int similarityPoint;

                        Pair(int id, int similarityPoint){
                            this.id = id;
                            this.similarityPoint = similarityPoint;
                        }

                        public int compareTo(Pair p){
                            return this.similarityPoint < p.similarityPoint ? 1 : 0;
                        }
                    }

                    List<FormattedTimeDto> FormattedTimeDtoList = new ArrayList<>();

                    int count = (int) lectureRepository.count();

                    int[] similarityPoint = new int[count + 1];
                    boolean[] attendingTime = new boolean[count + 1];
                    boolean[] attendingLectureId = new boolean[count + 1];

                    userRepository.findLecturesByEmail(email)
                            .ifPresent(lectures ->
                                    lectures.stream()
                                            .forEach(lecture -> {
                                                attendingLectureId[lecture.getId()] = true;

                                                String[] classTimeList = lecture.getClassTime().split(",");

                                                for (String classTime : classTimeList) {
                                                    int[] timeRange = Arrays.stream(classTime.split("-"))
                                                            .mapToInt(Integer::parseInt)
                                                            .toArray();

                                                    for(int i = timeRange[0]; i <= timeRange[1]; i++) {
                                                        attendingTime[i] = true;
                                                    }
                                                }
                                    })
                            );

                    recommendRepository.findRecommendsByEmail(email)
                            .stream()
                            .forEach(similarity -> {
                                String[] similarArr = similarity.split(",");

                                for(int i = 1; i <= similarArr.length; i++){
                                    similarityPoint[i] += Integer.parseInt(similarArr[i-1]);
                                }
                            });

                    PriorityQueue<Pair> priorityQueue = new PriorityQueue<>();
                    
                    for (int i = 1; i < similarityPoint.length; i++) {
                        if(similarityPoint[i] != 0){
                            priorityQueue.add(new Pair(i, similarityPoint[i]));
                        }
                    }

                    User user = userRepository.findByEmail(email).get();

                    while(!priorityQueue.isEmpty() && FormattedTimeDtoList.size() < 4){
                        Pair pair = priorityQueue.poll();

                        Lecture lecture = lectureRepository.findById(pair.id).get();

                        boolean isAttendingLectureId = attendingLectureId[lecture.getId()];
                        boolean isAttendingTime =
                                Arrays.stream(lecture.getClassTime().split(","))
                                        .anyMatch(classTime -> {
                                            int start = Integer.parseInt(classTime.split("-")[0]);
                                            int end = Integer.parseInt(classTime.split("-")[1]);

                                            return IntStream.rangeClosed(start, end)
                                                    .anyMatch(i -> attendingTime[i]);
                                });
                        boolean isMatchWithMajor = lecture.getDepartment() != user.getMajor() &&
                                        !lecture.getDepartment().equals("교양") && !lecture.getDepartment().equals("일선");

                        if(!isAttendingLectureId && !isAttendingTime && !isMatchWithMajor){
                            FormattedTimeDtoList.add(new FormattedTimeDto(lecture));
                        }
                    }

                    return FormattedTimeDtoList;
                })
                .orElseGet(ArrayList::new);
    }
}
