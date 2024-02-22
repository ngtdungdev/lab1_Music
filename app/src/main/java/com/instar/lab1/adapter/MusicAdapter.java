package com.instar.lab1.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.instar.lab1.DTO.Music;
import com.instar.lab1.R;

import java.io.IOException;
import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {
    private Context context;
    private List<Music> itemList;
    private  boolean flag = false;
    private  View hiddenLayout = null;
    private View getHiddenLayout;
    private boolean isPlaying = false;
    private MediaPlayer saveMedia;

    private String audioURL = "";

    public MediaPlayer getSaveMedia() {
        return saveMedia;
    }

    public void setSaveMedia(MediaPlayer saveMedia) {
        this.saveMedia = saveMedia;
    }

    private ImageView savePlay;
    public MusicAdapter(Context context, List<Music> itemList, View hiddenLayout) {
        this.hiddenLayout = hiddenLayout;
        this.context = context;
        this.itemList = itemList;
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
        Bitmap albumArt = getAlbumArt(item.getFilePath());
        if (albumArt != null) {
            holder.imageDisc.setBackgroundResource(R.drawable.baseline_background);
            holder.imageDisc.setImageBitmap(albumArt);
        }
        holder.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!flag || !audioURL.equals(item.getFilePath())) {
                    audioURL = item.getFilePath();
                    if(saveMedia != null) {
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
                if(saveMedia.isPlaying()) {
                    saveMedia.pause();
                    holder.play.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.play_black));
                }
                else {
                    saveMedia.start();
                    holder.play.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.pause_black));
                }
                if(hiddenLayout.getVisibility() == View.VISIBLE) {
                    hiddenLayout.setVisibility(View.GONE);
                    Intent intent = new Intent("ACTION_STOP_MUSIC");
                    context.sendBroadcast(intent);
                }
            }
        });
        holder.pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flag) stopSong();
                if(savePlay != null) imageDrawablePlay();
                else holder.play.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.play_black));
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                if (listener != null) {
                    listener.onItemClick(adapterPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameMusic;
        ImageView play, pause, imageDisc;
        public ViewHolder (View itemView) {
            super(itemView);
            nameMusic = itemView.findViewById(R.id.nameMusic);
            play = itemView.findViewById(R.id.mediaPlay);
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
