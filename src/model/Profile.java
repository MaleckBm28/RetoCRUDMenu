package model;

import javax.persistence.*;

@Entity
@Table(name = "PROFILE_")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Profile {

    @Id
    @Column(name = "USERNAME", length = 40)
    private String username;

    @Column(name = "PASSWORD_", nullable = false, length = 40)
    private String password;

    @Column(name = "EMAIL", nullable = false, unique = true, length = 40)
    private String email;

    // OJO: en tu BD USER_CODE es AUTO_INCREMENT pero NO es PK.
    // Lo más seguro es que lo genere la BD y Hibernate solo lo lea.
    @Column(name = "USER_CODE", insertable = false, updatable = false)
    private int userCode;

    @Column(name = "NAME_", length = 40)
    private String name;

    @Column(name = "SURNAME", length = 40)
    private String surname;

    @Column(name = "TELEPHONE", length = 9)
    private String telephone;

    public Profile() { }

    public Profile(String username, String password, String email, int userCode, String name, String telephone, String surname) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.userCode = userCode;
        this.name = name;
        this.telephone = telephone;
        this.surname = surname;
    }

    // getters/setters
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
    public int getUserCode() { return userCode; }
    public String getName() { return name; }
    public String getTelephone() { return telephone; }
    public String getSurname() { return surname; }

    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setEmail(String email) { this.email = email; }
    public void setUserCode(int userCode) { this.userCode = userCode; } // normalmente no lo tocarás
    public void setName(String name) { this.name = name; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public void setSurname(String surname) { this.surname = surname; }

    @Override
    public String toString() {
        return "Profile{" + "username=" + username + ", email=" + email + ", userCode=" + userCode + '}';
    }

    public abstract void logIn();
}
