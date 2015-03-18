package com.nextlynxtech.gdspushnotification;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class AboutActivity extends ActionBarActivity {
    @InjectView(R.id.tvVersion)
    TextView tvVersion;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;

	//When activity is called
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        try {
            String versionName = getPackageManager()
                    .getPackageInfo(getPackageName(), 0).versionName;
            tvVersion.setText("Group Dissemination System\n" + versionName);
        } catch (Exception e) {
            tvVersion.setText("Group Dissemination System\nv1.00");
        }
    }

	//Back button on action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
