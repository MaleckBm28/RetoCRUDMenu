package model;

import javax.persistence.*;

@Entity
@Table(name = "ADMIN_")
@PrimaryKeyJoinColumn(name = "USERNAME")
public class Admin extends Profile {

    @Column(name = "CURRENT_ACCOUNT", length = 40)
    private String currentAccount;

    public Admin() {
        super();
        this.currentAccount = "";
    }

    public Admin(String currentAccount, String username, String password, String email, int userCode, String name, String telephone, String surname) {
        super(username, password, email, userCode, name, telephone, surname);
        this.currentAccount = currentAccount;
    }

    public String getCurrentAccount() { return currentAccount; }
    public void setCurrentAccount(String currentAccount) { this.currentAccount = currentAccount; }

    @Override
    public void logIn() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
