package drucc.sittichok.heyheybread;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by mosza_000 on 2/1/2559.
 */
public class ManageTABLE {

    //Explicit
    private MyOpenHelper objMyOpenHelper;
    private SQLiteDatabase writeSqLiteDatabase, readSqLiteDatabase;
    // static ค่าคงที่ final ที่ไม่สามารถเปลี่ยนแปลงได้
    public static final String TABLE_USER = "userTABLE";
    public static final String COLUMN_id = "_id";
    public static final String COLUMN_User = "User";
    public static final String COLUMN_Password = "Password";
    public static final String COLUMN_Name = "Name";
    public static final String COLUMN_Surname = "Surname";
    public static final String COLUMN_Address = "Address";
    public static final String COLUMN_Phone = "Phone";
    public static final String COLUMN_Balance = "Balance";

    public static final String TABLE_BREAD = "breadTABLE";
    public static final String COLUMN_Bread = "Bread";
    public static final String COLUMN_Price = "Price";
    public static final String COLUMN_Image = "Image";
    public static final String COLUMN_Status = "Status";

    public static final String TABLE_ORDER = "orderTABLE";
    public static final String COLUMN_Date = "Date";
    public static final String COLUMN_Item = "Item";

    public static final String TABLE_TBORDER = "tborder";
    public static final String COLUMN_OrderDate = "OrderDate";
    public static final String COLUMN_CustomerID = "CustomerID";
    public static final String COLUMN_GrandTotal = "GrandTotal";

    public static final String TABLE_TBORDER_DETAIL = "tborderdetail";
    public static final String COLUMN_OrderNo = "OrderNo";
    public static final String COLUMN_OrderDetail_ID = "OrderDetail_ID";
    public static final String COLUMN_Product_ID = "Product_ID";
    public static final String COLUMN_Amount = "Amount";
    public static final String COLUMN_PriceTotal = "PriceTotal";

    public ManageTABLE(Context context) {

        //Create & Connected
        objMyOpenHelper = new MyOpenHelper(context);
        writeSqLiteDatabase = objMyOpenHelper.getWritableDatabase();
        readSqLiteDatabase = objMyOpenHelper.getReadableDatabase();

    } //Constructor

    public long addtbOrderDetail(String strOrderNo,
                                 String strOrderDetail_ID,
                                 String strProduct_ID,
                                 String strAmount,
                                 String strPrice,
                                 String strPriceTotal) {

        ContentValues objContentValues = new ContentValues();
        //ContentValues คือ obj ที่ใช้ในการเชื่อมต่อฐานข้อมูล มันคือตัวกลาง

        objContentValues.put(COLUMN_OrderNo, strOrderNo);
        objContentValues.put(COLUMN_OrderDetail_ID,strOrderDetail_ID);
        objContentValues.put(COLUMN_Product_ID,strProduct_ID);
        objContentValues.put(COLUMN_Amount,strAmount);
        objContentValues.put(COLUMN_Price,strPrice);
        objContentValues.put(COLUMN_PriceTotal,strPriceTotal);

        return writeSqLiteDatabase.insert(TABLE_TBORDER_DETAIL,null,objContentValues);
    }   // addtbOrderDetail

    public long addtbOrder(String strorderdate,
                                  String strCustomerID,
                                  String strGrandTotal,
                                  String strStatus) {

        ContentValues objContentValues = new ContentValues();
        //ContentValues คือ obj ที่ใช้ในการเชื่อมต่อฐานข้อมูล มันคือตัวกลาง

        objContentValues.put(COLUMN_OrderDate, strorderdate);
        objContentValues.put(COLUMN_CustomerID,strCustomerID);
        objContentValues.put(COLUMN_GrandTotal,strGrandTotal);
        objContentValues.put(COLUMN_Status,strStatus);

        return writeSqLiteDatabase.insert(TABLE_TBORDER,null,objContentValues);
    }   // addtbOrder


    // หาชื่อขนมปังจาก orderTABLE
    public String[] SearchBread(String strBread) {

        try {

            String[] resultStrings = null;
            Cursor objCursor = readSqLiteDatabase.query(TABLE_ORDER,
                    new String[]{COLUMN_id, COLUMN_Bread, COLUMN_Item},
                    COLUMN_Bread + "=?",
                    new String[]{String.valueOf(strBread)},
                    null,null,null,null);

            if (objCursor != null) {
                if (objCursor.moveToFirst()) {

                    resultStrings = new String[objCursor.getColumnCount()]; // จอง หน่วยความจำ
                    for (int i=0; i<objCursor.getColumnCount();i++) {
                        resultStrings[i] = objCursor.getString(i);
                    }
                } // if2

            }   //if1
            objCursor.close();
            return resultStrings;

        } catch (Exception e) {
            return null;
        }

        //return new String[0];
    }

