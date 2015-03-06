package com.nextlynxtech.gdspushnotification;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class RegistrationDoneActivity extends ActionBarActivity {
    @InjectView(R.id.tvRegistrationDone)
    TextView tvRegistrationDone;
    @InjectView(R.id.bRegistrationDone)
    Button bRegistrationDone;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_done);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        if (getIntent().hasExtra(Consts.REGISTER_USER_NAME) && getIntent().hasExtra(Consts.REGISTER_USER_GROUP)) {
            tvRegistrationDone.setText("Welcome " + getIntent().getStringExtra(Consts.REGISTER_USER_NAME) + ". You are registered in the " + getIntent().getStringExtra(Consts.REGISTER_USER_GROUP) + " group");
        } else {
            Toast.makeText(RegistrationDoneActivity.this, "Unable to receive data from registration", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @OnClick(R.id.bRegistrationDone)
    public void registrationDone() {
        startActivity(new Intent(RegistrationDoneActivity.this, MainActivity.class));
        finish();
    }
}
