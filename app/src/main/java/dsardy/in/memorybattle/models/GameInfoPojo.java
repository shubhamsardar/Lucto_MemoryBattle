package dsardy.in.memorybattle.models;



public class GameInfoPojo {
    private String mUserId;
    private String mUserName;
    private String mOpponentName;
    private String mGameId = "";

    public GameInfoPojo(String mUserId, String mUserName, String mGameId, String mOpponentName) {
        this.mUserId = mUserId;
        this.mUserName = mUserName;
        this.mGameId = mGameId;
        this.mOpponentName = mOpponentName;
    }
    public GameInfoPojo(){

    }

    public void setmOpponentName(String mOpponentName) {
        this.mOpponentName = mOpponentName;
    }

    public String getmOpponentName() {
        return mOpponentName;
    }

    public String getmUserId() {
        return mUserId;
    }

    public void setmUserId(String mUserId) {
        this.mUserId = mUserId;
    }

    public String getmUserName() {
        return mUserName;
    }

    public void setmUserName(String mUserName) {
        this.mUserName = mUserName;
    }

    public String getmGameId() {
        return mGameId;
    }

    public void setmGameId(String mGameId) {
        this.mGameId = mGameId;
    }
}
