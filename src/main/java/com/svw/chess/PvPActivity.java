package com.svw.chess;

import static com.svw.chess.HomeActivity.MIN_CLICK_DELAY_TIME;
import static com.svw.chess.HomeActivity.backMusic;
import static com.svw.chess.HomeActivity.clickMusic;
import static com.svw.chess.HomeActivity.curClickTime;
import static com.svw.chess.HomeActivity.lastClickTime;
import static com.svw.chess.HomeActivity.playEffect;
import static com.svw.chess.HomeActivity.playMusic;
import static com.svw.chess.HomeActivity.selectMusic;
import static com.svw.chess.HomeActivity.setting;
import static com.svw.chess.HomeActivity.sharedPreferences;
import static com.svw.chess.HomeActivity.stopMusic;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.svw.chess.ChessMove.Rule;
import com.svw.chess.CustomDialog.ChessCommonDialog;
import com.svw.chess.CustomDialog.PvpSettingDialog;
import com.svw.chess.CustomView.ChessView;
import com.svw.chess.CustomView.RoundView;
import com.svw.chess.Info.ChessInfo;
import com.svw.chess.Info.InfoSet;
import com.svw.chess.Info.Pos;
import com.svw.chess.Info.SaveInfo;
import com.svw.chess.Utils.ChessSoundPlayUtil;
import com.zyn.common.LogUtils;
import com.zyn.common.Toaster;

//todo 双人对决
public class PvPActivity extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener {
    private RelativeLayout relativeLayout;
    private ChessInfo chessInfo;
    private InfoSet infoSet;
    private ChessView chessView;
    private RoundView roundView;
    private ImageButton btn_back;

    private boolean isLandscape = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pvp);
        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);


        if (SaveInfo.fileIsExists("ChessInfo_pvp.bin")) {
            try {
                chessInfo = SaveInfo.DeserializeChessInfo("ChessInfo_pvp.bin");
            } catch (Exception e) {
                LogUtils.e("chen", e.toString());
            }
        } else {
            chessInfo = new ChessInfo();
        }

        if (SaveInfo.fileIsExists("InfoSet_pvp.bin")) {
            try {
                infoSet = SaveInfo.DeserializeInfoSet("InfoSet_pvp.bin");
            } catch (Exception e) {
                LogUtils.e("chen", e.toString());
            }
        } else {
            infoSet = new InfoSet();
        }

        roundView = new RoundView(this, chessInfo);


        // 获取到屏幕密度，用于将dp转换为px
        float density = getResources().getDisplayMetrics().density;
        int marginInDp = 40;
        int marginInPx = (int) (marginInDp * density);
        RelativeLayout.LayoutParams roundViewParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);


        roundViewParams.addRule(RelativeLayout.LEFT_OF, R.id.chessView);
        roundViewParams.addRule(RelativeLayout.CENTER_VERTICAL);
        roundViewParams.rightMargin = marginInPx; // 设置左边距
        roundView.setLayoutParams(roundViewParams);
        roundView.setId(R.id.roundView);


        chessView = new ChessView(this, chessInfo);
        chessView.setOnTouchListener(this);

        RelativeLayout.LayoutParams chessViewParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        chessViewParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        chessViewParams.setMargins(0, 10, 20, 10); // 设置左、上、右、下的边距

        chessView.setLayoutParams(chessViewParams);
        chessView.setId(R.id.chessView);


        LayoutInflater inflater = LayoutInflater.from(this);
        RelativeLayout buttonGroup = (RelativeLayout) inflater.inflate(R.layout.button_group, null, false);
        RelativeLayout.LayoutParams paramsV = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);

// 设置按钮组位于 chessView 的右侧
        paramsV.addRule(RelativeLayout.RIGHT_OF, R.id.chessView);

// 设置按钮组垂直居中
        paramsV.addRule(RelativeLayout.CENTER_VERTICAL);

// 应用布局参数
        buttonGroup.setLayoutParams(paramsV);

