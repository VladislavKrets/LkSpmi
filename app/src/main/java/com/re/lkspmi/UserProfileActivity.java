package com.re.lkspmi;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import ru.spmi.lk.authorization.LkSpmi;

public class UserProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Профиль");

        ImageView imageView = findViewById(R.id.profile_image_view);
        String image = getIntent().getStringExtra("photo");
        File file = getExternalCacheDir();
        file = new File(file, "cache");
        if (!file.exists() || !file.isDirectory()) file.mkdir();
        file = new File(getExternalCacheDir(), "cache/" + image);
        if (file.exists()) {
            try {
                InputStream istr = new FileInputStream(file);
                Bitmap bitmap = BitmapFactory.decodeStream(istr);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            InputStream istr = null;
            try {
                istr = getAssets().open("default_image.jpg");
                Bitmap bitmap = BitmapFactory.decodeStream(istr);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        TextView textView = findViewById(R.id.full_name);
        textView.setText(getIntent().getStringExtra("name"));
        textView = findViewById(R.id.qualification);
        textView.setText("Квалификация: " + getIntent().getStringExtra("qualification"));
        textView = findViewById(R.id.specialization);
        textView.setText("Специализация: " + getIntent().getStringExtra("specialization"));
        textView = findViewById(R.id.direction);
        textView.setText("Направление: " + getIntent().getStringExtra("direction"));
        textView = findViewById(R.id.department);
        textView.setText("Факультет: " + getIntent().getStringExtra("department"));
        textView = findViewById(R.id.group);
        textView.setText("Группа: " + getIntent().getStringExtra("group"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
