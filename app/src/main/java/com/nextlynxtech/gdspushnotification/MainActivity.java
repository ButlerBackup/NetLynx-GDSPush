package com.nextlynxtech.gdspushnotification;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;
import com.nextlynxtech.gdspushnotification.adapter.MainAdapter;
import com.nextlynxtech.gdspushnotification.classes.Message;
import com.nextlynxtech.gdspushnotification.classes.NewMessageCalls;
import com.nextlynxtech.gdspushnotification.classes.NewMessageResult;
import com.nextlynxtech.gdspushnotification.classes.SQLFunctions;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends ActionBarActivity {
    @InjectView(R.id.lvMain)
    ListView lvMain;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    getNewMessages mGetNewMessages;
    loadEventMessages mLoadEventMessages;

    ArrayList<Message> data = new ArrayList<>();

    boolean stopLoading = false;

    @Override
    protected void onPause() {
        super.onPause();
        if (mGetNewMessages != null && mGetNewMessages.getStatus() != AsyncTask.Status.FINISHED) {
            mGetNewMessages.cancel(true);
        }
        if (mLoadEventMessages != null && mLoadEventMessages.getStatus() != AsyncTask.Status.FINISHED) {
            mLoadEventMessages.cancel(true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        mLoadEventMessages = null;
        mLoadEventMessages = new loadEventMessages();
        mLoadEventMessages.execute();
    }

    private class loadEventMessages extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            SQLFunctions sql = new SQLFunctions(MainActivity.this);
            sql.open();
            data = sql.loadEventMessages();
            sql.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!isCancelled()) {
                        if (data.size() > 0) {
                            MainAdapter adapter = new MainAdapter(MainActivity.this, data);
                            lvMain.setAdapter(adapter);
                            lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Message m = data.get(position);
                                    startActivity(new Intent(MainActivity.this, ConversationActivity.class).putExtra("message", m));
                                }
                            });
                        }
                        if (!stopLoading) {
                            mGetNewMessages = null;
                            mGetNewMessages = new getNewMessages();
                            mGetNewMessages.execute();
                        }
                    }
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.menu_main_refresh).setIcon(
                new IconDrawable(this, Iconify.IconValue.md_refresh)
                        .colorRes(R.color.white)
                        .actionBarSize());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_main_refresh) {
            mGetNewMessages = null;
            mGetNewMessages = new getNewMessages();
            mGetNewMessages.execute();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class getNewMessages extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                NewMessageResult m = MainApplication.service.GetNewMessages(new NewMessageCalls("12", "1234"));
                Log.e("SIZE", m.getMessages().size() + " messages");
                if (m.getMessages().size() > 0) {
                    SQLFunctions sql = new SQLFunctions(MainActivity.this);
                    sql.open();
                    for (Message message : m.getMessages()) {
                        sql.insertMessage(message, 0);
                    }
                    sql.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (!isCancelled()) {
                stopLoading = true;
                mLoadEventMessages = null;
                mLoadEventMessages = new loadEventMessages();
                mLoadEventMessages.execute();
            }
        }
    }
}
