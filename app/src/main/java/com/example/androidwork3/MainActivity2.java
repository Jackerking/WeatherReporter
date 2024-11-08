package com.example.androidwork3;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class MainActivity2 extends AppCompatActivity {
    private Weather weather;
    TextView textViewWendu;
    TextView textViewTianqi;
    TextView textViewDate;
    TextView textViewFeng;
    TextView textView5;
    TextView textViewNotice;
    ImageView imageViewTianqi;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        textViewWendu = findViewById(R.id.textViewWendu);
        textViewTianqi = findViewById(R.id.textViewTianqi);
        textViewDate = findViewById(R.id.textViewYmd);
        textViewFeng = findViewById(R.id.textViewFeng);
        textViewNotice = findViewById(R.id.textViewNotice);
        textView5 = findViewById(R.id.textView5);
        imageViewTianqi = findViewById(R.id.imageViewTianqi);
        ConstraintLayout mainLayout = findViewById(R.id.main);


        Intent intent = getIntent();
        weather = (Weather) intent.getSerializableExtra("msg");
        assert weather != null;
        textViewWendu.setText(getHigh(weather.getHigh()));
        textViewTianqi.setText(weather.getType());
        textViewDate.setText(weather.getYmd()+" "+weather.getWeek());
        textViewFeng.setText(weather.getFx()+weather.getFl());
        textViewNotice.setText(weather.getNotice());

        switch (weather.getType()) {
            case "晴":
                imageViewTianqi.setImageResource(R.mipmap.psunny);
                break;
            case "多云":
                imageViewTianqi.setImageResource(R.mipmap.pcloudy);
                mainLayout.setBackgroundResource(R.drawable.backc);
                textView5.setTextColor(getResources().getColor(android.R.color.white));
                textViewNotice.setTextColor(getResources().getColor(android.R.color.white));
                break;
            case "阴":
                imageViewTianqi.setImageResource(R.mipmap.pyin);
                mainLayout.setBackgroundResource(R.drawable.backy);
                textView5.setTextColor(getResources().getColor(android.R.color.white));
                textViewNotice.setTextColor(getResources().getColor(android.R.color.white));
                break;
            default:
                imageViewTianqi.setImageResource(R.mipmap.prainy);
                mainLayout.setBackgroundResource(R.drawable.backr);
                textView5.setTextColor(getResources().getColor(android.R.color.white));
                textViewNotice.setTextColor(getResources().getColor(android.R.color.white));
                break;
        }

    }

    public String getHigh(String wendu) {
        return wendu != null ? wendu.replace("高温 ", "") : "";
    }
}
