package com.example.demo.Entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.*;

import javax.validation.constraints.NotBlank;
@Data
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class RequireSecond {
    @TableId(value ="id",type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    @NotBlank(message = "first class is blank")
    private String firstClass;
    @NotBlank(message = "second class is blank")
    private String secondClass;
    @NotBlank(message = "title is blank")
    private String title;
    @NotBlank(message = "desc sentence is blank")
    private String descSentence;
    private String photoPath;
    @NotBlank(message = "campus is blank")
    private String campus;
    @NotBlank(message = "contact is blank")
    private String contact;
    private Integer checkUserId;
    private Boolean isCheck;
    private String failReason;
    private String timeStamp;
    private Boolean isGet;
}
