package com.re.lkspmi;

import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import ru.spmi.lk.entities.search.employees.EmployeeSearchResponseItem;
import ru.spmi.lk.entities.search.employees.EmployeeSearchResponseItemProfile;
import ru.spmi.lk.entities.search.employees.EmployeeSearchResponseItemProfileDepartment;

public class EmployeeUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_user);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Профиль");

        LinearLayout linearLayout = findViewById(R.id.employee_user_layout);
        ImageView imageView = new ImageView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(400, 400);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        imageView.setLayoutParams(params);
        imageView.layout(0, 20, 0, 0);
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
        linearLayout.addView(imageView);

        String json = getIntent().getStringExtra("object");
        Gson gson = new Gson();
        EmployeeSearchResponseItem employeeSearchResponseItem
                = gson.fromJson(json, EmployeeSearchResponseItem.class);
        TextView textView = new TextView(this);
        textView.setText(employeeSearchResponseItem.getFullname());
        textView.setTextSize(dpToPx(8));
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        linearLayout.addView(textView);

        for (EmployeeSearchResponseItemProfile employeeSearchResponseItemProfile
                : employeeSearchResponseItem.getProfiles()){
            textView = new TextView(this);
            textView.setText("Профиль работы");
            textView.setTextSize(dpToPx(7));
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            textView.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
            linearLayout.addView(textView);

            textView = new TextView(this);
            textView.setText(employeeSearchResponseItemProfile.getJobTitle());
            textView.setTextSize(dpToPx(6));
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            textView.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
            linearLayout.addView(textView);

            for(EmployeeSearchResponseItemProfileDepartment employeeSearchResponseItemProfileDepartment
                    : employeeSearchResponseItemProfile.getDepartment()){
                textView = new TextView(this);
                textView.setText(employeeSearchResponseItemProfileDepartment.getTitle());
                textView.setTextSize(dpToPx(5));
                textView.setGravity(Gravity.CENTER_HORIZONTAL);
                textView.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
                linearLayout.addView(textView);
            }
        }

    }
    private int dpToPx(int dp) {
        Resources r = getResources();
        float px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                r.getDisplayMetrics()
        );
        return (int) px;
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
