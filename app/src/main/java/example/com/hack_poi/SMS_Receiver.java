package example.com.hack_poi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import example.com.hack_poi.Declaration.DataDeclaration;

public class SMS_Receiver extends BroadcastReceiver {
    String TAG = "Received Message";
    private static EditText editText;

    public void setEditText(EditText editText)
    {
        SMS_Receiver.editText = editText;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        //---get the SMS message passed in---
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs = null;
        String messageReceived = "";
        if (bundle != null)
        {
            //---retrieve the SMS message received---
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];
            for (int i=0; i<msgs.length; i++)

            {
                msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                messageReceived += msgs[i].getMessageBody().toString();
                messageReceived += "\n";
            }
            Log.e(TAG, "Full Message Body : " + messageReceived);
            try{
                //String mMessage= "Dear Customer, You have made a Debit Card purchase of INR1,600.00 on 30 Jan. Info.VPS*AGGARWAL SH.";
                Pattern regEx = Pattern.compile("(?i)(?:\\sInfo.\\s*)([A-Za-z0-9*]*\\s?-?\\s?[A-Za-z0-9*]*\\s?-?\\.?)");
                // Find instance of pattern matches
                Matcher m = regEx.matcher(messageReceived);
                if(m.find()){
                    String mMerchantName = m.group();
                    mMerchantName = mMerchantName.replaceAll("^\\s+|\\s+$", "");//trim from start and end
                    mMerchantName = mMerchantName.replace("Info.","");
                    Log.e(TAG, "MERCHANT NAME : " + mMerchantName);
                    editText.setText(mMerchantName);
                    DataDeclaration.MERCHANT_NAME = mMerchantName;
                    Log.e(TAG, "DataDeclaration.MERCHANT_NAME : " + DataDeclaration.MERCHANT_NAME);
                }else{
                    Log.e(TAG, "MATCH NOTFOUND");
                }
            }catch(Exception e) {
                Log.e(TAG, e.toString());
            }

            // Get the Sender Phone Number

            String senderPhoneNumber=msgs[0].getOriginatingAddress ();

            Log.e(TAG, "getOriginatingAddress : " + senderPhoneNumber);

        }



    }
}
