package com.example.kgpi01.androidnfcsample;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;

/**
 * Created by KGPI01 on 2015/11/20.
 */
public class NfcByPendingIntentHelper {

    private String DebugTag;
    private Context context;
    private NfcAdapter nfcAdapter;

    public NfcByPendingIntentHelper(Context cxt, String debugTag) {
        context = cxt;
        DebugTag = debugTag;
        nfcAdapter = NfcAdapter.getDefaultAdapter(context);
    }

    public void start() {
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, new Intent(context, context.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        nfcAdapter.enableForegroundDispatch((Activity)context, pendingIntent, null, null);
    }

    public void stop() {
        nfcAdapter.disableForegroundDispatch((Activity)context);
    }
}
