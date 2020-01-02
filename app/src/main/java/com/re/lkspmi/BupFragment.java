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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import ru.spmi.lk.entities.bup.BupSection;

public class BupFragment extends Fragment {
    private View view;
    private boolean isDestroyed = false;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bup_fragment, null);
        isDestroyed = false;
        new BupTask().execute();
        return view;
    }
    private void initialize(BupSection[] sections){

        if (sections != null){
            TableLayout tableLayout = view.findViewById(R.id.table_bups);
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
            String[] lines = new String[] {"Дисциплина", "Часы", "Семестр", "Вид контроля"};

            for (String s : lines){
                addRowItem(row, layoutParams, px, s);
            }
            tableLayout.addView(row);

            StringBuilder examBuilder;
            for (BupSection bupSection : sections){
                if (bupSection.getType().equals("subject")){
                    examBuilder = new StringBuilder();
                    row = new TableRow(getContext());
                    addRowItem(row, layoutParams, px, bupSection.getTitle());
                    addRowItem(row, layoutParams, px, String.valueOf(bupSection.getHours()));
                    addRowItem(row, layoutParams, px, bupSection.getSem());
                    if (bupSection.getExam() != null) examBuilder.append("Экзамен[")
                            .append(bupSection.getExam().toString()).append("], ");
                    if (bupSection.getTest() != null) examBuilder.append("Зачет[")
                            .append(bupSection.getTest().toString()).append("], ");
                    if (bupSection.getTestdif() != null) examBuilder.append("Диф. зачет[")
                            .append(bupSection.getTestdif().toString()).append("], ");
                    if (bupSection.getKr() != null) examBuilder.append("Курсовая работа[")
                            .append(bupSection.getKr().toString()).append("], ");
                    if (bupSection.getKp() != null) examBuilder.append("Курсовой проект[")
                            .append(bupSection.getKp().toString()).append("], ");
                    addRowItem(row, layoutParams, px, examBuilder.substring(0, examBuilder.length() - 2));
                    System.out.println("last " + bupSection.getTitle());
                    tableLayout.addView(row);
                    System.out.println("added " + examBuilder.toString());
                }
            }
        }
    }
    private void addRowItem(TableRow row, LinearLayout.LayoutParams layoutParams, float px, String s) {
        LinearLayout linearLayout;
        TextView textView;
        linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(20, 20, 20, 20);
        textView = new TextView(getContext());
        textView.setMaxWidth(500);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(px);
        textView.setText(s);
        linearLayout.addView(textView);
        row.addView(linearLayout);
    }

    class BupTask extends AsyncTask<Void, Void, BupSection[]>{
        ProgressBar progressBar;
        RelativeLayout relativeLayout;

        public BupTask() {
            relativeLayout = null;
        }

        public BupTask(RelativeLayout relativeLayout) {
            this.relativeLayout = relativeLayout;
        }

        @Override
        protected void onPreExecute() {
            if (relativeLayout == null) {
                LinearLayout linearLayout = view.findViewById(R.id.bup_layout);
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
        }
        @Override
        protected BupSection[] doInBackground(Void... voids) {
            try {
                BupSection[] sections = LkSingleton.getInstance().getLkSpmi().getBup().getSections();
                return sections;
            } catch (IOException e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(BupSection[] bupSections) {
            if (!isDestroyed) {
                if (bupSections != null) {
                    LinearLayout linearLayout = view.findViewById(R.id.bup_layout);
                    linearLayout.removeView(relativeLayout);
                    initialize(bupSections);
                } else {
                    new BupTask(relativeLayout).execute();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isDestroyed = true;
    }
}
