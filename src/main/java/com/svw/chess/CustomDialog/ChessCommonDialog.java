package com.svw.chess.CustomDialog;

import static com.svw.chess.HomeActivity.clickMusic;
import static com.svw.chess.HomeActivity.playEffect;
import static com.svw.chess.HomeActivity.selectMusic;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.svw.chess.R;
import com.svw.chess.Utils.ChessSoundPlayUtil;

/**
 * Created by 77304 on 2021/4/19.
 */

public class ChessCommonDialog extends Dialog {
    public Button posBtn, negBtn;
    public TextView tv_content;
    public String title, content;

    public ChessCommonDialog(Context context, String title, String content) {
        super(context, R.style.CustomDialog);
        this.title = title;
        this.content = content;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chess_dialog_common);
        setCanceledOnTouchOutside(false);
        initView();
        initEvent();
    }

    private void initView() {
        posBtn = (Button) findViewById(R.id.posBtn);
        negBtn = (Button) findViewById(R.id.negBtn);


        tv_content = (TextView) findViewById(R.id.tv_content);
        tv_content.setText(content);

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

    public ChessCommonDialog setOnClickBottomListener(OnClickBottomListener onClickBottomListener) {
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
