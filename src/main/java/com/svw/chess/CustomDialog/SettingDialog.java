package com.svw.chess.CustomDialog;

import static com.svw.chess.HomeActivity.clickMusic;
import static com.svw.chess.HomeActivity.playEffect;
import static com.svw.chess.HomeActivity.selectMusic;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.suke.widget.SwitchButton;
import com.svw.chess.HomeActivity;
import com.svw.chess.R;
import com.svw.chess.Utils.ChessSoundPlayUtil;
import com.svw.link.Music.SoundPlayUtil;

/**
 * Created by 77304 on 2021/4/13.
 */

public class SettingDialog extends Dialog implements SwitchButton.OnCheckedChangeListener {
    public Button posBtn, negBtn;


    public boolean isMusicPlay, isEffectPlay;
    private SwitchButton musicSB;
    private SwitchButton wrapSB;

    public SettingDialog(Context context) {
        super(context, R.style.CustomDialog);
        isMusicPlay = HomeActivity.setting.isMusicPlay;
        isEffectPlay = HomeActivity.setting.isEffectPlay;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_setting);
        setCanceledOnTouchOutside(false);
        initView();
        initEvent();
        if (isMusicPlay) {
            musicSB.setChecked(true);
        } else {
            musicSB.setChecked(false);
        }
        if (isEffectPlay) {
            wrapSB.setChecked(true);
        } else {
            wrapSB.setChecked(false);
        }

    }

    private void initView() {
        posBtn = (Button) findViewById(R.id.posBtn);
        negBtn = (Button) findViewById(R.id.negBtn);


        musicSB = (SwitchButton) findViewById(R.id.musicSwitch);
        wrapSB = (SwitchButton) findViewById(R.id.wrapSwitch);


        musicSB.setOnCheckedChangeListener(this);
        wrapSB.setOnCheckedChangeListener(this);

    }


    private void initEvent() {
        //设置确定按钮被点击后，向外界提供监听
        posBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playEffect(selectMusic);
                if (onClickBottomListener != null) {
                    onClickBottomListener.onPositiveClick();
                }
            }
        });
        //设置取消按钮被点击后，向外界提供监听
        negBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playEffect(selectMusic);
                if (onClickBottomListener != null) {
                    onClickBottomListener.onNegtiveClick();
                }
            }
        });
    }

    public OnClickBottomListener onClickBottomListener;

    public SettingDialog setOnClickBottomListener(OnClickBottomListener onClickBottomListener) {
        this.onClickBottomListener = onClickBottomListener;
        return this;
    }


    @Override
    public void onCheckedChanged(SwitchButton view, boolean isChecked) {

        playEffect(selectMusic);

        if (view.getId() == R.id.musicSwitch) {
            if (isChecked) {
                isMusicPlay = true;
            } else {
                isMusicPlay = false;
            }
        } else if (view.getId() == R.id.wrapSwitch) {
            if (isChecked) {
                isEffectPlay = true;
                ChessSoundPlayUtil.getInstance(getContext()).setVoice((float) (100 / 100.0));

            } else {
                isEffectPlay = false;
                ChessSoundPlayUtil.getInstance(getContext()).setVoice(0);

            }
        }
    }

    public interface OnClickBottomListener {
        /**
         * 点击确定按钮事件
         */
        public void onPositiveClick();

        /**
         * 点击取消按钮事件
         */
        public void onNegtiveClick();
    }
}
