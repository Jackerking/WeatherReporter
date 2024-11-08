package com.example.androidwork3.Event;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;

import com.example.androidwork3.R;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

// EditEventDialog.java
public class EditEventDialog extends DialogFragment {
    private EditText titleInput, descriptionInput;
    private DatePicker datePicker;
    private Event event;
    private OnEventUpdateListener listener;

    public EditEventDialog(Event event, OnEventUpdateListener listener) {
        this.event = event;
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_edit_event, container, false);
        titleInput = view.findViewById(R.id.titleInput);
        descriptionInput = view.findViewById(R.id.descriptionInput);
        datePicker = view.findViewById(R.id.datePicker);

        titleInput.setText(event.getTitle());
        descriptionInput.setText(event.getDescription());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(event.getDate());
        datePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        view.findViewById(R.id.updateButton).setOnClickListener(v -> {
            String title = titleInput.getText().toString();
            String description = descriptionInput.getText().toString();
            Calendar updatedCalendar = Calendar.getInstance();
            updatedCalendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
            long date = updatedCalendar.getTimeInMillis();

            Event updatedEvent = new Event(title, description, date);
            listener.onEventUpdate(updatedEvent);
            dismiss();
        });

        return view;
    }

    interface OnEventUpdateListener {
        void onEventUpdate(Event updatedEvent);
    }
}

