package com.example.kgpi01.dialogfragmentsample;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 *
 * Created by KGPI01 on 2015/11/19.
 */
public class OkOnlyDialogFragment extends ExtendDialogFragment {

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     *
     * Bundle : オブジェクトの入れ物、入れたオブジェクトはonCreateで取り出す
     */
    public static OkOnlyDialogFragment newInstance(String title, String message,
                                                   DialogInterface.OnClickListener positiveListener,
                                                   DialogInterface.OnClickListener negativeListener) {
        OkOnlyDialogFragment f = new OkOnlyDialogFragment();
        f.setPositiveListener(positiveListener);
        f.setNegativeListener(negativeListener);
        f.setTitle(title);
        f.setMessage(message);

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
                .setNegativeButton(getString(R.string.dialog_cancel), negativeListener)
                .create();
    }
}
