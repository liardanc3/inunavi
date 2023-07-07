package com.maru.inunavi.lecture.domain.entity;

import com.maru.inunavi.recommend.domain.entity.Recommend;
import com.maru.inunavi.user.domain.entity.User;
import lombok.*;
import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Builder
@AllArgsConstructor
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Lecture {

    // 순번
    @Id
    @GeneratedValue
    @Column(name = "lecture_id")
    private Integer id;

    // 학과(부)
    @Column(length = 45, nullable = false)
    private String department;

    // 학년
    @Column(length = 45, nullable = false)
    private String grade;

    // 이수구분
    @Column(length = 45, nullable = false)
    private String category;

    // 학수번호
    @Column(length = 45)
    private String number;

    // 교과목명
    @Column(length = 45)
    private String lectureName;

    // 담당교수
    @Column(length = 100)
    private String professor;

    // 강의실(raw)
    @Column(length = 200)
    private String classRoomRaw;

    // 시간표(raw)
    @Column(length = 200)
    private String classTimeRaw;

    // 시간표
    @Column(length = 200)
    private String classRoom;
    @Column(length = 200)
    private String classTime;

    // 수업방법
    @Column(length = 100)
    private String how;

    // 학점
    @Column(length = 45)
    private String point;

    // placeCode
    @Column(length = 45)
    private String placeCode;

    @ManyToMany(mappedBy = "lectures")
    private Set<User> users = new HashSet<>();
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recommend_id")
    private Recommend recommend;

    // ------------------------------------------------ //

    public void addUser(User user){
        users.add(user);
        user.getLectures().add(this);
    }

    public List<Integer> getStartTimeList(){
        return classTime.equals("-") ? null :
                Arrays.stream(classTime.split(","))
                        .map(classTimeToken -> Integer.parseInt(classTimeToken.split("-")[0]))
                        .collect(Collectors.toList());
    }

    public String getFormattedTime(){
        if (this.classTime.equals("-")) {
            return "-";
        }
        else {
            StringBuilder classTime = new StringBuilder();

            StringTokenizer classTimeToken = new StringTokenizer(this.classTime.replaceAll(",", "-"));
            while (classTimeToken.hasMoreTokens()) {
                int start = Integer.parseInt(classTimeToken.nextToken("-"));
                int end = Integer.parseInt(classTimeToken.nextToken("-"));

                int dayOfWeek = start / 48;
                int startHour = (start % 48) / 2;
                int startHalf = (start % 2);
                int endHour = (end % 48) / 2 + 1;
                int endHalf = ~(end % 2);

                switch (dayOfWeek) {
                    case 0: classTime.append("월 "); break;
                    case 1: classTime.append("화 "); break;
                    case 2: classTime.append("수 "); break;
                    case 3: classTime.append("목 "); break;
                    case 4: classTime.append("금 "); break;
                    case 5: classTime.append("토 "); break;
                    case 6: classTime.append("일 "); break;
                }

                classTime.append(startHour);
                classTime.append(":");
                classTime.append(startHalf == 1 ? "30 - " : "00 - ");
                classTime.append(endHour);
                classTime.append(":");
                classTime.append(endHalf == 1 ? "30, " : "00, ");
            }
            return classTime.substring(0, classTime.length() - 2);
        }
    }
}
