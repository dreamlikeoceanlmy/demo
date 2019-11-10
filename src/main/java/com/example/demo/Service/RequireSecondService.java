package com.example.demo.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.DAO.RequireSecondMapper;
import com.example.demo.Entity.RequireSecond;
import com.example.demo.MyException.EmptyClassException;
import com.example.demo.MyException.ParamNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RequireSecondService {
    @Autowired
    private RequireSecondMapper requireSecondMapper;
    public void addNewRequireSecond(RequireSecond requireSecond) {
        requireSecond.setIsCheck(false);
        requireSecond.setFailReason(null);
        requireSecondMapper.insert(requireSecond);
    }
    public void checkRequireSecondById(int id,String failReason,int checkUserId)throws ParamNotFoundException{
        if (requireSecondMapper.checkRequireSecond(id,checkUserId, failReason)==0) {
            throw new ParamNotFoundException("id not found");
        }

    }

    public void modifyClassById(int id,String firstClass,String secondClass)throws EmptyClassException{
        if ( (firstClass==null&&secondClass==null)  || (firstClass.isBlank()||secondClass.isBlank()) ) {
            throw new EmptyClassException("class name empty");
        }
        requireSecondMapper.modifyClass(id,firstClass, secondClass);
    }

    public List<RequireSecond> showRequireSecondByClass(String firstClass,String secondClass,int current,int size)throws EmptyClassException{
        if (firstClass==null||firstClass.isBlank()) {
            throw new EmptyClassException("first class is blank");
        }
        Page<Object> page = new Page<>(current, size);
        return requireSecondMapper.selectAllRequiredSecondCheckedByClass(page, firstClass, secondClass).getRecords();
    }
    public List<RequireSecond> showMyRequiredSecondByUserId(int userId, int current, int size){
        return requireSecondMapper.selectPage(new Page<>(current,size), new QueryWrapper<RequireSecond>().eq("user_id", userId)).getRecords();
    }
    public void userGetHisRequiredSecond(int id,int userId){
        RequireSecond requireSecond = RequireSecond.builder().isGet(true).build();
        requireSecondMapper.update(requireSecond, new QueryWrapper<RequireSecond>().eq("id", id).eq("user_id", userId));
    }
    public List<RequireSecond> showRequiredSecondUnchecked(int current,int size){
        Page<Object> page = new Page<>(current, size);
        return requireSecondMapper.selectAllRequiredSecondUnChecked(page).getRecords();
    }
    public List<RequireSecond> showAllRequiredSecond(int current,int size){
        return requireSecondMapper.selectPage(new Page<>(current,size), null).getRecords();
    }
    public void updateMyRequireById(RequireSecond requireSecond)throws ParamNotFoundException{
        requireSecond.setIsCheck(false);
        if (requireSecondMapper.update(requireSecond,new UpdateWrapper<RequireSecond>().eq("id", requireSecond.getId())
                .eq("userId", requireSecond.getUserId()).set("ban_reason",null))==0) {
            throw new ParamNotFoundException("id not found");
        }
    }

}
