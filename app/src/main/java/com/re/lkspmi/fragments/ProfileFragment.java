package com.re.lkspmi.fragments;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.re.lkspmi.utils.LkSingleton;
import com.re.lkspmi.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import ru.spmi.lk.entities.profile.ProfileCurrent;


public class ProfileFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment, null);
        if (LkSingleton.getInstance().getProfileCurrent() == null) return view;

        File file = new File(getActivity().getExternalCacheDir(), "cache/profile_photo");
        if (file.exists()){
            InputStream istr;
            Bitmap bitmap = null;
            try {
                istr = new FileInputStream(file);
                bitmap = BitmapFactory.decodeStream(istr);
                ((ImageView) view.findViewById(R.id.profile_image_view)).setImageBitmap(bitmap);

            } catch (IOException e) {
                AssetManager assetManager = getActivity().getAssets();
                try {
                    istr = assetManager.open("default_image.jpg");
                    bitmap = BitmapFactory.decodeStream(istr);
                    ((ImageView) view.findViewById(R.id.profile_image_view)).setImageBitmap(bitmap);
                } catch (IOException e1) {
                    // handle exception
                }
            }
        }
        else {
            AssetManager assetManager = getActivity().getAssets();

            InputStream istr;
            Bitmap bitmap = null;
            try {
                istr = assetManager.open("default_image.jpg");
                bitmap = BitmapFactory.decodeStream(istr);
                ((ImageView) view.findViewById(R.id.profile_image_view)).setImageBitmap(bitmap);
            } catch (IOException e) {
                // handle exception
            }
        }
        ProfileCurrent profileCurrent = LkSingleton.getInstance().getProfileCurrent();
        ((TextView) view.findViewById(R.id.full_name)).setText(profileCurrent.getUser().getFullname());
        ((TextView) view.findViewById(R.id.email)).setText("Email: " + profileCurrent.getUser().getEmail());
        ((TextView) view.findViewById(R.id.faculthy)).setText("Факультет: " + profileCurrent.getFaculty().getTitle());
        ((TextView) view.findViewById(R.id.cathedra)).setText("Кафедра: " + profileCurrent.getCathedra());
        ((TextView) view.findViewById(R.id.specialization)).setText("Направление: " + profileCurrent.getEduSpecialization().getTitle());
        ((TextView) view.findViewById(R.id.group)).setText("Группа: " + profileCurrent.getEduGroup().getTitle());
        return view;
    }
}
