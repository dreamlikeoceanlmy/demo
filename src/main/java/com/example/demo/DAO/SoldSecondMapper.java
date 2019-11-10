package com.example.demo.DAO;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.Entity.SoldSecond;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface SoldSecondMapper extends BaseMapper<SoldSecond> {
    void modifyClass(@Param(value = "id") int id, @Param(value = "first") String firstClass, @Param(value = "second") String secondClass);
    int checkSoldSecond(@Param(value = "id")int id, @Param("checkUserId") int checkUserId,@Param(value = "failReason")String failReason);
    IPage<SoldSecond> selectAllSoldSecondCheckedByClass(Page page, @Param(value = "first") String firstClass, @Param(value = "second")String secondClass);
    IPage<SoldSecond> selectAllSoldSecondUnChecked(Page page);
}
