package com.example.kgpi01.androidnfcsample;

import android.content.Context;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.tech.Ndef;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by KGPI01 on 2015/11/21.
 */
public class NfcHelper {

    private Context context;

    public NfcHelper(Context cxt) {
        context = cxt;
    }

    public void readNdef(Ndef nfc) {
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
    public void writeNdef(String text, Ndef ndef) {
        try {
            NdefMessage ndefMessage = createTextMessage(text, "ja");
            writeLog("NdefMessageを書き込みます:" + text);
            try {
                ndef.connect();
                ndef.writeNdefMessage(ndefMessage);
            } catch (IOException e) {
                writeLog("IOException");
                e.printStackTrace();
                throw new RuntimeException(e);
            } catch (FormatException e) {
                writeLog("FormatException");
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        } finally {
            try {
                ndef.close();
            } catch(Exception ex) {
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
        Log.d("NFC", s);
    }
}
