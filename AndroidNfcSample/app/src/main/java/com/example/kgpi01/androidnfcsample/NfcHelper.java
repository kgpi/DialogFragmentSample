package com.example.kgpi01.androidnfcsample;

import android.content.Context;
import android.content.Intent;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by KGPI01 on 2015/11/21.
 */
public class NfcHelper {

    public NfcHelper() {
    }

    /**
     * 一番最初のペイロードに格納されている文字列を取得
     * 先頭の3バイト[/ja]は除く
     *
     * 前提条件
     * ・android.nfc.action.NDEF_DISCOVERED
     * ・LanguageCode：ja
     *
     * @param intent インテント
     * @return 文字列、取得できない場合は空文字列
     */
    public String readFirstRecord(Intent intent) {
        final String empty = "";
        if(!intent.getAction().equals("android.nfc.action.NDEF_DISCOVERED"))
            return empty;

        return intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
    }

    public String readFirstRecord(Tag tag) {
        final String empty = "";

        Ndef nfc = null;
        try {
            try {
                nfc = Ndef.get(tag);
                nfc.connect();
            } catch (IOException e) {
                writeLog("Failed Ndef.connect()");
                e.printStackTrace();
                return empty;
            } catch(NullPointerException e) {
                writeLog("Tag or NDef is null");
                return empty;
            }
            //
            // Ndefデータが存在しない場合はNdef.getNdefMessage()でNULLが返る。
            NdefMessage ndefMsg;
            try {
                ndefMsg = nfc.getNdefMessage();
            } catch (IOException e) {
                e.printStackTrace();
                return empty;
            } catch (FormatException e) {
                e.printStackTrace();
                return empty;
            }

            if(ndefMsg == null) {
                writeLog("NdefMessage is null");
                return empty;
            }

            // 最初のレコードのペイロードのみを取得
            // エンコーディングはUTF-8(ヘッダは除去)
            NdefRecord[] records = ndefMsg.getRecords();
            if(records.length == 0)
                return empty;
            try {
                return substringText(records[0].getPayload());
            } catch(FormatException e) {
                e.printStackTrace();
                return empty;
            }

        } finally {
            try {
                nfc.close();
            } catch(Exception e) {
                // ignore
            }
        }
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
                    writeLog(String.format("%s", b));
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
     * ペイロードからヘッダ情報を除いた文字列を取得
     * @param bytes ペイロード
     * @return ヘッダ情報を除いた文字列
     * @throws FormatException ヘッダ情報不正、デコード失敗
     */
    private String substringText(byte[] bytes) throws FormatException {

        try {
            Pattern p = Pattern.compile("^[\u0002][\u006a][\u0061](.*)$");   // STX j a
            Matcher m = p.matcher(new String(bytes, "UTF-8"));
            Log.d("NFC", new String(bytes, "UTF-8"));
            if(!m.find()) {
                throw new FormatException("無効なフォーマット");
            }
            return m.group(1);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch(Exception e) {
            throw new RuntimeException(e);
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
