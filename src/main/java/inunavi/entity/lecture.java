package inunavi.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;
import java.util.List;
import java.util.StringTokenizer;

@Builder
@Entity(name="lectureTable")
@AllArgsConstructor
@Getter
public class lecture {

    // A_ 순번
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // C_학과(부)
    @Column(length = 45, nullable = false)
    private String department;

    // D_학년
    @Column(length = 45, nullable = false)
    private String grade;

    // E_이수구분
    @Column(length = 45, nullable = false)
    private String category;

    // F_학수번호
    @Column(length = 45, nullable = true)
    private String number;

    // G_교과목명
    @Column(length = 45, nullable = true)
    private String lecturename;

    // H_담당교수
    @Column(length = 45, nullable = true)
    private String professor;

    // I_강의실
    @Column(length = 45, nullable = true)
    private String classroom;

    // J_시간표
    @Column(length = 45, nullable = true)
    private String classtime;

    // K_수업방법
    @Column(length = 45, nullable = true)
    private String how;

    // L_학점
    @Column(length = 45, nullable = true)
    private String point;


    public lecture() {}

    public lecture(List<String> csv) {
        this.department = csv.get(0);
        this.grade = csv.get(1);
        this.category = csv.get(2);
        this.number = csv.get(3);
        this.lecturename = csv.get(4);
        this.professor = csv.get(5);
        this.classroom = csv.get(6);
        this.classtime = csv.get(7);
        this.how = csv.get(8);
        this.point = csv.get(9);
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
