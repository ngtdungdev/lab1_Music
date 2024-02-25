package com.instar.lab1.adapter;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.instar.lab1.DTO.Music;
import com.instar.lab1.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {
    private Context context;
    private List<Music> itemList;
    private String itemSelect;
    private boolean flag = false;
    private View hiddenLayout = null;
    private View getHiddenLayout;
    private boolean isPlaying = false;
    private MediaPlayer saveMedia;

    private String audioURL = "";
    private static final int REQUEST_POST_NOTIFICATION = 101;

    public MediaPlayer getSaveMedia() {
        return saveMedia;
    }

    public void setSaveMedia(MediaPlayer saveMedia) {
        this.saveMedia = saveMedia;
    }

    private ImageView savePlay;
    private String rootDirectoryPath;

    public MusicAdapter(Context context, List<Music> itemList, View hiddenLayout, String rootDirectoryPath) {
        this.hiddenLayout = hiddenLayout;
        this.context = context;
        this.itemList = itemList;
        this.rootDirectoryPath = rootDirectoryPath;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View convertView = inflater.inflate(R.layout.activity_music_item, null);
        return new ViewHolder(convertView);
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
        void OnDownload(int position, String filePath);
    }

    private int location;
    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Music item = itemList.get(position);
        holder.nameMusic.setText(item.getFileName());
        holder.textFilePath.setText(item.getFilePath());
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = connectivityManager.getActiveNetwork();
        Bitmap albumArt = null;
        if(!item.getFilePath().contains("https://drive.google.com/uc?export=download&id=")) {
            holder.btnDownLoad.setVisibility(View.GONE);
        }
        if(network != null || holder.btnDownLoad.getVisibility() == View.GONE) {
            albumArt = getAlbumArt(item.getFilePath());
        }
        if (albumArt != null) {
            holder.imageDisc.setBackgroundResource(R.drawable.baseline_background);
            holder.imageDisc.setImageBitmap(albumArt);
        }
        holder.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(network != null || holder.btnDownLoad.getVisibility() == View.GONE) {
                    if (!flag || !audioURL.equals(item.getFilePath())) {
                        audioURL = item.getFilePath();
                        if (saveMedia != null) {
                            imageDrawablePlay();
                            stopSong();
                        }
                        savePlay = holder.play;
                        try {
                            saveMedia = new MediaPlayer();
                            saveMedia.setDataSource(audioURL);
                            saveMedia.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        flag = true;
                    }
                    if (saveMedia.isPlaying()) {
                        saveMedia.pause();
                        holder.play.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.play_black));
                    } else {
                        saveMedia.start();
                        holder.play.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.pause_black));
                    }
                    if (hiddenLayout.getVisibility() == View.VISIBLE) {
                        hiddenLayout.setVisibility(View.GONE);
                        Intent intent = new Intent("ACTION_STOP_MUSIC");
                        context.sendBroadcast(intent);
                    }
                }
            }
        });