    //หาข้อมูล ที่ User login อยู่
    public String[] readAtPosition(int intID) {

        String[] resultStrings = null;
        Cursor objCursor = readSqLiteDatabase.query(TABLE_USER,
                new String[]{COLUMN_id, COLUMN_Name, COLUMN_Surname,
                        COLUMN_Address,COLUMN_Phone },
                null, null, null, null, null);
        objCursor.moveToFirst();
        resultStrings = new String[objCursor.getColumnCount()];
        objCursor.moveToPosition(intID);
        for (int i=0; i<objCursor.getColumnCount();i++) {
            resultStrings[i] = objCursor.getString(i);
        }   // for
        objCursor.close();


        return resultStrings;
    }   //  readAtPosition


    public String[] searchUser(String strUser) {

        try {

            String[] resultStrings = null;
            Cursor objCursor = readSqLiteDatabase.query(TABLE_USER,
                    new String[]{COLUMN_id, COLUMN_User, COLUMN_Password,
                            COLUMN_Name, COLUMN_Surname, COLUMN_Address,
                            COLUMN_Phone, COLUMN_Balance},
                    COLUMN_User + "=?",
                    new String[]{String.valueOf(strUser)},
                    null,null,null,null);
            if (objCursor != null) {

                if (objCursor.moveToFirst()) { //moveToFirst หาจากบนลงล่าง

                    int intTimes = objCursor.getColumnCount(); //getColumnCount(); นับคอลัม ก่อน ว่า มี กี่คอมลัม

                    resultStrings = new String[intTimes];
                    for (int i=0; i<intTimes; i++) {
                        resultStrings[i] = objCursor.getString(i);
                    }
                }   //if2

            }   // if1

            objCursor.close();
            return resultStrings;

        } catch (Exception e) {
            return null;
        }

        //return new String[0];
    }   //searchUser


    public long addNewOrder(String strName,
                            String strDate,
                            String strSurname,
                            String strAddress,
                            String strPhone,
                            String strBread,
                            String strPrice,
                            String strItem
                            ) {

        ContentValues objContentValues = new ContentValues();
        //ContentValues คือ obj ที่ใช้ในการเชื่อมต่อฐานข้อมูล มันคือตัวกลาง
        objContentValues.put(COLUMN_Name,strName);
        objContentValues.put(COLUMN_Date,strDate);
        objContentValues.put(COLUMN_Surname,strSurname);
        objContentValues.put(COLUMN_Address,strAddress);
        objContentValues.put(COLUMN_Phone,strPhone);
        objContentValues.put(COLUMN_Bread,strBread);
        objContentValues.put(COLUMN_Price,strPrice);
        objContentValues.put(COLUMN_Item,strItem);

        return writeSqLiteDatabase.insert(TABLE_ORDER,null,objContentValues);
    }   // addNewOrder


    public long addNewBread(String strBread,
                            String strPrice,
                            String strImage,
                            String strStatus) {
        ContentValues objContentValues = new ContentValues();
        //ContentValues คือ obj ที่ใช้ในการเชื่อมต่อฐานข้อมูล มันคือตัวกลาง
        objContentValues.put(COLUMN_Bread,strBread);
        objContentValues.put(COLUMN_Price,strPrice);
        objContentValues.put(COLUMN_Image,strImage);
        objContentValues.put(COLUMN_Status,strStatus);

        return writeSqLiteDatabase.insert(TABLE_BREAD,null,objContentValues);
    }   // addNewBread

    public long addNewUser(
                           String strUser,
                           String strPassword,
                           String strName,
                           String strSurname,
                           String strAddress,
                           String strPhone,
                           String strBalance) {
        ContentValues objContentValues = new ContentValues();
        //ContentValues คือ obj ที่ใช้ในการเชื่อมต่อฐานข้อมูล มันคือตัวกลาง

        objContentValues.put(COLUMN_User,strUser);
        objContentValues.put(COLUMN_Password,strPassword);
        objContentValues.put(COLUMN_Name,strName);
        objContentValues.put(COLUMN_Surname,strSurname);
        objContentValues.put(COLUMN_Address,strAddress);
        objContentValues.put(COLUMN_Phone,strPhone);
        objContentValues.put(COLUMN_Balance,strBalance);

        return writeSqLiteDatabase.insert(TABLE_USER,null,objContentValues);

    }   // addNewUser



}   //Main class
