package com.instar.lab1.activity;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Build;
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
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.instar.lab1.DTO.Music;
import com.instar.lab1.ListMusic;
import com.instar.lab1.R;
import com.instar.lab1.adapter.MusicAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private String[] permissions = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_music);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (checkPermissions()) {
                initViews();
            }
        } else initViews();
    }

    private int currentPermissionIndex = 0;
    private static final int REQUEST_PERMISSIONS_CODE = 10;

    private boolean checkPermissions() {
        if (currentPermissionIndex < permissions.length) {
            String permission = permissions[currentPermissionIndex];
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{permission}, REQUEST_PERMISSIONS_CODE);
            } else {
                currentPermissionIndex++;
                checkPermissions();
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                currentPermissionIndex++;
                checkPermissions();
            } else {
                finish();
            }
        }
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
    private String spinnerItem;

    public String getSpinnerItem() {
        return spinnerItem;
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(!isConnectedToInternet()) {
            imageWifi.setImageResource(R.drawable.baseline_wifi_off_24);
        }
        else imageWifi.setImageResource(R.drawable.baseline_wifi_24);
        loadRecyclerView();
    }
    private ImageButton imageDownLoad;
    private ImageView imageWifi;
    public View getHiddenLayout() {
        return hiddenLayout;
    }

    public void initViews() {
        dataMusic = new MediaPlayer();
        hiddenLayout = findViewById(R.id.layoutmusic);
        filePath = new ArrayList<>();
        listName = new ArrayList<>();
        hiddenLayout.setVisibility(View.GONE);
        imagePlay = findViewById(R.id.play);
        imageNext = findViewById(R.id.next);
        imagePrevious = findViewById(R.id.previous);
        imagePause = findViewById(R.id.pause);
        imageView = findViewById(R.id.imageDisc);
        textMusic = findViewById(R.id.nameMusic);
        imageWifi = findViewById(R.id.imageWifi);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterSpinner.add("Nhạc suy");
        adapterSpinner.add("Nhạc remix");
        adapterSpinner.add("Nhạc yêu thích");
        spinner.setAdapter(adapterSpinner);
        loadRecyclerView();
        adapter.updateSelectItem(spinner.getSelectedItem().toString());
        if(!isConnectedToInternet()) {
            imageWifi.setImageResource(R.drawable.baseline_wifi_off_24);
        }


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object selectedItem = parent.getItemAtPosition(position);
                adapter.updateSelectItem(selectedItem.toString());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail()
//                .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
//                .build();
//        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
//        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
////        startActivityGGDrive.launch(signInIntent);
//        try {
//            dataMusic = new MediaPlayer();
//            dataMusic.setDataSource("https://drive.google.com/uc?export=download&id=1z8QIL9hmvnR5Q_JHrqMK-deo5gEXg9lg");
//            dataMusic.prepareAsync();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        dataMusic.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                mp.start();
//            }
//        });
        imagePlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dataMusic.isPlaying()) {
                    dataMusic.pause();
                    stopAnimation();
                    imagePlay.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.baseline_play_circle_filled_24));
                }
                else {
                    dataMusic.start();
                    startAnimation();
                    imagePlay.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.baseline_pause_circle_24));
                }
            }
        });

        imagePause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dataMusic.isPlaying()) StopSong();
                StartMusic(filePath.get(location));
                stopAnimation();
                imagePlay.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.baseline_play_circle_filled_24));
            }
        });


        imageNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(location < (filePath.size() - 1)) location++;
                else location = 0;
                StopSong();
                startAnimation();
                StartMusic(filePath.get(location));
                dataMusic.start();
                textMusic.setText(listName.get(location));
                imagePlay.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.baseline_pause_circle_24));
            }
        });

        imagePrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(location > 0) location--;
                else location = filePath.size() - 1;
                StopSong();
                StartMusic(filePath.get(location));
                dataMusic.start();
                textMusic.setText(listName.get(location));
                startAnimation();
                imagePlay.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.baseline_pause_circle_24));
            }
        });

        hiddenLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MusicActivity.class);
                intent.putStringArrayListExtra("ListPath", filePath);
                intent.putExtra("Location", location);
                intent.putStringArrayListExtra("NameMusic", listName);
                intent.putExtra("SeekTo", dataMusic.getCurrentPosition());
                if(hiddenLayout.getVisibility() == View.VISIBLE) intent.putExtra("isPlaying", dataMusic.isPlaying());
                StopSong();
                stopAnimation();
                imagePlay.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.baseline_play_circle_filled_24));
                if (adapter.checkNull()) {
                    adapter.stopSong();
                    adapter.imageDrawablePlay();
                }
                someActivityResultLauncher.launch(intent);
            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                StopSong();
                if (adapter.checkNull()) {
                    adapter.stopSong();
                    adapter.imageDrawablePlay();
                }
            }
        };

        getOnBackPressedDispatcher().addCallback(this, callback);

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (dataMusic.isPlaying()) {
                    StopSong();
                }
            }
        };
        IntentFilter filter = new IntentFilter("ACTION_STOP_MUSIC");
        registerReceiver(receiver, filter);
    }
