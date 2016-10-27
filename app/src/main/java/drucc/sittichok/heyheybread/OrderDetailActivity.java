package drucc.sittichok.heyheybread;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class OrderDetailActivity extends AppCompatActivity {

    // Explicit
    private String strID;
    private ListView detailListView;
    // private String noString,nameString,amountString,priceString, sumpriceString;
    private String strOrderID;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        try {

            strID = getIntent().getStringExtra("ID");
            strOrderID = getIntent().getStringExtra("NO");
            Log.d("hey","strOrderID ==> "+ strOrderID );

            deleteOrderdetail();
            synOrderDetail();

            bindWiget();

            readAllorderdetail();

        } catch (Exception e) {
            Log.d("hey","strOrderID ="+ strOrderID  + e.toString());
        }

    }

    private void synOrderDetail() {
        StrictMode.ThreadPolicy threadPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(threadPolicy);
        int intTimes = 1;
        while (intTimes <= 1) {
            InputStream objInputStream = null;
            String strJSON = null;
            String strURLtborderdetail = "http://www.fourchokcodding.com/mos/php_get_tborderdetail.php";
            HttpPost objHttpPost = null;
            // 1 Create InputStream
            try {
                HttpClient objHttpClient = new DefaultHttpClient();
                switch (intTimes) {
                    case 1:
                        objHttpPost = new HttpPost(strURLtborderdetail);
                        break;
                }   // switch

                HttpResponse objHttpResponse = objHttpClient.execute(objHttpPost);
                HttpEntity objHttpEntity = objHttpResponse.getEntity();
                objInputStream = objHttpEntity.getContent();

            } catch (Exception e) {
                Log.d("sss", "InputStream ==> " + e.toString());
            }
            // 2 Create JSON String
            try {
                BufferedReader objBufferedReader = new BufferedReader(new InputStreamReader(objInputStream, "UTF-8"));
                StringBuilder objStringBuilder = new StringBuilder();
                String strLine = null;
                while ((strLine = objBufferedReader.readLine())!= null) {
                    objStringBuilder.append(strLine);
                }
                objInputStream.close();
                strJSON = objStringBuilder.toString();


            } catch (Exception e) {
                Log.d("sss", "InputStream ==> " + e.toString());
            }

            // 3 Update JSON String to SQLite
            try {
                JSONArray objJsonArray = new JSONArray(strJSON);
                for (int i=0; i<objJsonArray.length(); i++) {
                    JSONObject object = objJsonArray.getJSONObject(i);
                    switch (intTimes) {
                        case 1: // tborderdetail
                            ManageTABLE objManageTABLE = new ManageTABLE(this);
                            String strID = object.getString("id");
                            String strOrderNo = object.getString(ManageTABLE.COLUMN_OrderNo);
                            String strOrderDetail_ID = object.getString(ManageTABLE.COLUMN_OrderDetail_ID);
                            String strProduct_ID = object.getString(ManageTABLE.COLUMN_Product_ID);
                            String strAmount2 = object.getString(ManageTABLE.COLUMN_Amount);
                            String strPrice2 = object.getString(ManageTABLE.COLUMN_Price);
                            String strPriceTotal = object.getString(ManageTABLE.COLUMN_PriceTotal);
                            objManageTABLE.addtbOrderDetail(strID, strOrderNo, strOrderDetail_ID, strProduct_ID,
                                    strAmount2,strPrice2,strPriceTotal);
                            break;
                    }
                }   // for

            } catch (Exception e) {
                Log.d("sss", "InputStream ==> " + e.toString());
            }
            intTimes += 1;
        }   // while

    }   // synOrderDetail


    private void deleteOrderdetail() {
        SQLiteDatabase objSqLiteDatabase = openOrCreateDatabase(MyOpenHelper.DATABASE_NAME,
                MODE_PRIVATE, null);
        objSqLiteDatabase.delete(ManageTABLE.TABLE_TBORDER_DETAIL, null, null);

    }   // deleteOrderdetail

    private void readAllorderdetail() {
        SQLiteDatabase objSqLiteDatabase = openOrCreateDatabase(MyOpenHelper.DATABASE_NAME,
                MODE_PRIVATE,null);
        Cursor objCursor = objSqLiteDatabase.rawQuery("SELECT Bread, * FROM tborderdetail INNER JOIN breadTABLE ON (breadTABLE._id = tborderdetail.Product_ID) WHERE OrderNo = " + strOrderID , null);
        objCursor.moveToFirst();

        final String[] Orderdetail = new String[objCursor.getCount()];
        final String[] Productdetail = new String[objCursor.getCount()];
        final String[] Amountdetail = new String[objCursor.getCount()];
        final String[] Pricedetail = new String[objCursor.getCount()];
        final String[] Sumpricedetail = new String[objCursor.getCount()];

        for (int i = 0; i<objCursor.getCount(); i++) {
            Orderdetail[i] = objCursor.getString(objCursor.getColumnIndex(ManageTABLE.COLUMN_OrderDetail_ID));
            Productdetail[i] = objCursor.getString(objCursor.getColumnIndex(ManageTABLE.COLUMN_Bread));
            Amountdetail[i] = objCursor.getString(objCursor.getColumnIndex(ManageTABLE.COLUMN_Amount));
            Pricedetail[i] = objCursor.getString(objCursor.getColumnIndex(ManageTABLE.COLUMN_Price));
            Sumpricedetail[i] = objCursor.getString(objCursor.getColumnIndex(ManageTABLE.COLUMN_PriceTotal));
            // หา ชื่อสินค้า จาก id product
            objCursor.moveToNext();

        }   // for


        objCursor.close();



        //  Create ListView
        final DetailAdapter objDetailAdapter = new DetailAdapter(OrderDetailActivity.this, Orderdetail, Productdetail, Amountdetail, Pricedetail, Sumpricedetail);

        detailListView.setAdapter(objDetailAdapter);


    }   // readAllorderdetail




    private void bindWiget() {

        detailListView = (ListView) findViewById(R.id.ListViewDetail);

    }   // bindWiget
}
