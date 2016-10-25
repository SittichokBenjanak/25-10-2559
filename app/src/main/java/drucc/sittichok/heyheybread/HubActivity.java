package drucc.sittichok.heyheybread;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

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

public class HubActivity extends AppCompatActivity implements View.OnClickListener {

    // Explicit

    private ImageView orderImageView, readImageView,
    editImageView, mapImageView, complacencyImageView;
    private String idString;    // รับค่า Receive id ที่ user login อยู่

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hub);
        deleteOrder();
        syntborder();

        //Bind Widget
        bindWidget();

        //Image Controller
        imageController();
    }   // onCreate
    private void syntborder() {
        StrictMode.ThreadPolicy threadPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(threadPolicy);
        int intTimes = 1;
        while (intTimes <= 1) {
            InputStream objInputStream = null;
            String strJSON = null;
            String strURLtborder = "http://swiftcodingthai.com/mos/php_get_tborder_mos.php";
            HttpPost objHttpPost = null;
            // 1 Create InputStream
            try {
                HttpClient objHttpClient = new DefaultHttpClient();
                switch (intTimes) {
                    case 1:
                        objHttpPost = new HttpPost(strURLtborder);
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
                while ((strLine = objBufferedReader.readLine()) != null) {
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
                        case 1:
                            ManageTABLE objManageTABLE = new ManageTABLE(this);
                            String strOrderDate = object.getString(ManageTABLE.COLUMN_OrderDate);
                            String strCustomerID = object.getString(ManageTABLE.COLUMN_CustomerID);
                            String strGrandTotal = object.getString(ManageTABLE.COLUMN_GrandTotal);
                            String strStatus1 = object.getString(ManageTABLE.COLUMN_Status);
                            objManageTABLE.addtbOrder(strOrderDate, strCustomerID, strGrandTotal, strStatus1);
                            break;
                    }

                }   // for

            } catch (Exception e) {
                Log.d("sss", "InputStream ==> " + e.toString());
            }
            intTimes += 1;

        }   // while


    }   // syntborder

    private void deleteOrder() {
        SQLiteDatabase objSqLiteDatabase = openOrCreateDatabase(MyOpenHelper.DATABASE_NAME,
                MODE_PRIVATE, null);
        objSqLiteDatabase.delete(ManageTABLE.TABLE_TBORDER,null,null);

    }   // deleteOrder


    private void imageController() {
        idString = getIntent().getStringExtra("ID");
        orderImageView.setOnClickListener(this);
        readImageView.setOnClickListener(this);
        editImageView.setOnClickListener(this);
        mapImageView.setOnClickListener(this);
        complacencyImageView.setOnClickListener(this);
    }

    private void bindWidget() {
        orderImageView = (ImageView) findViewById(R.id.imageView2);
        readImageView = (ImageView) findViewById(R.id.imageView3);
        editImageView = (ImageView) findViewById(R.id.imageView4);
        mapImageView = (ImageView) findViewById(R.id.imageView5);
        complacencyImageView = (ImageView) findViewById(R.id.imageView6);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageView2:
                //Order Bread
                Intent objIntent = new Intent(HubActivity.this, showMenuActivity.class);
                objIntent.putExtra("ID", idString);
                startActivity(objIntent);
                break;

            case R.id.imageView3:
                //Read Order
                clickReadOrder();
                break;

            case R.id.imageView4:
                //Edit Account
                Intent intent = new Intent(HubActivity.this, EditUser.class);
                intent.putExtra("ID", idString);
                startActivity(intent);

                break;

            case R.id.imageView5:
                //My Map
                Intent Maps = new Intent(HubActivity.this, MapsActivity.class);
                startActivity(Maps);

                break;

            case R.id.imageView6:
                // OrderHistory
                checkHistory();


                break;
        }   //switch
    }   // onClick

    private void checkHistory() {
        SQLiteDatabase objSqLiteDatabase = openOrCreateDatabase(MyOpenHelper.DATABASE_NAME,
                MODE_PRIVATE, null);
        Cursor objCursor = objSqLiteDatabase.rawQuery("SELECT * FROM tborder WHERE CustomerID = " + idString, null);
        if (objCursor.getCount() > 0) {
            Intent intent2 = new Intent(HubActivity.this, HistoryActivity.class);
            intent2.putExtra("ID", idString);
            startActivity(intent2);
        } else {
            MyAlertDialog objMyAlertDialog = new MyAlertDialog();
            objMyAlertDialog.errorDialog(HubActivity.this,"ยังไม่มีประวัติการสั่งซื้อ","กรุณาสั่งสินค้าก่อนครับ");
        }


    }   // checkHistory

    private void clickReadOrder() {
        SQLiteDatabase objSqLiteDatabase = openOrCreateDatabase(MyOpenHelper.DATABASE_NAME,
                MODE_PRIVATE, null);
        Cursor objCursor = objSqLiteDatabase.rawQuery("SELECT * FROM " + ManageTABLE.TABLE_ORDER, null);
        if (objCursor.getCount() > 0) {
            Intent objIntent = new Intent(HubActivity.this, ConfirmOrderActivity.class);
            objIntent.putExtra("Status", true);
            startActivity(objIntent);
        } else {
            MyAlertDialog objMyAlertDialog = new MyAlertDialog();
            objMyAlertDialog.errorDialog(HubActivity.this,"ยังไม่มีการสั่งซื้อ","กรุณาสั่งสินค้าก่อนครับ");
        }
    }
}   // Main Class
