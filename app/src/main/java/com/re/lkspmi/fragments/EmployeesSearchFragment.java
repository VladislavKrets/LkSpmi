package com.re.lkspmi.fragments;

import android.content.Intent;
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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.florent37.expansionpanel.ExpansionHeader;
import com.github.florent37.expansionpanel.ExpansionLayout;
import com.google.gson.Gson;
import com.re.lkspmi.EmployeeUserActivity;
import com.re.lkspmi.utils.LkSingleton;
import com.re.lkspmi.MainActivity;
import com.re.lkspmi.R;
import com.re.lkspmi.adapters.SearchEmployeesAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.spmi.lk.entities.search.employees.EmployeeSearchRequestBuilder;
import ru.spmi.lk.entities.search.employees.EmployeeSearchResponseItem;

public class EmployeesSearchFragment extends Fragment {
    private View view;
    private ProgressBar headerProgressBar;
    private EditText nameEditText;
    private ListView listView;
    private List<EmployeeSearchResponseItem> dataListView = new ArrayList<>();
    private SearchEmployeesAdapter listViewAdapter;
    private boolean isFullUploaded = false;
    private boolean isWorking = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.employees_search_fragment, null);

        ExpansionHeader expansionHeader = createExpansionHeader("Параметры поиска");
        ExpansionLayout expansionLayout = createExpansionLayout();
        LinearLayout linearLayout = view.findViewById(R.id.employees_search_layout);
        linearLayout.addView(expansionHeader);
        linearLayout.addView(expansionLayout);
        expansionHeader.setExpansionLayout(expansionLayout);
        listView = new ListView(getContext());
        listView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayout.addView(listView);
        listViewAdapter = new SearchEmployeesAdapter(getContext(), dataListView);
        listView.setAdapter(listViewAdapter);
        listViewAdapter.notifyDataSetChanged();
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                int lastItem = firstVisibleItem + visibleItemCount;
                if (!isWorking && !isFullUploaded && lastItem == totalItemCount){
                    new GetEmployeesTask(totalItemCount).execute();
                }
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), EmployeeUserActivity.class);

                intent.putExtra("photo", "empl_profile_photo_"
                        + dataListView.get(position).getId());
                Gson gson = new Gson();
                String json = gson.toJson(dataListView.get(position));
                intent.putExtra("object", json);
                startActivity(intent);
            }
        });
        return view;
    }

    class GetEmployeesTask extends AsyncTask<Void, Void, EmployeeSearchResponseItem[]>{
        private String name;
        private int firstItem;

        @Override
        protected void onPreExecute() {
            isWorking = true;
            headerProgressBar.setVisibility(View.VISIBLE);
            name = nameEditText.getText().toString();
        }

        public GetEmployeesTask() {
            firstItem = 0;
        }

        public GetEmployeesTask(int firstItem) {
            this.firstItem = firstItem;
        }

        @Override
        protected EmployeeSearchResponseItem[] doInBackground(Void... voids) {
            EmployeeSearchRequestBuilder requestBuilder = LkSingleton.getInstance().
                    getLkSpmi().searchEmployees().setFirst(firstItem).setRows(25);
            if (name != null && !name.trim().isEmpty()){
                requestBuilder = requestBuilder.addGlobalFilter(name);
            }
            try {
                System.out.println("before");
                EmployeeSearchResponseItem[] employeeSearchResponseItems = requestBuilder.execute().getItems();
                System.out.println("after");
                return employeeSearchResponseItems;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(EmployeeSearchResponseItem[] employeeSearchResponseItems) {
            if (employeeSearchResponseItems != null){
                if (employeeSearchResponseItems.length == 0) isFullUploaded = true;
                headerProgressBar.setVisibility(View.INVISIBLE);
                for (EmployeeSearchResponseItem employeeSearchResponseItem : employeeSearchResponseItems){
                    System.out.println(employeeSearchResponseItem.getFullname());
                    if (dataListView.size() > 0 && employeeSearchResponseItem.getFullname()
                            .equals(dataListView.get(0).getFullname())){
                        break;
                    }
                    dataListView.add(employeeSearchResponseItem);
                }
                listViewAdapter.notifyDataSetChanged();
                isWorking = false;
            }
            else {
                new GetEmployeesTask().execute();
            }
        }
    }

    private ExpansionLayout createExpansionLayout() {
        final ExpansionLayout expansionLayout = new ExpansionLayout(getContext());
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        expansionLayout.addView(linearLayout);
        TextView textView = new TextView(getContext());
        textView.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);

        textView.setText("ФИО");
        textView.setTextSize(dpToPx(6));
        linearLayout.addView(textView);
        nameEditText = new EditText(getContext());
        nameEditText.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        nameEditText.setGravity(Gravity.CENTER_HORIZONTAL);
        linearLayout.addView(nameEditText);

        Button searchButton = new Button(getContext());
        searchButton.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        searchButton.setText("Поиск");
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFullUploaded = false;
                dataListView.clear();
                listViewAdapter.bitmapsReset();
                listViewAdapter.notifyDataSetChanged();
                new GetEmployeesTask().execute();
                expansionLayout.collapse(true);
                MainActivity.hideKeyboard(getActivity());
            }
        });
        linearLayout.addView(searchButton);
        return expansionLayout;
    }

    private ExpansionHeader createExpansionHeader(String text) {
        final ExpansionHeader expansionHeader = new ExpansionHeader(getContext());
        expansionHeader.setBackgroundColor(Color.rgb(89, 181, 201));

        expansionHeader.setPadding(dpToPx(14), dpToPx(8), dpToPx(14), dpToPx(8));

        final LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.HORIZONTAL);
        expansionHeader.addView(layout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT); //equivalent to addView(linearLayout)
        layout.setPadding(20, 20, 20, 20);
        //label
        Resources r = getContext().getResources();
        float px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                6,
                r.getDisplayMetrics()
        );
        TextView textView = new TextView(getContext());
        textView.setTextSize(px);
        textView.setText(text);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.rgb(223, 241, 245));

        headerProgressBar = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleLarge);
        headerProgressBar.setPadding(20, 0, 0, 0);
        headerProgressBar.setLayoutParams(new ViewGroup.LayoutParams(70, 70));
        headerProgressBar.setVisibility(View.INVISIBLE);

        final ImageView expansionIndicator = new AppCompatImageView(getContext());
        expansionIndicator.setImageResource(R.drawable.ic_expansion_header_indicator_grey_24dp);
        final RelativeLayout.LayoutParams imageLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imageLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        imageLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        layout.addView(expansionIndicator, imageLayoutParams);

        layout.addView(textView);
        layout.addView(headerProgressBar);

        expansionHeader.setExpansionHeaderIndicator(expansionIndicator);
        return expansionHeader;
    }


    private int dpToPx(int dp) {
        Resources r = getContext().getResources();
        float px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                6,
                r.getDisplayMetrics()
        );
        return (int) px;
    }
}
