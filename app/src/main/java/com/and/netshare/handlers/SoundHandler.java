package com.and.netshare.handlers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.SoundPool;

import com.and.netshare.MainActivity;
import com.and.netshare.R;

public class SoundHandler {
    private static SoundPool sound1;
    private static int soundID1;

    private static void initSound(Context context) {
        sound1 = new SoundPool.Builder().build();
        soundID1 = sound1.load(context, R.raw.ui_click, 1);
    }

    public static void playSoundClick(Context context) {
        initSound(context);
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
        sound1.play(soundID1, 0.3f, 0.3f, 1, 0, 1);
    }
}