//         holder.btnDownLoad.setOnClickListener(new View.OnClickListener() {
//             @Override
//             public void onClick(View v) {
//                 Toast.makeText(context, rootDirectoryPath + "/Download/Album/" + itemSelect + "/" + item.getFileName(), Toast.LENGTH_SHORT).show();
//                 Log.i("com",rootDirectoryPath + "/Download/Album/" + itemSelect + "/" + item.getFileName());
//             }
//         });


        holder.btnDownLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(network != null || holder.btnDownLoad.getVisibility() == View.GONE) {
//                DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
//                Uri uri = Uri.parse(item.getFilePath());
//
//                DownloadManager.Request request = new DownloadManager.Request(uri);
//                request.setTitle(item.getFileName());
//                request.setDescription("Đang tải xuống bài hát vào thư mục:");
//                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "file.mp3");
//
//                long downloadId = downloadManager.enqueue(request);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        CharSequence name = "Download Notification";
                        String description = "Channel for download notifications";
                        int importance = NotificationManager.IMPORTANCE_LOW;
                        NotificationChannel channel = new NotificationChannel("DOWNLOAD_CHANNEL", name, importance);
                        channel.setDescription(description);
                        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
                        notificationManager.createNotificationChannel(channel);
                    }

                    // Tạo Notification Manager và Builder
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "DOWNLOAD_CHANNEL")
                            .setSmallIcon(android.R.drawable.stat_sys_download)
                            .setContentTitle("Downloading " + item.getFileName())
                            .setContentText("Download in progress")
                            .setPriority(NotificationCompat.PRIORITY_LOW)
                            .setOngoing(true)
                            .setOnlyAlertOnce(true)
                            .setProgress(0, 0, true);

                    // ID cho Notification
                    int notificationId = 1;
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_POST_NOTIFICATION);
                    } else {
                        notificationManager.notify(notificationId, builder.build());
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                HttpURLConnection urlConnection = (HttpURLConnection) new URL(item.getFilePath()).openConnection();
                                urlConnection.setRequestMethod("GET");
                                urlConnection.setDoOutput(true);
                                urlConnection.connect();
                                int responseCode = urlConnection.getResponseCode();
                                if (responseCode == 307) {
                                    String newUrl = urlConnection.getHeaderField("Location");
                                    urlConnection.disconnect();
                                    URL newUrlObj = new URL(newUrl);
                                    HttpURLConnection newUrlConnection = (HttpURLConnection) newUrlObj.openConnection();
                                    urlConnection = newUrlConnection;
                                }
                                File directory = new File(rootDirectoryPath + "/Download/Album/" + itemSelect);
                                if (!directory.exists()) {
                                    directory.mkdirs();
                                }
                                File file = new File(directory, item.getFileName() + ".mp3");
                                FileOutputStream fileOutput = new FileOutputStream(file);
                                InputStream inputStream = urlConnection.getInputStream();
                                byte[] buffer = new byte[1024];
                                int bufferLength;
                                int fileSize = urlConnection.getContentLength();
                                int downloadedSize = 0;

                                while ((bufferLength = inputStream.read(buffer)) > 0) {
                                    fileOutput.write(buffer, 0, bufferLength);
                                    downloadedSize += bufferLength;
                                    int progress = (int) ((downloadedSize / (float) fileSize) * 100);
                                    builder.setProgress(100, progress, false);
                                    builder.setContentText("Downloaded " + progress + "%");
                                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                                        notificationManager.notify(notificationId, builder.build());
                                    }
                                }

                                fileOutput.close();
                                inputStream.close();
                                builder.setContentText("Download complete")
                                        .setProgress(0, 0, false)
                                        .setOngoing(false);
                                notificationManager.notify(notificationId, builder.build());
                                String url = rootDirectoryPath + "/Download/Album/" + itemSelect + "/" +item.getFileName() + ".mp3";
                                holder.textFilePath.setText(url);
                                int adapterPosition = holder.getAdapterPosition();
                                holder.btnDownLoad.setVisibility(View.GONE);
                                if (listener != null) {
                                    listener.OnDownload(adapterPosition,holder.textFilePath.getText().toString());
                                }
                                notificationManager.cancelAll();
                            } catch (Exception e) {
                                e.printStackTrace();
                                builder.setContentText("Download failed")
                                        .setProgress(0, 0, false)
                                        .setOngoing(false);
                                notificationManager.notify(notificationId, builder.build());
                                notificationManager.cancelAll();
                            }
                        }
                    }).start();
                }
            }
        });
        holder.pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(network != null || holder.btnDownLoad.getVisibility() == View.GONE) {
                    if (flag) stopSong();
                    if (savePlay != null) imageDrawablePlay();
                    else holder.play.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.play_black));
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(network != null || holder.btnDownLoad.getVisibility() == View.GONE) {
                    int adapterPosition = holder.getAdapterPosition();
                    if (listener != null) {
                        listener.onItemClick(adapterPosition);
                    }
                }
            }
        });
    }

    public void updateSelectItem(String item) {itemSelect = item;
    }
    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameMusic;
        ImageButton btnDownLoad;
        TextView textFilePath;
        ImageView play, pause, imageDisc;
        public ViewHolder (View itemView) {
            super(itemView);
            nameMusic = itemView.findViewById(R.id.nameMusic);
            play = itemView.findViewById(R.id.mediaPlay);
            btnDownLoad = itemView.findViewById(R.id.btnDownLoad);
            textFilePath = itemView.findViewById(R.id.textFilePath);
            pause = itemView.findViewById(R.id.mediaPause);
            imageDisc = itemView.findViewById(R.id.imageDisc);
        }
    }

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

    public void imageDrawablePlay() {
        savePlay.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.play_black));
    }

    public boolean checkNull() {
        if(saveMedia != null)  {
            if(saveMedia.isPlaying())return true;
            else return false;
        }
        else return false;
    }


    public void stopSong() {
        saveMedia.stop();
        saveMedia.reset();
        flag = false;
    }
}
