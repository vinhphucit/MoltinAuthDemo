package com.roxwin.moltinauthdemo;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import moltin.android_sdk.Moltin;
import moltin.android_sdk.utilities.Constants;

public class MainActivity extends AppCompatActivity {

    Button btnLogin;
    TextView loginStatus;
    RelativeLayout rl_progress;

    private Moltin moltin;
    private boolean wasLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupView();
        setupMoltin();
        tryToLoginToMoltin();
    }

    private void setupView() {
        btnLogin = (Button) findViewById(R.id.btnLogin);
        loginStatus = (TextView) findViewById(R.id.loginStatus);
        rl_progress = (RelativeLayout) findViewById(R.id.rl_progress);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tryToLoginToMoltin();
            }
        });
    }

    private void setupMoltin() {
        moltin = new Moltin(this);
    }

    private void setComponentsStatus() {
        if (wasLogin) {
            btnLogin.setText(R.string.logout);
        } else {
            btnLogin.setText(R.string.login);
            loginStatus.setText(R.string.not_login);
        }
    }

    public void showLoading() {
        this.rl_progress.setVisibility(View.VISIBLE);
        setProgressBarIndeterminateVisibility(true);
        btnLogin.setEnabled(false);
    }

    public void hideLoading() {
        this.rl_progress.setVisibility(View.GONE);
        setProgressBarIndeterminateVisibility(false);
        btnLogin.setEnabled(true);
    }

    private void tryToLoginToMoltin() {
        if (!wasLogin) {
            try {
                showLoading();
                moltin.authenticate(getString(R.string.moltin_api_key), new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {
                        if (msg != null && msg.obj != null) {
                            loginStatus.setText(msg.obj.toString());
                        }
                        if (msg.what == Constants.RESULT_OK) {
                            wasLogin = true;
                        } else {
                            wasLogin = false;
                        }

                        setComponentsStatus();
                        hideLoading();
                        return wasLogin;
                    }
                });
            } catch (Exception e) {
                wasLogin = false;
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                hideLoading();
            }
        } else {
            moltin.resetAuthenticationData();
            wasLogin = false;

        }

        setComponentsStatus();

    }
}
