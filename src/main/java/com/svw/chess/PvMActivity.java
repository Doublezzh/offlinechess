package com.svw.chess;

import static com.svw.chess.AICore.AI.getBestMove;
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
import android.os.Looper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.svw.chess.AICore.KnowledgeBase;
import com.svw.chess.AICore.Move;
import com.svw.chess.AICore.TransformTable;
import com.svw.chess.ChessMove.Rule;
import com.svw.chess.CustomDialog.ChessCommonDialog;
import com.svw.chess.CustomDialog.RetryDialog;
import com.svw.chess.CustomDialog.SettingDialog_PvM;
import com.svw.chess.CustomView.ChessView;
import com.svw.chess.CustomView.RoundView;
import com.svw.chess.Info.ChessInfo;
import com.svw.chess.Info.InfoSet;
import com.svw.chess.Info.Pos;
import com.svw.chess.Info.SaveInfo;
import com.svw.chess.Utils.ChessSoundPlayUtil;
import com.zyn.common.LogUtils;
import com.zyn.common.Toaster;


public class PvMActivity extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener {
    public RelativeLayout relativeLayout;
    public ChessInfo chessInfo;
    public InfoSet infoSet;
    public ChessView chessView;
    public RoundView roundView;

    public static KnowledgeBase knowledgeBase;
    public static TransformTable transformTable;


