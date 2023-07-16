package com.maru.inunavi.user.service;

import com.maru.inunavi.aspect.exceptionhandler.exception.LoginProcessException;
import com.maru.inunavi.aspect.exceptionhandler.exception.SelectClassException;
import com.maru.inunavi.aspect.exceptionhandler.exception.UpdateException;
import com.maru.inunavi.aspect.exceptionhandler.exception.VerifyException;
import com.maru.inunavi.lecture.domain.dto.FormattedTimeDto;
import com.maru.inunavi.lecture.domain.entity.Lecture;
import com.maru.inunavi.lecture.repository.LectureRepository;
import com.maru.inunavi.recommend.domain.entity.Recommend;
import com.maru.inunavi.user.domain.dto.LoginResultDto;
import com.maru.inunavi.user.domain.dto.UpdateDto;
import com.maru.inunavi.user.domain.dto.VerifyDto;
import com.maru.inunavi.user.domain.entity.User;
import com.maru.inunavi.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final LectureRepository lectureRepository;

    private final JavaMailSender javaMailSender;

    /**
     * Registers a user as a member.
     * @param email
     * @param password
     * @param major
     * @return UpdateDto
     */
    @Transactional
    public UpdateDto signUp(String email, String password, String major) {
        return userRepository.isNotPresentByEmail(email)
                .map(result -> {
                    userRepository.save(
                            User.builder()
                                    .email(email)
                                    .password(new BCryptPasswordEncoder().encode(password))
                                    .major(major)
                                    .build()
                    );
                    return UpdateDto.builder()
                            .email(email)
                            .success("true")
                            .build();
                })
                .orElseThrow(() -> new UpdateException(email));
    }

    /**
     * Retrieves the user's lecture list.
     * @param email
     * @return A list of FormattedTimeDto
     */
    public List<FormattedTimeDto> userLectureList(String email) {
        return userRepository.findByEmail(email)
                .map(user ->
                        user.getLectures().stream()
                                .map(FormattedTimeDto::new)
                                .collect(Collectors.toList())
                )
                .orElseThrow(SelectClassException::new);
    }

    /**
     * Checks for duplicate email.
     * @param email
     * @return UpdateDto
     */
    public UpdateDto idCheck(String email) {
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
     * Performs user login.
     * @param email
     * @param password
     * @return LoginResultDto
     */
    public LoginResultDto login(String email, String password) {
        return userRepository.findByEmail(email)
                .map(user -> {
                    System.out.println("user = " + user);
                    System.out.println("user = " + user);
                    if (new BCryptPasswordEncoder().matches(password, user.getPassword())) {
                        return LoginResultDto.builder()
                                .email(email)
                                .success("success")
                                .message("로그인 성공")
                                .major(user.getMajor())
                                .build();
                    } else {
                        throw new LoginProcessException(email);
                    }
                })
                .orElseThrow(() -> new LoginProcessException(email));
    }

    /**
     * Sends an email to verify the user's email address to update password.
     * @param email
     * @return VerifyDto
     */
    public VerifyDto verify(String email) {
        return userRepository.findByEmail(email)
                .map(user -> {
                    String code = UUID.randomUUID().toString().substring(0, 8);

                    SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
                    simpleMailMessage.setTo(email);
                    simpleMailMessage.setSubject("INUNAVI 인증코드 발송");
                    simpleMailMessage.setText("인증코드 : " + code);

                    javaMailSender.send(simpleMailMessage);

                    return VerifyDto.builder()
                            .success("true")
                            .code(code)
                            .build();
                })
                .orElseThrow(() -> new VerifyException("Unauthorized Access"));
    }

    /**
     * Updates the user's password.
     * @param email
     * @param password
     * @return UpdateDto
     */
    @Transactional
    public UpdateDto updatePassword(String email, String password) {
        return userRepository.findByEmail(email)
                .map(user -> {
                    user.updatePassword(password);

                    return UpdateDto.builder()
                            .success("true")
                            .email(email)
                            .build();
                })
                .orElseThrow(() -> new UpdateException(email));
    }

    /**
     * Updates the user's major.
     * @param email
     * @param major
     * @return UpdateDto
     */
    @Transactional
    public UpdateDto updateMajor(String email, String major) {
        return userRepository.findByEmail(email)
                .map(user -> {
                    user.updateMajor(major);

                    return UpdateDto.builder()
                            .success("true")
                            .email(email)
                            .build();
                })
                .orElseThrow(() -> new UpdateException(email));
    }

    /**
     * Deletes the user's account.
     * @param email
     * @param password
     * @return UpdateDto
     */
    @Transactional
    public UpdateDto deleteUser(String email, String password) {
        return userRepository.findByEmail(email)
                .map(user -> {
                    if (new BCryptPasswordEncoder().matches(password, user.getPassword())) {

                        user.getLectures().forEach(lecture -> deleteLecture(email, lecture.getNumber()));
                        user.removeLectures();

                        userRepository.delete(user);

                        return UpdateDto.builder()
                                .success("true")
                                .email(email)
                                .build();
                    } else {
                        throw new UpdateException(email);
                    }
                })
                .orElseThrow(() -> new UpdateException(email));
    }

    /**
     * Inserts a lecture for the user.
     * @param email
     * @param lectureNumber
     * @return UpdateDto
     */
    @Transactional
    public UpdateDto insertLecture(String email, String lectureNumber) {
        return lectureRepository.findByNumber(lectureNumber)
                .map(lectureToAdd -> {

                    User user = userRepository.findByEmail(email)
                            .orElseThrow(() -> new UpdateException(email));

                    user.getLectures().forEach(lecture -> {
                        lecture.getRecommend().updateSimilarityPoint(lectureToAdd.getId(), 1);
                        lectureToAdd.getRecommend().updateSimilarityPoint(lecture.getId(), 1);
                    });
                    user.addLecture(lectureToAdd);

                    return UpdateDto.builder()
                            .success("true")
                            .email(email)
                            .build();
                })
                .orElseThrow(() -> new UpdateException(email));
    }

    /**
     * Deletes a lecture from the user's lecture list.
     * @param email
     * @param lectureNumber
     * @return UpdateDto
     */
    @Transactional
    public UpdateDto deleteLecture(String email, String lectureNumber) {
        return lectureRepository.findByNumber(lectureNumber)
                .map(lectureToDelete -> {

                    User user = userRepository.findByEmail(email).orElseThrow(() -> new UpdateException(email));
                    user.getLectures().forEach(lecture -> {
                        lecture.getRecommend().updateSimilarityPoint(lectureToDelete.getId(), -1);
                        lectureToDelete.getRecommend().updateSimilarityPoint(lecture.getId(), -1);
                    });
                    user.removeLecture(lectureToDelete);

                    return UpdateDto.builder()
                            .success("true")
                            .email(email)
                            .build();
                })
                .orElseThrow(() -> new UpdateException(email));
    }
}
