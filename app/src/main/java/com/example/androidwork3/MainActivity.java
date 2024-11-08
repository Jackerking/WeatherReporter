package com.example.androidwork3;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.alibaba.fastjson.JSONArray;
import com.example.androidwork3.Event.ActivityEve;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Button selectCityButton;
    static final int REQUEST_CODE_CITY = 1;
    ConstraintLayout mainLayout;
    ListView listView;
    ImageButton musicButton;
    ImageButton eventButton;
    TextView textViewCityName;
    TextView textViewHL;
    TextView textViewType;
    TextView textViewTime;
    TextView textViewNowWeather;
    MyHandler handler = new MyHandler();
    ArrayList<Weather> list = new ArrayList<>();
    ArrayList<Weather> weatherList = new ArrayList<>();
    String cityUrl = "http://t.weather.itboy.net/api/weather/city/101070101";
    Now now;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private HashMap<String, String> cityCodeMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainLayout = findViewById(R.id.frameLayoutBackground);
        textViewNowWeather = findViewById(R.id.textViewNowWeather);
        textViewTime = findViewById(R.id.textViewTime);
        textViewHL = findViewById(R.id.textViewHL);
        textViewType = findViewById(R.id.textViewType);
        textViewCityName = findViewById(R.id.textViewCityName);
        listView = findViewById(R.id.listView);
        musicButton = findViewById(R.id.musicButton);
        eventButton = findViewById(R.id.eventButton);
        selectCityButton = findViewById(R.id.selectCityButton);
        now = new Now();

        // 初始化位置客户端
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // 初始化城市代码映射表
        cityCodeMap = new HashMap<>();
        initializeCityCodeMap();

        // 检查位置权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getLastKnownLocation();
        }

        selectCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CitySelectionActivity.class);
                startActivityForResult(intent, REQUEST_CODE_CITY);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                intent.putExtra("msg", weatherList.get(position));
                startActivity(intent);
            }
        });

        musicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ActivityMP3.class);
                startActivity(intent);
            }
        });

        eventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ActivityEve.class);
                startActivity(intent);
            }
        });

        // Start background tasks
        new FetchWeatherDataTask().start();
        new FetchCurrentTemperatureTask().start();
        new Thread(new TimeThread()).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CITY && resultCode == RESULT_OK) {
            Toast.makeText(getApplicationContext(), "切换成功！！", Toast.LENGTH_SHORT).show();
            String cityName = data.getStringExtra("cityName");
            String cityCode = data.getStringExtra("cityCode");
            updateUrl(cityCode, cityName);
        }
    }

    private void updateUrl(String cityCode, String cityName) {
        if (cityCode != null) {
            textViewCityName.setText(cityName + "天气");
            cityUrl = "http://t.weather.itboy.net/api/weather/city/" + cityCode;
            new FetchWeatherDataTask().start();
            new FetchCurrentTemperatureTask().start();
        } else {
            Log.e("CityError", "City code not found");
        }
    }

    // 初始化城市代码映射表
    private void initializeCityCodeMap() {
        cityCodeMap.put("北京", "101010100");
        cityCodeMap.put("沈阳", "101070101");
        // 添加其他城市和对应的城市代码
    }

    // 获取最后已知位置
    private void getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    Location location = task.getResult();
                    getCityNameFromLocation(location);
                }
            }
        });
    }

    // 从位置获取城市名
    private void getCityNameFromLocation(Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                String cityName = addresses.get(0).getLocality();
                String cityCode = cityCodeMap.get(cityName);

                if (cityCode != null) {
                    updateUrl(cityCode, cityName);
                } else {
                    Log.e("CityError", "City code not found for " + cityName);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("HandlerLeak")
    class MyHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                MyAdapter adapter = new MyAdapter();
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged(); // Ensure the adapter refresh
            } else if (msg.what == 2) {
                textViewTime.setText(new Date().toLocaleString());
            } else if (msg.what == 3) {
                textViewNowWeather.setText((String) msg.obj);
            }
        }
    }

    class TimeThread implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(1000);
                    handler.sendEmptyMessage(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class FetchWeatherDataTask extends Thread {
        @SuppressLint("SetTextI18n")
        @Override
        public void run() {
            super.run();
            try {
                String str = NetUtil.net(cityUrl, null, "GET");
                Log.i("NET", str);
                int startIndex = str.indexOf("[");
                int endIndex = str.indexOf("]");
                String subStr = str.substring(startIndex, (endIndex + 1));
                list = (ArrayList<Weather>) JSONArray.parseArray(subStr, Weather.class);
                weatherList = new ArrayList<>(list);
                now.setWeather(list.get(0));
                String weatherType = now.getWeather().getType();

                String high = now.getWeather().getHigh();
                String low = now.getWeather().getLow();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textViewType.setText(weatherType);
                        textViewHL.setText(high + "/" + low);
                        setLayoutBackground(weatherType);
                    }
                });
                handler.sendEmptyMessage(1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class FetchCurrentTemperatureTask extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                String str = NetUtil.net(cityUrl, null, "GET");
                Log.i("NET", str);
                int startIndex2 = str.indexOf("\"wendu\":\"") + "\"wendu\":\"".length();
                int endIndex2 = str.indexOf("\"", startIndex2);
                String wendu = str.substring(startIndex2, endIndex2) + "°C";
                Message msg = handler.obtainMessage(3, wendu);
                handler.sendMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = View.inflate(MainActivity.this, R.layout.item, null);
            }
            TextView textViewDate = view.findViewById(R.id.textViewDate);
            TextView textViewHigh = view.findViewById(R.id.textViewHigh);
            TextView textViewLow = view.findViewById(R.id.textViewLow);
            ImageView imageView = view.findViewById(R.id.imageViewType);

            Weather weather = list.get(position);
            textViewDate.setText(weather.getDate() + "日");
            textViewHigh.setText(weather.getHigh());
            textViewLow.setText(weather.getLow());

            switch (weather.getType()) {
                case "晴":
                    imageView.setImageResource(R.mipmap.psunny);
                    break;
                case "多云":
                    imageView.setImageResource(R.mipmap.pcloudy);
                    break;
                case "阴":
                    imageView.setImageResource(R.mipmap.pyin);
                    break;
                default:
                    imageView.setImageResource(R.mipmap.prainy);
                    break;
            }

            return view;
        }
    }

    private void setLayoutBackground(String weatherType) {
        int backgroundResId;

        switch (weatherType) {
            case "晴":
                backgroundResId = R.drawable.sunny_back;
                break;
            case "阴":
                backgroundResId = R.drawable.yin_back;
                break;
            case "多云":
                backgroundResId = R.drawable.cloudy_back;
                break;
            default:
                backgroundResId = R.drawable.rainy_back;
                break;
        }

        mainLayout.setBackgroundResource(backgroundResId);
    }
}
