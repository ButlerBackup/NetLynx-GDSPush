package com.nextlynxtech.gdspushnotification;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.nextlynxtech.gdspushnotification.adapter.ConversationAdapter;
import com.nextlynxtech.gdspushnotification.classes.GenericResult;
import com.nextlynxtech.gdspushnotification.classes.Message;
import com.nextlynxtech.gdspushnotification.classes.MessageReply;
import com.nextlynxtech.gdspushnotification.classes.SQLFunctions;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ConversationActivity extends ActionBarActivity {
    @InjectView(R.id.lvConversation)
    ListView lvConversation;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    int eventId;
    ArrayList<Message> data = new ArrayList<>();
    ConversationAdapter adapter;
    loadConversation mLoadConversation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_conversation);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        if (getIntent().hasExtra("message")) {
            Message m = (Message) getIntent().getSerializableExtra("message");
            eventId = m.getEventId();
            getSupportActionBar().setTitle(m.getEventName());
            new loadConversation().execute();
        } else {
            Toast.makeText(ConversationActivity.this, "Unable to get message content", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private class loadConversation extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            SQLFunctions sql = new SQLFunctions(ConversationActivity.this);
            sql.open();
            data = sql.loadMessages(String.valueOf(eventId));
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
                            adapter = new ConversationAdapter(ConversationActivity.this, data);
                            lvConversation.setAdapter(adapter);
                            lvConversation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Message m = data.get(position);
                                    CharSequence[] replies = null;
                                    if (m.getRecallFlag() == 1) {
                                        final ArrayList<String> r = new ArrayList<>();
                                        if (m.getReplies() != null && m.getReplies().size() > 0) {
                                            for (String s : m.getReplies()) {
                                                r.add(s);
                                            }
                                            r.add("Custom Reply");
                                            replies = r.toArray(new CharSequence[r.size()]);

                                            new MaterialDialog.Builder(ConversationActivity.this)
                                                    .title("Send Reply")
                                                    .items(replies)
                                                    .itemsCallback(new MaterialDialog.ListCallback() {
                                                        @Override
                                                        public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                                            if (r.get(which).equals("Custom Reply")) {
                                                                showCustomReply();
                                                            } else {
                                                                new sendReplyToMessage().execute(r.get(which));
                                                            }
                                                        }
                                                    })
                                                    .show();
                                        } else {
                                            showCustomReply();
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            });
        }
    }

    private void showCustomReply() {
        EditText etReply;
        final View positiveAction;
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title("Custom Reply")
                .customView(R.layout.activity_conversation_custom_reply, true)
                .positiveText("Send")
                .negativeText(android.R.string.cancel)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        String reply = ((EditText) dialog.getCustomView().findViewById(R.id.etReply)).getText().toString();
                        new sendReplyToMessage().execute(reply);
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                    }
                }).build();
        etReply = (EditText) dialog.getCustomView().findViewById(R.id.etReply);
        positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
        etReply.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                positiveAction.setEnabled(s.toString().trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        dialog.show();
        positiveAction.setEnabled(false); // disabled by default
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mLoadConversation != null && mLoadConversation.getStatus() != AsyncTask.Status.FINISHED) {
            mLoadConversation.cancel(true);
        }
    }

    private class sendReplyToMessage extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            String message = params[0].trim();
            try {
                GenericResult m = MainApplication.service.UpdateMessageReply(new MessageReply("", "1234", 1, message));
                Log.e("Result", m.getStatusDescription());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
