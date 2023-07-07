package com.maru.inunavi.user.service;

import com.maru.inunavi.lecture.domain.dto.FormattedTimeDto;
import com.maru.inunavi.lecture.domain.entity.Lecture;
import com.maru.inunavi.lecture.repository.LectureRepository;
import com.maru.inunavi.recommend.repository.RecommendRepository;
import com.maru.inunavi.user.domain.dto.LoginResultDto;
import com.maru.inunavi.user.domain.dto.UpdateDto;
import com.maru.inunavi.user.domain.dto.VerifyDto;
import com.maru.inunavi.user.domain.entity.User;
import com.maru.inunavi.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final LectureRepository LectureRepository;
    private final RecommendRepository recommendRepository;

    private final JavaMailSender javaMailSender;

    /**
     * Register user as member
     * @param email
     * @param password
     * @param major
     * @return SignUpDto
     */
    @Transactional
    public UpdateDto signUp(String email, String password, String major){
        return userRepository.findByEmail(email)
                .map(user -> {
                    userRepository.save(
                            User.builder()
                                    .email(email)
                                    .password(new BCryptPasswordEncoder().encode(password))
                                    .major(major)
                                    .build()
                    );

                    return UpdateDto.builder()
                            .email(email)
                            .success("success")
                            .build();
                })
                .orElseGet(() ->
                        UpdateDto
                                .builder()
                                .email(email)
                                .success("false")
                                .build()
                );
    }

    /**
     * Retrieves user's lecture list
     * @param email
     * @return A List of FormattedTimeDto
     */
    public List<FormattedTimeDto> userLectureList(String email) {
        return userRepository.findLecturesByEmail(email)
                .map(lectures ->
                        lectures.stream()
                                .map(FormattedTimeDto::new)
                                .collect(Collectors.toList())
                )
                .orElseGet(ArrayList::new);
    }

    /**
     * Duplicate check
     * @param email
     * @return SignUpDto
     */
    public UpdateDto idCheck(String email){
        return userRepository.findByEmail(email)
                .map(user ->
                        UpdateDto.builder()
                                .email(email)
                                .success("true")
                                .build()
                )
                .orElseGet(() ->
                        UpdateDto
                                .builder()
                                .email(email)
                                .success("false")
                                .build()
                );
    }

    /**
     * Login
     * @param email
     * @param password
     * @return LoginResultDto
     */
    public LoginResultDto login(String email, String password) {
        return userRepository.findByEmail(email)
                .map(user -> {
                    if (new BCryptPasswordEncoder().matches(password, user.getPassword())) {
                        return LoginResultDto.builder()
                                .email(email)
                                .success("success")
                                .message("로그인 성공")
                                .major(user.getMajor())
                                .build();
                    } else {
                        return LoginResultDto.builder()
                                .email(email)
                                .success("false")
                                .message("비밀번호 오류")
                                .build();
                    }
                })
                .orElse(
                        LoginResultDto.builder()
                                .email(email)
                                .success("false")
                                .message("아이디가 틀림")
                                .build()
                );
    }

    /**
     * Send email to update password when forgot password
     * @param email
     * @return VerifyDto
     */
    public VerifyDto verify(String email){
        try {
            String code = UUID.randomUUID().toString().substring(0, 7);

            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setTo(email);
            simpleMailMessage.setSubject("verify");
            simpleMailMessage.setText(code);

            javaMailSender.send(simpleMailMessage);

            return VerifyDto.builder()
                    .success("true")
                    .code(code)
                    .build();
        } catch (Exception e) {

            return VerifyDto.builder()
                    .success("false")
                    .message(e.getMessage())
                    .build();
        }
    }

    @Transactional
    public UpdateDto updatePassword(String email, String password){
        return userRepository.save(
                    User.builder()
                            .email(email)
                            .password(new BCryptPasswordEncoder().encode((password)))
                            .build();
            );
            json.put("success", "true");
        }else{
            json.put("success","false");
        }
        return json;
    }











    @Transactional
    public Map<String, String> AddLecture(String email, String lectureId) {

        Map<String, String> json = new HashMap<>();

        // lectureId -> lectureIdx
        Long lectureIdx = LectureRepository.findByNumber(lectureId).getId();

        json.put("email", email);
        if(userLectureTableRepository.findByEmailAndLectureIdx(email, Math.toIntExact(lectureIdx)) == null){
            userLectureTableRepository.save(new UserLectureTable(email, Math.toIntExact(lectureIdx)));
            json.put("success", "true");
            updateRecommendTable(email, Math.toIntExact(lectureIdx),true);
        }else{
            json.put("success", "false");
        }

        User user = userRepository.findByEmail(email);
        Lecture lecture = LectureRepository.findByNumber(lectureId);
        System.out.println("user.getId() = " + user.getId());
        System.out.println("lecture.getId() = " + lecture.getId());
        lecture.getUsers().add(user);
        user.getLectures().add(lecture);
        return json;
    }

    public Map<String, String> deleteLecture(String email, String lectureID){
        Map<String, String> json = new HashMap<>();

        Long lectureIdx = LectureRepository.findByNumber(lectureID).getId();

        json.put("email", email);
        UserLectureTable userLectureTable = userLectureTableRepository.findByEmailAndLectureIdx(email, Math.toIntExact(lectureIdx)) ;
        if(userLectureTable != null){
            json.put("success", "true");
            updateRecommendTable(email, Math.toIntExact(lectureIdx),false);
            userLectureTableRepository.delete(userLectureTable);
        }
        else {
            json.put("success", "false");
        }
        return json;
    }



    public Map<String, String> updateMajor(String email, String major){
        Map<String, String> json = new HashMap<>();
        json.put("email", email);
        User user = userRepository.findByEmail(email);
        if (user != null){
            userRepository.save(
                    User.builder()
                            .email(email)
                            .password(user.getPassword())
                            .major(major)
                            .build()
            );
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
        User user = userRepository.findByEmail(email);
        if (user == null){
            json.put("success", "false");
        }else if (!passwordencoder.matches(password, user.getPassword())){
            json.put("success","false");
        }else{
            userRepository.delete(user);
            List<UserLectureTable> userLectureTableList = userLectureTableRepository.findAllByEmail(email);
            for(int i = 0; i< userLectureTableList.size(); i++){
                updateRecommendTable(email, userLectureTableList.get(i).getLectureIdx(),false);
                userLectureTableRepository.delete(userLectureTableList.get(i));
            }
            json.put("success","true");
        }
        return json;
    }

    public void updateRecommendTable(String email, int lectureIdx, boolean add){
        List<UserLectureTable> userLectureTableList = userLectureTableRepository.findAllByEmail(email);

        // 유저가 듣는 수업의 인덱스 리스트
        List<Integer> userLectureIdxList = new ArrayList<>();
        for(int i = 0; i< userLectureTableList.size(); i++)
            userLectureIdxList.add(userLectureTableList.get(i).getLectureIdx());

        // 유사도행렬 업데이트
        int len = (int) LectureRepository.count();
        int[][] similarityArr = new int[len+1][len+1];
        for(int i=0; i<userLectureIdxList.size(); i++){
            int idx = userLectureIdxList.get(i);
            String similarityString = recommendRepository.getById(idx).getSimilarity();
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
            recommendRepository.updateSimilarityString(idx,similarityString);
        }
    }

    public List<User> memberList() {
        return userRepository.findAll();
    }
}
