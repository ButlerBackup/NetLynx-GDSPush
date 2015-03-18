package com.nextlynxtech.gdspushnotification;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nextlynxtech.gdspushnotification.classes.Utils;
import com.nextlynxtech.gdspushnotification.classes.VerifyPin;
import com.nextlynxtech.gdspushnotification.classes.WebAPIOutput;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class VerifyPinActivity extends ActionBarActivity {
    @InjectView(R.id.etPin)
    EditText etPin;
    @InjectView(R.id.bVerifyPin)
    Button bRegister;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;

	//When activity is called
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_pin);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        etPin.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) { // if keyboard detects button "Next" pressed, auto click register button
                    bRegister.performClick();
                    return true;
                }
                return false;
            }
        });
    }

	//Calling of class when clicked
    @OnClick(R.id.bVerifyPin)
    public void verify() {
        new verifyPin().execute();
    }

	//Verfity pin class
    private class verifyPin extends AsyncTask<Void, Void, Void> {
        WebAPIOutput res;
        MaterialDialog pd;

		//Contacting server before execute
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new MaterialDialog.Builder(VerifyPinActivity.this).cancelable(false).title("Contacting server..").progress(true, 0).content(R.string.please_wait).build();
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            VerifyPin pin = new VerifyPin(new Utils(VerifyPinActivity.this).getUnique(), etPin.getText().toString(), new Utils(VerifyPinActivity.this).getPhoneNumber());
            res = MainApplication.apiService.verifyPin(pin);
            return null;
        }

		//Verify pin 
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            VerifyPinActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (pd != null && pd.isShowing()) {
                        pd.dismiss();
                    }
                    if (res.getStatusCode() == 1) { //Start next activity if verify successful
                        new Utils(VerifyPinActivity.this).storeSecurePreferenceValue(Consts.REGISTER_USER_NAME, res.getVerifyPinUsername());
                        new Utils(VerifyPinActivity.this).storeSecurePreferenceValue(Consts.REGISTER_USER_GROUP, res.getVerifyPinUserGroup());
                        startActivity(new Intent(VerifyPinActivity.this, RegistrationDoneActivity.class).putExtra(Consts.REGISTER_USER_NAME, res.getVerifyPinUsername()).putExtra(Consts.REGISTER_USER_GROUP, res.getVerifyPinUserGroup()));
                        finish();
                    } else { //Display status result
                        Toast.makeText(VerifyPinActivity.this, res.getStatusDescription(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}
