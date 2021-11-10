package inunavi.object;

public class userJoin {

    private String success;
    private String userID;

    public userJoin(String success, String userID){
        this.success=success;
        this.userID=userID;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
