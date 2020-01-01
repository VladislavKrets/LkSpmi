package com.re.lkspmi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

import ru.spmi.lk.entities.profile.ProfileCurrent;
import ru.spmi.lk.entities.schedule.Schedule;
import ru.spmi.lk.entities.schedule.ScheduleGroup;

public class ScheduleFragment extends Fragment {

    private SimpleDateFormat dateFormat;
    private List<Schedule> schedules;
    private ListView scheduleListView;
    private ScheduleAdapter scheduleAdapter;
    private Date date;
    private EditText dateEditText;
    private Button setDateEditText;
    private View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.schedule_fragment, null);
        this.view = view;
        scheduleListView = view.findViewById(R.id.schedule_list_view);
        dateEditText = view.findViewById(R.id.dateEditText);
        setDateEditText = view.findViewById(R.id.setDateEditText);

        dateFormat = new SimpleDateFormat("yyyy.MM.dd");
        date = new Date(System.currentTimeMillis());
        dateEditText.setText(dateFormat.format(date));
        final GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        int inType = dateEditText.getInputType();
        dateEditText.setInputType(InputType.TYPE_NULL);

        setDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                Calendar calendar1 = new GregorianCalendar();
                                calendar1.set(year, month, dayOfMonth);
                                date = calendar1.getTime();
                                dateEditText.setText(dateFormat.format(date));
                                startTask();
                            }
                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        startTask();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        hideKeyboard();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    private void startTask() {
        try {
            schedules = new ScheduleTask().execute().get();
            if (schedules != null) {
                scheduleAdapter = new ScheduleAdapter(this.getContext(), schedules);
                scheduleListView.setAdapter(scheduleAdapter);
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    class ScheduleTask extends AsyncTask<Void, Void, List<Schedule>> {

        @Override
        protected List<Schedule> doInBackground(Void... voids) {
            ProfileCurrent profileCurrent = LkSingleton.getInstance().getProfileCurrent();
            do {
                if (profileCurrent != null) {
                    try {
                        String group = LkSingleton.getInstance()
                                .getProfileCurrent().getEduGroup().getTitle();
                        ScheduleGroup scheduleGroup = LkSingleton.getInstance().getLkSpmi().getScheduleGroups(group).get(0);
                        List<Schedule> schedules = LkSingleton.getInstance()
                                .getLkSpmi().getSchedules(scheduleGroup.getId(), date,
                                        date);
                        return schedules;
                    } catch (IOException e) {
                        return null;
                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            while (!isCancelled());
            return null;
        }
    }

}

