package com.svw.chess.Utils;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;
import android.util.Log;

import com.zyn.common.AudioMessageEvent;

import org.greenrobot.eventbus.EventBus;

public class ChessAudioFocusManager {

    private AudioManager audioManager;
    private AudioFocusRequest audioFocusRequest;
    private ChessAudioFocusChangeListener focusChangeListener;

    public ChessAudioFocusManager(Context context, ChessAudioFocusChangeListener listener) {
        this.audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        this.focusChangeListener = listener;
        initAudioFocusRequest();
    }

    private void initAudioFocusRequest() {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(audioAttributes)
                    .setOnAudioFocusChangeListener(focusChange -> {
                        Log.e("AudioFocus", "Focus change: " + focusChange);
                        switch (focusChange) {
                            case AudioManager.AUDIOFOCUS_GAIN:
                                if (focusChangeListener != null) focusChangeListener.onFocusGain();
                                break;
                            case AudioManager.AUDIOFOCUS_LOSS:
                                EventBus.getDefault().post(new AudioMessageEvent("mute"));

                                if (focusChangeListener != null) focusChangeListener.onFocusLoss();
                                break;
                            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                                if (focusChangeListener != null) focusChangeListener.onFocusLossTransient();
                                break;
                            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                                if (focusChangeListener != null) focusChangeListener.onCanDuck();
                                break;
                        }
                    })
                    .build();
        }
    }

    public boolean requestAudioFocus() {
        int result = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            result = audioManager.requestAudioFocus(audioFocusRequest);
        }
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    public boolean abandonAudioFocus() {
        int result = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            result = audioManager.abandonAudioFocusRequest(audioFocusRequest);
        }
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }
}
