package com.maru.inunavi.dao;

import com.maru.inunavi.entity.Lecture;
import com.maru.inunavi.entity.user.ClassList;
import com.maru.inunavi.entity.user.User;
import com.maru.inunavi.entity.Constant;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;

public class UserDao {


    private JdbcTemplate template;

    public UserDao() {
        System.out.println("Hi2");
        this.template = Constant.template;
    }

    public ArrayList<User> selectAll(){
        String sql = "select * from user_table";
        return (ArrayList<User>) this.template.query(sql, BeanPropertyRowMapper.newInstance(User.class));
    }

    public boolean insert(String id, String password, String name, String email){
        if(!idCheck(id)){
            return false;
        }
        String sql = "insert into user_table (id, password, name, email) values (?, ?, ?, ?)";
        this.template.update(sql, id, password, name, email);
        return true;
    }

    public ArrayList<Lecture> selectClass(String id){
        String sql = "select a.* from all_lecture as a join class_list as c on c.id = ? and c.class_id = a.number";
        return (ArrayList<Lecture>) this.template.query(sql, BeanPropertyRowMapper.newInstance(Lecture.class), id);
    }

    public boolean insertClass(String id, String class_id){
        if(idCheck(id)) return false;
        String sql = "select count(*) from class_list where id=? and class_id=?";
        if(this.template.queryForObject(sql, int.class, id, class_id) > 0) return false;
        sql = "insert into class_list (id, class_id) values (?, ?)";
        this.template.update(sql, id, class_id);
        return true;
    }

    public boolean deleteClass(String id, String class_id){
        if(idCheck(id)) return false;
        String sql = "delete from class_list where id=? and class_id=?";
        this.template.update(sql, id, class_id);
        return true;
    }

    public boolean idCheck(String id){
        //System.out.println("sex");
        String sql = "select count(*) from user_table where id = ?";
        int count = this.template.queryForObject(sql, int.class, id);
        if (count == 0) {
            return true;
        }
        return false;
    }

    public int login(String id, String password){
        if(idCheck(id)){
            return -1;
        }else{
            String sql = "select password from user_table where id = ?";
            if(password.equals(this.template.queryForObject(sql, String.class, id))){
                return 1;
            }else{
                return 0;
            }
        }
    }

}
