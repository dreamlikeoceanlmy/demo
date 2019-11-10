package com.example.demo.DAO;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.Entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface MessageMapper extends BaseMapper<Message> {
}
