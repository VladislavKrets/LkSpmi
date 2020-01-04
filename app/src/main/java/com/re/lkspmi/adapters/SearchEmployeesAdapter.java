package com.re.lkspmi.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.re.lkspmi.LkSingleton;
import com.re.lkspmi.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import ru.spmi.lk.authorization.LkSpmi;
import ru.spmi.lk.entities.search.employees.EmployeeSearchResponseItem;
import ru.spmi.lk.entities.search.students.StudentsSearchResponseItem;

public class SearchEmployeesAdapter extends BaseAdapter {

    Context ctx;
    LayoutInflater lInflater;
    List<EmployeeSearchResponseItem> objects;
    List<Bitmap> bitmaps;
    int prevPosition;
    int currPosition;
    boolean isWorking = false;
    private int viewTypeCount;

    public SearchEmployeesAdapter(Context context, List<EmployeeSearchResponseItem> products) {
        ctx = context;
        objects = products;
        prevPosition = 0;
        currPosition = objects.size();
        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewTypeCount = getCount() > 1 ? getCount() : 1;
        bitmaps = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public EmployeeSearchResponseItem getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public int getViewTypeCount() {

        return viewTypeCount;
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.search_view_item, parent, false);

        }
        ProgressBar progressBar = view.findViewById(R.id.search_item_progressbar);
        ImageView imageView = view.findViewById(R.id.search_item_avatar);

        TextView name = view.findViewById(R.id.search_item_name);
        LinearLayout linearLayout = view.findViewById(R.id.search_view_item_layout);
        EmployeeSearchResponseItem p = getItem(position);
        name.setText(p.getFullname());
        System.out.println("before imagetask " + bitmaps.size());
        if (position >= bitmaps.size() && !isWorking)
            new ImageTask(progressBar, imageView, p, linearLayout, prevPosition).execute();
        else {
            progressBar.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
        }
        if (bitmaps.size() > position){
            imageView.setImageBitmap(bitmaps.get(position));
            progressBar.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
        }

        return view;

    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        if (currPosition != objects.size()) {
            viewTypeCount = getCount() > 1 ? getCount() : 1;
            prevPosition = currPosition;
            currPosition = objects.size();
        }
    }

    class ImageTask extends AsyncTask<Void, Void, Void> {
        ProgressBar progressBar;
        EmployeeSearchResponseItem employeeSearchResponseItem;
        ImageView imageView;
        LinearLayout linearLayout;
        int position = 0;

        public ImageTask(ProgressBar progressBar, ImageView imageView,
                         EmployeeSearchResponseItem employeeSearchResponseItem, LinearLayout linearLayout, int position) {
            this.progressBar = progressBar;
            this.employeeSearchResponseItem = employeeSearchResponseItem;
            this.imageView = imageView;
            this.linearLayout = linearLayout;
            this.position = position;
        }

        @Override
        protected void onPreExecute() {
            isWorking = true;

        }

        @Override
        protected Void doInBackground(Void... voids) {
            for (int i = position; i < objects.size(); i++){
                if (objects.get(i).getPhoto().getOrig() != null){
                    File file = ctx.getExternalCacheDir();
                    file = new File(file, "cache");
                    if (!file.exists() || !file.isDirectory()) file.mkdir();
                    LkSpmi lkSpmi = LkSingleton.getInstance().getLkSpmi();
                    try {
                        lkSpmi.downloadImage("empl_profile_photo_" + objects.get(i).getId(),
                                objects.get(i).getPhoto().getOrig(), file.getAbsolutePath());
                        file = new File(ctx.getExternalCacheDir(), "cache/empl_profile_photo_"
                                + objects.get(i).getId());
                        InputStream istr = new FileInputStream(file);
                        Bitmap bitmap = BitmapFactory.decodeStream(istr);
                        bitmaps.add(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    InputStream istr = null;
                    try {
                        istr = ctx.getAssets().open("default_image.jpg");
                        Bitmap bitmap = BitmapFactory.decodeStream(istr);
                        bitmaps.add(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            System.out.println("bitmapsss " + bitmaps.size());
            if (bitmaps.size() != objects.size()){
                bitmaps.clear();
                new ImageTask(progressBar, imageView, employeeSearchResponseItem, linearLayout, position)
                        .execute();
            }
            else {
                isWorking = false;
                notifyDataSetChanged();
            }

        }
    }

    public void bitmapsReset(){
        bitmaps.clear();
        viewTypeCount = 1;
        prevPosition = 0;
        currPosition = 0;
        isWorking = false;
    }
}
