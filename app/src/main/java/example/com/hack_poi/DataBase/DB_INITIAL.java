package example.com.hack_poi.DataBase;

import android.content.ContentValues;
import android.content.Context;

import example.com.hack_poi.Model.InitialDetails;


public class DB_INITIAL {

    // Table name
    public static final String INITIAL_DETAILS = "INITIAL_DETAILS";

    // coloums
    public static final String COLUMN_ID = "_ID"; // primary key, unique generated

    public static final String COLUMN_MERCHANT_NAME = "Merchant_name";
    public static final String COLUMN_LATITUDE = "Latitude";
    public static final String COLUMN_LONGITUDE = "Longitude";

    //Create transaction table
    public static String createInitialTable() {

        String InitialTable = "CREATE TABLE " + INITIAL_DETAILS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_MERCHANT_NAME + " VARCHAR(50), "
                + COLUMN_LATITUDE + " VARCHAR(50), "
                + COLUMN_LONGITUDE + " VARCHAR(50)) ";

        return InitialTable;
    }

    public static int getINITIALRowCount(Context context) {
        DB db = new DB(context);
        return db.getRowCount(INITIAL_DETAILS);
    }

    public static boolean getDuplicateMerchantName(Context context, String merchantName){
        DB db = new DB(context);
        return db.getDuplicateMerchant(INITIAL_DETAILS,COLUMN_MERCHANT_NAME,merchantName);
    }

    public long insertData(Context context, InitialDetails initialDetails) {
        ContentValues contentValues = addInitialRow(initialDetails);
        DB db = new DB(context);
        long id = db.insertTransactionRecord(context, contentValues, INITIAL_DETAILS);

        return id;
    }

    public ContentValues addInitialRow(InitialDetails initialDetails) {
        ContentValues contentValues = new ContentValues();

        if (initialDetails != null) {

            contentValues.put(COLUMN_MERCHANT_NAME, initialDetails.getMerchant_name());
            contentValues.put(COLUMN_LATITUDE, initialDetails.getLatitude());
            contentValues.put(COLUMN_LONGITUDE, initialDetails.getLongitude());
        }

        return contentValues;
    }


}
