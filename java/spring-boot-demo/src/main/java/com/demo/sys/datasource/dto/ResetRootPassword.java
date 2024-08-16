package com.demo.sys.datasource.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetRootPassword {
    private String code;
    private String password;
}
