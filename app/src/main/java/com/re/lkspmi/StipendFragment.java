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
import java.util.List;
import java.util.concurrent.ExecutionException;

import ru.spmi.lk.entities.stipend.Stipend;

public class StipendFragment extends Fragment {
    private View view;
    private boolean isDestroyed = false;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.stipend_fragment, null);
        isDestroyed = false;
        new StipendTask().execute();

        return view;
    }
    private void initialize(List<Stipend> stipends){
        if (stipends != null){

            TableLayout tableLayout = view.findViewById(R.id.table_stipends);
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
            String[] lines = new String[] {"Вид стипендии", "Приказ", "Дата начала", "Дата окончания", "Сумма"};

            for (String s : lines){
                addRowItem(row, layoutParams, px, s);
            }
            tableLayout.addView(row);

            for (Stipend stipend : stipends) {
                row = new TableRow(getContext());
                addRowItem(row, layoutParams, px, stipend.getType());
                addRowItem(row, layoutParams, px, stipend.getOrder());
                addRowItem(row, layoutParams, px, stipend.getDateBegin());
                addRowItem(row, layoutParams, px, stipend.getDateEnd());
                addRowItem(row, layoutParams, px, String.valueOf(stipend.getSum()));
                tableLayout.addView(row);
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

    class StipendTask extends AsyncTask<Void, Void, List<Stipend>>{
        ProgressBar progressBar;
        RelativeLayout relativeLayout;

        public StipendTask() {
            relativeLayout = null;
        }

        public StipendTask(RelativeLayout relativeLayout) {
            this.relativeLayout = relativeLayout;
        }

        @Override
        protected void onPreExecute() {
            if (relativeLayout == null) {
                LinearLayout linearLayout = view.findViewById(R.id.stipend_layout);
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
        protected List<Stipend> doInBackground(Void... voids) {
            try {
                List<Stipend> stipends = LkSingleton.getInstance().getLkSpmi().getStipend();
                return stipends;
            } catch (IOException e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Stipend> stipends) {
            if (!isDestroyed) {
                if (stipends != null) {
                    LinearLayout linearLayout = view.findViewById(R.id.stipend_layout);
                    linearLayout.removeView(relativeLayout);
                    initialize(stipends);
                }
                else {
                    new StipendTask(relativeLayout).execute();
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
