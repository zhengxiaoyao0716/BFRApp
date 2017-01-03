package com.zhengxiaoyao0716.manage.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.baidubce.services.bos.BosClient;
import com.baidubce.services.bos.model.BosObjectSummary;
import com.baidubce.services.bos.model.ListObjectsRequest;
import com.baidubce.services.bos.model.ListObjectsResponse;
import com.zhengxiaoyao0716.manage.R;
import com.zhengxiaoyao0716.manage.dialog.InputDialog;
import com.zhengxiaoyao0716.manage.net.*;

import java.util.List;

/**
 * 入口界面.
 * Created by zhengxiaoyao0716 on 2015/12/25.
 */
public class MainActivity extends Activity {
    private List<String> members;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);


        new CheckNetwork(this){
            @Override
            protected void doAfterConnected() {
                loadMembers();
            }
        }.sureConnect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, R.string.addMember)
                .setIcon(android.R.drawable.ic_menu_add)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        new InputDialog(MainActivity.this, R.string.addMember, R.string.add) {
            @Override
            protected void doAfterCommit(String inputStr) {
                final String memberPath = "faces/" + inputStr + "/";

                new BaseBosThread(new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);

                        if (msg.what == 1)
                        {
                            loadMembers();
                            Intent intent = new Intent(MainActivity.this, FaceActivity.class);
                            intent.putExtra("member", memberPath);
                            startActivity(intent);
                        }
                        else Toast.makeText(MainActivity.this, R.string.failed, Toast.LENGTH_LONG).show();
                    }
                }) {
                    @Override
                    protected void doInRun(BosClient client, Message message) {
                        BosHelper.INSTANCE.getClient().putObject(BosHelper.BUCKET_NAME, memberPath, "");
                    }
                }.start();
            }
        }.show();
        return super.onOptionsItemSelected(item);
    }

    private void loadMembers() {
        new Thread(new BaseBosThread(new BaseHandler(this) {
            @Override
            protected void doInHandleMessage(Message msg) {
                if (msg.obj == null) {
                    Toast.makeText(MainActivity.this, R.string.loadFailed, Toast.LENGTH_LONG).show();
                    return;
                }

                //noinspection unchecked
                members = (List<String>) msg.obj;
                ArrayAdapter<String> memberArrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, members);
                ListView memberListView = (ListView) findViewById(R.id.memberListView);
                memberListView.setAdapter(memberArrayAdapter);
                memberListView.setOnItemClickListener(onMemberClick);
                memberListView.setOnItemLongClickListener(onMemberLongClick);
            }
        }) {
            @Override
            protected void doInRun(BosClient client, Message message)
            {
                ListObjectsRequest listObjectsRequest = new ListObjectsRequest(BosHelper.BUCKET_NAME);
                listObjectsRequest.setDelimiter("/");
                listObjectsRequest.setPrefix("faces/");
                ListObjectsResponse listing = client.listObjects(listObjectsRequest);

                message.obj = listing.getCommonPrefixes();
            }
        }).start();
    }
    private AdapterView.OnItemClickListener onMemberClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            listMemberFaces(position);
        }

        private void listMemberFaces(int position) {
            Intent intent = new Intent(MainActivity.this, FaceActivity.class);
            intent.putExtra("member", members.get(position));
            startActivity(intent);
        }
    };
    private AdapterView.OnItemLongClickListener onMemberLongClick = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            deleteMember(position);
            return true;
        }

        private void deleteMember(final int position) {

            new AlertDialog.Builder(MainActivity.this)
                    .setTitle(members.get(position))
                    .setMessage(R.string.deleteMemberTip)
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new BaseBosThread(new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    super.handleMessage(msg);

                                    if (msg.what == 1) loadMembers();
                                    else Toast.makeText(MainActivity.this, R.string.failed, Toast.LENGTH_LONG).show();
                                }
                            }) {
                                @Override
                                protected void doInRun(BosClient client, Message message) {
                                    String member = members.get(position);
                                    ListObjectsRequest listObjectsRequest = new ListObjectsRequest(BosHelper.BUCKET_NAME);
                                    //listObjectsRequest.setDelimiter("/");
                                    listObjectsRequest.setPrefix(member);
                                    ListObjectsResponse listing = BosHelper.INSTANCE.getClient().listObjects(listObjectsRequest);

                                    for (BosObjectSummary objectSummary : listing.getContents()) {
                                        BosHelper.INSTANCE.getClient().deleteObject(BosHelper.BUCKET_NAME, objectSummary.getKey());
                                    }
                                    BfrHelper.manager.deletePerson(member.substring(1 + member.indexOf("/"), member.length() - 1));
                                    Log.e("xxxxxxxxxxxxx", member);
                                }
                            }.start();
                        }
                    }).create().show();
        }
    };
}