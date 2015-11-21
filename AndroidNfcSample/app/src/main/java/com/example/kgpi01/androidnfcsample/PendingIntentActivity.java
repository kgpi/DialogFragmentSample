package com.example.kgpi01.androidnfcsample;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

public class PendingIntentActivity extends AppCompatActivity {

    private String DebugTag = "NFC";
    private NfcByPendingIntentHelper nfcHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_intent);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        nfcHelper = new NfcByPendingIntentHelper(this, DebugTag);
    }

    @Override
    public void onResume() {
        super.onResume();
        nfcHelper.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        nfcHelper.stop();
    }

    /**
     * NDEFフォーマット済のタグでは、Intent.getAction():android.nfc.action.NDEF_DISCOVEREDが返る
     * 未フォーマット(購入直後)のタグでは、Intent.getAction():android.nfc.action.TAG_DISCOVEREDが返る
     *
     * 公式リファレンスサンプルでは、MifareUltralight nfc = MifareUltralight.get(tag);と
     * 記載しているが、NULLが帰ってきてしまう。
     * NdefFormatable nfc = NdefFormatable.get(tag);もダメ
     *
     * 現状ではNdef nfc = Ndef.get(tag)しか動かない.
     *
     * @param intent
     */
    @Override
    public void onNewIntent(Intent intent) {
        writeLog("------------------------------------------------------------------------");
        writeLog("Called onNewIntent");
        writeLog("getAction():" + intent.getAction());

        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if(tag == null) {
            writeLog("Tag is null.");
            return;
        }

        String[] techList = tag.getTechList();
        writeLog("TechList Size:" + String.valueOf(techList.length));
        for(String s : techList) {
            writeLog("TechList:" + s);
        }

        if(MifareUltralight.get(tag) != null) {
            writeLog("種別:MifareUltralight");
        } else if(MifareClassic.get(tag) != null) {
            writeLog("種別:MifareClassic");
        } else if(NdefFormatable.get(tag) != null) {
            writeLog("種別:NdefFormatable");
        } else if(Ndef.get(tag) != null) {
            writeLog("種別:Ndef");
        } else if(NfcA.get(tag) != null) {
            writeLog("種別:NfcA");
        } else {
            writeLog("種別:不明");
            return;
        }

        NfcHelper nfcHelper = new NfcHelper();

        String text = nfcHelper.readFirstRecord(tag);
        writeLog("結果：" + text);

        //String text = nfcHelper.readFirstRecord(tag);
        //writeLog(text);

        //nfcHelper.readNdef(Ndef.get(tag));
        //nfcHelper.writeNdef("free", Ndef.get(tag));
        //nfcHelper.readNdef(Ndef.get(tag));

    }

    private void writeLog(String s) {
        Log.d(DebugTag, s);
    }

}
