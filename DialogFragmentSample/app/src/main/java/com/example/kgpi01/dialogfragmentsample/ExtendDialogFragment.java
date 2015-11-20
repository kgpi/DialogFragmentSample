package com.example.kgpi01.dialogfragmentsample;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by KGPI01 on 2015/11/19.
 */
public class ExtendDialogFragment extends DialogFragment {

    public static final String TitleKeyword = "title";
    public static final String MessageKeyword = "message";

    String title = "";       // タイトル
    String message = "";     // ダイアログ本文
    DialogInterface.OnClickListener positiveListener;
    DialogInterface.OnClickListener negativeListener;

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     *
     * Bundle : オブジェクトの入れ物、入れたオブジェクトはonCreateで取り出す
     */
    public static ExtendDialogFragment newInstance(String title, String message, DialogInterface.OnClickListener positiveListener) {
        ExtendDialogFragment f = new ExtendDialogFragment();
        f.setPositiveListener(positiveListener);
        f.setCancelable(false);

        Bundle args = new Bundle();
        args.putString(TitleKeyword, title);
        args.putString(MessageKeyword, message);
        f.setArguments(args);

        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null) {
            setTitle(getArguments().getString(TitleKeyword));
            setMessage(getArguments().getString(MessageKeyword));
        }

        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(getString(R.string.dialog_ok), positiveListener)
                .create();
    }

    protected void setPositiveListener(DialogInterface.OnClickListener listener) {
        this.positiveListener = listener;
    }

    protected void setNegativeListener(DialogInterface.OnClickListener listener) {
        this.negativeListener = listener;
    }

    protected void setTitle(String title) {
        this.title = (title == null) ? "" : title;
    }

    protected void setMessage(String message) {
        this.message = (message == null) ? "" : message;
    }
}
