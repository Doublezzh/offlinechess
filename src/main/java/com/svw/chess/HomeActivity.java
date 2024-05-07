package com.svw.chess;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.svw.chess.AICore.KnowledgeBase;
import com.svw.chess.AICore.TransformTable;
import com.svw.chess.CustomDialog.ChessCommonDialog;
import com.svw.chess.CustomDialog.OnlyReadDialog;
import com.svw.chess.CustomDialog.SettingDialog;
import com.svw.chess.Info.ChessInfo;
import com.svw.chess.Info.InfoSet;
import com.svw.chess.Info.SaveInfo;
import com.svw.chess.Info.Setting;
import com.svw.chess.Info.Zobrist;
import com.svw.chess.Utils.ChessAudioFocusChangeListener;
import com.svw.chess.Utils.ChessAudioFocusManager;
import com.svw.chess.Utils.PermissionUtils;
import com.zyn.common.LogUtils;


public class HomeActivity extends AppCompatActivity implements View.OnClickListener, ChessAudioFocusChangeListener {
    public RelativeLayout btn_pvm;
    public RelativeLayout btn_pvp;
    public Button btn_help;
    public ImageButton game_over;
    public ImageButton btn_setting;

    public static Setting setting;

    public static MediaPlayer backMusic;
    public static MediaPlayer selectMusic;
    public static MediaPlayer clickMusic;


    public static SharedPreferences sharedPreferences;

    public static Zobrist zobrist;

    public static long curClickTime = 0L;
    public static long lastClickTime = 0L;
    public static final int MIN_CLICK_DELAY_TIME = 100;


    private ChessAudioFocusManager audioFocusManager;

    private ImageButton mute_ib;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        PermissionUtils.isGrantExternalRW(this, 1);

        initMusic();


        sharedPreferences = getSharedPreferences("setting", MODE_PRIVATE);

        zobrist = new Zobrist();

        setting = new Setting(sharedPreferences);

        mute_ib = findViewById(R.id.mute_ib);
        btn_pvm = (RelativeLayout) findViewById(R.id.btn_pvm);
        btn_pvm.setOnClickListener(this);

        btn_pvp = (RelativeLayout) findViewById(R.id.btn_pvp);
        btn_pvp.setOnClickListener(this);

//        btn_help = (Button) findViewById(R.id.btn_help);
//        btn_help.setOnClickListener(this);

        game_over = (ImageButton) findViewById(R.id.game_over);
        game_over.setOnClickListener(this);

        btn_setting = (ImageButton) findViewById(R.id.btn_setting);
        btn_setting.setOnClickListener(this);


