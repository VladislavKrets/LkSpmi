package com.re.lkspmi;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import java.io.IOException;

import ru.spmi.lk.authorization.LkSpmi;
import ru.spmi.lk.authorization.LkSpmiAuthorization;
import ru.spmi.lk.exceptions.NotAuthorizedException;

public class LoginTask extends AsyncTask<Void, Void, LkSpmi> {

    private String login;
    private String password;
    private CallbackInterface callbackInterface;
    private LinearLayout linearLayout;
    private Context context;
    ProgressBar progressBar;
    RelativeLayout relativeLayout;

    public LoginTask(String login, String password, LinearLayout linearLayout, Context context, CallbackInterface callbackInterface) {
        this.login = login;
        this.password = password;
        this.linearLayout = linearLayout;
        this.context = context;
        this.callbackInterface = callbackInterface;
    }


    @Override
    protected void onPreExecute() {
        relativeLayout = new RelativeLayout(context);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        relativeLayout.setLayoutParams(params);
        linearLayout.addView(relativeLayout);
        progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleLarge);
        params = new RelativeLayout.LayoutParams(300, 300);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        relativeLayout.addView(progressBar, params);

        relativeLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }
    @Override
    protected LkSpmi doInBackground(Void... voids) {
        LkSpmiAuthorization authorization = new LkSpmiAuthorization();
        try {
            LkSpmi lkSpmi = authorization.authorize(login, password);
            return lkSpmi;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NotAuthorizedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(LkSpmi lkSpmi) {
        relativeLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        callbackInterface.callback(lkSpmi);
    }
}