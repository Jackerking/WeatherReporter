package com.example.androidwork3.Event;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.androidwork3.R;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

// EventAdapter.java
// EventAdapter.java
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private List<Event> eventList;
    private OnEventClickListener listener;
    private OnEventLongClickListener longClickListener;

    public EventAdapter(List<Event> eventList, OnEventClickListener listener, OnEventLongClickListener longClickListener) {
        this.eventList = eventList;
        this.listener = listener;
        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.title.setText(event.getTitle());
        holder.description.setText(event.getDescription());
        holder.date.setText(DateFormat.getDateInstance().format(new Date(event.getDate())));
        holder.itemView.setOnClickListener(v -> listener.onEventClick(event));
        holder.itemView.setOnLongClickListener(v -> {
            longClickListener.onEventLongClick(event);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, date;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.eventTitle);
            description = itemView.findViewById(R.id.eventDescription);
            date = itemView.findViewById(R.id.eventDate);
        }
    }

    interface OnEventClickListener {
        void onEventClick(Event event);
    }

    interface OnEventLongClickListener {
        void onEventLongClick(Event event);
    }
}
