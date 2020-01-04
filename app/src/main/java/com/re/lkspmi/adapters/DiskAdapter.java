package com.re.lkspmi.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.re.lkspmi.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import ru.spmi.lk.entities.disk.Disk;
import ru.spmi.lk.entities.search.employees.EmployeeSearchResponseItem;

public class DiskAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    List<Disk> objects;

    public DiskAdapter(Context context, List<Disk> products) {
        ctx = context;
        objects = products;
        objects.add(0, null);
        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Disk getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.disk_item, parent, false);
        }
        if (position == 0){
            ImageView imageButton = view.findViewById(R.id.image_button);
            InputStream istr = null;
            String spriteName = "folder.png";
            try {
                istr = ctx.getAssets().open(spriteName);
                Bitmap bitmap = BitmapFactory.decodeStream(istr);
                imageButton.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            TextView textView = view.findViewById(R.id.file_name);
            textView.setText("..");
            return view;
        }
        Disk disk = getItem(position);
        ImageView imageButton = view.findViewById(R.id.image_button);
        InputStream istr = null;
        String spriteName = "unknown.png";
        if (disk.isFolder()) {
            spriteName = "folder.png";
        }
        else if (disk.getName().endsWith(".doc") || disk.getName().endsWith(".docx")){
            spriteName = "doc.png";
        }
        else if (disk.getName().endsWith(".png") || disk.getName().endsWith(".jpeg")
                || disk.getName().endsWith(".gif") || disk.getName().endsWith(".bmp")){
            spriteName = "image.png";
        }
        else if (disk.getName().endsWith(".mp4") || disk.getName().endsWith(".avi")
                || disk.getName().endsWith(".dvd") || disk.getName().endsWith(".flv")
                || disk.getName().endsWith(".mov")){
            spriteName = "video.png";
        }
        else if (disk.getName().endsWith(".pdf")){
            spriteName = "pdf.png";
        }
        else if (disk.getName().endsWith(".php")){
            spriteName = "php.png";
        }
        else if (disk.getName().endsWith(".ppt") || disk.getName().endsWith(".pptx")){
            spriteName = "ppt.png";
        }
        else if (disk.getName().endsWith(".properties")){
            spriteName = "properties.png";
        }
        else if (disk.getName().endsWith(".zip")){
            spriteName = "zip.png";
        }
        else if (disk.getName().endsWith(".rar")){
            spriteName = "rar.png";
        }
        else if (disk.getName().endsWith(".txt")){
            spriteName = "txt.png";
        }
        else if (disk.getName().endsWith(".xls") || disk.getName().endsWith(".xlsx")){
            spriteName = "xls.png";
        }
        try {
            istr = ctx.getAssets().open(spriteName);
            Bitmap bitmap = BitmapFactory.decodeStream(istr);
            imageButton.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        TextView textView = view.findViewById(R.id.file_name);
        textView.setText(disk.getName());
        return view;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        if (objects.isEmpty() || objects.get(0) != null){
            objects.add(0, null);
        }
    }
}
