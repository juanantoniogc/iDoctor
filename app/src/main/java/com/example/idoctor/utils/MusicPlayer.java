package com.example.idoctor.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import com.example.idoctor.R;

public final class MusicPlayer {

    private static MediaPlayer reproductor;

    private MusicPlayer() {
        // Utilidad sin instancias.
    }

    public static synchronized void reproducirInicio(Context context) {
        detener();

        Context appContext = context.getApplicationContext();
        Uri uri = Uri.parse("android.resource://" + appContext.getPackageName() + "/" + R.raw.inicio);
        reproductor = MediaPlayer.create(appContext, uri);

        if (reproductor == null) {
            return;
        }

        reproductor.setOnCompletionListener(mediaPlayer -> detener());
        reproductor.start();
    }

    public static synchronized void detener() {
        if (reproductor != null) {
            if (reproductor.isPlaying()) {
                reproductor.stop();
            }

            reproductor.release();
            reproductor = null;
        }
    }
}
