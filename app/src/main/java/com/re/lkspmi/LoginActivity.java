package com.re.lkspmi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import ru.spmi.lk.authorization.LkSpmi;
import ru.spmi.lk.authorization.LkSpmiAuthorization;
import ru.spmi.lk.exceptions.NotAuthorizedException;

public class LoginActivity extends AppCompatActivity {

    private EditText loginEditText;
    private EditText passwordEditText;
    private TextView errorTextView;
    private Button authorizationButton;
    private boolean isAuth = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_login);
        loginEditText = findViewById(R.id.editTextLogin);
        passwordEditText = findViewById(R.id.editTextPassword);
        errorTextView = findViewById(R.id.errorTextView);
        authorizationButton = findViewById(R.id.authorization_button);

        authorizationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String login = loginEditText.getText().toString();
                final String password = passwordEditText.getText().toString();
                LinearLayout linearLayout = findViewById(R.id.login_layout);
                authorizationButton.setEnabled(false);
                MainActivity.hideKeyboard(LoginActivity.this);
                new LoginTask(login, password, linearLayout,
                        LoginActivity.this, new CallbackInterface<LoginTask.ResultLoginTask>() {
                    @Override
                    public void callback(LoginTask.ResultLoginTask resultLoginTask) {
                        if (resultLoginTask.getStatus().equals("auth")) {
                            authorizationButton.setEnabled(true);
                            errorTextView.setText("Неправильный логин или пароль");
                        } else if (resultLoginTask.getStatus().equals("ok")){
                            LkSingleton.getInstance().setLkSpmi(resultLoginTask.getLkSpmi());
                            SharedPreferences sPref = getSharedPreferences("preferences", MODE_PRIVATE);
                            SharedPreferences.Editor ed = sPref.edit();
                            ed.putString("login", login);
                            ed.putString("password", password);
                            ed.apply();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                }).execute();

            }
        });

    }

}
