package com.demo.sys.datasource.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class UserBindJobAndRole {
    private Integer userId;
    private List<Integer> jobIds;
    private List<Integer> roleIds;
}
