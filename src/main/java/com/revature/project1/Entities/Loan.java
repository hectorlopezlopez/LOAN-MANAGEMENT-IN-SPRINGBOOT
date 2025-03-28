package com.revature.project1.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id",
        scope = Loan.class // Explicitly set the scope to Loan.class
)
@Entity
@Table(name = "loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "loan_id")
    private Long id;

    @Column(name = "amount_requested")
    private Long amountRequested;

    @Column(name = "last_update")
    private String lastUpdate;

    @Column(name = "status_reason")
    private String statusReason;

    @Column(name = "manager_update")
    private String managerUpdate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    @ManyToOne
    @JoinColumn(name = "loan_status_id", referencedColumnName = "loan_status_id")
    private LoanStatus loanStatus;

    @ManyToOne
    @JoinColumn(name = "loan_type_id", referencedColumnName = "loan_type_id")
    private LoanType loanType;

    public Loan(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAmountRequested() {
        return amountRequested;
    }

    public void setAmountRequested(Long amountRequested) {
        this.amountRequested = amountRequested;
    }

    public LoanType getLoanType() {
        return loanType;
    }

    public void setLoanType(LoanType loanType) {
        this.loanType = loanType;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getStatusReason() {
        return statusReason;
    }

    public void setStatusReason(String statusReason) {
        this.statusReason = statusReason;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LoanStatus getLoanStatus() {
        return loanStatus;
    }

    public void setLoanStatus(LoanStatus loanStatus) {
        this.loanStatus = loanStatus;
    }

    public String getManagerUpdate() {
        return managerUpdate;
    }

    public void setManagerUpdate(String managerUpdate) {
        this.managerUpdate = managerUpdate;
    }
}
