package com.example.demo.Entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
@Data
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class SoldSecond {
    @TableId(value ="id",type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    @NotBlank(message = "first class is blank")
    private String firstClass;
    @NotBlank(message = "second class is blank")
    private String secondClass;
    @NotBlank(message = "thing name is blank")
    private String thingName;
    @NotBlank(message = "new degree is blank")
    private String newDegree;
    @NotBlank(message = "thing source" )
    private String thingSource;
    @NotBlank(message = "sold reason is blank")
    private String soldReason;
    private String remark;
    @NotBlank(message = "price state is blank")
    private String priceState;
    @Min(value = 0)
    private Integer price;
    @NotBlank(message = "campus is blank")
    private String campus;
    @NotBlank(message = "contact is blank")
    private String contact;
    private String photoPath;
    private Integer checkUserId;
    private Boolean isCheck;
    private String failReason;
    private String timeStamp;
    private Boolean isSold;
}
