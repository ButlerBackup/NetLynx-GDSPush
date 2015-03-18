package com.nextlynxtech.gdspushnotification;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.VideoView;

import com.nextlynxtech.gdspushnotification.classes.Utils;
import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;
import it.sephiroth.android.library.imagezoom.ImageViewTouch;

//Fullscreen image video class
public class FullScreenImageActivity extends ActionBarActivity {
    @InjectView(R.id.ivImageZoom)
    ImageViewTouch ivImageZoom;

    @InjectView(R.id.vvVideoZoom)
    VideoView vvVideoZoom;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image);
        ButterKnife.inject(FullScreenImageActivity.this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        if (getIntent().hasExtra("image")) { //Get file type if it is image
            Picasso.with(FullScreenImageActivity.this).load(new File(new Utils(FullScreenImageActivity.this).createFolder(), getIntent().getStringExtra("image"))).into(ivImageZoom);
            vvVideoZoom.setVisibility(View.GONE); //Disable video loading
        } else if (getIntent().hasExtra("video")) { //Get file type if it is video
            ivImageZoom.setVisibility(View.GONE); //Disable image loading
            vvVideoZoom.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setLooping(true); //Video will loop when end
                }
            });
			//Set video file path and play
            vvVideoZoom.setVideoURI(Uri.parse(new Utils(FullScreenImageActivity.this).createFolder() + "/" + getIntent().getStringExtra("video")));
            vvVideoZoom.start();
        }
    }

	//Back button on the action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
