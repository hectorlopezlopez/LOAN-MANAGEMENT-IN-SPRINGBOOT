package com.revature.project1.Entities;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "idUser")
@Entity
@Table (name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long idUser;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name",length = 20)
    private String lastName;

    @Column(name = "email",length = 50)
    private String email;

    @Column(name = "phone",length = 13)
    private String phone;

    @Column(name = "created_at",length = 10)
    private String createdAt;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private Address address;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Loan> Loans = new ArrayList<>();

    @JsonManagedReference
    @OneToOne
    @JoinColumn(name = "account_id")
    private Account account;

    public User() {}

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getFirstName() {return firstName;}

    public void setFirstName(String firstName) {this.firstName = firstName;}

    public Address getAddress() {return address;}

    public void setAddress(Address address) {
        this.address = address;
    }

    public List<Loan> getLoans() {
        return Loans;
    }

    public void setLoans(List<Loan> loans) {
        Loans = loans;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }



}
