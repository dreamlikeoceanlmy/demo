package com.example.demo.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.DAO.SoldSecondMapper;
import com.example.demo.Entity.SoldSecond;
import com.example.demo.MyException.EmptyClassException;
import com.example.demo.MyException.ParamNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SoldSecondService {
    @Autowired
    private SoldSecondMapper soldSecondMapper;
    public void addNewSoldSecond(SoldSecond soldSecond){
        soldSecond.setIsCheck(false);
        soldSecond.setFailReason(null);
        soldSecondMapper.insert(soldSecond);
    }
    public void checkSoldSecondById(int id,String failReason,int checkUserId)throws ParamNotFoundException{
        if (soldSecondMapper.checkSoldSecond(id,checkUserId,failReason)==0) {
            throw new ParamNotFoundException("id not found");
        }
    }
    public void modifyClassById(int id,String firstClass,String secondClass)throws EmptyClassException{
        if ( (firstClass==null&&secondClass==null)  || (firstClass.isBlank()||secondClass.isBlank()) ) {
            throw new EmptyClassException("class name empty");
        }
        soldSecondMapper.modifyClass(id,firstClass,secondClass);
    }
    public List<SoldSecond> showAllSoldSecond(int current, int size){
        return soldSecondMapper.selectPage(new Page<>(current,size), null).getRecords();
    }
    public List<SoldSecond> showAllSoldSecondUnchecked(int current, int size){
        return soldSecondMapper.selectAllSoldSecondUnChecked(new Page(current, size)).getRecords();
    }
    public List<SoldSecond> showAllSoldSecondByClass(int current,int size,String firstClass,String secondClass)throws EmptyClassException{
        if (firstClass==null||firstClass.isBlank()) {
            throw new EmptyClassException("first class is blank");
        }
        Page<Object> page = new Page<>(current, size);
        return soldSecondMapper.selectAllSoldSecondCheckedByClass(page, firstClass, secondClass).getRecords();
    }
    public List<SoldSecond> showMySoldSecondByUserId(int current,int size,int userId){
        return soldSecondMapper.selectPage(new Page<>(current, size), new QueryWrapper<SoldSecond>().eq("user_id", userId))
                .getRecords();
    }
    public void userSoldHisSecond(int id,int userId){
        SoldSecond soldSecond = SoldSecond.builder().isSold(true).build();
        soldSecondMapper.update(soldSecond, new QueryWrapper<SoldSecond>().eq("id", id).eq("user_id", userId));
    }
    public void updateMySoldByUserId(SoldSecond soldSecond)throws ParamNotFoundException{
        soldSecond.setIsCheck(false);
        if (soldSecondMapper.update(soldSecond, new UpdateWrapper<SoldSecond>().eq("id", soldSecond.getId()).eq("user_id", soldSecond.getUserId()).set("ban_reason",null ))==0) {
            throw new ParamNotFoundException("id not found");
        }
    }
}
