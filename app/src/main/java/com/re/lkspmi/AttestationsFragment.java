package com.re.lkspmi;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

import ru.spmi.lk.entities.attestations.Attestation;
import ru.spmi.lk.entities.attestations.AttestationDisciplineData;
import ru.spmi.lk.entities.attestations.AttestationSemester;
import ru.spmi.lk.entities.attestations.AttestationSemesterData;

public class AttestationsFragment extends Fragment {

    String[] months = new String[]{
            "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.attestations_fragment, null);

        try {
            AttestationSemesterData[] data = new AttestationsTask().execute().get();
            TableLayout tableLayout = view.findViewById(R.id.table_attestations);
            TableRow row = new TableRow(getContext());
            row.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            ViewGroup.LayoutParams layoutParams;
            layoutParams = view.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;

            Resources r = getContext().getResources();
            float px = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    12,
                    r.getDisplayMetrics()
            );

            LinearLayout linearLayout = new LinearLayout(getContext());
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            TextView textView = new TextView(getContext());
            textView.setGravity(Gravity.CENTER);
            view.setLayoutParams(layoutParams);
            textView.setTextSize(px);
            textView.setText("Дисциплина");
            linearLayout.addView(textView);
            row.addView(linearLayout, 0);



            int currMonth = Calendar.getInstance().get(Calendar.MONTH);
            int start = 0;

            if (currMonth < 6) start = 1;
            else start = 8;

            for (int i = 1; i < 6; i++){
                linearLayout = new LinearLayout(getContext());
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                textView = new TextView(getContext());
                textView.setGravity(Gravity.CENTER);
                view.setLayoutParams(layoutParams);
                textView.setTextSize(px);
                textView.setText(months[i + start - 1]);
                linearLayout.addView(textView);
                row.addView(linearLayout, i);
            }
            tableLayout.addView(row);


            for (AttestationSemesterData semesterData : data){
                row = new TableRow(getContext());
                linearLayout = new LinearLayout(getContext());
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                textView = new TextView(getContext());
                textView.setGravity(Gravity.CENTER);
                view.setLayoutParams(layoutParams);
                textView.setTextSize(px);
                textView.setText(semesterData.getDiscipline());
                linearLayout.addView(textView);
                row.addView(linearLayout, 0);

                for (AttestationDisciplineData disciplineData : semesterData.getData()){
                    linearLayout = new LinearLayout(getContext());
                    linearLayout.setOrientation(LinearLayout.VERTICAL);
                    textView = new TextView(getContext());
                    textView.setGravity(Gravity.CENTER);
                    view.setLayoutParams(layoutParams);
                    textView.setTextSize(px);
                    textView.setText(disciplineData.getLecturer());
                    linearLayout.addView(textView);

                    textView = new TextView(getContext());
                    textView.setGravity(Gravity.CENTER);
                    view.setLayoutParams(layoutParams);
                    textView.setTextSize(px);
                    textView.setText(disciplineData.getResult() == 0 ? "Неаттестация" : "Аттестация");
                    linearLayout.addView(textView);
                    row.addView(linearLayout, disciplineData.getMonth() + 1);
                }
            }


        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return view;
    }

    class AttestationsTask extends AsyncTask<Void, Void, AttestationSemesterData[]>{

        @Override
        protected AttestationSemesterData[] doInBackground(Void... voids) {
            try {
                AttestationSemesterData[] data
                        = LkSingleton.getInstance().getLkSpmi()
                        .getAttestations().get(0).getSemesters()[0].getData();

                return data;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