// 假设 relativeLayout 是您的根布局
        relativeLayout.addView(buttonGroup);
        relativeLayout.addView(chessView);
        relativeLayout.addView(roundView);

        ImageButton setting = buttonGroup.findViewById(R.id.chess_setting);
        RelativeLayout rl_retry = buttonGroup.findViewById(R.id.rl_retry);
        RelativeLayout rl_recall = buttonGroup.findViewById(R.id.rl_recall);
        setting.setOnClickListener(this);
        rl_retry.setOnClickListener(this);
        rl_recall.setOnClickListener(this);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        lastClickTime = System.currentTimeMillis();
        if (lastClickTime - curClickTime < MIN_CLICK_DELAY_TIME) {
            return false;
        }
        curClickTime = lastClickTime;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();
            if (chessInfo.status == 1) {
                if (x >= 0 && y >= 0) {
                    chessInfo.Select = getPos(event);
                    int i = chessInfo.Select[0], j = chessInfo.Select[1];
                    if (i >= 0 && i <= 8 && j >= 0 && j <= 9) {
                        if (chessInfo.IsRedGo == true) {
                            if (chessInfo.IsChecked == false) {
                                if (chessInfo.piece[j][i] >= 8 && chessInfo.piece[j][i] <= 14) {
                                    chessInfo.prePos = new Pos(i, j);
                                    chessInfo.IsChecked = true;
                                    chessInfo.ret = Rule.PossibleMoves(chessInfo.piece, i, j, chessInfo.piece[j][i]);
                                     playEffect(selectMusic);
                                }
                            } else {
                                if (chessInfo.piece[j][i] >= 8 && chessInfo.piece[j][i] <= 14) {
                                    chessInfo.prePos = new Pos(i, j);
                                    chessInfo.ret = Rule.PossibleMoves(chessInfo.piece, i, j, chessInfo.piece[j][i]);
                                     playEffect(selectMusic);
                                } else if (chessInfo.ret.contains(new Pos(i, j))) {
                                    int tmp = chessInfo.piece[j][i];
                                    chessInfo.piece[j][i] = chessInfo.piece[chessInfo.prePos.y][chessInfo.prePos.x];
                                    chessInfo.piece[chessInfo.prePos.y][chessInfo.prePos.x] = 0;
                                    if (Rule.isKingDanger(chessInfo.piece, true)) {
                                        chessInfo.piece[chessInfo.prePos.y][chessInfo.prePos.x] = chessInfo.piece[j][i];
                                        chessInfo.piece[j][i] = tmp;

                                        Toaster.showLong("帅被将军");

                                    } else {
                                        chessInfo.IsChecked = false;
                                        chessInfo.IsRedGo = false;
                                        chessInfo.curPos = new Pos(i, j);

                                        chessInfo.updateAllInfo(chessInfo.prePos, chessInfo.curPos, chessInfo.piece[j][i], tmp);

                                        try {
                                            infoSet.pushInfo(chessInfo);
                                        } catch (CloneNotSupportedException e) {
                                            e.printStackTrace();
                                        }

                                        playEffect(clickMusic);
                                        int key = 0;
                                        if (Rule.isKingDanger(chessInfo.piece, false)) {
                                            key = 1;
                                        }
                                        if (Rule.isDead(chessInfo.piece, false)) {
                                            key = 2;
                                        }
                                        if (key == 1) {

                                            Toaster.showShort("将军");


                                        } else if (key == 2) {

                                            chessInfo.status = 2;
                                            Toaster.showLong("红方获得胜利");

                                        }

                                        if (chessInfo.status == 1) {
                                            if (chessInfo.peaceRound >= 60) {
                                                chessInfo.status = 2;
                                                Toaster.showLong("双方60回合内未吃子，此乃和棋");
                                            } else if (chessInfo.attackNum_B == 0 && chessInfo.attackNum_R == 0) {
                                                chessInfo.status = 2;
                                                Toaster.showLong("双方都无攻击性棋子，此乃和棋");

                                            } else if (infoSet.ZobristInfo.get(chessInfo.ZobristKeyCheck) >= 4) {
                                                chessInfo.status = 2;
                                                Toaster.showLong("重复局面出现4次，此乃和棋");
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            if (chessInfo.IsChecked == false) {
                                if (chessInfo.piece[j][i] >= 1 && chessInfo.piece[j][i] <= 7) {
                                    chessInfo.prePos = new Pos(i, j);
                                    chessInfo.IsChecked = true;
                                    chessInfo.ret = Rule.PossibleMoves(chessInfo.piece, i, j, chessInfo.piece[j][i]);
                                     playEffect(selectMusic);
                                }
                            } else {
                                if (chessInfo.piece[j][i] >= 1 && chessInfo.piece[j][i] <= 7) {
                                    chessInfo.prePos = new Pos(i, j);
                                    chessInfo.ret = Rule.PossibleMoves(chessInfo.piece, i, j, chessInfo.piece[j][i]);
                                     playEffect(selectMusic);
                                } else if (chessInfo.ret.contains(new Pos(i, j))) {
                                    int tmp = chessInfo.piece[j][i];
                                    chessInfo.piece[j][i] = chessInfo.piece[chessInfo.prePos.y][chessInfo.prePos.x];
                                    chessInfo.piece[chessInfo.prePos.y][chessInfo.prePos.x] = 0;
                                    if (Rule.isKingDanger(chessInfo.piece, false)) {
                                        chessInfo.piece[chessInfo.prePos.y][chessInfo.prePos.x] = chessInfo.piece[j][i];
                                        chessInfo.piece[j][i] = tmp;

                                        Toaster.showShort("双将被将军");


                                    } else {
                                        chessInfo.IsChecked = false;
                                        chessInfo.IsRedGo = true;
                                        chessInfo.curPos = new Pos(i, j);

                                        chessInfo.updateAllInfo(chessInfo.prePos, chessInfo.curPos, chessInfo.piece[j][i], tmp);

                                        try {
                                            infoSet.pushInfo(chessInfo);
                                        } catch (CloneNotSupportedException e) {
                                            e.printStackTrace();
                                        }

                                        playEffect(clickMusic);
                                        int key = 0;
                                        if (Rule.isKingDanger(chessInfo.piece, true)) {
                                            key = 1;
                                        }
                                        if (Rule.isDead(chessInfo.piece, true)) {
                                            key = 2;
                                        }
                                        if (key == 1) {

                                            Toaster.showShort("将军");
                                        } else if (key == 2) {

                                            chessInfo.status = 2;
                                            Toaster.showShort("黑方获得胜利");
                                        }

                                        if (chessInfo.status == 1) {
                                            if (chessInfo.peaceRound >= 60) {
                                                chessInfo.status = 2;
                                                Toaster.showShort("双方60回合内未吃子，此乃和棋");


                                            } else if (chessInfo.attackNum_B == 0 && chessInfo.attackNum_R == 0) {
                                                chessInfo.status = 2;

                                                Toaster.showShort("双方都无攻击性棋子，此乃和棋");

                                            } else if (infoSet.ZobristInfo.get(chessInfo.ZobristKeyCheck) >= 4) {
                                                chessInfo.status = 2;
                                                Toaster.showShort("重复局面出现4次，此乃和棋");

                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public int[] getPos(MotionEvent e) {
        int[] pos = new int[2];
        double x = e.getX();
        double y = e.getY();

        // 棋盘旋转后的边距和格子尺寸需要重新计算
        int[] dis;

        if (chessView.isRotated) {
            int boardWidth = chessView.Board_width;
            // 旋转90度后的边距和格子尺寸
            dis = new int[]{
                    chessView.Scale(15), // 左侧边距现在变为顶部边距，取决于原始的设置
                    chessView.Scale(0), // 上方边距现在变为左侧边距，可能需要调整
                    chessView.Scale(50), // 有效触摸区域的容错值
                    chessView.Scale(56) // 棋盘格子的尺寸
            };

            // 对坐标进行旋转后的调整
            float tempX = (float) x;
            x = boardWidth - y - dis[1]; // 调整y坐标，并减去新的左侧边距
            y = tempX - dis[0]; // x成为新的y，减去顶部边距
        } else {
            // 如果棋盘没有旋转，使用原始边距逻辑
            dis = new int[]{
                    chessView.Scale(0), // 左侧边距
                    chessView.Scale(15), // 顶部边距
                    chessView.Scale(50), // 有效触摸区域的容错值
                    chessView.Scale(56) // 棋盘格子的尺寸
            };

            // 调整原始坐标，减去边距
            x = x - dis[0];
            y = y - dis[1];
        }

        // 计算触摸点所在的格子，适用于旋转和未旋转的情况
        if ((x % dis[3] <= dis[2]) && (y % dis[3] <= dis[2])) {
            pos[0] = (int) Math.floor(x / dis[3]);
            pos[1] = (int) Math.floor(y / dis[3]);
            if (pos[0] >= 9 || pos[1] >= 10) {
                pos[0] = pos[1] = -1;
            }
        } else {
            pos[0] = pos[1] = -1;
        }
        return pos;
    }

    @Override
    public void onClick(View view) {
        lastClickTime = System.currentTimeMillis();
        if (lastClickTime - curClickTime < MIN_CLICK_DELAY_TIME) {
            return;
        }
        curClickTime = lastClickTime;


        if (view.getId() == R.id.rl_retry) {
            final ChessCommonDialog retryDialog = new ChessCommonDialog(this, "新局", "确认要开始一个新局吗");
            retryDialog.setOnClickBottomListener(new ChessCommonDialog.OnClickBottomListener() {

                @Override
                public void onPositiveClick() {

                    try {
                        chessInfo.setInfo(new ChessInfo());
                        infoSet.newInfo();
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                    retryDialog.dismiss();
                }

                @Override
                public void onNegtiveClick() {

                    retryDialog.dismiss();
                }
            });
            retryDialog.show();
        } else if (view.getId() == R.id.rl_recall) {
            playEffect(selectMusic);
            if (!infoSet.preInfo.empty()) {
                ChessInfo tmp = infoSet.preInfo.pop();
                try {
                    infoSet.recallZobristInfo(chessInfo.ZobristKeyCheck);
                    chessInfo.setInfo(tmp);
                    infoSet.curInfo.setInfo(tmp);
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
            }
        } else if (view.getId() == R.id.chess_setting) {
            final PvpSettingDialog settingDialog = new PvpSettingDialog(this, isLandscape);
            settingDialog.setOnClickBottomListener(new PvpSettingDialog.OnClickBottomListener() {
                @Override
                public void onPositiveClick() {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
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

                @Override
                public void onInvertSelected(boolean isSelected) {
                    isLandscape = isSelected;
                    chessView.toggleBoardRotation(isLandscape);

                }
            });
            settingDialog.show();
        } else if (view.getId() == R.id.btn_back) {
            playEffect(selectMusic);
            final ChessCommonDialog backDialog = new ChessCommonDialog(this, "返回", "确认要返回吗");
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
            final ChessCommonDialog backDialog = new ChessCommonDialog(this, "返回", "确认要返回吗");
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
    protected void onPause() {
        stopMusic(backMusic);
        super.onPause();
    }

    @Override
    protected void onStop() {
        try {
            SaveInfo.SerializeChessInfo(chessInfo, "ChessInfo_pvp.bin");
            SaveInfo.SerializeInfoSet(infoSet, "InfoSet_pvp.bin");
        } catch (Exception e) {
            LogUtils.e("chen", e.toString());
        }
        super.onStop();
    }

    @Override
    protected void onStart() {
        playMusic(backMusic);
        super.onStart();
    }
}
