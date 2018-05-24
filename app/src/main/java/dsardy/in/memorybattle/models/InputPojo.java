package dsardy.in.memorybattle.models;


public class InputPojo {
    private int mRank = 0;
    private int mTapCount = 0;
    private int mStatus = 0;
    private int mKey = 0;

    public InputPojo(int mRank, int mTapCount, int mStatus, int mKey) {
        this.mRank = mRank;
        this.mTapCount = mTapCount;
        this.mStatus = mStatus;
        this.mKey = mKey;
    }
    public InputPojo(){

    }

    public int getmKey() {
        return mKey;
    }

    public void setmKey(int mKey) {
        this.mKey = mKey;
    }

    public int getmRank() {
        return mRank;
    }

    public int getmStatus() {
        return mStatus;
    }

    public void setmStatus(int mStatus) {
        this.mStatus = mStatus;
    }

    public void setmRank(int mRank) {
        this.mRank = mRank;
    }

    public int getmTapCount() {
        return mTapCount;
    }

    public void setmTapCount(int mTapCount) {
        this.mTapCount = mTapCount;
    }
}
