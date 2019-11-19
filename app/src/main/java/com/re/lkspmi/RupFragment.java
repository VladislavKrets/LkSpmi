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
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.github.florent37.expansionpanel.ExpansionHeader;
import com.github.florent37.expansionpanel.ExpansionLayout;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import ru.spmi.lk.entities.rup.Rup;
import ru.spmi.lk.entities.rup.RupSemester;
import ru.spmi.lk.entities.rup.RupSemesterSection;
import ru.spmi.lk.entities.rup.RupSemesterSectionTerm;

public class RupFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.rup_fragment, null);
        try {
            RupSemester[] semesters = new RupTask().execute().get();
            LinearLayout linearLayout = view.findViewById(R.id.dynamicLayoutController);
            if (semesters != null){
                ExpansionHeader expansionHeader;
                ExpansionLayout expansionLayout;
                for (RupSemester semester : semesters){
                    expansionHeader = createExpansionHeader(semester.getSemester() + " семестр");
                    expansionLayout = createExpansionLayout(semester);
                    linearLayout.addView(expansionHeader);
                    linearLayout.addView(expansionLayout);
                    expansionHeader.setExpansionLayout(expansionLayout);
                }
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return view;
    }

    private ExpansionLayout createExpansionLayout(RupSemester semester) {
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
        String[] lines = new String[] {"Дисциплина", "Часы", "Лекции", "Лаб.", "Практ.", "Сам. раб.", "Вид контроля"};

        for (String s : lines){
            addRowItem(row, layoutParams, px, s);
        }
        tableLayout.addView(row);
        RupSemesterSection[] sections = semester.getSections();

        StringBuilder examBuilder;
        for (RupSemesterSection rupSection : sections){
            if (rupSection.getType().equals("subject")){
                examBuilder = new StringBuilder();
                row = new TableRow(getContext());
                addRowItem(row, layoutParams, px, rupSection.getTitle());
                addRowItem(row, layoutParams, px, String.valueOf(rupSection.getHours()));

                for (RupSemesterSectionTerm term : rupSection.getTerms()) {
                    if (term.getNum() == semester.getSemester()){
                        addRowItem(row, layoutParams, px, term.getLections() == null ? "0" : term.getLections().toString());
                        addRowItem(row, layoutParams, px, term.getLabs() == null ? "0" : term.getLabs().toString());
                        addRowItem(row, layoutParams, px, term.getPractice() == null ? "0" : term.getPractice().toString());
                        addRowItem(row, layoutParams, px, term.getSelf() == null ? "0" : term.getSelf().toString());
                        if (term.getExam() != null) examBuilder.append("Экзамен[")
                                .append(term.getExam().toString()).append("], ");
                        if (term.getTest() != null) examBuilder.append("Зачет[")
                                .append(term.getTest().toString()).append("], ");
                        if (term.getTestdif() != null) examBuilder.append("Диф. зачет[")
                                .append(term.getTestdif().toString()).append("], ");
                        if (term.getKr() != null) examBuilder.append("Курсовая работа[")
                                .append(term.getKr().toString()).append("], ");
                        if (term.getKp() != null) examBuilder.append("Курсовой проект[")
                                .append(term.getKp().toString()).append("], ");
                        addRowItem(row, layoutParams, px, examBuilder.substring(0, examBuilder.length() - 2));
                        break;
                    }
                }
                tableLayout.addView(row);
            }
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
        expansionHeader.setBackgroundColor(Color.BLUE);

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
        textView.setTextColor(Color.WHITE);

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
    class RupTask extends AsyncTask<Void, Void, RupSemester[]>{

        @Override
        protected RupSemester[] doInBackground(Void... voids) {
            try {
                RupSemester[] semesters = LkSingleton.getInstance().getLkSpmi().getRup().getSemesters();
                return semesters;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
