package com.lucsoninfotech.cmi.cmiworld.other;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.util.AttributeSet;
import android.view.View;

import com.lucsoninfotech.cmi.cmiworld.R;

import java.io.InputStream;

/**
 * Created by lucsonmacpc8 on 25/10/16.
 */
public class CustomGifViewAudio extends View {

    private Movie gifMovie;
    private int movieWidth, movieHeight;
    private long movieDuration;
    private long mMovieStart;

    public CustomGifViewAudio(Context context) {
        super(context);
        init(context);
    }

    public CustomGifViewAudio(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomGifViewAudio(Context context, AttributeSet attrs,
                              int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setFocusable(true);
        InputStream gifInputStream = context.getResources()
                .openRawResource(R.raw.gif_audio);

        gifMovie = Movie.decodeStream(gifInputStream);
        movieWidth = gifMovie.width();
        movieHeight = gifMovie.height();
        movieDuration = gifMovie.duration();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec,
                             int heightMeasureSpec) {
        setMeasuredDimension(movieWidth, movieHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        long now = android.os.SystemClock.uptimeMillis();
        if (mMovieStart == 0) {   // first time
            mMovieStart = now;
        }

        if (gifMovie != null) {

            int dur = gifMovie.duration();
            if (dur == 0) {
                dur = 2000;
            }

            int relTime = (int) ((now - mMovieStart) % dur);

            gifMovie.setTime(relTime);

            gifMovie.draw(canvas, 0, 0);
            invalidate();

        }

    }

}
