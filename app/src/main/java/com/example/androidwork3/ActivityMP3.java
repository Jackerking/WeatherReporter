package com.example.androidwork3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class ActivityMP3 extends AppCompatActivity {

    private static final int REQUEST_READ_STORAGE_PERMISSION = 999;

    private MediaPlayer mediaPlayer;
    private ImageView imageViewDisc;
    private ImageButton imageButtonPre;
    private ImageButton imageButtonNext;
    private ImageButton imageButtonBack;
    private ImageButton imageButtonForward;
    private ImageButton imageButtonPlayOrPause;
    private SeekBar seekBar;
    private RatingBar ratingBar;
    private TextView textView;
    private ListView listView;

    private String[] mp3List = {"1.mp3", "2.mp3", "3.mp3"};
    private int index = 0;
    private boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mp3);

        initializeViews();
        setListeners();

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            play(index);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_STORAGE_PERMISSION);
        }

        new MyTask().execute();
    }

    private void initializeViews() {
        mediaPlayer = new MediaPlayer();
        imageButtonPlayOrPause = findViewById(R.id.imageButtonPlayOrPause);
        seekBar = findViewById(R.id.seekBar);
        ratingBar = findViewById(R.id.ratingBar);
        textView = findViewById(R.id.textViewYmd);
        imageButtonBack = findViewById(R.id.imageButtonBack);
        imageButtonForward = findViewById(R.id.imageButtonForward);
        imageViewDisc = findViewById(R.id.imageView);
        listView = findViewById(R.id.listView);
        imageButtonNext = findViewById(R.id.imageButtonNext);
        imageButtonPre = findViewById(R.id.imageButtonPre);

        seekBar.setMax(100);
        ratingBar.setMax(10);
        ratingBar.setNumStars(10);
        ratingBar.setProgress(5);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mp3List);
        listView.setAdapter(arrayAdapter);
    }

    private void setListeners() {
        imageButtonPre.setOnClickListener(v -> {
            if (index > 0) {
                index = index - 1;
                play(index);
            } else {
                Toast.makeText(this, "已经是第一首歌了", Toast.LENGTH_SHORT).show();
            }
        });

        imageButtonNext.setOnClickListener(v -> {
            if (index < mp3List.length - 1) {
                index = index + 1;
                play(index);
            } else {
                Toast.makeText(this, "已经是最后一首歌了", Toast.LENGTH_SHORT).show();
            }
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            index = position;
            play(index);
        });

        imageButtonBack.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                mediaPlayer.seekTo(Math.max(mediaPlayer.getCurrentPosition() - 10000, 0));
            }
        });

        imageButtonForward.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                mediaPlayer.seekTo(Math.min(mediaPlayer.getCurrentPosition() + 10000, mediaPlayer.getDuration()));
            }
        });

        ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (mediaPlayer != null) {
                float volume = rating / 10;
                mediaPlayer.setVolume(volume, volume);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo((int) (mediaPlayer.getDuration() * (progress / 100.0f)));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        imageButtonPlayOrPause.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    imageButtonPlayOrPause.setImageResource(android.R.drawable.ic_media_play);
                } else {
                    mediaPlayer.start();
                    imageButtonPlayOrPause.setImageResource(android.R.drawable.ic_media_pause);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_READ_STORAGE_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            play(index);
        }
    }

    private void play(int index) {
        String path = Environment.getExternalStorageDirectory() + File.separator + "Music" + File.separator + mp3List[index];
        Log.i("PATH", path);
        try {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.reset();
            }
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.start();
            mediaPlayer.setVolume(0.5f, 0.5f);
            imageButtonPlayOrPause.setImageResource(android.R.drawable.ic_media_pause);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class MyTask extends AsyncTask<String, Integer, Object> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (mediaPlayer != null) {
                int currentPosition = mediaPlayer.getCurrentPosition();
                int duration = mediaPlayer.getDuration();
                int percent = Math.round((currentPosition / (float) duration) * 100);
                seekBar.setProgress(percent);
                String text = "文件名：" + mp3List[index]
                        + " 时长：" + duration / 1000 / 60 + "分" + duration / 1000 % 60 + "秒"
                        + " 当前进度：" + currentPosition / 1000 / 60 + "分" + currentPosition / 1000 % 60 + "秒";
                textView.setText(text);
            }
        }

        @Override
        protected Object doInBackground(String... strings) {
            while (flag) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                publishProgress();
            }
            return null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        flag = false;
    }
}
