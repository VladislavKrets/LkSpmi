package com.re.lkspmi;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.github.florent37.expansionpanel.ExpansionHeader;
import com.github.florent37.expansionpanel.ExpansionLayout;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import ru.spmi.lk.entities.marks.Mark2;
import ru.spmi.lk.entities.marks.Mark2Semester;
import ru.spmi.lk.entities.marks.Mark2SemesterData;
import ru.spmi.lk.entities.rup.RupSemester;

public class MarksFragment extends Fragment {
    private View view;
    private boolean isDestroyed = false;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.marks_fragment, null);
        isDestroyed = false;
        new MarksTask().execute();
        return view;
    }
    private void initialize(List<Mark2> marks){
        LinearLayout linearLayout = view.findViewById(R.id.marks_container);
        ExpansionHeader expansionHeader;
        ExpansionLayout expansionLayout;
        if (marks != null){
            for (Mark2 mark : marks){

                for (Mark2Semester semester : mark.getSemesters()){
                    expansionHeader = createExpansionHeader(String.valueOf(mark.getYear())
                            + "/" + String.valueOf(mark.getYear() + 1) + " " + semester.getSemester() + " семестр");
                    expansionLayout = createExpansionLayout(semester);
                    linearLayout.addView(expansionHeader);
                    linearLayout.addView(expansionLayout);
                    expansionHeader.setExpansionLayout(expansionLayout);
                }

            }
        }
    }
    private ExpansionLayout createExpansionLayout(Mark2Semester semester) {
        final ExpansionLayout expansionLayout = new ExpansionLayout(getContext());
        ScrollView scrollView = new ScrollView(getContext());
        expansionLayout.addView(scrollView);
        scrollView.setFillViewport(true);
        scrollView.setScrollBarSize(0);
        scrollView.setHorizontalScrollBarEnabled(false);
        scrollView.setVerticalScrollBarEnabled(false);
        HorizontalScrollView horizontalScrollView = new HorizontalScrollView(getContext());
        scrollView.addView(horizontalScrollView);
        TableLayout tableLayout = new TableLayout(getContext());
        horizontalScrollView.addView(tableLayout);

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
        String[] lines = new String[] {"Дисциплина", "Вид контроля", "Часы", "Дата", "Преподаватель", "Результат"};

        for (String s : lines){
            addRowItem(row, layoutParams, px, s);
        }
        tableLayout.addView(row);

        Mark2SemesterData[] semesterData = semester.getData();

        for (Mark2SemesterData semesterSection : semesterData){
            row = new TableRow(getContext());
            addRowItem(row, layoutParams, px, semesterSection.getSubject());
            addRowItem(row, layoutParams, px, semesterSection.getControlType());
            addRowItem(row, layoutParams, px, semesterSection.getHours());
            addRowItem(row, layoutParams, px, semesterSection.getDate());
            addRowItem(row, layoutParams, px, semesterSection.getLecturers());
            addRowItem(row, layoutParams, px, semesterSection.getMarkTitle());
            tableLayout.addView(row);
        }
        return expansionLayout;
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

    private ExpansionHeader createExpansionHeader(String text) {
        final ExpansionHeader expansionHeader = new ExpansionHeader(getContext());
        expansionHeader.setBackgroundColor(Color.rgb(89, 181, 201));

        expansionHeader.setPadding(dpToPx(16), dpToPx(8), dpToPx(16), dpToPx(8));

        final LinearLayout layout = new LinearLayout(getContext());
        expansionHeader.addView(layout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT); //equivalent to addView(linearLayout)
        layout.setPadding(40, 40, 40, 40);
        //label
        Resources r = getContext().getResources();
        float px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                10,
                r.getDisplayMetrics()
        );
        TextView textView = new TextView(getContext());
        textView.setTextSize(px);
        textView.setText(text);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.rgb(223, 241, 245));

        final ImageView expansionIndicator = new AppCompatImageView(getContext());
        expansionIndicator.setImageResource(R.drawable.ic_expansion_header_indicator_grey_24dp);
        final RelativeLayout.LayoutParams imageLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imageLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        imageLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        layout.addView(expansionIndicator, imageLayoutParams);

        layout.addView(textView);

        expansionHeader.setExpansionHeaderIndicator(expansionIndicator);
        return expansionHeader;
    }
    private int dpToPx(int dp){
        Resources r = getContext().getResources();
        float px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                6,
                r.getDisplayMetrics()
        );
        return (int)px;
    }

    class MarksTask extends AsyncTask<Void, Void, List<Mark2>>{
        ProgressBar progressBar;
        RelativeLayout relativeLayout;

        public MarksTask() {
            relativeLayout = null;
        }

        public MarksTask(RelativeLayout relativeLayout) {
            this.relativeLayout = relativeLayout;
        }

        @Override
        protected void onPreExecute() {
            if (relativeLayout == null) {
                LinearLayout linearLayout = view.findViewById(R.id.marks_layout);
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
        protected List<Mark2> doInBackground(Void... voids) {
            try {
                List<Mark2> marks = LkSingleton.getInstance().getLkSpmi().getMarks();
                return marks;
            } catch (IOException e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Mark2> marks) {
            if (!isDestroyed) {
                if (marks != null) {
                    LinearLayout linearLayout = view.findViewById(R.id.marks_layout);
                    linearLayout.removeView(relativeLayout);
                    initialize(marks);
                }
                else {
                    new MarksTask(relativeLayout).execute();
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
