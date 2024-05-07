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
import com.svw.chess.HomeActivity;
import com.svw.chess.R;
import com.svw.chess.Utils.ChessSoundPlayUtil;

/**
 * Created by 77304 on 2021/4/19.
 */

public class RetryDialog extends Dialog {
    public Button posBtn, negBtn;

    public boolean isPlayerRed;
    private TabLayout move_tab;
    private TabLayout.Tab tab = null;
    public int mLevel;

    public RetryDialog(Context context) {
        super(context, R.style.CustomDialog);
        mLevel = HomeActivity.setting.mLevel;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_retry);
        setCanceledOnTouchOutside(false);
        initView();
        initEvent();
        isPlayerRed = true;
    }

    private void initView() {
        posBtn = (Button) findViewById(R.id.posBtn);
        negBtn = (Button) findViewById(R.id.negBtn);


        String[] title = new String[]{"中级", "高级"};
        String[] titletwo = new String[]{"红方", "黑方"};


        move_tab = (TabLayout) findViewById(R.id.move_tab);

        for (String tabTitle : titletwo) {
            TabLayout.Tab tab = move_tab.newTab();
            View tabView = LayoutInflater.from(getContext()).inflate(R.layout.custom_tab_layout, null);
            TextView textView = tabView.findViewById(R.id.custom_text);
            textView.setText(tabTitle); // 设置文本
            tab.setCustomView(tabView);
            tab.setTag(tabTitle); // 设置标签
            move_tab.addTab(tab);
        }

        TabLayout.Tab firstTab = move_tab.getTabAt(0); // 获取第一个选项卡对象
        if (firstTab != null) {
            firstTab.select(); // 选中第一个选项卡 红方
        }


        move_tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab != null) {
                    playEffect(selectMusic);
                    String tabTag = (String) tab.getTag(); // 获取标签
                    isPlayerRed = "红方".equals(tabTag);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }


    private void initEvent() {
        //设置确定按钮被点击后，向外界提供监听
        posBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickBottomListener != null) {
                    playEffect(selectMusic);
                    onClickBottomListener.onPositiveClick();
                }
            }
        });
        //设置取消按钮被点击后，向外界提供监听
        negBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickBottomListener != null) {
                    playEffect(selectMusic);
                    onClickBottomListener.onNegtiveClick();
                }
            }
        });
    }

    public OnClickBottomListener onClickBottomListener;

    public RetryDialog setOnClickBottomListener(OnClickBottomListener onClickBottomListener) {
        this.onClickBottomListener = onClickBottomListener;
        return this;
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
