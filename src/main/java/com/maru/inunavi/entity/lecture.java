package com.maru.inunavi.entity;

import com.maru.inunavi.repository.lectureRepository;
import lombok.*;

import javax.persistence.*;
import java.util.StringTokenizer;

@Builder
@Entity(name="lectureTable")
@AllArgsConstructor
@Getter
public class lecture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // 학년
    @Column(length = 45, nullable = false)
    private String age;
    // 이수구분
    @Column(length = 45, nullable = false)
    private String category;
    // 학수번호
    @Column(length = 45, nullable = false)
    private String number;
    // 교과목명
    @Column(length = 45, nullable = true)
    private String lectureName;
    // 학점
    @Column(length = 45, nullable = true)
    private String point;
    // 교수명
    @Column(length = 45, nullable = true)
    private String professor;
    // 요일 및 시간1
    @Column(length = 45, nullable = true)
    private String daytime1;
    // 요일 및 시간2
    @Column(length = 45, nullable = true)
    private String daytime2;
    // 요일 및 시간3
    @Column(length = 45, nullable = true)
    private String daytime3;
    // 강의실1
    @Column(length = 45, nullable = true)
    private String position1;
    // 강의실2
    @Column(length = 45, nullable = true)
    private String position2;
    // 강의실3
    @Column(length = 45, nullable = true)
    private String position3;

    // 수강대상
    @Column(length = 45, nullable = true)
    private String who;

    // 수업방법
    @Column(length = 45, nullable = true)
    private String how;

    // 비고
    @Column(length = 45, nullable = true)
    private String etc;

    // 생성자
    public lecture(String age, String category, String number, String lectureName, String point,
                   String professor, String daytime1, String daytime2, String daytime3, String position1, String position2, String position3, String who, String how, String etc) {
        this.age = age;
        this.category = category;
        this.number = number;
        this.lectureName = lectureName;
        this.point = point;
        this.professor = professor;
        this.daytime1 = Daytime2Int(daytime1);
        this.daytime2 = Daytime2Int(daytime2);
        this.daytime3 = Daytime2Int(daytime3);
        this.position1 = position1;
        this.position2 = position2;
        this.position3 = position3;
        this.who = who;
        this.how = how;
        this.etc = etc;
    }

    public lecture() {

    }

    // 요일 및 시간 정수화
    public String Daytime2Int(String daytime) {

        // 요일 시간 없을 때
        if(daytime.equals("-")){
            return daytime;
        }

        // 시간 초기값 설정(30분 분할, 월요일 오전 9시 기준 0, 화요일 오전 9시 = 48)
        final char [] dayARR = {'월','화','수','목','금','토'};
        int t = 0; int start=99999; int end=0;
        for(int i=0; i<7; i++){
            if(dayARR[i]==daytime.charAt(0)){
                t = i*47+18;
                break;
            }
        }
        // 야간여부 확인
        boolean flag = false;
        for(int i=0; i<daytime.length(); i++){
            if(daytime.charAt(i)=='야'){
                flag=true;
                break;
            }
        }
        if(flag) t+=18;

        // 정수화
        String _daytime = daytime.substring(1,daytime.length()-1);
        StringTokenizer s = new StringTokenizer(_daytime);
        while(s.hasMoreTokens()){
            String ss = s.nextToken(" -야");
            System.out.println(ss);

            // 숫자만 입력된 경우 (7 8 9 등)
            if(ss.length()==1){
                int now = Integer.parseInt(ss);
                start = Math.min(start,t+(now-1)*2);
                end = Math.max(end,t+(now-1)*2+1);
            }

            // 섞인 경우 (8A 8B 등)
            else{
                int now = Integer.parseInt(ss.substring(0,1));
                start = Math.min(start,t+(now-1)*2+1);
                end = Math.max(end,t+(now-1)*2);
            }
        }

        // ex "0,5" -> (월 1 2 3)
        String _start = Integer.toString(start);
        String _end = Integer.toString(end);
        return _start + "," + _end;
    }
}
