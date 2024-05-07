package com.svw.chess.Utils;

public interface ChessAudioFocusChangeListener {
    void onFocusGain(); //获得焦点
    void onFocusLoss();
    void onFocusLossTransient();
    void onCanDuck();
}