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
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.florent37.expansionpanel.ExpansionHeader;
import com.github.florent37.expansionpanel.ExpansionLayout;
import com.re.lkspmi.adapters.SearchEduDepAdapter;
import com.re.lkspmi.adapters.SearchQualificationsAdapter;
import com.re.lkspmi.adapters.SearchSpecializationsAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ru.spmi.lk.entities.search.students.EduDep;
import ru.spmi.lk.entities.search.students.Qualification;
import ru.spmi.lk.entities.search.students.Specialization;
import ru.spmi.lk.entities.search.students.StudentsSearchRequestBuilder;
import ru.spmi.lk.entities.search.students.StudentsSearchResponseItem;

public class StudentsSearchFragment extends Fragment {
    private View view;
    private EditText nameEditText;
    private ArrayAdapter<String> specializationsAdapter, qualificationsAdapter, departmentsAdapter, listViewAdapter;
    private Spinner specializationSpinner, qualificationsSpinner, departmentsSpinner;
    private ProgressBar headerProgressBar;
    private ListView listView;
    private boolean isFullUploaded = false;
    private List<String> dataListView = new ArrayList<>();
    private boolean isWorking = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.students_search_fragment, null);
        new BuildViewTask().execute();
        return view;
    }

    private ExpansionLayout createExpansionLayout() {
        final ExpansionLayout expansionLayout = new ExpansionLayout(getContext());

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

    class BuildViewTask extends AsyncTask<Void, Void, Void> {
        ProgressBar progressBar;
        RelativeLayout relativeLayout;
        private List<Specialization> specializations = null;
        private List<Qualification> qualifications = null;
        private List<EduDep> departments = null;

        public BuildViewTask() {
        }

        public BuildViewTask(RelativeLayout relativeLayout) {
            this.relativeLayout = relativeLayout;
        }

        @Override
        protected void onPreExecute() {
            if (relativeLayout == null) {
                LinearLayout linearLayout = view.findViewById(R.id.students_search_layout);
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
        protected Void doInBackground(Void... voids) {
            try {
                specializations = LkSingleton.getInstance().getLkSpmi().getAllSpecializations();
                qualifications = LkSingleton.getInstance().getLkSpmi().getAllQualifications();
                departments = LkSingleton.getInstance().getLkSpmi().getAllEduDeps();
                List<EduDep> copy = new ArrayList<>(departments);
                for (EduDep eduDep : copy){
                    for (EduDep eduDep1 : copy){
                        if (eduDep1.getId() == eduDep.getParentId() && departments.contains(eduDep1)){
                            departments.remove(eduDep1);
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (specializations == null || qualifications == null || departments == null) {
                new BuildViewTask().execute();
            } else {
                LinearLayout linearLayout = view.findViewById(R.id.students_search_layout);
                linearLayout.removeView(relativeLayout);
                ExpansionHeader expansionHeader = createExpansionHeader("Параметры поиска");
                final ExpansionLayout expansionLayout = createExpansionLayout();

                linearLayout = new LinearLayout(getContext());
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
                textView = new TextView(getContext());
                textView.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
                textView.setGravity(Gravity.CENTER_HORIZONTAL);
                textView.setText("Направление");
                textView.setTextSize(dpToPx(6));
                linearLayout.addView(textView);


                String[] data = new String[specializations.size() + 1];
                data[0] = "Ничего";
                for (int i = 0; i < specializations.size(); i++) {
                    data[i + 1] = specializations.get(i).getLabel();
                }
                specializations.add(0, null);
                specializationsAdapter = new SearchSpecializationsAdapter(getContext(), R.layout.adapter_item, data, specializations);
                specializationsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                specializationSpinner = new Spinner(getContext());
                specializationSpinner.setMinimumWidth(LinearLayout.LayoutParams.MATCH_PARENT);
                linearLayout.addView(specializationSpinner);
                specializationSpinner.setAdapter(specializationsAdapter);


                textView = new TextView(getContext());
                textView.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
                textView.setGravity(Gravity.CENTER_HORIZONTAL);
                textView.setText("Квалификация");
                textView.setTextSize(dpToPx(6));
                linearLayout.addView(textView);


                data = new String[qualifications.size() + 1];
                data[0] = "Ничего";
                for (int i = 0; i < qualifications.size(); i++) {
                    data[i + 1] = qualifications.get(i).getLabel();
                }
                qualifications.add(0, null);
                qualificationsAdapter = new SearchQualificationsAdapter(getContext(), R.layout.adapter_item, data, qualifications);
                qualificationsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                qualificationsSpinner = new Spinner(getContext());
                qualificationsSpinner.setMinimumWidth(LinearLayout.LayoutParams.MATCH_PARENT);
                linearLayout.addView(qualificationsSpinner);
                qualificationsSpinner.setAdapter(qualificationsAdapter);


                textView = new TextView(getContext());
                textView.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
                textView.setGravity(Gravity.CENTER_HORIZONTAL);
                textView.setText("Группа");
                textView.setTextSize(dpToPx(6));
                linearLayout.addView(textView);


                data = new String[departments.size() + 1];
                data[0] = "Ничего";
                for (int i = 0; i < departments.size(); i++) {
                    data[i + 1] = departments.get(i).getTitle();
                }
                departments.add(0, null);
                departmentsAdapter = new SearchEduDepAdapter(getContext(), R.layout.adapter_item, data, departments);
                departmentsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                departmentsSpinner = new Spinner(getContext());
                departmentsSpinner.setMinimumWidth(LinearLayout.LayoutParams.MATCH_PARENT);
                linearLayout.addView(departmentsSpinner);
                departmentsSpinner.setAdapter(departmentsAdapter);

                Button searchButton = new Button(getContext());
                searchButton.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
                searchButton.setText("Поиск");
                searchButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        expansionLayout.collapse(true);
                        isFullUploaded = false;
                        dataListView.clear();
                        listViewAdapter.notifyDataSetChanged();
                        new GetStudentsTask().execute();
                    }
                });
                linearLayout.addView(searchButton);

                linearLayout = view.findViewById(R.id.students_search_params);
                linearLayout.addView(expansionHeader);
                linearLayout.addView(expansionLayout);
                expansionHeader.setExpansionLayout(expansionLayout);

                listView = new ListView(getContext());
                listView.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                linearLayout.addView(listView);
                listViewAdapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_list_item_1, dataListView);
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
                            new GetStudentsTask(totalItemCount).execute();
                        }
                    }
                });

            }
        }
    }

    class GetStudentsTask extends AsyncTask<Void, Void, StudentsSearchResponseItem[]> {
        private String name;
        private int qualificationIndex, specializationIndex, departmentIndex;
        private int firstItem;

        @Override
        protected void onPreExecute() {
            isWorking = true;
            headerProgressBar.setVisibility(View.VISIBLE);
            name = nameEditText.getText().toString();
            qualificationIndex = qualificationsSpinner.getSelectedItemPosition();
            specializationIndex = specializationSpinner.getSelectedItemPosition();
            departmentIndex = departmentsSpinner.getSelectedItemPosition();

        }

        public GetStudentsTask() {
            firstItem = 0;
        }

        public GetStudentsTask(int firstItem) {
            this.firstItem = firstItem;
        }

        @Override
        protected StudentsSearchResponseItem[] doInBackground(Void... voids) {
            StudentsSearchRequestBuilder builder = LkSingleton.getInstance().getLkSpmi()
                    .searchStudents().setRows(50).setFirst(firstItem);
            if (name != null && !name.trim().isEmpty()) {
                builder = builder.addEduFullNameFilter(name);
            }
            if (departmentIndex != 0) {
                builder = builder.addDepartmentFilter(
                        ((SearchEduDepAdapter) departmentsAdapter).getLabelId(departmentIndex));
                System.out.println(
                        ((SearchEduDepAdapter) departmentsAdapter).getLabelId(departmentIndex ));
            }
            if (qualificationIndex != 0) {
                builder = builder.addEduQualificationFilter(
                        ((SearchQualificationsAdapter) qualificationsAdapter).getLabelId(qualificationIndex));
            }
            if (specializationIndex != 0) {
                builder = builder.addEduSpecializationFilter(
                        ((SearchSpecializationsAdapter) specializationsAdapter).getLabelId(specializationIndex));
            }
            try {
                StudentsSearchResponseItem[] studentsSearchResponseItems = builder.execute().getItems();
                System.out.println(Arrays.toString(studentsSearchResponseItems));
                return studentsSearchResponseItems;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(StudentsSearchResponseItem[] studentsSearchResponseItems) {
            if (studentsSearchResponseItems != null) {
                if (studentsSearchResponseItems.length == 0) isFullUploaded = true;
                headerProgressBar.setVisibility(View.INVISIBLE);
                    for (StudentsSearchResponseItem studentsSearchResponseItem : studentsSearchResponseItems) {
                        if (dataListView.size() > 0 && studentsSearchResponseItem.getFullname().equals(dataListView.get(0))){
                            break;
                        }
                        System.out.println(studentsSearchResponseItem.getFullname());
                        dataListView.add(studentsSearchResponseItem.getFullname());
                    }
                    listViewAdapter.notifyDataSetChanged();
                    isWorking = false;
            } else {
                new GetStudentsTask().execute();
            }
        }
    }
}
