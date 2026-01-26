package model;

import javax.persistence.*;

@Entity
@Table(name = "USER_")
@PrimaryKeyJoinColumn(name = "USERNAME")
public class User extends Profile {

    @Column(name = "GENDER", length = 40)
    private String gender;

    @Column(name = "CARD_NUMBER", length = 24)
    private String cardNumber;

    public User() {
        super();
        this.gender = "";
        this.cardNumber = "";
    }

    public User(String gender, String cardNumber, String username, String password, String email, int userCode, String name, String telephone, String surname) {
        super(username, password, email, userCode, name, telephone, surname);
        this.gender = gender;
        this.cardNumber = cardNumber;
    }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    @Override
    public void logIn() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
