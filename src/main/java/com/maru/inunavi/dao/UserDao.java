package com.maru.inunavi.dao;

import com.maru.inunavi.entity.User;
import com.maru.inunavi.entity.Constant;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;

public class UserDao {


    private JdbcTemplate template;

    public UserDao() {
        this.template = Constant.template;
    }

    public ArrayList<User> selectAll(){
        String sql = "select * from user_table";
        return (ArrayList<User>) this.template.query(sql, BeanPropertyRowMapper.newInstance(User.class));
    }

    public boolean insert(String id, String password, String name, String email){
        String sql = "insert into user_table (id, password, name, email) values (?, ?, ?, ?)";
        this.template.update(sql, id, password, name, email);
        return true;
    }

    public boolean updateClassList(String id, String classList){
        if(idCheck(id)) return false;
        String sql = "update user_table set class_list = ? where id = ?";
        this.template.update(sql, classList, id);
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
