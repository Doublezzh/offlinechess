package com.svw.chess.Info;

import android.content.SharedPreferences;

/**
 * Created by 77304 on 2021/4/7.
 */

public class Setting {
    public boolean isMusicPlay = false;
    public boolean isEffectPlay = true;
    public boolean isPlayerRed = true;
    public int mLevel = 2;



    public Setting(SharedPreferences sharedPreferences) {
        isMusicPlay = sharedPreferences.getBoolean("isMusicPlay", true);
        isEffectPlay = sharedPreferences.getBoolean("isEffectPlay", true);
        isPlayerRed = sharedPreferences.getBoolean("isPlayerRed", true);
        mLevel = sharedPreferences.getInt("mLevel", 2);
    }
}