    //是否为竖屏
    private boolean isLandscape = true;
    public Thread AIThread = new Thread(new Runnable() {
        @Override
        public void run() {
            AIMove(chessInfo.IsRedGo, chessInfo.status);
        }
    });
    private ImageButton btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pvm_chess);
        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);


        if (SaveInfo.fileIsExists("ChessInfo_pvm.bin")) {
            try {
                chessInfo = SaveInfo.DeserializeChessInfo("ChessInfo_pvm.bin");
            } catch (Exception e) {
                LogUtils.e("chen", e.toString());
            }
        } else {
            chessInfo = new ChessInfo();
        }

        if (SaveInfo.fileIsExists("InfoSet_pvm.bin")) {
            try {
                infoSet = SaveInfo.DeserializeInfoSet("InfoSet_pvm.bin");
            } catch (Exception e) {
                LogUtils.e("chen", e.toString());
            }
        } else {
            infoSet = new InfoSet();
        }

        if (SaveInfo.fileIsExists("TransformTable.bin")) {
            try {
                transformTable = SaveInfo.DeserializeTransformTable("TransformTable.bin");
            } catch (Exception e) {
                LogUtils.e("chen", e.toString());
            }
        } else {
            transformTable = new TransformTable();
            setting.isPlayerRed = true;
        }

        if (SaveInfo.fileIsExists("KnowledgeBase.bin")) {
            try {
                knowledgeBase = SaveInfo.DeserializeKnowledgeBase("KnowledgeBase.bin");
            } catch (Exception e) {
                LogUtils.e("chen", e.toString());
            }
        } else {
            knowledgeBase = new KnowledgeBase();

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
                    Pos realPos = Rule.reversePos(new Pos(chessInfo.Select[0], chessInfo.Select[1]), chessInfo.IsReverse);
                    int i = realPos.x, j = realPos.y;

                    if (i >= 0 && i <= 8 && j >= 0 && j <= 9) {
                        if (chessInfo.IsRedGo == true && setting.isPlayerRed == true) {
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
                                        Toast toast = Toast.makeText(PvMActivity.this, "帅被将军", Toast.LENGTH_SHORT);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
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

                                        AIThread = new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    Thread.sleep(400);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }

                                                chessInfo.isMachine = true;

                                                AIMove(chessInfo.IsRedGo, chessInfo.status);
                                            }
                                        });
                                        AIThread.start();
                                    }
                                }
                            }
                        } else if (chessInfo.IsRedGo == false && setting.isPlayerRed == false) {
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

                                        Toaster.showShort("将被将军");


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


                                            try {
                                                Thread.sleep(1000);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        } else if (key == 2) {
                                            chessInfo.status = 2;

                                            Toaster.showLong("黑方获得胜利");

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

                                        AIThread = new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    Thread.sleep(400);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }

                                                chessInfo.isMachine = true;

                                                AIMove(chessInfo.IsRedGo, chessInfo.status);
                                            }
                                        });
                                        AIThread.start();
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

        playEffect(selectMusic);


        if (view.getId() == R.id.rl_retry) {
            if (chessInfo.isMachine == true && chessInfo.status == 1) {
                Toaster.showShort("请等待思考中");
                return;
            }
            final RetryDialog retryDialog = new RetryDialog(this);
            retryDialog.setOnClickBottomListener(new RetryDialog.OnClickBottomListener() {

                @Override
                public void onPositiveClick() {
                    try {
                        chessInfo.setInfo(new ChessInfo());
                        infoSet.newInfo();
                        transformTable.transformInfos.clear();
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    if (setting.isPlayerRed != retryDialog.isPlayerRed) {
                        setting.isPlayerRed = retryDialog.isPlayerRed;
                        editor.putBoolean("isPlayerRed", retryDialog.isPlayerRed);
                        editor.commit();
                    }
                    retryDialog.dismiss();
                    chessInfo.IsReverse = (setting.isPlayerRed == true) ? false : true;
                    if (chessInfo.IsReverse == true) {
                        try {
                            infoSet.curInfo.setInfo(chessInfo);
                        } catch (CloneNotSupportedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (setting.isPlayerRed == false) {
                        try {
                            Thread.sleep(400);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        AIFirstGo();
                    }
                }

                @Override
                public void onNegtiveClick() {

                    retryDialog.dismiss();
                }
            });
            retryDialog.show();
        } else if (view.getId() == R.id.rl_recall) {
            if (chessInfo.isMachine == true && chessInfo.status == 1) {
                Toaster.showShort("请等待思考中");

                return;
            }
            int cnt = 0;
            int total = 2;
            if (chessInfo.status == 2 && chessInfo.isMachine == true) {
                total = 1;
            }
            if (infoSet.preInfo.size() < total) {
                return;
            }
            while (!infoSet.preInfo.empty()) {
                ChessInfo tmp = infoSet.preInfo.pop();
                cnt++;
                try {
                    infoSet.recallZobristInfo(chessInfo.ZobristKeyCheck);
                    chessInfo.setInfo(tmp);
                    infoSet.curInfo.setInfo(tmp);
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
                if (cnt >= total) {
                    break;
                }
            }
        } else if (view.getId() == R.id.chess_setting) {

            // chessView.toggleBoardRotation(); // 旋转棋盘

            final SettingDialog_PvM settingDialog_pvm = new SettingDialog_PvM(this, isLandscape);
            settingDialog_pvm.setOnClickBottomListener(new SettingDialog_PvM.OnClickBottomListener() {
                @Override
                public void onPositiveClick() {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    boolean flag = false;
                    if (setting.isMusicPlay != settingDialog_pvm.isMusicPlay) {
                        setting.isMusicPlay = settingDialog_pvm.isMusicPlay;
                        if (setting.isMusicPlay) {
                            playMusic(backMusic);
                        } else {
                            stopMusic(backMusic);
                        }
                        editor.putBoolean("isMusicPlay", settingDialog_pvm.isMusicPlay);
                        flag = true;
                    }
                    if (setting.isEffectPlay != settingDialog_pvm.isEffectPlay) {
                        setting.isEffectPlay = settingDialog_pvm.isEffectPlay;
                        editor.putBoolean("isEffectPlay", settingDialog_pvm.isEffectPlay);
                        flag = true;
                    }
                    if (setting.mLevel != settingDialog_pvm.mLevel) {
                        setting.mLevel = settingDialog_pvm.mLevel;
                        editor.putInt("mLevel", settingDialog_pvm.mLevel);
                        flag = true;
                    }
                    if (flag) {
                        editor.commit();
                    }
                    settingDialog_pvm.dismiss();
                }

                @Override
                public void onNegtiveClick() {
                    settingDialog_pvm.dismiss();
                }

                @Override
                public void onInvertSelected(boolean isSelected) {
                    isLandscape = isSelected;
                    chessView.toggleBoardRotation(isLandscape);
                }
            });
            settingDialog_pvm.show();
        } else if (view.getId() == R.id.btn_back) {
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

    public void AIMove(boolean isRed, int status) {
        if (status != 1) return;
        int depth = setting.mLevel * 2;
        if (isRed == true) {
            Move move = knowledgeBase.readBestMoves(chessInfo.ZobristKey, chessInfo.ZobristKeyCheck, depth);
            if (move == null) {
                long t1 = System.currentTimeMillis();
                move = getBestMove(chessInfo.piece, true, depth, chessInfo.ZobristKey, chessInfo.ZobristKeyCheck, infoSet.ZobristInfo);
                long t2 = System.currentTimeMillis();
                LogUtils.i("AI思考", "AI思考的时间：" + String.valueOf(t2 - t1) + "ms");
                knowledgeBase.saveBestMove(chessInfo.ZobristKey, chessInfo.ZobristKeyCheck, depth, move);
            }

            Pos fromPos = move.fromPos;
            Pos toPos = move.toPos;
            int tmp = chessInfo.piece[toPos.y][toPos.x];
            chessInfo.piece[toPos.y][toPos.x] = chessInfo.piece[fromPos.y][fromPos.x];
            chessInfo.piece[fromPos.y][fromPos.x] = 0;
            chessInfo.IsChecked = false;
            chessInfo.IsRedGo = false;
            chessInfo.Select = new int[]{-1, -1};
            chessInfo.ret.clear();
            chessInfo.prePos = new Pos(fromPos.x, fromPos.y);
            chessInfo.curPos = new Pos(toPos.x, toPos.y);

            chessInfo.updateAllInfo(chessInfo.prePos, chessInfo.curPos, chessInfo.piece[toPos.y][toPos.x], tmp);
            chessInfo.isMachine = false;

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
                Looper.prepare();
                Toaster.showShort("将军");
                Looper.loop();
            } else if (key == 2) {

                chessInfo.status = 2;
                Looper.prepare();
                Toaster.showLong("红方获得胜利");
                Looper.loop();
            }

            if (chessInfo.status == 1) {
                if (chessInfo.peaceRound >= 60) {
                    chessInfo.status = 2;
                    Looper.prepare();

                    Toaster.showLong("双方60回合内未吃子，此乃和棋");

                    ;
                    Looper.loop();
                } else if (chessInfo.attackNum_B == 0 && chessInfo.attackNum_R == 0) {
                    chessInfo.status = 2;
                    Looper.prepare();

                    Toaster.showLong("双方都无攻击性棋子，此乃和棋");


                    Looper.loop();
                } else if (infoSet.ZobristInfo.get(chessInfo.ZobristKeyCheck) >= 4) {
                    chessInfo.status = 2;
                    Looper.prepare();

                    Toaster.showLong("重复局面出现4次，此乃和棋");


                    Looper.loop();
                }
            }
        } else {
            Move move = knowledgeBase.readBestMoves(chessInfo.ZobristKey, chessInfo.ZobristKeyCheck, depth);
            if (move == null) {
                long t1 = System.currentTimeMillis();
                move = getBestMove(chessInfo.piece, false, depth, chessInfo.ZobristKey, chessInfo.ZobristKeyCheck, infoSet.ZobristInfo);
                long t2 = System.currentTimeMillis();
                LogUtils.i("AI思考", "AI思考的时间：" + String.valueOf(t2 - t1) + "ms");
                knowledgeBase.saveBestMove(chessInfo.ZobristKey, chessInfo.ZobristKeyCheck, depth, move);
            }

            Pos fromPos = move.fromPos;
            Pos toPos = move.toPos;
            int tmp = chessInfo.piece[toPos.y][toPos.x];
            chessInfo.piece[toPos.y][toPos.x] = chessInfo.piece[fromPos.y][fromPos.x];
            chessInfo.piece[fromPos.y][fromPos.x] = 0;
            chessInfo.IsChecked = false;
            chessInfo.IsRedGo = true;
            chessInfo.Select = new int[]{-1, -1};
            chessInfo.ret.clear();
            chessInfo.prePos = new Pos(fromPos.x, fromPos.y);
            chessInfo.curPos = new Pos(toPos.x, toPos.y);

            chessInfo.updateAllInfo(chessInfo.prePos, chessInfo.curPos, chessInfo.piece[toPos.y][toPos.x], tmp);
            chessInfo.isMachine = false;

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

                Looper.prepare();

                Toaster.showShort("将军");

                Looper.loop();
            } else if (key == 2) {

                chessInfo.status = 2;
                Looper.prepare();

                Toaster.showLong("黑方获得胜利");


                Looper.loop();
            }

            if (chessInfo.status == 1) {
                if (chessInfo.peaceRound >= 60) {
                    chessInfo.status = 2;
                    Looper.prepare();
                    Toaster.showLong("双方60回合内未吃子，此乃和棋");


                    Looper.loop();
                } else if (chessInfo.attackNum_B == 0 && chessInfo.attackNum_R == 0) {
                    chessInfo.status = 2;
                    Looper.prepare();
                    Toaster.showLong("双方都无攻击性棋子，此乃和棋");

                    Looper.loop();
                } else if (infoSet.ZobristInfo.get(chessInfo.ZobristKeyCheck) >= 4) {
                    chessInfo.status = 2;
                    Looper.prepare();

                    Toaster.showLong("重复局面出现4次，此乃和棋");
                    Looper.loop();
                }
            }
        }
    }

    public void AIFirstGo() {
        Move[] firstMoves = new Move[8];
        firstMoves[0] = new Move(new Pos(1, 9), new Pos(2, 7));     //走马
        firstMoves[1] = new Move(new Pos(7, 9), new Pos(6, 7));     //走马
        firstMoves[2] = new Move(new Pos(2, 9), new Pos(4, 7));     //走相
        firstMoves[3] = new Move(new Pos(6, 9), new Pos(4, 7));     //走相
        firstMoves[4] = new Move(new Pos(1, 7), new Pos(4, 7));     //走炮
        firstMoves[5] = new Move(new Pos(7, 7), new Pos(4, 7));     //走炮
        firstMoves[6] = new Move(new Pos(2, 6), new Pos(2, 5));     //走兵
        firstMoves[7] = new Move(new Pos(6, 6), new Pos(6, 5));     //走兵

        int num = (int) (Math.random() * firstMoves.length);
        //LogUtils.e("chen",String.valueOf(num));
        Move firstMove = firstMoves[num];
        Pos fromPos = firstMove.fromPos;
        Pos toPos = firstMove.toPos;
        int tmp = chessInfo.piece[toPos.y][toPos.x];
        chessInfo.piece[toPos.y][toPos.x] = chessInfo.piece[fromPos.y][fromPos.x];
        chessInfo.piece[fromPos.y][fromPos.x] = 0;
        chessInfo.IsChecked = false;
        chessInfo.IsRedGo = false;
        chessInfo.Select = new int[]{-1, -1};
        chessInfo.ret.clear();
        chessInfo.prePos = new Pos(fromPos.x, fromPos.y);
        chessInfo.curPos = new Pos(toPos.x, toPos.y);

        chessInfo.updateAllInfo(chessInfo.prePos, chessInfo.curPos, chessInfo.piece[toPos.y][toPos.x], tmp);

        try {
            infoSet.pushInfo(chessInfo);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        playEffect(clickMusic);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
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
            SaveInfo.SerializeChessInfo(chessInfo, "ChessInfo_pvm.bin");
            SaveInfo.SerializeInfoSet(infoSet, "InfoSet_pvm.bin");
            SaveInfo.SerializeKnowledgeBase(knowledgeBase, "KnowledgeBase.bin");
            SaveInfo.SerializeTransformTable(transformTable, "TransformTable.bin");
        } catch (Exception e) {
            LogUtils.e("chen", e.toString());
        }
        super.onStop();
    }

    @Override
    protected void onStart() {
        playMusic(backMusic);
        if (chessInfo.isMachine) {
            if (AIThread.isAlive() == false) {
                AIThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        AIMove(chessInfo.IsRedGo, chessInfo.status);
                    }
                });
                AIThread.start();
            }
        }
        super.onStart();
    }


}
