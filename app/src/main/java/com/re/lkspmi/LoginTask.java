package com.re.lkspmi;

import android.os.AsyncTask;

import java.io.IOException;

import ru.spmi.lk.authorization.LkSpmi;
import ru.spmi.lk.authorization.LkSpmiAuthorization;
import ru.spmi.lk.exceptions.NotAuthorizedException;

public class LoginTask extends AsyncTask<Void, Void, LkSpmi> {

    private String login;
    private String password;

    public LoginTask(String login, String password) {
        this.login = login;
        this.password = password;
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
}