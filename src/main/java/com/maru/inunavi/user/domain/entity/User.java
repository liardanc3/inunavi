package com.maru.inunavi.user.domain.entity;

import com.maru.inunavi.lecture.domain.entity.Lecture;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Builder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    // email
    @Column(length = 100, nullable = false)
    private String email;

    // password
    @Column(length = 100, nullable = false)
    private String password;

    // major
    @Column(length = 50, nullable = false)
    private String major;

    @ManyToMany
    @JoinTable(name = "user_lecture",
            joinColumns = @JoinColumn(name = "email"),
            inverseJoinColumns = @JoinColumn(name = "lecture_id")
    )
    private Set<Lecture> lectures = new HashSet<>();

    // --------------------------------------------------- //

    public void addLecture(Lecture lecture){
        lectures.add(lecture);
        lecture.getUsers().add(this);
    }

    public void removeLecture(Lecture lecture){
        lectures.remove(lecture);
        lecture.getUsers().remove(this);
    }

    public void removeLectures(){
        lectures.clear();
    }

    public void updatePassword(String password){
        this.password = password;
    }

    public void updateMajor(String major) {
        this.major = major;
    }
}
