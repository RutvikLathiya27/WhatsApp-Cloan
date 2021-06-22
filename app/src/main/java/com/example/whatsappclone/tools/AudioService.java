package com.example.whatsappclone.tools;

import android.content.Context;
import android.media.MediaPlayer;

public class AudioService {

    private Context context;
    private MediaPlayer tmpMediaPlayer;

    public AudioService(Context context) {
        this.context = context;
    }

    public void playAudioFromUrl(String url, final OnPlayCallBack onPlayCallBack){

        if (tmpMediaPlayer!=null){

            tmpMediaPlayer.stop();
        }

        MediaPlayer mediaPlayer =new MediaPlayer();
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
            mediaPlayer.start();

            tmpMediaPlayer =mediaPlayer;
        }catch (Exception e){
            e.printStackTrace();
        }

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
                onPlayCallBack.onFinidshed();
            }
        });
    }

    public interface OnPlayCallBack{

        void onFinidshed();
    }
}
