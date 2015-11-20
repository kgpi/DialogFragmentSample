package com.example.kgpi01.dialogfragmentsample;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * 公式リファレンスサンプル
 *
 * Created by KGPI01 on 2015/11/18.
 */
public class AlertDialogFragment extends DialogFragment {

    private static final String TitleKeyword = "title";
    private static final String MessageKeyword = "message";

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
    public static AlertDialogFragment newInstance(String title, String message, DialogInterface.OnClickListener positiveListener,
                                                                                DialogInterface.OnClickListener negativeListener) {
        AlertDialogFragment f = new AlertDialogFragment();
        f.setOnClickListener(positiveListener, negativeListener);
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
            title = getArguments().getString(TitleKeyword);
            message = getArguments().getString(MessageKeyword);
        }

        return new AlertDialog.Builder(getActivity())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(getString(R.string.dialog_ok), positiveListener)
            .setNegativeButton(getString(R.string.dialog_cancel), negativeListener)
            .setNegativeButton(null, null)
            .create();
    }

    public void setOnClickListener(DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener negativeListener) {
        this.positiveListener = positiveListener;
        this.negativeListener = negativeListener;
    }

}
