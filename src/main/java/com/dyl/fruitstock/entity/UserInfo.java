package com.dyl.fruitstock.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo implements Entity<Integer> {


    private Integer id;


    private String userName;


    private String userPassword;

    /**
     * 用户状态:1表示冻结
     */
    private Integer userState;



}
