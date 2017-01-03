package com.zhengxiaoyao0716.manage.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputFilter;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.Toast;
import com.zhengxiaoyao0716.manage.R;

/**
 * 输入对话框.
 * Created by zhengxiaoyao0716 on 2015/12/29.
 */
public abstract class InputDialog {
    private AlertDialog dialog;
    public InputDialog(final Context context, int titleStrId, int posStrId)
    {
        final EditText nameEditText = new EditText(context);
        nameEditText.setPadding(0, 60, 0, 30);
        nameEditText.setGravity(Gravity.CENTER);
        nameEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        nameEditText.setHint(R.string.editTextTip);
        nameEditText.selectAll();
        dialog = new AlertDialog.Builder(context)
                .setTitle(titleStrId)
                .setView(nameEditText)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(posStrId, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface d, int i) {
                        String inputStr = nameEditText.getText().toString();
                        if (!inputStr.matches("[0-9a-zA-Z]{1,}"))
                        {
                            Toast.makeText(context, R.string.editTextTip, Toast.LENGTH_LONG).show();
                            return;
                        }
                        doAfterCommit(inputStr);
                    }
                }).create();
    }
    protected abstract void doAfterCommit(String inputStr);

    public void show() { dialog.show(); }
}
