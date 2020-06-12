package example.com.hack_poi.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import java.util.ArrayList;
import java.util.List;

import example.com.hack_poi.Declaration.DataDeclaration;
import example.com.hack_poi.Model.FinalDetails;

public class DB extends SQLiteOpenHelper {

    private static String TAG = "DataBase";

    public DB(Context context) {
        super(context, DataDeclaration.DATABASE_BANE, null, DataDeclaration.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(DB_INITIAL.createInitialTable());
            db.execSQL(DB_FULL_DETAILS.createFinalTable());
            Log.e("DB", "Table created");
        } catch (Exception e) {
            Log.e("DB", "Exception, " + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

       /* if (newVersion > oldVersion) {
            db.execSQL("ALTER TABLE " + DB_INITIAL.AEPS_DETAILS + " ADD COLUMN " + DB_INITIAL.COLUMN_CustomerID + " VARCHAR(50)");
            db.execSQL("ALTER TABLE " + DB_INITIAL.AEPS_DETAILS + " ADD COLUMN " + DB_INITIAL.COLUMN_CustomerMobile + " VARCHAR(10)");
        }*/
    }

    public long insertTransactionRecord(Context context, ContentValues contentValues, String tableName) {
        Log.e(TAG, "Initiating Inserting data in " + tableName + " table ");
        DB db = new DB(context);
        SQLiteDatabase database = db.getWritableDatabase();
        long id = database.insert(tableName, null, contentValues);
        Log.e("id", id + "");
        Log.e(TAG, "Inserted data in " + tableName + " table");
        database.close();
        return id;
    }


    public int getRowCount(String tableName) {
        String countQuery = "SELECT  * FROM " + tableName;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public boolean getDuplicateMerchant(String tableName,String COLUMN_MERCHANT_NAME,String merchant_name){
        //String name = merchant_name;
        //String query = "Select * From " + tableName + " where " + COLUMN_MERCHANT_NAME + "= '"+name+"'";
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String query="SELECT "+ COLUMN_MERCHANT_NAME +" FROM "+tableName+" WHERE " + COLUMN_MERCHANT_NAME + " = " + merchant_name;
        Cursor cursor = sqLiteDatabase.rawQuery(query,null);
        int count = cursor.getCount();
        Log.e("count", count + "");
        if (cursor.getCount() > 0) {
            return false;
        } else {
            return true;
        }
    }

    public List<FinalDetails> getTxnRecord(String query) {
        Log.e("Query", query);

        List<FinalDetails> finalDetailsList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null && cursor.getCount() > 0) {
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {

                    FinalDetails finalDetails = null;
                    finalDetails = FullDetailsFromDB(cursor);

                    finalDetailsList.add(finalDetails);

                } while (cursor.moveToNext());
            }

            cursor.close();
        }
        db.close();

        return finalDetailsList;
    }

    private FinalDetails FullDetailsFromDB(Cursor cursor) {
        FinalDetails finalDetails = new FinalDetails();

        finalDetails.setId(cursor.getString(cursor.getColumnIndex(DB_FULL_DETAILS._ID)));

        finalDetails.setMerchant_name(cursor.getString(cursor.getColumnIndex(DB_FULL_DETAILS.COLUMN_Merchant_Name)));
        finalDetails.setLat_long(cursor.getString(cursor.getColumnIndex(DB_FULL_DETAILS.COLUMN_LatLong)));
        finalDetails.setMobileNumber(cursor.getString(cursor.getColumnIndex(DB_FULL_DETAILS.COLUMN_Merachant_Number)));
        finalDetails.setType(cursor.getString(cursor.getColumnIndex(DB_FULL_DETAILS.COLUMN_type)));

        return finalDetails;
    }
}
