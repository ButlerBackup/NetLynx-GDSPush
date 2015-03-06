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
import com.nextlynxtech.gdspushnotification.classes.Utils;

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
            mLoadConversation = null;
            mLoadConversation = new loadConversation();
            mLoadConversation.execute();
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
                                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                                    Message m = data.get(position);
                                    final String messageId = String.valueOf(m.getMessageId());
                                    CharSequence[] replies = null;
                                    if (m.getMine() == 0 && m.getRecallFlag() == 1) {
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
                                                                showCustomReply(messageId);
                                                            } else {
                                                                new sendReplyToMessage().execute(r.get(which), messageId);
                                                            }
                                                        }
                                                    })
                                                    .show();
                                        } else {
                                            showCustomReply(messageId);
                                        }
                                    } else if (m.getMine() == 1 && m.getReplySuccess() == 0) {
                                        new MaterialDialog.Builder(ConversationActivity.this).content("Resend message?").title("Resend").positiveText("Yes").negativeText("No").cancelable(false).callback(new MaterialDialog.ButtonCallback() {
                                            @Override
                                            public void onPositive(MaterialDialog dialog) {
                                                super.onPositive(dialog);
                                                Message m = data.get(position);
                                                new resendReply().execute(m);
                                            }
                                        }).build().show();
                                    }
                                }
                            });
                        }
                    }
                }
            });
        }
    }

    private void showCustomReply(final String messageId) {
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
                        new sendReplyToMessage().execute(reply, messageId);
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

    private class resendReply extends AsyncTask<Message, Void, Void> {
        GenericResult r = null;
        Message m;

        @Override
        protected Void doInBackground(Message... params) {
            m = params[0];
            try {
                r = MainApplication.service.UpdateMessageReply(new MessageReply("", "1234", Integer.parseInt(m.getReplyToMessageId()), m.getMessage()));
                Log.e("Result", r.getStatusDescription() + "|" + r.getStatusCode());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    SQLFunctions sql = new SQLFunctions(ConversationActivity.this);
                    sql.open();
                    if (r != null && r.getStatusDescription().equals("OK") && r.getStatusCode().equals("1")) {
                        sql.updateReply(m, "1");
                    }
                    sql.close();
                    new loadConversation().execute();
                }
            });
        }
    }

    private class sendReplyToMessage extends AsyncTask<String, Void, Void> {
        GenericResult m = null;
        String messageId = "", messageContent = "";
        int id = 0;

        @Override
        protected Void doInBackground(String... params) {
            messageContent = params[0].trim();
            messageId = params[1].trim();
            SQLFunctions sql = new SQLFunctions(ConversationActivity.this);
            sql.open();
            id = sql.insertReply(messageContent, messageId, "2"); //sending
            sql.close();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new loadConversation().execute();
                }
            });
            try {
                m = MainApplication.service.UpdateMessageReply(new MessageReply("", new Utils(ConversationActivity.this).getUnique(), Integer.parseInt(messageId), messageContent));
                Log.e("Result", m.getStatusDescription() + "|" + m.getStatusCode());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    SQLFunctions sql = new SQLFunctions(ConversationActivity.this);
                    sql.open();
                    if (m != null && m.getStatusDescription().equals("OK") && m.getStatusCode().equals("1")) {
                        sql.updateReply(id, "1");
                    } else {
                        sql.updateReply(id, "0");
                    }
                    sql.close();
                    new loadConversation().execute();
                }
            });
        }
    }
}
