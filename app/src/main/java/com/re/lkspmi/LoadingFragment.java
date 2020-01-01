package com.re.lkspmi;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LoadingFragment extends Fragment {
    private Fragment fragment;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.loading_fragment, null);
        fragment = (Fragment) getArguments().getSerializable("fragment");
        System.out.println("loading");
        new LoadingTask(this).execute();
        return view;
    }

    static class LoadingTask extends AsyncTask<Void, Void, Void>{
        private LoadingFragment loadingFragment;
        public LoadingTask(LoadingFragment loadingFragment){
            this.loadingFragment = loadingFragment;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            while (!LkSingleton.getInstance().isLoaded());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            FragmentManager fragmentManager = loadingFragment.getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(loadingFragment);
            LkSingleton.getInstance().setLoaded(false);
            fragmentTransaction.commit();
        }
    }
}
