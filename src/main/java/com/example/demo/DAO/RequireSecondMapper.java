package com.example.demo.DAO;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.Entity.RequireSecond;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface RequireSecondMapper extends BaseMapper<RequireSecond> {
    void modifyClass(@Param(value = "id") int id, @Param(value = "first") String firstClass, @Param(value = "second") String secondClass);
    int checkRequireSecond(@Param(value = "id")int id, @Param("checkUserId") int checkUserId,@Param(value = "failReason")String failReason);
    IPage<RequireSecond> selectAllRequiredSecondCheckedByClass(Page page, @Param(value = "first") String firstClass, @Param(value = "second")String secondClass);
    IPage<RequireSecond> selectAllRequiredSecondUnChecked(Page page);
}
