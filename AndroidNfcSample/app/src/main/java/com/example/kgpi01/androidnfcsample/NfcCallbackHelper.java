package com.example.kgpi01.androidnfcsample;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;

/**
 * Created by KGPI01 on 2015/11/21.
 */
public class NfcCallbackHelper {


    private String DebugTag;
    private Context context;
    private NfcAdapter nfcAdapter;

    public NfcCallbackHelper(Context cxt, String debugTag) {
        context = cxt;
        DebugTag = debugTag;
        nfcAdapter = NfcAdapter.getDefaultAdapter(context);
    }

    public void start(NfcAdapter.ReaderCallback callback) {
        nfcAdapter.enableReaderMode((Activity)context, callback, NfcAdapter.FLAG_READER_NFC_A, null);
    }

    public void stop() {
        nfcAdapter.disableReaderMode((Activity)context);
    }
}
