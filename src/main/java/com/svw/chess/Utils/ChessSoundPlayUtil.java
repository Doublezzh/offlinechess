package com.svw.chess.Utils;

import static com.svw.chess.HomeActivity.setting;

import android.content.Context;
import android.media.SoundPool;

import com.svw.chess.R;


public class ChessSoundPlayUtil {
    //及时音频播放类
    private SoundPool soundPool;

    //声音大小
    private float voice = 1;

    //单例模式
    private static ChessSoundPlayUtil instance;

    private ChessSoundPlayUtil(Context context) {
        if (soundPool == null) {
            //设置同时播放流的最大数量
            soundPool = new SoundPool.Builder().setMaxStreams(3).build();

            soundPool.load(context, R.raw.click, 1); //1 点击按钮的音效
            soundPool.load(context, R.raw.select, 1);    // 2 选择
        }
    }

    public static synchronized ChessSoundPlayUtil getInstance(Context context) {
        if (instance == null) {
            instance = new ChessSoundPlayUtil(context);
        }
        return instance;
    }
//    public void play(int soundID) {
//        if (soundPool!=null){
//            soundPool.play(soundID, voice, voice, 0, 0, 1);
//
//        }
//    }

    //播放指定音乐
    public float getVoice() {
        return voice;
    }

    public void setVoice(float voice) {
        this.voice = voice;
    }




    // 释放SoundPool资源
    public void releaseSoundPool() {
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }
}
