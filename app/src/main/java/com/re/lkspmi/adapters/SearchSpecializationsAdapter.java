package com.re.lkspmi.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import ru.spmi.lk.entities.search.students.Specialization;

public class SearchSpecializationsAdapter extends ArrayAdapter<String> {
    private List<Specialization> specializations;

    public SearchSpecializationsAdapter(Context context, int resource, String[] objects, List<Specialization> specializations) {
        super(context, resource, objects);
        this.specializations = specializations;
    }

    public int getLabelId(int index){
        return specializations.get(index).getValue();
    }
}
