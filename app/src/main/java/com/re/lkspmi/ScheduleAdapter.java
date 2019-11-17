package com.re.lkspmi;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.spmi.lk.entities.schedule.Schedule;
import ru.spmi.lk.entities.schedule.ScheduleGroup;

public class ScheduleAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    List<List<Schedule>> schedules;

    ScheduleAdapter(Context context, List<Schedule> schedules) {
        ctx = context;
        this.schedules = new ArrayList<>();
        List<Schedule> list = null;
        for (Schedule schedule : schedules){
            if (list == null){
                list = new ArrayList<>();
                list.add(schedule);
            }
            else {
                if (list.get(0).getBeginLesson().equals(schedule.getBeginLesson())){
                    list.add(schedule);
                }
                else {
                    this.schedules.add(list);
                    list = new ArrayList<>();
                    list.add(schedule);
                }
            }
        }
        if (list != null) this.schedules.add(list);
        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // кол-во элементов
    @Override
    public int getCount() {
        return schedules.size();
    }

    // элемент по позиции
    @Override
    public List<Schedule> getItem(int position) {
        return schedules.get(position);
    }

    // id по позиции
    @Override
    public long getItemId(int position) {
        return position;
    }

    // пункт списка
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // используем созданные, но не используемые view
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.item_schedule_list_view, parent, false);
        }

        if (this.schedules.isEmpty()) return view;

        List<Schedule> schedules = getItem(position);

        // заполняем View в пункте списка данными из товаров: наименование, цена
        // и картинка
        LinearLayout linearLayout = view.findViewById(R.id.item_linear_layout);

        ViewGroup.LayoutParams layoutParams;
        layoutParams = view.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        Resources r = ctx.getResources();
        float px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                12,
                r.getDisplayMetrics()
        );
        TextView textView = view.findViewById(R.id.first_field);
        textView.setText(schedules.get(0).getBeginLesson() + " - " + schedules.get(0).getEndLesson());
        textView = view.findViewById(R.id.second_field);
        textView.setText(schedules.get(0).getDiscipline());
        textView = view.findViewById(R.id.third_field);
        textView.setText(schedules.get(0).getAuditorium() + ", " + schedules.get(0).getLecturer());

        if (schedules.size() > 1){
            textView = view.findViewById(R.id.fourth_field);
            textView.setText("или");
            textView = view.findViewById(R.id.fifth_field);
            textView.setText(schedules.get(1).getDiscipline());
            textView = view.findViewById(R.id.sixth_field);
            textView.setText(schedules.get(1).getAuditorium() + ", " + schedules.get(1).getLecturer());
        }
        else {
            textView = view.findViewById(R.id.fourth_field);
            textView.setText("");
            textView = view.findViewById(R.id.fifth_field);
            textView.setText("");
            textView = view.findViewById(R.id.sixth_field);
            textView.setText("");
        }
        if (schedules.size() > 2){
            textView = view.findViewById(R.id.seventh_field);
            textView.setText("или");
            textView = view.findViewById(R.id.eighth_field);
            textView.setText(schedules.get(2).getDiscipline());
            textView = view.findViewById(R.id.nineth_field);
            textView.setText(schedules.get(2).getAuditorium() + ", " + schedules.get(2).getLecturer());
        }
        else {
            textView = view.findViewById(R.id.seventh_field);
            textView.setText("");
            textView = view.findViewById(R.id.eighth_field);
            textView.setText("");
            textView = view.findViewById(R.id.nineth_field);
            textView.setText("");
        }



        return view;
    }

    private void printTextView(View view, LinearLayout linearLayout, ViewGroup.LayoutParams layoutParams, float px, String line) {
        TextView textView;
        textView = new TextView(ctx);
        textView.setGravity(Gravity.CENTER);
        view.setLayoutParams(layoutParams);
        textView.setTextSize(px);
        textView.setText(line);
        linearLayout.addView(textView);
    }

}
