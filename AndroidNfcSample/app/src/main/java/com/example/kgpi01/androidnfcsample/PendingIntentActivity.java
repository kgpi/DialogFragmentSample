package com.example.kgpi01.androidnfcsample;

import android.content.Intent;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class PendingIntentActivity extends AppCompatActivity {

    private String DebugTag = "NFC";
    private NfcByPendingIntentHelper nfcHealper;

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

        nfcHealper = new NfcByPendingIntentHelper(this, DebugTag);
    }

    @Override
    public void onResume() {
        super.onResume();
        nfcHealper.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        nfcHealper.stop();
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

        readNdef(Ndef.get(tag));
        writeNdef("free", Ndef.get(tag));
        readNdef(Ndef.get(tag));

    }

    private void writeNdef(String text, Ndef ndef) {
        try {
            NdefMessage ndefMessage = createTextMessage(text, "ja");
            writeLog("NdefMessageを書き込みます:" + text);
            try {
                ndef.connect();
                ndef.writeNdefMessage(ndefMessage);
            } catch (IOException e) {
                writeLog("IOException");
                e.printStackTrace();
            } catch (FormatException e) {
                writeLog("FormatException");
                e.printStackTrace();
            }
        } finally {
            try {
                ndef.close();
            } catch(Exception ex) {
                // ignore
            }
        }
    }

    private void readNdef(Ndef nfc) {
        if(nfc == null) {
            writeLog("MifareUltralight is null.処理終了します");
            return;
        }
        writeLog("MifareUltralight.connect開始");
        try {

            try {
                nfc.connect();
            } catch (IOException e) {
                writeLog("MifareUltralight.connectで例外発生。処理終了します");
                e.printStackTrace();
                return;
            }
            writeLog("MifareUltralight.connect完了");
            writeLog("canMakeReadOnly:" + String.valueOf(nfc.canMakeReadOnly()));
            writeLog("getMaxSize:" + String.valueOf(nfc.getMaxSize()));
            writeLog("isWritable:" + String.valueOf(nfc.isWritable()));

            //
            // Ndefデータが存在しない場合はNdef.getNdefMessage()でNULLが返る。
            //
            //
            //
            writeLog("NdefMessageを取得します");
            NdefMessage ndefMsg;
            try {
                ndefMsg = nfc.getNdefMessage();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            } catch (FormatException e) {
                e.printStackTrace();
                return;
            }

            if(ndefMsg == null) {
                writeLog("NdefMessage is null");
                return;
            }


            writeLog("getByteArrayLength:" + String.valueOf(ndefMsg.getByteArrayLength()));
            writeLog("ndefMsg.getRecords().length:" + String.valueOf(ndefMsg.getRecords().length));
            writeLog("describeContents:" + String.valueOf(ndefMsg.describeContents()));
            writeLog("NdefRecords内容表示");

            int recordCount = 0;
            for(NdefRecord record : ndefMsg.getRecords()) {
                writeLog(String.format("---Record:%s", ++recordCount));
                writeLog("toMimeType:" + record.toMimeType());
                writeLog(String.format("getTnf:%s", record.getTnf()));
                for(byte b : record.getPayload()) {
                    writeLog(String.format("%c", b));
                }
            }
        } finally {
            try {
                nfc.close();
            } catch(Exception e) {
                // ignore
            }
        }
    }

    /**
     * RTD Text Recordを含んだNdefMessageを作成します
     *
     * @param text テキスト
     * @param languageCode 言語コード(ISO/IANA)
     * @return NdefMessage
     */
    private NdefMessage createTextMessage(String text, String languageCode) {
        try {
            byte statusByte = (byte)languageCode.length();
            byte[] rawLanguageCode = languageCode.getBytes("US-ASCII");
            byte[] rawText = text.getBytes("UTF-8");

            ByteArrayOutputStream o = new ByteArrayOutputStream(1 + rawLanguageCode.length + rawText.length);
            o.write(statusByte);
            o.write(rawLanguageCode, 0, rawLanguageCode.length);
            o.write(rawText, 0, rawText.length);
            byte[] payload = o.toByteArray();
            NdefMessage message = new NdefMessage(
                    new NdefRecord[] {
                            new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0],
                                    payload)
                    });
            return message;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeLog(String s) {
        Log.d(DebugTag, s);
    }

}
