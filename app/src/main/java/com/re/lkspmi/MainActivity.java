package com.re.lkspmi;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ViewDragHelper;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.concurrent.ExecutionException;

import ru.spmi.lk.authorization.LkSpmi;
import ru.spmi.lk.entities.profile.ProfileCurrent;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ScheduleFragment scheduleFragment;
    private Fragment prevFragment;
    private AttestationsFragment attestationsFragment;
    private FragmentTransaction fragmentTransaction;
    private ProfileFragment profileFragment;
    private BupFragment bupFragment;
    private RupFragment rupFragment;
    private MarksFragment marksFragment;
    private OrdersFragment ordersFragment;
    private StipendFragment stipendFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        checkLogin();
        hideKeyboard(this);
    }

    public static void hideKeyboard(Activity activity){
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null){
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            if (prevFragment != profileFragment){
                fragmentTransaction.add(R.id.frame_layout, profileFragment);
                fragmentTransaction.remove(prevFragment);
                prevFragment = profileFragment;
            }
        } else if (id == R.id.nav_schedule) {
            if (prevFragment != scheduleFragment){
                fragmentTransaction.add(R.id.frame_layout, scheduleFragment);
                fragmentTransaction.remove(prevFragment);
                prevFragment = scheduleFragment;
            }
        } else if (id == R.id.nav_attestations) {
            if (prevFragment != attestationsFragment){
                fragmentTransaction.add(R.id.frame_layout, attestationsFragment);
                fragmentTransaction.remove(prevFragment);
                prevFragment = attestationsFragment;
            }
        } else if (id == R.id.nav_disk) {

        } else if (id == R.id.nav_portfolio) {

        } else if (id == R.id.nav_search) {

        } else if (id == R.id.nav_bup) {
            if (prevFragment != bupFragment){
                fragmentTransaction.add(R.id.frame_layout, bupFragment);
                fragmentTransaction.remove(prevFragment);
                prevFragment = bupFragment;
            }
        } else if (id == R.id.nav_rup) {
            if (prevFragment != rupFragment){
                fragmentTransaction.add(R.id.frame_layout, rupFragment);
                fragmentTransaction.remove(prevFragment);
                prevFragment = rupFragment;
            }
        } else if (id == R.id.nav_marks) {
            if (prevFragment != marksFragment){
                fragmentTransaction.add(R.id.frame_layout, marksFragment);
                fragmentTransaction.remove(prevFragment);
                prevFragment = marksFragment;
            }
        } else if (id == R.id.nav_orders) {
            if (prevFragment != ordersFragment){
                fragmentTransaction.add(R.id.frame_layout, ordersFragment);
                fragmentTransaction.remove(prevFragment);
                prevFragment = ordersFragment;
            }
        } else if (id == R.id.nav_stipend) {
            if (prevFragment != stipendFragment){
                fragmentTransaction.add(R.id.frame_layout, stipendFragment);
                fragmentTransaction.remove(prevFragment);
                prevFragment = stipendFragment;
            }
        }

        fragmentTransaction.commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

            if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    private void checkLogin() {
        SharedPreferences sPref = getSharedPreferences("preferences", MODE_PRIVATE);
        String login = sPref.getString("login", "");
        String password = sPref.getString("password", "");

        if(login == null || password == null || login.isEmpty() || password.isEmpty()){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            if (LkSingleton.getInstance().getLkSpmi() == null){
                try {
                    LkSpmi lkSpmi = new LoginTask(login, password).execute().get();
                    LkSingleton.getInstance().setLkSpmi(lkSpmi);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
            setContentView(R.layout.activity_main);
            initializationView();
            getPermissions();
            new ProfileCurrentTask().execute();
        }
    }

    private void initializationView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);


        setDrawerSwipeOpen(drawer);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);

        scheduleFragment = new ScheduleFragment();
        prevFragment = scheduleFragment;
        attestationsFragment = new AttestationsFragment();
        profileFragment = new ProfileFragment();
        bupFragment = new BupFragment();
        rupFragment = new RupFragment();
        marksFragment = new MarksFragment();
        ordersFragment = new OrdersFragment();
        stipendFragment = new StipendFragment();
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.frame_layout, scheduleFragment);
        fragmentTransaction.commit();
    }

    private void setDrawerSwipeOpen(DrawerLayout drawer) {
        Field mDragger = null;//mRightDragger for right obviously
        try {
            mDragger = drawer.getClass().getDeclaredField(
                    "mLeftDragger");
            mDragger.setAccessible(true);
            ViewDragHelper draggerObj = (ViewDragHelper) mDragger
                    .get(drawer);

            Field mEdgeSize = draggerObj.getClass().getDeclaredField(
                    "mEdgeSize");
            mEdgeSize.setAccessible(true);
            int edge = mEdgeSize.getInt(draggerObj);

            mEdgeSize.setInt(draggerObj, edge * 5);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_exit) {
            SharedPreferences sPref = getSharedPreferences("preferences", MODE_PRIVATE);
            SharedPreferences.Editor ed = sPref.edit();
            ed.putString("login", "");
            ed.putString("password", "");
            ed.apply();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class ProfileCurrentTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            LkSpmi lkSpmi = LkSingleton.getInstance().getLkSpmi();
            try {
                ProfileCurrent profileCurrent = lkSpmi.getProfileCurrent();
                LkSingleton.getInstance().setProfileCurrent(profileCurrent);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            ProfileCurrent profileCurrent = LkSingleton.getInstance().getProfileCurrent();
            if (profileCurrent != null) {
                String name = profileCurrent.getUser().getName();
                String lastname = profileCurrent.getUser().getLastname();
                NavigationView navigationView = findViewById(R.id.nav_view);
                View parentView = navigationView.getHeaderView(0);
                TextView text = (TextView)parentView.findViewById(R.id.name);
                text.setText(name + " " + lastname);
                if (profileCurrent.getUser().getPhoto().getOrig() == null){
                    AssetManager assetManager = getAssets();

                    InputStream istr;
                    Bitmap bitmap = null;
                    try {
                        istr = assetManager.open("default_image.jpg");
                        bitmap = BitmapFactory.decodeStream(istr);
                        ImageView imageView = parentView.findViewById(R.id.imageView);
                        imageView.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        // handle exception
                    }

                }
                else {
                    new ImageDownloadTask().execute();
                }
            }
        }
    }

    class ImageDownloadTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            ProfileCurrent profileCurrent = LkSingleton.getInstance().getProfileCurrent();
            if(profileCurrent != null && profileCurrent.getUser().getPhoto().getOrig() != null){
                File file = getExternalCacheDir();
                file = new File(file, "cache");
                if (!file.exists() || !file.isDirectory()) file.mkdir();
                LkSpmi lkSpmi = LkSingleton.getInstance().getLkSpmi();
                try {
                    lkSpmi.downloadImage("profile_photo",
                            profileCurrent.getUser().getPhoto().getOrig(), file.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            File file = new File(getExternalCacheDir(), "cache/profile_photo");
            if (file.exists()){
                InputStream istr;
                Bitmap bitmap = null;
                try {
                    istr = new FileInputStream(file);
                    bitmap = BitmapFactory.decodeStream(istr);

                    NavigationView navigationView = findViewById(R.id.nav_view);
                    View parentView = navigationView.getHeaderView(0);
                    ImageView imageView = parentView.findViewById(R.id.imageView);
                    imageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    // handle exception
                }
            }
        }
    }
}
