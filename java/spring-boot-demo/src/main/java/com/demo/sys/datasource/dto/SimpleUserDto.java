package com.demo.sys.datasource.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class SimpleUserDto {
    @Id
    private Integer id;
    private String realName;
    private String phone;

    public SimpleUserDto() {
    }

    public SimpleUserDto(Integer id, String realName, String phone) {
        this.id = id;
        this.realName = realName;
        this.phone = phone;
    }
}
