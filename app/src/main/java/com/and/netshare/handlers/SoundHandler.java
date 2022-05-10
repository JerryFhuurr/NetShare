package com.and.netshare.handlers;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;

import com.and.netshare.R;

import java.util.HashMap;

public class SoundHandler {
    private static SoundPool soundPool;
    private static HashMap<Integer, Integer> sounds = new HashMap<>();

    public static void initSound(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(2).setAudioAttributes(audioAttributes).build();
        } else {
            soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        }
        sounds.put(0, soundPool.load(context, R.raw.ui_click, 1));
        sounds.put(1, soundPool.load(context, R.raw.ui_click2, 1));

        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            Log.e("sound load error", e.getMessage());
        }
    }

    /*
            soundPool.play(
                    soundID,
                    0.1f,      //左耳道音量【0~1】
                    0.5f,      //右耳道音量【0~1】
                    0,         //播放优先级【0表示最低优先级】
                    0,         //循环模式【0表示循环一次，-1表示一直循环，其他表示数字+1表示当前数字对应的循环次数】
                    1          //播放速度【1是正常，范围从0~2】
            );
             */
    public static void playSoundClick() {
        soundPool.play(sounds.get(0), 1, 1, 1, 0, 1);
    }

    public static void playMainPageSoundClick(){
        soundPool.play(sounds.get(1), 1, 1, 1, 0, 1);
    }
}
