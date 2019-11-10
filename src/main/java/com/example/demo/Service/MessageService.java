package com.example.demo.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.DAO.MessageMapper;
import com.example.demo.Entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService  {
    @Autowired
    private MessageMapper messageMapper;
    public void sendMessage(String context,int userId){
        Message message = Message.builder().context(context).userId(userId).build();
        messageMapper.insert(message);
    }
    public List<Message> getMessage(int userId,int current,int size){
        return messageMapper.selectPage(new Page<>(current, size), new QueryWrapper<Message>().eq("user_id", userId)).getRecords();
    }

}