        getAudioFocusChange();
        mute_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAudioFocusChange();
            }
        });

    }

    private void getAudioFocusChange() {
        audioFocusManager = new ChessAudioFocusManager(this, this);
        if (audioFocusManager.requestAudioFocus()) {
            // Focused

            mute_ib.setVisibility(View.GONE);
            playMusic(backMusic);


        } else {
            // Failed to gain focus
        }
    }


    public void initMusic() {
        backMusic = MediaPlayer.create(this, R.raw.background);
        backMusic.setAudioStreamType(AudioManager.STREAM_MUSIC);
        backMusic.setLooping(true);

        backMusic.setVolume(0.2f, 0.2f);
        selectMusic = MediaPlayer.create(this, R.raw.select);
        selectMusic.setAudioStreamType(AudioManager.STREAM_MUSIC);
        selectMusic.setVolume(5f, 5f);
        clickMusic = MediaPlayer.create(this, R.raw.click);
        clickMusic.setAudioStreamType(AudioManager.STREAM_MUSIC);
        clickMusic.setVolume(5f, 5f);


    }

    private MediaPlayer createMediaPlayer(int resourceId, float volume, int contentType) {
        MediaPlayer mediaPlayer = MediaPlayer.create(this, resourceId);
        mediaPlayer.setLooping(false); // 根据需要设置是否循环播放
        mediaPlayer.setVolume(volume, volume); // 设置音量
        // 使用AudioAttributes代替setAudioStreamType，适用于API 21及以上
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            mediaPlayer.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setContentType(contentType)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
            );
        } else {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
        return mediaPlayer;
    }

    public static void playMusic(MediaPlayer mediaPlayer) {
        if (setting.isMusicPlay) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                mediaPlayer.seekTo(0);
            }
            mediaPlayer.start();
        }
    }

    public static void stopMusic(MediaPlayer mediaPlayer) {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);
        }

    }

    public static void playEffect(MediaPlayer mediaPlayer) {
        if (setting.isEffectPlay) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                mediaPlayer.seekTo(0);
            }
            mediaPlayer.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (audioFocusManager != null) {
            audioFocusManager.abandonAudioFocus();
        }

    }

    @Override
    public void onClick(View view) {
        lastClickTime = System.currentTimeMillis();
        if (lastClickTime - curClickTime < MIN_CLICK_DELAY_TIME) {
            return;
        }
        curClickTime = lastClickTime;
        Intent intent;


        if (view.getId() == R.id.btn_pvm) {
            playEffect(selectMusic);
            intent = new Intent(HomeActivity.this, PvMActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.btn_pvp) {
            playEffect(selectMusic);
            intent = new Intent(HomeActivity.this, PvPActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.btn_help) {
            playEffect(selectMusic);
            final OnlyReadDialog helpDialog = new OnlyReadDialog(this, "帮助", "本款象棋软件有两种玩\n法，分别是双人博弈和\n人机博弈，具体玩法请\n参照中国象棋规则。");
            helpDialog.setOnClickBottomListener(new OnlyReadDialog.OnClickBottomListener() {

                @Override
                public void onPositiveClick() {
                    playEffect(selectMusic);
                    helpDialog.dismiss();
                }
            });
            helpDialog.show();
        } else if (view.getId() == R.id.game_over) {
            playEffect(selectMusic);
            final ChessCommonDialog backDialog = new ChessCommonDialog(HomeActivity.this, "退出", "确认要退出吗");
            backDialog.setOnClickBottomListener(new ChessCommonDialog.OnClickBottomListener() {
                @Override
                public void onPositiveClick() {
                    backDialog.dismiss();
                    finish();
                }

                @Override
                public void onNegtiveClick() {
                    backDialog.dismiss();
                }
            });
            backDialog.show();


        } else if (view.getId() == R.id.btn_setting) {
            final SettingDialog settingDialog = new SettingDialog(this);
            settingDialog.setOnClickBottomListener(new SettingDialog.OnClickBottomListener() {
                @Override
                public void onPositiveClick() {
                    Editor editor = sharedPreferences.edit();
                    boolean flag = false;
                    if (setting.isMusicPlay != settingDialog.isMusicPlay) {
                        setting.isMusicPlay = settingDialog.isMusicPlay;
                        if (setting.isMusicPlay) {
                            playMusic(backMusic);
                        } else {
                            stopMusic(backMusic);
                        }
                        editor.putBoolean("isMusicPlay", settingDialog.isMusicPlay);
                        flag = true;
                    }
                    if (setting.isEffectPlay != settingDialog.isEffectPlay) {
                        setting.isEffectPlay = settingDialog.isEffectPlay;
                        editor.putBoolean("isEffectPlay", settingDialog.isEffectPlay);

                        flag = true;
                    }
                    if (flag) {
                        editor.commit();
                    }
                    settingDialog.dismiss();
                }

                @Override
                public void onNegtiveClick() {
                    settingDialog.dismiss();
                }
            });
            settingDialog.show();
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        lastClickTime = System.currentTimeMillis();
        if (lastClickTime - curClickTime < MIN_CLICK_DELAY_TIME) {
            return true;
        }
        curClickTime = lastClickTime;

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            final ChessCommonDialog backDialog = new ChessCommonDialog(this, "退出", "确认要退出吗");
            backDialog.setOnClickBottomListener(new ChessCommonDialog.OnClickBottomListener() {

                @Override
                public void onPositiveClick() {
                    backDialog.dismiss();
                    finish();
                }

                @Override
                public void onNegtiveClick() {
                    backDialog.dismiss();
                }
            });
            backDialog.show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LogUtils.e("chen", "获取存储权限成功");

                    Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isMusicPlay", true);
                    editor.putBoolean("isEffectPlay", true);
                    editor.putBoolean("isPlayerRed", true);
                    editor.putInt("mLevel", 2);
                    editor.commit();
                    try {
                        SaveInfo.SerializeChessInfo(new ChessInfo(), "ChessInfo_pvp.bin");
                        SaveInfo.SerializeInfoSet(new InfoSet(), "InfoSet_pvp.bin");
                        SaveInfo.SerializeChessInfo(new ChessInfo(), "ChessInfo_pvm.bin");
                        SaveInfo.SerializeInfoSet(new InfoSet(), "InfoSet_pvm.bin");
                        SaveInfo.SerializeKnowledgeBase(new KnowledgeBase(), "KnowledgeBase.bin");
                        SaveInfo.SerializeTransformTable(new TransformTable(), "TransformTable.bin");
                    } catch (Exception e) {
                        LogUtils.e("chen", e.toString());
                    }

                    playMusic(backMusic);
                } else {
                    Toast.makeText(this, "获取存储权限失败，请手动开启存储权限", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onPause() {
        stopMusic(backMusic);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        playMusic(backMusic);
    }

    @Override
    protected void onStart() {
        playMusic(backMusic);
        super.onStart();
    }

    @Override
    public void onFocusGain() {
        // 更新UI，重新获得焦点
    }

    @Override
    public void onFocusLoss() {
        mute_ib.setVisibility(View.VISIBLE);
        // 更新UI，失去焦点
        stopMusic(backMusic);
    }

    @Override
    public void onFocusLossTransient() {
        // 更新UI，暂时失去焦点
    }

    @Override
    public void onCanDuck() {
        // 更新UI，可以降低音量
    }


}
