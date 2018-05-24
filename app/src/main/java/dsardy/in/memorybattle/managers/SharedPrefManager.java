package dsardy.in.memorybattle.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class SharedPrefManager {

    public static final String PREF_YOUR_NUMBER = "your_imei_no";
    public static final String PREF_YOUR_NAME = "your_name";
    public static final String PREF_OPPONENT_NUMBER = "your_imei_no";
    public static final String PREF_OPPONENT_NAME = "user_name";

    public static final String PREF_YOUR_SCORE = "user_reg";
    public static final String PREF_MATCHES = "user_pass";
    public static final String INTRO_WATCHED = "is_intro_watched";



    private SharedPreferences mSharedPref;
    private Context mContext;
    private static SharedPreferences.Editor editor;


    public SharedPrefManager(Context context){
        mContext = context;
        mSharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        editor = mSharedPref.edit();
    }

    /**
     * @return
     */
    public String getMyNumber() {
        return mSharedPref.getString(PREF_YOUR_NUMBER, "");
    }

    /**
     * @param reginfo
     */
    public void setMyNumber(String reginfo) {
        editor.putString(PREF_YOUR_NUMBER, reginfo);
        editor.commit();
    }
    public String getMyName() {
        return mSharedPref.getString(PREF_YOUR_NAME, "");
    }

    public void setMyName(String reginfo) {
        editor.putString(PREF_YOUR_NAME, reginfo);
        editor.commit();
    }
    public String getMyScore() {
        return mSharedPref.getString(PREF_YOUR_SCORE, "0");
    }

    public void setMyScore(String mobileNo) {
        editor.putString(PREF_YOUR_SCORE, mobileNo);
        editor.commit();
    }

    public String getMyMatches() {
        return mSharedPref.getString(PREF_MATCHES, "0");
    }

    public void setMyMatches(String userName) {
        editor.putString(PREF_MATCHES, userName);
        editor.commit();
    }
    public int getIsIntroWatched() {
        return mSharedPref.getInt(INTRO_WATCHED,0);
    }

    public void setIsIntroWatched(int value) {
        editor.putInt(INTRO_WATCHED, value);
        editor.commit();
    }


}
