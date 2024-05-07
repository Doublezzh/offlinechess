package com.svw.chess.CustomDialog;

import static com.svw.chess.HomeActivity.clickMusic;
import static com.svw.chess.HomeActivity.playEffect;
import static com.svw.chess.HomeActivity.selectMusic;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.suke.widget.SwitchButton;
import com.svw.chess.HomeActivity;
import com.svw.chess.R;
import com.svw.chess.Utils.ChessSoundPlayUtil;
import com.svw.link.Music.SoundPlayUtil;

/**
 * Created by 77304 on 2021/4/14.
 */

public class SettingDialog_PvM extends Dialog implements SwitchButton.OnCheckedChangeListener {
    public Button posBtn, negBtn;


    public boolean isMusicPlay, isEffectPlay;
    public int mLevel;
    private SwitchButton musicSB;
    private SwitchButton wrapSB;
    private TabLayout levels_tab, level_invert;
    private TabLayout.Tab tab = null;
    private TabLayout.Tab tab2 = null;

    //是否为竖屏
    public boolean isLandscape;

    public SettingDialog_PvM(Context context, Boolean isinvert) {
        super(context, R.style.CustomDialog);

        isMusicPlay = HomeActivity.setting.isMusicPlay;
        isEffectPlay = HomeActivity.setting.isEffectPlay;
        mLevel = HomeActivity.setting.mLevel;

        isLandscape = isinvert;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_setting_pvm);
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


        if (isLandscape) {
            tab2 = level_invert.getTabAt(0);
        } else {
            tab2 = level_invert.getTabAt(1);
        }
        if (tab2 != null) {
            level_invert.selectTab(tab2);
        }


        if (mLevel == 1) {
            tab = levels_tab.getTabAt(0);
        } else if (mLevel == 2) {
            tab = levels_tab.getTabAt(1);
        } else {
            tab = levels_tab.getTabAt(2);
        }
        if (tab != null) {
            levels_tab.selectTab(tab);
        }
    }

    private void initView() {
        posBtn = (Button) findViewById(R.id.posBtn);
        negBtn = (Button) findViewById(R.id.negBtn);

        musicSB = (SwitchButton) findViewById(R.id.musicSwitch);
        wrapSB = (SwitchButton) findViewById(R.id.wrapSwitch);


        musicSB.setOnCheckedChangeListener(this);
        wrapSB.setOnCheckedChangeListener(this);

        String[] title = new String[]{"中级", "高级"};

        String[] invert_title = new String[]{"纵向", "横向"};
        levels_tab = (TabLayout) findViewById(R.id.levels_tab);
        level_invert = (TabLayout) findViewById(R.id.level_invert);


        for (String tabTitle : title) {
            TabLayout.Tab tab = levels_tab.newTab();
            View tabView = LayoutInflater.from(getContext()).inflate(R.layout.custom_tab_layout, null);
            TextView textView = tabView.findViewById(R.id.custom_text);
            textView.setText(tabTitle); // 确保这里设置了文本
            tab.setCustomView(tabView);
            levels_tab.addTab(tab);
        }


        for (String tabTitle : invert_title) {
            TabLayout.Tab tab2 = level_invert.newTab();
            View tabView = LayoutInflater.from(getContext()).inflate(R.layout.custom_tab_layout, null);
            TextView textView = tabView.findViewById(R.id.custom_text);
            textView.setText(tabTitle); // 确保这里设置了文本
            tab2.setCustomView(tabView);
            level_invert.addTab(tab2);
        }

        level_invert.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                playEffect(selectMusic);
                if (onClickBottomListener != null) {
                    if (tab.getPosition() == 0) {
                        isLandscape = true;
                        onClickBottomListener.onInvertSelected(true);
                    } else if (tab.getPosition() == 1) {
                        onClickBottomListener.onInvertSelected(false);
                        isLandscape = false;
                    }
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // 当选项卡退出选择状态时调用
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // 当已选中的选项卡再次被用户选中时调用
            }
        });
        levels_tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                playEffect(selectMusic);
                if (tab != null) {
                    if (tab.getPosition() == 0) {
                        mLevel = 1;
                    } else if (tab.getPosition() == 1) {
                        mLevel = 2;
                    } else {
                        mLevel = 3;
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // 当选项卡退出选择状态时调用
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // 当已选中的选项卡再次被用户选中时调用
            }
        });


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

    public SettingDialog_PvM setOnClickBottomListener(OnClickBottomListener onClickBottomListener) {
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

        /**
         * 点击取消按钮事件
         */
        //
        public void onInvertSelected(boolean isSelected);
    }
}