package com.example.demo.DAO;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.Entity.LoginUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface LoginUserMapper extends BaseMapper<LoginUser> {
    //返回值为影响条数
       @Update("<script> " +
        "update login_user set role=#{role} where id in" +
        "<foreach collection='ids' open='(' item='id_' separator=',' close=')'> #{id_}</foreach>\n" +
        "        </script>")
    int updateRole(String role,int[] ids);
    @Select("<script> " +
            "select username from login_user where id in" +
            "<foreach collection='ids' open='(' item='id_' separator=',' close=')'> #{id_}</foreach>\n" +
            "        </script>")
    String[] findUsernameByIds(@Param("ids") int[] ids);
}
