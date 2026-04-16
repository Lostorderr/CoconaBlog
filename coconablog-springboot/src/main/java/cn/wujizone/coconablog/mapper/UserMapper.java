package cn.wujizone.coconablog.mapper;

import cn.wujizone.coconablog.entity.User;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface UserMapper {
    
    @Select("SELECT * FROM user WHERE id = #{id}")
    User findById(@Param("id") Long id);
    
    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUsername(@Param("username") String username);
    
    @Select("SELECT * FROM user WHERE email = #{email}")
    User findByEmail(@Param("email") String email);
    
    @Select("SELECT * FROM user")
    List<User> findAll();
    
    @Insert("INSERT INTO user(username, password, email, security_question, security_answer, avatar, role, status, create_time, update_time) " +
            "VALUES(#{username}, #{password}, #{email}, #{securityQuestion}, #{securityAnswer}, #{avatar}, #{role}, #{status}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);
    
    @Update("UPDATE user SET username=#{username}, email=#{email}, avatar=#{avatar}, " +
            "role=#{role}, status=#{status}, update_time=NOW() WHERE id=#{id}")
    int update(User user);
    
    @Select("SELECT id, username, security_question, security_answer FROM user WHERE username = #{username}")
    User findByUsernameForSecurity(@Param("username") String username);

    @Update("UPDATE user SET password = #{password}, update_time = NOW() WHERE username = #{username}")
    int updatePasswordByUsername(@Param("username") String username, @Param("password") String password);
    
    @Update("UPDATE user SET last_login = NOW() WHERE id = #{id}")
    int updateLastLogin(@Param("id") Long id);
    
    @Update("UPDATE user SET password = #{password}, update_time = NOW() WHERE id = #{id}")
    int updatePassword(@Param("id") Long id, @Param("password") String password);
    
    @Delete("DELETE FROM user WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
}
