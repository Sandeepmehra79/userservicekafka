package com.userService.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.List;
@Entity(name = "ROLES")
@Setter
@Getter
@JsonDeserialize(as = Role.class)
public class Role extends BaseModel{
    @Column(name = "name", unique = true)
    private String name;
}
