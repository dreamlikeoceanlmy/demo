package com.example.demo.DAO;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.Entity.Report;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface ReportMapper extends BaseMapper<Report> {
}
