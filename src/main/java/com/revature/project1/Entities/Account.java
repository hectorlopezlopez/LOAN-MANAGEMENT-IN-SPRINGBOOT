package com.revature.project1.Entities;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;

@Entity
@Table(name = "Accounts")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "accountId")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long accountId;
    @Column(name = "username", unique = true, nullable = false, length = 124)
    private String username;
    @Column(name = "password", nullable = false, length = 512)
    private String password;

    // Owner:
    @ManyToOne
    @JoinColumn(name = "role_id", referencedColumnName = "role_id")
    private Role role;

    // Mapper:
    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private User user;

    public Account() {}

    public Account(String username, Role role) {}

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountId=" + accountId +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                ", user=" + user +
                '}';
    }
}