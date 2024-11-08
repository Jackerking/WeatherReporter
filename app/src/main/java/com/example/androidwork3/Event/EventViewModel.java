package com.example.androidwork3.Event;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

// EventViewModel.java
public class EventViewModel extends ViewModel {
    private MutableLiveData<List<Event>> eventList;

    public EventViewModel() {
        eventList = new MutableLiveData<>();
        eventList.setValue(new ArrayList<>()); // 初始化空列表
    }

    public LiveData<List<Event>> getEventList() {
        return eventList;
    }

    public void addEvent(Event event) {
        List<Event> currentList = eventList.getValue();
        currentList.add(event);
        eventList.setValue(currentList);
    }

    public void deleteEvent(Event event) {
        List<Event> currentList = eventList.getValue();
        currentList.remove(event);
        eventList.setValue(currentList);
    }

    public void updateEvent(Event oldEvent, Event newEvent) {
        List<Event> currentList = eventList.getValue();
        int index = currentList.indexOf(oldEvent);
        if (index != -1) {
            currentList.set(index, newEvent);
            eventList.setValue(currentList);
        }
    }
}

