package drucc.sittichok.heyheybread;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HistoryActivity extends AppCompatActivity {

    // Explicit
    private String strID;
    private ListView UserOrderListView;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        strID = getIntent().getStringExtra("ID");
        deleteOrder();
        syntborder();

        bindWidget();   // ตัวแปล UserOrderListView = ตำแหน่งของ ListViewHistory

        readAllorder();


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }   // onCreate

    private void syntborder() {
        StrictMode.ThreadPolicy threadPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(threadPolicy);
        int intTimes = 1;
        while (intTimes <= 1) {
            InputStream objInputStream = null;
            String strJSON = null;
            String strURLtborder = "http://www.fourchokcodding.com/mos/php_get_tborder.php";
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
                for (int i = 0; i < objJsonArray.length(); i++) {
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
        objSqLiteDatabase.delete(ManageTABLE.TABLE_TBORDER, null, null);

    }   // deleteOrder

    private void readAllorder() {
        SQLiteDatabase objSqLiteDatabase = openOrCreateDatabase(MyOpenHelper.DATABASE_NAME,
                MODE_PRIVATE, null);
        final Cursor objCursor = objSqLiteDatabase.rawQuery("SELECT * FROM tborder WHERE CustomerID = " + strID, null);
        objCursor.moveToFirst();  // ไปอยู่ที่แถวแรก ของ tborder

        final String[] NumberOrder = new String[objCursor.getCount()];
        final String[] DateOrder = new String[objCursor.getCount()];
        final String[] PriceOrder = new String[objCursor.getCount()];
        final String[] StatusOrder = new String[objCursor.getCount()];

        for (int i = 0; i < objCursor.getCount(); i++) {

            NumberOrder[i] = objCursor.getString(objCursor.getColumnIndex(ManageTABLE.COLUMN_id));
            DateOrder[i] = objCursor.getString(objCursor.getColumnIndex(ManageTABLE.COLUMN_OrderDate));
            PriceOrder[i] = objCursor.getString(objCursor.getColumnIndex(ManageTABLE.COLUMN_GrandTotal));
            StatusOrder[i] = objCursor.getString(objCursor.getColumnIndex(ManageTABLE.COLUMN_Status));

            objCursor.moveToNext();

        }   // for

        objCursor.close();

        // Create ListView
        final OrderUserAdapter objOrderUserAdapter = new OrderUserAdapter(HistoryActivity.this, NumberOrder, DateOrder, PriceOrder, StatusOrder);

        UserOrderListView.setAdapter(objOrderUserAdapter);

        // Show orderdetail

        UserOrderListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                ShowDialog(NumberOrder[i]);

//               Intent objIntent = new Intent(HistoryActivity.this, OrderDetailActivity.class);
////                objIntent.putExtra("ID", strID);
////                objIntent.putExtra("ORDER", NumberOrder);
//                startActivity(objIntent);
            }
        });


    }   // readAllorder

    private void ShowDialog(final String NumberOrder) {

        AlertDialog.Builder objBuilder = new AlertDialog.Builder(this);
        objBuilder.setIcon(R.drawable.icon_myaccount);
        objBuilder.setTitle("เลือกสิ่งที่ต้องการ");  // หัวข้อ
        objBuilder.setMessage("1.เลือกเพิ่มรูปภาพ คือ การเพิ่มรูปภาพ เพื่อยืนยันการชำระเงิน" +"\n" +
                "2.เลือกรายละเอียดการสั่งซื้อ คือ การดูรายละเอียดของการสังซื้อ");
        objBuilder.setPositiveButton("ดูรายละเอียดการสั่งซื้อ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                ChooseOrder(NumberOrder);
                dialogInterface.dismiss();
            }
        });

        objBuilder.setNegativeButton("เพิ่มรูปภาพการชำระเงิน", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                showPickPictureDialog();

                dialogInterface.dismiss();

            }
        });
        objBuilder.show();

    }   // ShowDialog

    private final static int PICK_IMAGE = 1;
    private String imageFilePath;

    private void showPickPictureDialog() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"เลือกรูปภาพ"),PICK_IMAGE);

    }   // showPickPictureDialog

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == PICK_IMAGE && data != null && data.getData() != null) {
            Uri uri = data.getData();

            if (uri != null) {
                Cursor cursor = getContentResolver().query(
                        uri, new String[]{ MediaStore.Images.ImageColumns.DATA

                        },null,null,null
                );
                cursor.moveToFirst();

                imageFilePath = cursor.getString(0);

                showConfirmPostPictureDialog();
                cursor.close();

            }
        }
    }   // onActivityResult

    private void showConfirmPostPictureDialog() {
        Bitmap picture = BitmapFactory.decodeFile(imageFilePath);

        final ImageView imageView = new ImageView(this);
        imageView.setImageBitmap(picture);

        AlertDialog.Builder objBuilder = new AlertDialog.Builder(this);
        objBuilder.setTitle("อัฟเดทรูปภาพเข้าระบบ");
        objBuilder.setView(imageView);
        objBuilder.setPositiveButton("ยืนยัน", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {



            }
        }).setNegativeButton("ยกเลิก",null);
        objBuilder.show();



    }   // showConfirmPostPictureDialog

    private void ChooseOrder(final String NumberOrder) {

        Intent objIntent = new Intent(HistoryActivity.this, OrderDetailActivity.class);
        objIntent.putExtra("NO",NumberOrder);
        startActivity(objIntent);

    }   // ChooseOrder


    private void bindWidget() {

        UserOrderListView = (ListView) findViewById(R.id.ListViewHistory);

    }   // bindWidget

}   // Main Class
