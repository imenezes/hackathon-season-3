package example.com.hack_poi.DataBase;

import android.content.ContentValues;
import android.content.Context;

import example.com.hack_poi.Model.FinalDetails;


public class DB_FULL_DETAILS {
    // Table name
    public static final String ALL_DETAILS = "ALL_DETAILS";

    // coloums
    public static final String _ID = "_ID"; // primary key, unique generated

    public static final String COLUMN_Merchant_Name = "Merchant_Name";
    public static final String COLUMN_LatLong = "LatLong";
    public static final String COLUMN_Merachant_Number = "Merchant_Number";
    public static final String COLUMN_Time = "Time";
    public static final String COLUMN_type = "Type";
    public static final String COLUMN_Rating = "Rating";

    //Create Sender table

    public static String createFinalTable() {

        String FinalTable = "CREATE TABLE " + ALL_DETAILS + " ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_Merchant_Name + " VARCHAR(50), "
                + COLUMN_LatLong + " VARCHAR(50), "
                + COLUMN_Merachant_Number + " VARCHAR(50), "
                + COLUMN_Time + " VARCHAR(50), "
                + COLUMN_type + " VARCHAR(50), "
                + COLUMN_Rating + " VARCHAR(50)) ";

        return FinalTable;
    }

    public static int getDetailsRowCount(Context context) {
        DB db = new DB(context);
        return db.getRowCount(ALL_DETAILS);
    }

    public static boolean getDuplicateMerchantName(Context context, String merchantName){
        DB db = new DB(context);
        return db.getDuplicateMerchant(ALL_DETAILS,COLUMN_Merchant_Name,merchantName);
    }

    public long insertData(Context context, FinalDetails finalDetails) {
        ContentValues contentValues = addInitialRow(finalDetails);
        DB db = new DB(context);
        long id = db.insertTransactionRecord(context, contentValues, ALL_DETAILS);

        return id;
    }

    public ContentValues addInitialRow(FinalDetails finalDetails) {
        ContentValues contentValues = new ContentValues();

        if (finalDetails != null) {

            contentValues.put(COLUMN_Merchant_Name, finalDetails.getMerchant_name());
            contentValues.put(COLUMN_LatLong, finalDetails.getLat_long());
            contentValues.put(COLUMN_Merachant_Number, finalDetails.getMobileNumber());
            contentValues.put(COLUMN_Time, finalDetails.getTime());
            contentValues.put(COLUMN_type, finalDetails.getType());
            contentValues.put(COLUMN_Rating, finalDetails.getRating());

        }

        return contentValues;
    }


}
