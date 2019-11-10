package com.example.demo.Entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.*;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Report {
    @TableId(value ="id",type = IdType.AUTO)
    private Integer id;
    @NotBlank(message = "reason is blank")
    private String reason;
    private String photoPath;
    @NotBlank(message = "contact is blank")
    private String contact;
    private Integer informerId;
    private Integer checkUserId;
    private Boolean isCheck;
    private String failReason;
    private String timeStamp;

}
