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

public class LoginTask extends AsyncTask<Void, Void, LoginTask.ResultLoginTask> {

    private String login;
    private String password;
    private CallbackInterface<ResultLoginTask> callbackInterface;
    private LinearLayout linearLayout;
    private Context context;
    ProgressBar progressBar;
    RelativeLayout relativeLayout;

    public LoginTask(String login, String password, LinearLayout linearLayout, Context context,
                     CallbackInterface<ResultLoginTask> callbackInterface) {
        this.login = login;
        this.password = password;
        this.linearLayout = linearLayout;
        this.context = context;
        this.callbackInterface = callbackInterface;
        relativeLayout = null;
    }

    public LoginTask(String login, String password, LinearLayout linearLayout,
                     Context context, CallbackInterface<ResultLoginTask> callbackInterface, RelativeLayout relativeLayout) {
        this.login = login;
        this.password = password;
        this.linearLayout = linearLayout;
        this.context = context;
        this.callbackInterface = callbackInterface;
        this.relativeLayout = relativeLayout;
    }
    @Override
    protected void onPreExecute() {
        if (relativeLayout == null) {
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
    }
    @Override
    protected ResultLoginTask doInBackground(Void... voids) {
        LkSpmiAuthorization authorization = new LkSpmiAuthorization();
        try {
            LkSpmi lkSpmi = authorization.authorize(login, password);
            return new ResultLoginTask(lkSpmi, "ok");
        }
        catch (NotAuthorizedException e) {
            return new ResultLoginTask(null, "auth");
        }
        catch (IOException e) {
            return new ResultLoginTask(null, "connection");
        }

    }

    @Override
    protected void onPostExecute(ResultLoginTask resultLoginTask) {
        if (resultLoginTask.status.equals("connection")){
            new LoginTask(login, password, linearLayout, context, callbackInterface, relativeLayout).execute();
        }
        else {
            linearLayout.removeView(relativeLayout);
            callbackInterface.callback(resultLoginTask);
        }
    }

    public static class ResultLoginTask{
        private LkSpmi lkSpmi;
        private String status;

        public ResultLoginTask(LkSpmi lkSpmi, String status) {
            this.lkSpmi = lkSpmi;
            this.status = status;
        }

        public LkSpmi getLkSpmi() {
            return lkSpmi;
        }

        public void setLkSpmi(LkSpmi lkSpmi) {
            this.lkSpmi = lkSpmi;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}