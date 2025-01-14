package com.userService.demo.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.userService.demo.model.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Getter
@Setter
@NoArgsConstructor
@JsonDeserialize(as = CustomGrantedAuthority.class)
public class CustomGrantedAuthority implements GrantedAuthority {
    private Role role;
    public CustomGrantedAuthority(Role role) {
        this.role = role;
    }
    @Override
    @JsonIgnore
    public String getAuthority() {
        return role.getName();
    }
}
