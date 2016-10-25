package drucc.sittichok.heyheybread;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by mosza_000 on 2/1/2559.
 */
public class MyOpenHelper extends SQLiteOpenHelper{

    // Explicit
    public static final String DATABASE_NAME = "Heyhey.db";
    private static final int DATABASE_VERSION = 1;
    private static final String CREATE_TABLE_USER = "create table userTABLE (" +
            "_id integer primary key, " +
            "User text," +
            "Password text," +
            "Name text," +
            "Surname text," +
            "Address text," +
            "Phone text," +
            "Complacency text);";

    private static final String CREATE_TABLE_BREAD = "create table breadTABLE (" +
            "_id integer primary key," +
            "Bread text," +
            "Price text," +
            "Image text," +
            "Status text);";

    private static final String CREATE_TABLE_ORDER = "create table orderTABLE (" +
            "_id integer primary key," +
            "Date text," +
            "Name text," +
            "Surname text," +
            "Address text," +
            "Phone text," +
            "Bread text," +
            "Price text," +
            "Item text)";

    private static final String CREATE_TBORDER = "create table tborder(" +
            "_id integer primary key," +
            "OrderDate text," +
            "CustomerID text," +
            "GrandTotal text," +
            "Status text)";

    private static final String CREATE_TBORDER_DETAIL = "create table tborderdetail(" +
            "_id integer primary key," +
            "OrderNo text," +
            "OrderDetail_ID text," +
            "Product_ID text," +
            "Amount text," +
            "Price text," +
            "PriceTotal text)";

    public MyOpenHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);

    }   //Constructor

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_BREAD);
        db.execSQL(CREATE_TABLE_ORDER);
        db.execSQL(CREATE_TBORDER);
        db.execSQL(CREATE_TBORDER_DETAIL);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
} //Main Class
