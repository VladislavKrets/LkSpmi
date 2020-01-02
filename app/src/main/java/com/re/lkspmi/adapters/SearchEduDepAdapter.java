package com.re.lkspmi.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

import ru.spmi.lk.entities.search.students.EduDep;
import ru.spmi.lk.entities.search.students.Qualification;

public class SearchEduDepAdapter extends ArrayAdapter<String> {
    private List<EduDep> departments;

    public SearchEduDepAdapter(Context context, int resource, String[] objects, List<EduDep> departments) {
        super(context, resource, objects);
        this.departments = departments;
    }

    public int getLabelId(int index){
        return departments.get(index).getId();
    }
}
