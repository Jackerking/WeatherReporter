package com.example.androidwork3.Event;

import android.os.Bundle;

import com.example.androidwork3.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

// MainActivity.java
// MainActivity.java
public class ActivityEve extends AppCompatActivity {
    private EventViewModel eventViewModel;
    private EventAdapter eventAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);
        eventAdapter = new EventAdapter(eventViewModel.getEventList().getValue(), event -> {
            // 修改事件
            EditEventDialog dialog = new EditEventDialog(event, updatedEvent -> {
                eventViewModel.updateEvent(event, updatedEvent);
            });
            dialog.show(getSupportFragmentManager(), "EditEventDialog");
        }, event -> {
            // 删除事件
            new AlertDialog.Builder(this)
                    .setTitle("删除事件")
                    .setMessage("确定要删除这个事件吗？")
                    .setPositiveButton("删除", (dialog, which) -> eventViewModel.deleteEvent(event))
                    .setNegativeButton("取消", null)
                    .show();
        });
        recyclerView.setAdapter(eventAdapter);

        eventViewModel.getEventList().observe(this, events -> eventAdapter.notifyDataSetChanged());

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            AddEventDialog dialog = new AddEventDialog(event -> {
                eventViewModel.addEvent(event);
            });
            dialog.show(getSupportFragmentManager(), "AddEventDialog");
        });
    }
}
