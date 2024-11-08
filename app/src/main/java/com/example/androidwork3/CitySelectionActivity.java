package com.example.androidwork3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class CitySelectionActivity extends AppCompatActivity {

    ListView cityListView;
    EditText searchEditText;
    Button searchButton;
    Button backButton;
    ArrayAdapter<String> adapter;
    List<String> cityNameList;
    HashMap<String, String> cityMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_selection);

        // Initialize cityMap
        cityMap = new HashMap<>();
        String[] cityNameArray = getResources().getStringArray(R.array.cityName);
        String[] cityCodeArray = getResources().getStringArray(R.array.cityCode);
        for (int i = 0; i < cityNameArray.length; i++) {
            cityMap.put(cityNameArray[i], cityCodeArray[i]);
        }

        cityNameList = Arrays.asList(cityNameArray);

        cityListView = findViewById(R.id.cityListView);
        searchEditText = findViewById(R.id.searchEditText);
        searchButton = findViewById(R.id.searchButton);
        backButton = findViewById(R.id.backButton);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, cityNameList);
        cityListView.setAdapter(adapter);

        cityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedCity = cityNameList.get(position);
                Intent resultIntent = new Intent();
                resultIntent.putExtra("cityName", selectedCity);
                resultIntent.putExtra("cityCode", getCityCode(selectedCity));
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchQuery = searchEditText.getText().toString().trim();
                String cityCode = getCityCode(searchQuery);
                if (cityCode != null) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("cityName", searchQuery);
                    resultIntent.putExtra("cityCode", cityCode);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    searchEditText.setError("City not found");
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private String getCityCode(String cityName) {
        return cityMap.get(cityName);
    }
}
