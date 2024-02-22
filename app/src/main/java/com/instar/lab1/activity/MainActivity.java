package com.instar.lab1.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.api.services.drive.DriveScopes;
import com.instar.lab1.DTO.Music;
import com.instar.lab1.ListMusic;
import com.instar.lab1.R;
import com.instar.lab1.adapter.MusicAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity  extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_music);
        listMusic();
    }
    private ImageView imageView;

    private ImageView imagePlay;
    private ImageView imageNext;
    private ImageView imagePrevious;
    private ImageView imagePause;
    private ArrayList<String> filePath;
    private ArrayList<String> listName;

    private int location;
    private int currentTime;

    private MediaPlayer dataMusic;
    private TextView textMusic;
    private View hiddenLayout = null;
    private MusicAdapter adapter;
    public View getHiddenLayout() {
        return hiddenLayout;
    }

    public void listMusic() {
        dataMusic = new MediaPlayer();
        hiddenLayout = findViewById(R.id.layoutmusic);
        filePath = new ArrayList<>();
        listName = new ArrayList<>();
        hiddenLayout.setVisibility(View.GONE);
        imagePlay = findViewById(R.id.play);
        imageNext = findViewById(R.id.next);
        imagePrevious = findViewById(R.id.previous);
        imagePause = findViewById(R.id.pause);
        imageView= findViewById(R.id.imageDisc);
        textMusic = findViewById(R.id.nameMusic);
        RecyclerView listView = findViewById(R.id.listMusic);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityGGDrive.launch(signInIntent);


//        File externalStorageDirectory = Environment.getExternalStorageDirectory();
//        String rootDirectoryPath = externalStorageDirectory.getAbsolutePath();
//        ListMusic listViewMusic = new ListMusic();
//        List<Music> music = listViewMusic.findMP3Files(MainActivity.this,rootDirectoryPath);

//        adapter = new MusicAdapter(MainActivity.this, music, hiddenLayout);
//        listView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
//        adapter.setOnItemClickListener(new MusicAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(int position) {
//                Intent intent = new Intent(MainActivity.this, MusicActivity.class);
//                intent.putStringArrayListExtra("ListPath", filePath);
//                intent.putExtra("Location", position);
//                intent.putStringArrayListExtra("NameMusic", listName);
//                if(position != location) {
//                    intent.putExtra("isPlaying", true);
//                }
//                else if(hiddenLayout.getVisibility() == View.VISIBLE) intent.putExtra("isPlaying", dataMusic.isPlaying());
//                stopAnimation();
//                imagePlay.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.baseline_play_circle_filled_24));
//                if(location == position) {
//                    intent.putExtra("SeekTo", dataMusic.getCurrentPosition());
//                }
//                StopSong();
//                if(adapter.checkNull()) {
//                    adapter.stopSong();
//                    adapter.imageDrawablePlay();
//                }
//                someActivityResultLauncher.launch(intent);
//            }
//        });
//        listView.setAdapter(adapter);
//
//        for(Music item: music) {
//            filePath.add(item.getFilePath());
//            listName.add(item.getFileName());
//        }
//
//
//        imagePlay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(dataMusic.isPlaying()) {
//                    dataMusic.pause();
//                    stopAnimation();
//                    imagePlay.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.baseline_play_circle_filled_24));
//                }
//                else {
//                    dataMusic.start();
//                    startAnimation();
//                    imagePlay.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.baseline_pause_circle_24));
//                }
//            }
//        });
//
//        imagePause.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(dataMusic.isPlaying()) StopSong();
//                StartMusic(filePath.get(location));
//                stopAnimation();
//                imagePlay.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.baseline_play_circle_filled_24));
//            }
//        });
//
//
//        imageNext.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(location < (filePath.size() - 1)) location++;
//                else location = 0;
//                StopSong();
//                startAnimation();
//                StartMusic(filePath.get(location));
//                dataMusic.start();
//                textMusic.setText(listName.get(location));
//                imagePlay.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.baseline_pause_circle_24));
//            }
//        });
//
//        imagePrevious.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(location > 0) location--;
//                else location = filePath.size() - 1;
//                StopSong();
//                StartMusic(filePath.get(location));
//                dataMusic.start();
//                textMusic.setText(listName.get(location));
//                startAnimation();
//                imagePlay.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.baseline_pause_circle_24));
//            }
//        });
//
//        hiddenLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(MainActivity.this, MusicActivity.class);
//                intent.putStringArrayListExtra("ListPath", filePath);
//                intent.putExtra("Location", location);
//                intent.putStringArrayListExtra("NameMusic", listName);
//                intent.putExtra("SeekTo", dataMusic.getCurrentPosition());
//                if(hiddenLayout.getVisibility() == View.VISIBLE) intent.putExtra("isPlaying", dataMusic.isPlaying());
//                StopSong();
//                stopAnimation();
//                imagePlay.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.baseline_play_circle_filled_24));
//                if (adapter.checkNull()) {
//                    adapter.stopSong();
//                    adapter.imageDrawablePlay();
//                }
//                someActivityResultLauncher.launch(intent);
//            }
//        });
//
//
//
//        BroadcastReceiver receiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                if (dataMusic.isPlaying()) {
//                    StopSong();
//                }
//            }
//        };
//        IntentFilter filter = new IntentFilter("ACTION_STOP_MUSIC");
//        registerReceiver(receiver, filter);
    }
    ActivityResultLauncher<Intent> startActivityGGDrive = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                GoogleSignInAccount account =  GoogleSignIn.getLastSignedInAccount(this);
                Toast.makeText(this, account+"1", Toast.LENGTH_SHORT).show();
                ListMusic listViewMusic = new ListMusic();
                List<Music> music = listViewMusic.findMP3Files(account,MainActivity.this);
            });
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        GoogleSignInAccount account =  GoogleSignIn.getLastSignedInAccount(this);
        ListMusic listViewMusic = new ListMusic();
        List<Music> music = listViewMusic.findMP3Files(account,MainActivity.this);
    }
    private boolean isPlaying;

    public void startMainMusic() {

    }
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    if(dataMusic.isPlaying()) StopSong();
                    Intent intent = result.getData();
                    location = intent.getIntExtra("Location", 0);
                    currentTime = intent.getIntExtra("resultData", 0);
                    isPlaying = intent.getBooleanExtra("isPlaying", false);
                    StartMusic(filePath.get(location));
                    if (isPlaying) {
                        dataMusic.start();
                        imagePlay.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.baseline_pause_circle_24));
                        startAnimation();
                    }
                    dataMusic.seekTo(currentTime);
                    textMusic.setText(listName.get(location));
                    Bitmap albumArt = getAlbumArt(filePath.get(location));
                    if (albumArt != null) {
                        imageView.setBackgroundResource(R.drawable.baseline_background);
                        imageView.setImageBitmap(albumArt);
                    }
                    hiddenLayout.setVisibility(View.VISIBLE);
                }
            }
    );

    public void StopSong() {
        dataMusic.stop();
        dataMusic.reset();
    }

    public void StartMusic(String audioURL){
        try {
            dataMusic = new MediaPlayer();
            dataMusic.setDataSource(audioURL);
            dataMusic.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startAnimation() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                imageView.animate().rotationBy(360).withEndAction(this).setDuration(10000)
                        .setInterpolator(new LinearInterpolator()).start();
            }
        };
        imageView.animate().rotationBy(360).withEndAction(runnable).setDuration(10000)
                .setInterpolator(new LinearInterpolator()).start();
    }

//    @Override
//    public void onBackPressed() {
//        StopSong();
//        if (adapter.checkNull()) {
//            adapter.stopSong();
//            adapter.imageDrawablePlay();
//        }
//        super.onBackPressed();
//    }
    public Bitmap getAlbumArt(String audioFilePath) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(audioFilePath);

        byte[] albumArtBytes = retriever.getEmbeddedPicture();

        if (albumArtBytes != null) {
            return BitmapFactory.decodeByteArray(albumArtBytes, 0, albumArtBytes.length);
        } else {
            return null;
        }
    }

    private void stopAnimation() {
        imageView.animate().cancel();
    }

}
