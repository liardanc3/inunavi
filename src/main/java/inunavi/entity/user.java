package inunavi.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.persistence.*;


@Builder
@Entity(name="userTable")
@AllArgsConstructor
public class user {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 45, nullable = false)
    private String userID;
    @Column(length = 45, nullable = false)
    private String userPassword;
    @Column(length = 45, nullable = false)
    private String userEmail;
    @Column(length = 45, nullable = true)
    private String userClass;

    public user(String userID, String userPassword, String userEmail) {
        this.userID=userID;
        this.userPassword=userPassword;
        this.userEmail=userEmail;
    }

    public user() {

    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserClass() {
        return userClass;
    }

    public void setUserClass(String userClass) {
        this.userClass = userClass;
    }
}
