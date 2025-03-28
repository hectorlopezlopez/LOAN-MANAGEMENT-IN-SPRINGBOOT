package com.revature.project1.Entities;

import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import java.util.*;

//@Setter
//@Getter
@Entity
@Table(name = "Roles")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "roleId")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleId;
    @Column(name = "role_name", nullable = false, length = 124)
    private String roleName;

    // MRs:
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Account> account = new ArrayList<>();

    public Role() {}

    public Role(Long roleId, String roleName) {
        this.roleId = roleId;
        this.roleName = roleName;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public List<Account> getAccount() {
        return account;
    }

    public void setAccount(List<Account> account) {
        this.account = account;
    }
}