package com.lucsoninfotech.cmi.cmiworld.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.lucsoninfotech.cmi.cmiworld.R;
import com.lucsoninfotech.cmi.cmiworld.other.Constant;
import com.squareup.picasso.Picasso;

public class MediaViewActivty extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_view_activty);

        VideoView videoview = findViewById(R.id.videoView);
        ImageView imageview = findViewById(R.id.imageview);

        Bundle b = getIntent().getExtras();
        String type = b.getString("type");
        if (Constant.isOnline(getApplicationContext())) {
            if (type.equals("1")) {
                videoview.setVisibility(View.GONE);
                imageview.setVisibility(View.VISIBLE);
                String image = b.getString("image");
                Picasso.get().load(image)
                        .placeholder(R.drawable.bg_timeline).into(imageview);
            } else {
                imageview.setVisibility(View.GONE);
                videoview.setVisibility(View.VISIBLE);
                String video = b.getString("video");
                videoview.setVideoPath(video);
                MediaController ctlr = new MediaController(MediaViewActivty.this);
                ctlr.setMediaPlayer(videoview);
                videoview.setMediaController(ctlr);
                videoview.start();
                videoview.requestFocus();

            }
        } else {
            Toast.makeText(MediaViewActivty.this, "Please Check Your Network Connection", Toast.LENGTH_SHORT).show();
        }


    }

  /*  @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }*/
}
