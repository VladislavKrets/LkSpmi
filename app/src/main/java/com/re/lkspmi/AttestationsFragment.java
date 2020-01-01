package com.re.lkspmi;

import android.content.res.Resources;
import android.graphics.Color;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
    private View view;
    String[] months = new String[]{
            "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.attestations_fragment, null);
        new AttestationsTask().execute();
        return view;
    }
    private void initialize(AttestationSemesterData[] data){
        TableLayout tableLayout = view.findViewById(R.id.table_attestations);
        TableRow row = new TableRow(getContext());
        row.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        Resources r = getContext().getResources();
        float px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                6,
                r.getDisplayMetrics()
        );

        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(20, 20, 20, 20);
        TextView textView = new TextView(getContext());
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(px);
        textView.setText("Дисциплина");
        linearLayout.addView(textView);
        row.addView(linearLayout, 0);



        int currMonth = Calendar.getInstance().get(Calendar.MONTH);
        int start = 0;

        if (LkSingleton.getInstance().getProfileCurrent().getEduSemester() == 2) start = 1;
        else start = 8;

        TableLayout.LayoutParams params = new TableLayout.LayoutParams();
        params.topMargin = 20;
        row.setLayoutParams(params);


        for (int i = 1; i < 5; i++){
            linearLayout = new LinearLayout(getContext());
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setPadding(20, 20, 20, 20);
            textView = new TextView(getContext());
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(px);
            textView.setText(months[i + start - 1]);
            linearLayout.addView(textView);
            row.addView(linearLayout, i);

        }
        row.setLayoutParams(params);
        tableLayout.addView(row);




        for (AttestationSemesterData semesterData : data){
            row = new TableRow(getContext());
            linearLayout = new LinearLayout(getContext());
            linearLayout.setPadding(20, 20, 20, 20);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            textView = new TextView(getContext());
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(px);
            textView.setMaxWidth(500);
            textView.setText(semesterData.getDiscipline());
            linearLayout.addView(textView);
            row.addView(linearLayout, 0);

            int i = 1;
            for (AttestationDisciplineData disciplineData : semesterData.getData()){
                linearLayout = new LinearLayout(getContext());
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                linearLayout.setPadding(20, 20, 20, 20);
                textView = new TextView(getContext());
                textView.setGravity(Gravity.CENTER);
                textView.setTextSize(px);
                textView.setText(disciplineData.getLecturer());
                linearLayout.addView(textView);

                textView = new TextView(getContext());
                textView.setGravity(Gravity.CENTER);
                textView.setTextSize(px);
                if(disciplineData.getResult() == 0){
                    textView.setText("Неаттестация");
                    textView.setTextColor(Color.RED);
                }
                else {
                    textView.setText("Аттестация");
                    textView.setTextColor(Color.GREEN);
                }
                linearLayout.addView(textView);
                row.addView(linearLayout, i);
                i++;
            }
            row.setLayoutParams(params);
            tableLayout.addView(row);
        }
    }
    class AttestationsTask extends AsyncTask<Void, Void, AttestationSemesterData[]>{
        ProgressBar progressBar;
        RelativeLayout relativeLayout;
        @Override
        protected void onPreExecute() {
            LinearLayout linearLayout = view.findViewById(R.id.attestations_layout);
            relativeLayout = new RelativeLayout(getContext());
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            relativeLayout.setLayoutParams(params);
            linearLayout.addView(relativeLayout);
            progressBar = new ProgressBar(getActivity(), null, android.R.attr.progressBarStyleLarge);
            params = new RelativeLayout.LayoutParams(300, 300);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            relativeLayout.addView(progressBar, params);

            relativeLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }
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

        @Override
        protected void onPostExecute(AttestationSemesterData[] attestationSemesterData) {
            relativeLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            if (attestationSemesterData != null)
                initialize(attestationSemesterData);
        }
    }
}
