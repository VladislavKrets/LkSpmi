package com.re.lkspmi.fragments;

import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.re.lkspmi.services.DownloadService;
import com.re.lkspmi.utils.LkSingleton;
import com.re.lkspmi.R;
import com.re.lkspmi.adapters.DiskAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.spmi.lk.entities.disk.Disk;

public class DiskFragment extends Fragment {

    private View view;
    private boolean isDestroyed = false;
    private DiskAdapter adapter;
    private List<Disk> data = new ArrayList<>();
    private GridView gridView;
    private String prevLink;
    private String currentLink;
    private List<String> links = new ArrayList<>();;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.disk_fragment, null);
        isDestroyed = false;
        gridView = view.findViewById(R.id.disk_gridview);
        adapter = new DiskAdapter(getContext(), data);
        gridView.setAdapter(adapter);
        prevLink = null;
        currentLink = null;
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int count = size.x / 400;
        gridView.setNumColumns(count);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0){
                    if (!links.isEmpty()) {
                        String link = links.remove(links.size() - 1);
                        prevLink = !links.isEmpty() ? links.get(links.size() - 1) : null;
                        currentLink = prevLink;
                        prevLink = links.size() > 1 ? links.get(links.size() - 2) : null;
                        LoadDiskTask diskTask = new LoadDiskTask(prevLink);
                        data.clear();
                        diskTask.execute();
                    }
                }
                else if (data.get(position).isFolder()){
                    links.add(data.get(position).getLink());
                    prevLink = currentLink;
                    currentLink = data.get(position).getLink();
                    LoadDiskTask diskTask = new LoadDiskTask(data.get(position).getLink());
                    data.clear();
                    diskTask.execute();
                }
                else {
                    Intent intent = new Intent(getContext(), DownloadService.class);
                    intent.putExtra("file_id", data.get(position).getId());
                    intent.putExtra("filename", data.get(position).getName());
                    intent.putExtra("path", Environment
                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());
                    getContext().startService(intent);
                }
            }
        });
        new LoadDiskTask(null).execute();
        return view;
    }

    class LoadDiskTask extends AsyncTask<Void, Void, List<Disk>>{
        ProgressBar progressBar;
        RelativeLayout relativeLayout;
        String url;

        public LoadDiskTask(String url) {
            relativeLayout = null;
            this.url = url;
        }

        public LoadDiskTask(RelativeLayout relativeLayout, String url) {
            this.relativeLayout = relativeLayout;
            this.url = url;
        }

        @Override
        protected void onPreExecute() {
            if (relativeLayout == null) {
                LinearLayout linearLayout = view.findViewById(R.id.disk_fragment_layout);
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
                gridView.setVisibility(View.GONE);
            }
        }

        @Override
        protected List<Disk> doInBackground(Void... voids) {
            try {
                if (url == null)
                    return LkSingleton.getInstance().getLkSpmi().getDisk();
                else
                    return LkSingleton.getInstance().getLkSpmi().getDisk(url);
            } catch (IOException e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Disk> disks) {
            if (!isDestroyed) {
                if (disks != null) {
                    LinearLayout linearLayout = view.findViewById(R.id.disk_fragment_layout);
                    linearLayout.removeView(relativeLayout);
                    gridView.setVisibility(View.VISIBLE);
                    //initialize(bupSections);
                    for (Disk disk : disks){

                        data.add(disk);
                    }

                    adapter.notifyDataSetChanged();
                } else {
                    new LoadDiskTask(relativeLayout, url).execute();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isDestroyed = true;
    }


}
