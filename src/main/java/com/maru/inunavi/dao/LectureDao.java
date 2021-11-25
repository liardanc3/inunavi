package com.maru.inunavi.dao;

import com.maru.inunavi.entity.Constant;
import com.maru.inunavi.entity.Lecture;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;

public class LectureDao {
    private JdbcTemplate template;

    public LectureDao() {
        this.template = Constant.template;
    }

    public ArrayList<Lecture> search(){
        return new ArrayList<>();
    }

}
