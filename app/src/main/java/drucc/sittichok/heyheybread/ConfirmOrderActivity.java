package drucc.sittichok.heyheybread;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ConfirmOrderActivity extends AppCompatActivity {
    // Explicit
    private TextView dateTextView, nameTextView,statusTextView,
            totalTextView,numberorderTextView;
    private String dateString,nameString,surnameString;
    private ListView orderListView;
    private int totalAnInt = 0;
    private String strIDuser;
    private String strDate;
    private String strOrderNo;
    private int orderDetailAnInt = 0;
    private String strOrderNumber ;

    private ManageTABLE objManageTABLE;
    private String Balane;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);

        // deletesynUserTable
        deleteUser();

        // synUserTable
        synUserTABLE();

        // Bind Widget  กำหนตตำแหน่งในรายละเอียดการสั่งซื้อ
        bindWidget();


        strIDuser = getIntent().getStringExtra("idUser");

        balance();

        // Read All Data  นำค่าที่ลูกค้าสั่งมาแสดง และ ส่งค่า ชื่อ นามสกุล ที่ อยู่ เบอร์ โทร ของ ลูกค้า และรายการที่สั่ง
        readAllData();

        // orderNumber
        orderNumber();

        //Show View   โชว์ ชื่อ นามสกุล ที่ อยู่ เบอร์โทร ราคารวม
        showView();

        //Find Last OrderNo
        findLastOrderNo();

    }   // Main Method

    private void deleteUser() {

        SQLiteDatabase objSqLiteDatabase = openOrCreateDatabase(MyOpenHelper.DATABASE_NAME,
                MODE_PRIVATE, null);
        objSqLiteDatabase.delete(ManageTABLE.TABLE_USER, null, null);


    }   // deleteUser

    private void balance() {

        String strID = getIntent().getStringExtra("idUser");

        SQLiteDatabase objSqLiteDatabase = openOrCreateDatabase(MyOpenHelper.DATABASE_NAME,
                MODE_PRIVATE, null);
        Cursor objCursor = objSqLiteDatabase.rawQuery("SELECT * FROM userTABLE WHERE _id = " + "'" + strID + "'", null);
        objCursor.moveToFirst();
        String[] resultStrings = new String[objCursor.getColumnCount()];
        for (int i=0; i<objCursor.getColumnCount(); i++) {
            resultStrings[i] = objCursor.getString(i);
        }   //for

        Balane = resultStrings[7]; // รับค่า ชื่อ

        objCursor.close();

    }   // balance

    private void synUserTABLE() {
        StrictMode.ThreadPolicy myPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(myPolicy);   //เปิดโปรโตรคอลให้แอพเชื่อมต่ออินเตอร์เน็ตได้ ใช้ได้ทั้งหมด โดยใช้คำสั่ง permitAll
        int intTimes = 1;
        while (intTimes <= 1) {
            InputStream objInputStream = null;
            String strJSON = null;
            String strURLuser = "http://www.fourchokcodding.com/mos/php_get_user.php";
            HttpPost objHttpPost = null;
            //1. Create InputStream
            try {
                HttpClient objHttpClient = new DefaultHttpClient();
                switch (intTimes) {
                    case 1:
                        objHttpPost = new HttpPost(strURLuser);
                        break;
                }   // switch
                HttpResponse objHttpResponse = objHttpClient.execute(objHttpPost);
                HttpEntity objHttpEntity = objHttpResponse.getEntity();
                objInputStream = objHttpEntity.getContent();
            } catch (Exception e) {
                Log.d("sss", "InputStream ==> " + e.toString());
            }
            //2. Create JSON String
            try {
                BufferedReader objBufferedReader = new BufferedReader(new InputStreamReader(objInputStream,"UTF-8"));
                StringBuilder objStringBuilder = new StringBuilder();
                String strLine = null;
                while ((strLine = objBufferedReader.readLine()) != null) {
                    objStringBuilder.append(strLine);
                }   //while
                objInputStream.close();
                strJSON = objStringBuilder.toString();
            } catch (Exception e) {
                Log.d("sss", "strJSON ==> " + e.toString());
            }

            //3. Update JSON String to SQLite
            try {
                JSONArray objJsonArray = new JSONArray(strJSON);
                for (int i=0; i<objJsonArray.length();i++) {
                    JSONObject object = objJsonArray.getJSONObject(i);
                    switch (intTimes) {
                        case 1: // userTABLE
                            ManageTABLE objManageTABLE = new ManageTABLE(this);

                            String strUser = object.getString(ManageTABLE.COLUMN_User);
                            String strPassword = object.getString(ManageTABLE.COLUMN_Password);
                            String strName = object.getString(ManageTABLE.COLUMN_Name);
                            String strSurname = object.getString(ManageTABLE.COLUMN_Surname);
                            String strAddress = object.getString(ManageTABLE.COLUMN_Address);
                            String strPhone = object.getString(ManageTABLE.COLUMN_Phone);
                            String strBalance = object.getString(ManageTABLE.COLUMN_Balance);
                            objManageTABLE.addNewUser(strUser, strPassword, strName, strSurname,
                                    strAddress, strPhone, strBalance);
                            break;
                    }   //switch
                }
            } catch (Exception e) {
                Log.d("sss", "Update ==> " + e.toString());
            }
            intTimes += 1;
        }   //while
    }   // synJSONtoSQLite

    private void orderNumber() {

        SQLiteDatabase objSqLiteDatabase = openOrCreateDatabase(MyOpenHelper.DATABASE_NAME,
                MODE_PRIVATE, null);
        Cursor cursor = objSqLiteDatabase.rawQuery("SELECT * FROM " + ManageTABLE.TABLE_TBORDER, null);
        cursor.moveToLast();
        strOrderNumber = cursor.getString(cursor.getColumnIndex(ManageTABLE.COLUMN_id));
        int intOrderNumber = Integer.parseInt(strOrderNumber)+1;
        strOrderNumber = Integer.toString(intOrderNumber);

        cursor.close();

    }   // orderNumber

    public class ConnectedOrderDetail extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            try {

                OkHttpClient okHttpClient = new OkHttpClient();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url("http://www.fourchokcodding.com/mos/php_get_last_orderdetail.php").build();
                Response response = okHttpClient.newCall(request).execute();
                return response.body().string();

            } catch (Exception e) {
                Log.d("12April", "doInBack ==> " + e.toString());
                return null;
            }
        }   // doInback

        @Override
        protected void onPostExecute(String strJSON) {
            super.onPostExecute(strJSON);

            try {
                JSONArray jsonArray = new JSONArray(strJSON);
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                strOrderNo = jsonObject.getString("OrderNo");
                Log.d("12April", "ค่าของ OrderNo ล่าสุดที่อ่าได้ ==> " + strOrderNo);
            } catch (Exception e) {
                Log.d("12April", "onPost ==> " + e.toString());
            }
        }   // onPost
    }   // ConnectedOrderDetail

    private void findLastOrderNo() {
        ConnectedOrderDetail connectedOrderDetail = new ConnectedOrderDetail();
        connectedOrderDetail.execute();

    } // findLastOrderNo

    public void clickFinish(View view) {

        int intBalane = Integer.parseInt(Balane);

        if (totalAnInt > intBalane) {
            MyAlertDialog objMyAlertDialog = new MyAlertDialog();
            objMyAlertDialog.errorDialog(ConfirmOrderActivity.this, "ยอดเงินไม่พอ", "กรุณาเติมเงินก่อน");

        } else {

            Log.d("12April", "clickFinish OrderNo ล่าสุดที่อ่าได้ ==> " + strOrderNo);
            //Read All orderTABLE
            SQLiteDatabase objSqLiteDatabase = openOrCreateDatabase(MyOpenHelper.DATABASE_NAME, // เปิดฐานข้อมูล
                    MODE_PRIVATE, null);
            Cursor objCursor = objSqLiteDatabase.rawQuery("SELECT * FROM " + ManageTABLE.TABLE_ORDER, null);  // เลือกOrder ที่ลูกค้าสั่งทั้งหมด
            objCursor.moveToFirst();  // moveToFirst ให้เลือกตำแหน่ง ของข้อมูล Order อยู่บนสุด

            //**********************************************************************************************************************
            // Update Stock
            //**********************************************************************************************************************

            for (int i =0; i<objCursor.getCount();i++) {    // นำOrder มานับแถว ถ้ามีข้อมูล ให้ทำ
                strDate = objCursor.getString(objCursor.getColumnIndex(ManageTABLE.COLUMN_Date));  // รับค่า เวลา
                String strBread = objCursor.getString(objCursor.getColumnIndex(ManageTABLE.COLUMN_Bread)); // รับค่าชื่อขนมปัง
                String strPrice = objCursor.getString(objCursor.getColumnIndex(ManageTABLE.COLUMN_Price)); // รับค่าราคา
                String strItem = objCursor.getString(objCursor.getColumnIndex(ManageTABLE.COLUMN_Item)); // รับค่าไอเทม

                // Update to mySQL
                StrictMode.ThreadPolicy myPolicy = new StrictMode.ThreadPolicy
                        .Builder().permitAll().build();
                StrictMode.setThreadPolicy(myPolicy);   // อนุญาตืให้ myPolicy เชื่อมต่อ โปรโตคอล ได้

                if (i == (objCursor.getCount() - 1) ) {
                    Toast.makeText(ConfirmOrderActivity.this,"สั่งซื้อสินค้าสำเร็จ", // โชว์ข้อความการยืนยัน 3.5 วินาที
                            Toast.LENGTH_SHORT).show();
                }   // if

                try {
                    //Find Id Bread
                    ManageTABLE objManageTABLE = new ManageTABLE(this);
                    String[] resultStrings = objManageTABLE.SearchBread(strBread);
                    Log.d("11April", "id bread ที่สั่งได้ " + strBread + " " + resultStrings[0]);

                } catch (Exception e) {
                    Log.d("16Feb", "Cannot Delete Stock");

                }// end of TryCase 2
                objCursor.moveToNext(); // ทำต่อ

                //**********************************************************************************************************************
                // Update tborderDetail
                //**********************************************************************************************************************

                // Update to tborderdetail on Server
                Log.d("12April", "clickFinish OrderNo ล่าสุดที่อ่าได้ ==> " + strOrderNo);
                int intOrderNo = Integer.parseInt(strOrderNo)+ 1;
                String strNextOrderNo = Integer.toString(intOrderNo);


                orderDetailAnInt += 1;
                Log.d("12April", "OrderDetailID(" + (i + 1) +")" + orderDetailAnInt);
                String strOrderDetail = Integer.toString(orderDetailAnInt);

                String strProductID = findProductID(strBread);

                Log.d("12April", strBread + " มี id = " + strProductID);

                int intAmount = Integer.parseInt(strItem);
                int intPrice = Integer.parseInt(strPrice);
                int PriceTotal = intAmount * intPrice ;
                String strPriceTotal = Integer.toString(PriceTotal);

                Log.d("12April", "Amount * Price = " + intAmount + "x" + intPrice + " = " + PriceTotal);

                updateTotborderdetail(strNextOrderNo,
                        strOrderDetail,
                        strProductID,
                        strItem,
                        strPrice,
                        strPriceTotal);

            }   // for
            objCursor.close(); // คืนหน่วยความจำ

            //**********************************************************************************************************************
            // จุดเปลี่ยน
            //**********************************************************************************************************************


            // Update tborder on Server
            updateTotborder(strDate,
                    strIDuser,
                    Integer.toString(totalAnInt),
                    "จัดเตรียม");

            // Intent HubActivity
            Intent objIntent = new Intent(ConfirmOrderActivity.this, HubActivity.class);
            // ทำเสร็จแล้ว ให้ กลับไปหน้า HubActivity.class
            String strID = getIntent().getStringExtra("idUser");
            objIntent.putExtra("ID", strID); //แล้วส่งค่า ID คืนไปที่หน้า HubActivity.class ด้วย

            Log.d("19Feb", "ID ที่ได้ ==> " + strID);

            startActivity(objIntent);

            //Delete OrderTABLE
            objSqLiteDatabase.delete(ManageTABLE.TABLE_ORDER,null,null);

        }



    }   // clickFinish

    private void updateTotborderdetail(String strOrderNo,
                                       String strorderDetail_ID,
                                       String strProductID,
                                       String strAmount,
                                       String strPrice,
                                       String strpriceTotal) {

        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = new FormEncodingBuilder()
                .add("isAdd", "true")
                .add("OrderNo", strOrderNo)
                .add("OrderDetail_ID", strorderDetail_ID)
                .add("Product_ID", strProductID)
                .add("Amount", strAmount)
                .add("Price", strPrice)
                .add("PriceTotal", strpriceTotal)
                .build();
        Request.Builder builder = new Request.Builder();
        Request request = builder.url("http://www.fourchokcodding.com/mos/php_add_tborderdetail.php")
                .post(requestBody).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.d("12April", "Fail to Upload");
            }
            @Override
            public void onResponse(Response response) throws IOException {

                try {
                    Log.d("12April", "Finish to update ==> "+ response.body().string());
                } catch (Exception e) {
                    Log.d("12April", "Error upload ==> "+ e.toString());
                }

            }
        });
    }   // updateTotborderdetail

    private String findProductID(String strBread) {

        String strProductID = null;

        try {

            SQLiteDatabase sqLiteDatabase = openOrCreateDatabase(MyOpenHelper.DATABASE_NAME,
                    MODE_PRIVATE,null);
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM breadTABLE WHERE Bread = " + "'" + strBread + "'", null);
            cursor.moveToFirst();
            strProductID = cursor.getString(0);
            return strProductID;

        } catch (Exception e) {
            return null;
        }

    }

    private void updateTotborder(String strDate,
                                 String strIDuser,
                                 String strSumtotal,
                                 String strStatus) {

        StrictMode.ThreadPolicy threadPolicy = new StrictMode.ThreadPolicy
                .Builder().permitAll().build();
        StrictMode.setThreadPolicy(threadPolicy);

        try {

            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("isAdd","true"));
            nameValuePairs.add(new BasicNameValuePair("OrderDate", strDate));
            nameValuePairs.add(new BasicNameValuePair("CustomerID",strIDuser));
            nameValuePairs.add(new BasicNameValuePair("GrandTotal",strSumtotal));
            nameValuePairs.add(new BasicNameValuePair("Status",strStatus));

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://www.fourchokcodding.com/mos/php_add_tborder.php");
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));
            httpClient.execute(httpPost);

            Log.i("11April", "Update Finish");

        } catch (Exception e) {
            Log.i("11April", "ไม่สามารถอัพไปที่ tborder ได้ จาก " + e.toString());
        }

    }   // updateTotborder


    public void clickMore(View view) {

        finish(); // ปิดหน้าต่าง แสดงรายการลง แล้ว จะไป โชว์ ที่หน้า สั่งซื้อสินค้า

    } // clickMore

    private void showView() {
        numberorderTextView.setText("เลขที่สั่งซื้อ : " + strOrderNumber );
        dateTextView.setText("วันที่สั่งซื้อ : " + dateString); // นำค่า Date ใส่ไปใน dateTextView
        nameTextView.setText("ผู้สั่งซื้อ : " + nameString + " " + surnameString); // นำค่า ชื่อ กัย นามสกุล ใส่ไปใน nameTextView
        statusTextView.setText("ยอดเงินคงเหลือ : " + Balane); // นำค่า ที่อยู่  ใส่ไปใน addressTextView
//        phoneTextView.setText("เบอร์โทรศีพท์ : " + phoneString );    // นำค่า เบอร์โทร ใส่ไปใน phoneTextView
        totalTextView.setText(Integer.toString(totalAnInt) + "      บาท"); // นำค่า ราคารวมทั้งหมด ใส่ไปใน totalTextView
    }   // showView

    private void readAllData() {

        SQLiteDatabase objSqLiteDatabase = openOrCreateDatabase(MyOpenHelper.DATABASE_NAME,
                MODE_PRIVATE, null); // เปิดฐานข้อมูล Heyhey.db
        Cursor objCursor = objSqLiteDatabase.rawQuery("SELECT * FROM orderTABLE", null); //ดึง Order ที่สั่งทั้งหมดจากฐานข้อมูล
        objCursor.moveToFirst();  // แล้วให้ไปอยู่ที่ Order แรก
        dateString = objCursor.getString(objCursor.getColumnIndex(ManageTABLE.COLUMN_Date)); // รับค่า เวลา
        nameString = objCursor.getString(objCursor.getColumnIndex(ManageTABLE.COLUMN_Name)); // รับค่า ชื่อ
        surnameString = objCursor.getString(objCursor.getColumnIndex(ManageTABLE.COLUMN_Surname)); // รับค่า นามสกุล
//        addressString = objCursor.getString(objCursor.getColumnIndex(ManageTABLE.COLUMN_Address)); // รับค่า ที่อยู่
//        phoneString = objCursor.getString(objCursor.getColumnIndex(ManageTABLE.COLUMN_Phone)); // รับค่าเบอร์โทร

        String[] nameOrderStrings = new String[objCursor.getCount()]; // นับจำนวนของ ชื่อสินค้า
        String[] priceStrings = new String[objCursor.getCount()]; // นับจำนวนของ ราคาสินค้า
        String[] itemStrings = new String[objCursor.getCount()]; // นับจำนวนของ จำนวนสินค้า
        String[] noStrings = new String[objCursor.getCount()]; // นับจำนวนของ ลำดับไอเทม
        String[] amountStrings = new String[objCursor.getCount()]; // นับจำนวน ราคารวม คือ item * price ได้ sum ผลรวม

        for (int i=0; i<objCursor.getCount();i++) {

            nameOrderStrings[i] = objCursor.getString(objCursor.getColumnIndex(ManageTABLE.COLUMN_Bread)); // รับค่าชื่อขนมปัง
            priceStrings[i] = objCursor.getString(objCursor.getColumnIndex(ManageTABLE.COLUMN_Price)); // รับค่าราคา
            itemStrings[i] = objCursor.getString(objCursor.getColumnIndex(ManageTABLE.COLUMN_Item)); // รับค่าจำนวน
            noStrings[i] = Integer.toString(i + 1); // +1 เพราะ ลำดับ เริ่มที่ 1 แล้ว บวก ไปเลื่อยๆ จนหมด เช่น 1 2 3 4
            amountStrings[i] = Integer.toString( Integer.parseInt(itemStrings[i])* Integer.parseInt(priceStrings[i]) );
            // Sum ผลรวมของ item*price

            objCursor.moveToNext(); // เช่น ทำลำดับที่ 1 เสร็จ แล้ว ทำลำดับที่ 2 ต่อ Next จนกว่าจะหมด

            totalAnInt = totalAnInt + Integer.parseInt(amountStrings[i]);
            // ค่าผมรวมทั้งหมด total เอา amountStrings[i] ทั้งหมด มา+กัน

        }   // for

        objCursor.close();

        // Create Listview
        MyOrderAdapter objMyOrderAdapter = new MyOrderAdapter(ConfirmOrderActivity.this,
                noStrings, nameOrderStrings, itemStrings, priceStrings, amountStrings);
        orderListView.setAdapter(objMyOrderAdapter);

        // Delete Order
        orderListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    myDeleteOrder(i);

                } catch (Exception e) {
                    e.toString();
                    Intent objIntent = new Intent(ConfirmOrderActivity.this, showMenuActivity.class);
                    objIntent.putExtra("ID", strIDuser);
                    startActivity(objIntent);
                }

            } // event
        });

    }   // readAllData

    private void myDeleteOrder(int position) {

        final SQLiteDatabase objSqLiteDatabase = openOrCreateDatabase(MyOpenHelper.DATABASE_NAME,
                MODE_PRIVATE, null);
        final Cursor objCursor = objSqLiteDatabase.rawQuery("SELECT * FROM " + ManageTABLE.TABLE_ORDER, null);
        objCursor.moveToFirst();
        objCursor.moveToPosition(position);
        String strBread = objCursor.getString(objCursor.getColumnIndex(ManageTABLE.COLUMN_Bread));
        String strItem = objCursor.getString(objCursor.getColumnIndex(ManageTABLE.COLUMN_Item));
        final String strID = objCursor.getString(objCursor.getColumnIndex(ManageTABLE.COLUMN_id));
        Log.d("Hay", "ID ==> " + strID);


        AlertDialog.Builder objBuilder = new AlertDialog.Builder(this);
        objBuilder.setIcon(R.drawable.icon_myaccount);
        objBuilder.setTitle("คุณแน่ใจใช่ไหม ? ");
        objBuilder.setMessage("ที่จะลบรายการ " + strBread +" " +strItem + "ชิ้น");
        objBuilder.setCancelable(false);
        objBuilder.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                try {
                    int intID = Integer.parseInt(strID);
                    objSqLiteDatabase.delete(ManageTABLE.TABLE_ORDER,
                            ManageTABLE.COLUMN_id + "=" + intID, null);
                    totalAnInt = 0;
                    readAllData();
                    totalTextView.setText(Integer.toString(totalAnInt));
                    dialogInterface.dismiss();

                } catch (Exception e) {
                    e.toString();
                    Intent objIntent = new Intent(ConfirmOrderActivity.this, showMenuActivity.class);
                    objIntent.putExtra("ID", strIDuser);
                    startActivity(objIntent);
                }



            }
        });
        objBuilder.setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        objBuilder.show();

        objCursor.close();

    }   // myDeleteOrder

    private void bindWidget() {

        dateTextView = (TextView) findViewById(R.id.textView19);  // ตำแหน่ง เวลา
        nameTextView = (TextView) findViewById(R.id.textView20);  // ตำแหน่ง ชื่อ
        statusTextView = (TextView) findViewById(R.id.textView21); // ตำแหน่งที่อยู่
//        phoneTextView = (TextView) findViewById(R.id.textView22); // ตำแหน่งเบอร์
        totalTextView = (TextView) findViewById(R.id.textView23); // ตำแหน่งราคารวม
        orderListView = (ListView) findViewById(R.id.listView2); // ตำแหน่งรายการสินค้าที่ลูกค้าสั่งซื้อ
        numberorderTextView = (TextView) findViewById(R.id.textView30); // ตำแหน่ง รหัสรายการสั่งซื้อ


    }   //bindWidget

}   // Main class