//    ActivityResultLauncher<Intent> startActivityGGDrive = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            result -> {
//                GoogleSignInAccount account =  GoogleSignIn.getLastSignedInAccount(this);
//                Toast.makeText(this, account+"1", Toast.LENGTH_SHORT).show();
//                ListMusic listViewMusic = new ListMusic();
//                List<Music> music = listViewMusic.findMP3Files(account,MainActivity.this);
//            });
//    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
//        GoogleSignInAccount account =  GoogleSignIn.getLastSignedInAccount(this);
//        ListMusic listViewMusic = new ListMusic();
//        List<Music> music = listViewMusic.findMP3Files(account,MainActivity.this);
//    }
    private boolean isPlaying;

    public void loadRecyclerView() {
        RecyclerView listView = findViewById(R.id.listMusic);
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        String rootDirectoryPath = externalStorageDirectory.getAbsolutePath();
        ListMusic listViewMusic = new ListMusic();
        List<Music> music = listViewMusic.findMP3Files(MainActivity.this,rootDirectoryPath);

        adapter = new MusicAdapter(this, music, hiddenLayout, rootDirectoryPath);
        listView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        adapter.setOnItemClickListener(new MusicAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(MainActivity.this, MusicActivity.class);
                intent.putStringArrayListExtra("ListPath", filePath);
                intent.putExtra("Location", position);
                intent.putStringArrayListExtra("NameMusic", listName);
                if (position != location) {
                    intent.putExtra("isPlaying", true);
                } else if (hiddenLayout.getVisibility() == View.VISIBLE)
                    intent.putExtra("isPlaying", dataMusic.isPlaying());
                stopAnimation();
                imagePlay.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.baseline_play_circle_filled_24));
                if (location == position) {
                    intent.putExtra("SeekTo", dataMusic.getCurrentPosition());
                }
                StopSong();
                if (adapter.checkNull()) {
                    adapter.stopSong();
                    adapter.imageDrawablePlay();
                }
                someActivityResultLauncher.launch(intent);
            }

            @Override
            public void OnDownload(int position, String filePath) {
                music.get(position).setFilePath(filePath);
            }
        });
        listView.setAdapter(adapter);

        for (Music item : music) {
            filePath.add(item.getFilePath());
            listName.add(item.getFileName());
        }
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
        Bitmap albumArt = getAlbumArt(filePath.get(location));
        if (albumArt != null) {
            imageView.setBackgroundResource(R.drawable.baseline_background);
            imageView.setImageBitmap(albumArt);
        }else {
            imageView.setImageResource(R.drawable.disk);
        }
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
//        String songTitle = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        if (albumArtBytes != null) {
            return BitmapFactory.decodeByteArray(albumArtBytes, 0, albumArtBytes.length);
        } else {
            return null;
        }
    }

    public boolean isConnectedToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            Network network = connectivityManager.getActiveNetwork();
            if (network != null) {
                return true;
            }
            else return false;
        }
        return false;
    }

    private void stopAnimation() {
        imageView.animate().cancel();
    }

}
