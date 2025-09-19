package io.goorm.team02.core.users.controller.dto;

import io.goorm.team02.core.users.domain.User;
import io.goorm.team02.core.users.domain.UserRole;

import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
//@AllArgsConstructor   // 전체 필드 생성자
//@NoArgsConstructor    // 기본 생성자

public class UserRoleAddResponse {
    private Long id;
    private String email;
    private String name;
    private List<String> roles;

    // 기본 생성자
    public UserRoleAddResponse() {}

    // 전체 필드 생성자
    public UserRoleAddResponse(Long id, String email, String name, List<String> roles) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.roles = roles;
    }

    // Getter & Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}

