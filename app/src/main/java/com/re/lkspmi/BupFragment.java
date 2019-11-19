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
import java.util.concurrent.ExecutionException;

import ru.spmi.lk.entities.bup.BupSection;

public class BupFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bup_fragment, null);

        try {
            BupSection[] sections = new BupTask().execute().get();

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
                    addRowItem(view, row, layoutParams, px, s);
                }
                tableLayout.addView(row);

                StringBuilder examBuilder;
                for (BupSection bupSection : sections){
                    if (bupSection.getType().equals("subject")){
                        examBuilder = new StringBuilder();
                        row = new TableRow(getContext());
                        addRowItem(view, row, layoutParams, px, bupSection.getTitle());
                        addRowItem(view, row, layoutParams, px, String.valueOf(bupSection.getHours()));
                        addRowItem(view, row, layoutParams, px, bupSection.getSem());
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
                        addRowItem(view, row, layoutParams, px, examBuilder.substring(0, examBuilder.length() - 2));
                        tableLayout.addView(row);
                    }
                }
            }

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return view;
    }

    private void addRowItem(View view, TableRow row, LinearLayout.LayoutParams layoutParams, float px, String s) {
        LinearLayout linearLayout;
        TextView textView;
        linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(20, 20, 20, 20);
        textView = new TextView(getContext());
        textView.setMaxWidth(500);
        textView.setGravity(Gravity.CENTER);
        view.setLayoutParams(layoutParams);
        textView.setTextSize(px);
        textView.setText(s);
        linearLayout.addView(textView);
        row.addView(linearLayout);
    }

    class BupTask extends AsyncTask<Void, Void, BupSection[]>{

        @Override
        protected BupSection[] doInBackground(Void... voids) {
            try {
                BupSection[] sections = LkSingleton.getInstance().getLkSpmi().getBup().getSections();
                return sections;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
