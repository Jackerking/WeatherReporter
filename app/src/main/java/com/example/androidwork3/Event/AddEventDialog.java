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

// AddEventDialog.java
public class AddEventDialog extends DialogFragment {
    private EditText titleInput, descriptionInput;
    private DatePicker datePicker;
    private OnEventAddListener listener;

    public AddEventDialog(OnEventAddListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_event, container, false);
        titleInput = view.findViewById(R.id.titleInput);
        descriptionInput = view.findViewById(R.id.descriptionInput);
        datePicker = view.findViewById(R.id.datePicker);

        view.findViewById(R.id.addButton).setOnClickListener(v -> {
            String title = titleInput.getText().toString();
            String description = descriptionInput.getText().toString();
            Calendar calendar = Calendar.getInstance();
            calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
            long date = calendar.getTimeInMillis();

            Event event = new Event(title, description, date);
            listener.onEventAdd(event);
            dismiss();
        });

        return view;
    }

    interface OnEventAddListener {
        void onEventAdd(Event event);
    }
}
