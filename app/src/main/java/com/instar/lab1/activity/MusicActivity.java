package com.instar.lab1.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


import com.instar.lab1.R;
import com.instar.lab1.customViews.VisualizerView;

import java.io.IOException;
import java.util.ArrayList;

public class MusicActivity extends AppCompatActivity {
    private ArrayList<String> listPath;
    private ArrayList<String> listName;
    private int currentTime;

    private int location;
    private boolean isPlaying;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.activity_music);
        listPath = intent.getStringArrayListExtra("ListPath");
        location = intent.getIntExtra("Location", 0);
        listName = intent.getStringArrayListExtra("NameMusic");
        currentTime = intent.getIntExtra("SeekTo",0);
        isPlaying = intent.getBooleanExtra("isPlaying", true);
        initView();
    }
    public static final int YOUR_REQUEST_CODE = 1;
    private ImageView imageDisc;
    private ImageView imageBack;
    private ImageView imagePlay;
    private ImageView imageNext;
    private ImageView imageAfter;
    private MediaPlayer dataMedia;
    private ImageView imagePause;

    private TextView textMusic;

    private SeekBar seekBar;
    private int totalDuration;
    private int currentPosition;

    private int partDuration;

    private Handler handler = new Handler();
    private Runnable updateSeekBar;

    private TextView startTime;
    private TextView endTime;

    private VisualizerView visualizerView;
    private Visualizer mVisualizer;

    public void initView() {
        imageDisc = findViewById(R.id.imageDisc);
        imageBack = findViewById(R.id.imageBack);
        imageNext = findViewById(R.id.imageNext);
        imageAfter = findViewById(R.id.imageAfter);
        imagePlay = findViewById(R.id.imagePlay);
        textMusic = findViewById(R.id.textMusic);
        imagePause = findViewById(R.id.imagePause);
        seekBar = findViewById(R.id.seekBar);
        startTime = findViewById(R.id.startTime);
        endTime = findViewById(R.id.endTime);
        visualizerView = findViewById(R.id.myvisualizerview);
        StartMusic(listPath.get(location));
        Log.i("com", String.valueOf(isPlaying));
        Bitmap albumArt = getAlbumArt(listPath.get(location));
        if (albumArt != null) {
            imageDisc.setImageBitmap(albumArt);
        }
        if(isPlaying) {
            dataMedia.start();
            startAnimation();
            imagePlay.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.baseline_pause_circle_24));
            int currentTimeMillis = dataMedia.getCurrentPosition();
            int minutes = (currentTimeMillis / 1000) / 60;
            int seconds = (currentTimeMillis / 1000) % 60;
            String currentTime = String.format("%02d:%02d", minutes, seconds);
            startTime.setText(currentTime);
        }
        if(currentTime != 0) dataMedia.seekTo(currentTime);
        textMusic.setText(listName.get(location));
        setEndTime();
        imagePlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dataMedia.isPlaying()) {
                    dataMedia.pause();
                    imagePlay.setImageDrawable(ContextCompat.getDrawable(MusicActivity.this, R.drawable.baseline_play_circle_filled_24));
                }
                else {
                    dataMedia.start();
                    imagePlay.setImageDrawable(ContextCompat.getDrawable(MusicActivity.this, R.drawable.baseline_pause_circle_24));
                }
            }
        });

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        mVisualizer.setEnabled(true);
        dataMedia.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mediaPlayer) {
                mVisualizer.setEnabled(false);
            }
        });


        imageNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(location < (listPath.size() - 1)) location++;
                else location = 0;
                StopSong();
                StartMusic(listPath.get(location));
                setVolumeControlStream(AudioManager.STREAM_MUSIC);
                mVisualizer.setEnabled(true);
                totalDuration = dataMedia.getDuration();
                partDuration = totalDuration / 100;
                Bitmap albumArt = getAlbumArt(listPath.get(location));
                if (albumArt != null) {
                    imageDisc.setImageBitmap(albumArt);
                }
                else  imageDisc.setImageDrawable(ContextCompat.getDrawable(MusicActivity.this, R.drawable.disk1));
                setEndTime();
                dataMedia.start();
                textMusic.setText(listName.get(location));
                startAnimation();
                imagePlay.setImageDrawable(ContextCompat.getDrawable(MusicActivity.this, R.drawable.baseline_pause_circle_24));
            }
        });



        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backs();
            }
        });

        imageAfter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(location > 0) location--;
                else location = listPath.size() - 1;
                StopSong();
                StartMusic(listPath.get(location));
                setVolumeControlStream(AudioManager.STREAM_MUSIC);
                mVisualizer.setEnabled(true);
                totalDuration = dataMedia.getDuration();
                partDuration = totalDuration / 100;
                Bitmap albumArt = getAlbumArt(listPath.get(location));
                if (albumArt != null) {
                    imageDisc.setImageBitmap(albumArt);
                }
                else  imageDisc.setImageDrawable(ContextCompat.getDrawable(MusicActivity.this, R.drawable.disk1));
                setEndTime();
                dataMedia.start();
                textMusic.setText(listName.get(location));
                startAnimation();
                imagePlay.setImageDrawable(ContextCompat.getDrawable(MusicActivity.this, R.drawable.baseline_pause_circle_24));
            }
        });
        imagePlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dataMedia.isPlaying()) {
                    dataMedia.pause();
                    stopAnimation();
                    imagePlay.setImageDrawable(ContextCompat.getDrawable(MusicActivity.this, R.drawable.baseline_play_circle_filled_24));
                }
                else {
                    dataMedia.start();
                    startAnimation();
                    setVolumeControlStream(AudioManager.STREAM_MUSIC);
                    mVisualizer.setEnabled(true);
                    imagePlay.setImageDrawable(ContextCompat.getDrawable(MusicActivity.this, R.drawable.baseline_pause_circle_24));
                }
            }
        });

        imagePause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seekBar.setProgress(0);
                StopSong();
                StartMusic(listPath.get(location));
                setVolumeControlStream(AudioManager.STREAM_MUSIC);
                mVisualizer.setEnabled(true);
                stopAnimation();
                startTime.setText("00:00");
                imagePlay.setImageDrawable(ContextCompat.getDrawable(MusicActivity.this, R.drawable.baseline_play_circle_filled_24));
            }
        });

        int parts = 100;
        seekBar.setMax(parts);
        totalDuration = dataMedia.getDuration();
        partDuration = totalDuration / parts;
        dataMedia.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
                currentPosition = mp.getCurrentPosition();
                int progress = currentPosition / partDuration;
                seekBar.setProgress(progress);
            }
        });

        dataMedia.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                checkMedia();
            }
        });


        updateSeekBar = new Runnable() {
            @Override
            public void run() {
                if (dataMedia.isPlaying()) {
                    int currentTimeMillis = dataMedia.getCurrentPosition();
                    int minutes = (currentTimeMillis / 1000) / 60;
                    int seconds = (currentTimeMillis / 1000) % 60;
                    String currentTime = String.format("%02d:%02d", minutes, seconds);
                    int progress = currentTimeMillis / partDuration;
                    seekBar.setProgress(progress);
                    startTime.setText(currentTime);
                }
                else startTime.setText(endTime.getText());
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(updateSeekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    int currentPosition = progress * partDuration;
                    int minutes = (currentPosition / 1000) / 60;
                    int seconds = (currentPosition / 1000) % 60;
                    String currentTime = String.format("%02d:%02d", minutes, seconds);
                    startTime.setText(currentTime);
                    dataMedia.seekTo(currentPosition);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    public void StartMusic(String audioURL){
        try {
            dataMedia = new MediaPlayer();
            dataMedia.setDataSource(audioURL);
            dataMedia.prepare();
            dataMedia.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    checkMedia();
                }
            });
            createVisualizer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void checkMedia() {
        if(!dataMedia.isPlaying()) {
            if(location < (listPath.size() - 1)) location++;
            else location = 0;
            StopSong();
            StartMusic(listPath.get(location));
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mVisualizer.setEnabled(true);
            Bitmap albumArt = getAlbumArt(listPath.get(location));
            if (albumArt != null) {
                imageDisc.setImageBitmap(albumArt);
            }
            else  imageDisc.setImageDrawable(ContextCompat.getDrawable(MusicActivity.this, R.drawable.disk1));
            totalDuration = dataMedia.getDuration();
            partDuration = totalDuration / 100;
            setEndTime();
            dataMedia.start();
            textMusic.setText(listName.get(location));
            startAnimation();
            imagePlay.setImageDrawable(ContextCompat.getDrawable(MusicActivity.this, R.drawable.baseline_pause_circle_24));
        }
    }

    public void setEndTime() {
        int currentTimeMillis = dataMedia.getDuration();
        int minutes = (currentTimeMillis / 1000) / 60;
        int seconds = (currentTimeMillis / 1000) % 60;
        String currentTime = String.format("%02d:%02d", minutes, seconds);
        endTime.setText(currentTime);
    }

    public void StopSong() {
        dataMedia.stop();
        dataMedia.reset();
    }

    public void backs() {
        Intent intent = new Intent();
        int currentTime = dataMedia.getCurrentPosition();
        intent.putExtra("resultData", currentTime);
        intent.putExtra("Location", location);
        intent.putExtra("isPlaying", dataMedia.isPlaying());
        imagePlay.setImageDrawable(ContextCompat.getDrawable(MusicActivity.this, R.drawable.baseline_play_circle_filled_24));
        stopAnimation();
        setResult(Activity.RESULT_OK, intent);
        StopSong();
        finish();
    }

    @Override
    public void onBackPressed() {
        backs();
        super.onBackPressed();
    }
    private void startAnimation() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                imageDisc.animate().rotationBy(360).withEndAction(this).setDuration(10000)
                        .setInterpolator(new LinearInterpolator()).start();
            }
        };
        imageDisc.animate().rotationBy(360).withEndAction(runnable).setDuration(10000)
                .setInterpolator(new LinearInterpolator()).start();
    }

    public void createVisualizer() {
        releaseVisualizer();
        mVisualizer = new Visualizer(dataMedia.getAudioSessionId());
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        mVisualizer.setDataCaptureListener(
                new Visualizer.OnDataCaptureListener() {
                    public void onWaveFormDataCapture(Visualizer visualizer,
                                                      byte[] bytes, int samplingRate) {
                        visualizerView.updateVisualizer(bytes);
                    }

                    public void onFftDataCapture(Visualizer visualizer,
                                                 byte[] bytes, int samplingRate) {
                    }
                }, Visualizer.getMaxCaptureRate() / 2, true, false);
    }

    private void releaseVisualizer() {
        if (mVisualizer != null) {
            mVisualizer.setEnabled(false); // Disable the Visualizer
            mVisualizer.release(); // Release the Visualizer
            mVisualizer = null; // Set it to null
        }
    }

    public Bitmap getAlbumArt(String audioFilePath) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(audioFilePath);

        byte[] albumArtBytes = retriever.getEmbeddedPicture();

        if (albumArtBytes != null) {
            Bitmap albumArt = BitmapFactory.decodeByteArray(albumArtBytes, 0, albumArtBytes.length);
            Bitmap resizedAlbumArt = Bitmap.createScaledBitmap(albumArt, 612, 612, false);
            return resizedAlbumArt;

        } else {
            return null;
        }
    }

    private void stopAnimation() {
        imageDisc.animate().cancel();
    }
}
