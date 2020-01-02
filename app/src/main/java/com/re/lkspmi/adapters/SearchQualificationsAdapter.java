package com.re.lkspmi.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

import ru.spmi.lk.entities.search.students.Qualification;
import ru.spmi.lk.entities.search.students.Specialization;

public class SearchQualificationsAdapter extends ArrayAdapter<String> {
    private List<Qualification> qualifications;

    public SearchQualificationsAdapter(Context context, int resource, String[] objects, List<Qualification> qualifications) {
        super(context, resource, objects);
        this.qualifications = qualifications;
    }

    public int getLabelId(int index){
        return qualifications.get(index).getValue();
    }
}
