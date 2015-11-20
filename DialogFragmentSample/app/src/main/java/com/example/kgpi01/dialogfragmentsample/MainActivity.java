package com.example.kgpi01.dialogfragmentsample;

import android.content.DialogInterface;
import android.preference.DialogPreference;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickAlertDialogFragmentButton(View v) {
        AlertDialogFragment fragment = AlertDialogFragment.newInstance("Title", "Message",
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getApplicationContext(), "OK", Toast.LENGTH_SHORT).show();
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getApplicationContext(), "Cancel", Toast.LENGTH_SHORT).show();
                }
            }
        );
        fragment.show(getFragmentManager(), "alert_dialog");
    }

    public void onClickOkOnlyDialogFragmentButton(View v) {
        OkOnlyDialogFragment fragment = OkOnlyDialogFragment.newInstance("Title", "Message", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "OK", Toast.LENGTH_SHORT).show();
            }
        });
        fragment.show(getFragmentManager(), "alert_dialog");
    }
}
