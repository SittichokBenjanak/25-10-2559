package drucc.sittichok.heyheybread;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

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
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CheckmoneyActivity extends AppCompatActivity {

    private String strID;

    private TextView dateTextView, moneyTextView;
    private String strDate ,Balane,strBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkmoney);
        strID = getIntent().getStringExtra("ID");
        // deletesynUserTable
        deleteUser();
        // synUserTable
        synUserTABLE();
        // Date
        date();
        blance();
        // bindWidget
        bindWidget();
    }

    public void onBackPressed(){
        Intent money = new Intent(CheckmoneyActivity.this, HubActivity.class);
        money.putExtra("ID", strID);
        startActivity(money);
        finish();
    }

    private void blance() {
        SQLiteDatabase objSqLiteDatabase = openOrCreateDatabase(MyOpenHelper.DATABASE_NAME,
                MODE_PRIVATE, null);
        Cursor objCursor = objSqLiteDatabase.rawQuery("SELECT * FROM userTABLE WHERE _id = " + "'" + strID + "'", null);
        objCursor.moveToFirst();
        String[] resultStrings = new String[objCursor.getColumnCount()];
        for (int i=0; i<objCursor.getColumnCount(); i++) {
            resultStrings[i] = objCursor.getString(i);
        }   //for

        Balane = resultStrings[7]; // รับค่า ชื่อ
        int balance = Integer.parseInt(Balane);
        NumberFormat objNumberFormat = NumberFormat.getInstance();
        strBalance = objNumberFormat.format(balance);
        objCursor.close();
    }

    private void bindWidget() {
        dateTextView = (TextView) findViewById(R.id.textView56);
        moneyTextView = (TextView) findViewById(R.id.textView57);
        dateTextView.setText(strDate);
        moneyTextView.setText(strBalance);

    }   // bindWidget

    private void date() {
        DateFormat myDateFormat = new SimpleDateFormat("dd/MM/yyyy"); // วันที่ปัจจุบัน
        Date clickDate = new Date();
        strDate = myDateFormat.format(clickDate);
    }   // date


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
                            String strID4 = object.getString("id");
                            String strUser = object.getString(ManageTABLE.COLUMN_User);
                            String strPassword = object.getString(ManageTABLE.COLUMN_Password);
                            String strName = object.getString(ManageTABLE.COLUMN_Name);
                            String strSurname = object.getString(ManageTABLE.COLUMN_Surname);
                            String strAddress = object.getString(ManageTABLE.COLUMN_Address);
                            String strPhone = object.getString(ManageTABLE.COLUMN_Phone);
                            String strBalance = object.getString(ManageTABLE.COLUMN_Balance);
                            objManageTABLE.addNewUser(strID4, strUser, strPassword, strName, strSurname,
                                    strAddress, strPhone, strBalance);
                            break;
                    }   //switch
                }
            } catch (Exception e) {
                Log.d("sss", "Update ==> " + e.toString());
            }
            intTimes += 1;
        }   //while

    }   // synUserTABLE

    private void deleteUser() {

        SQLiteDatabase objSqLiteDatabase = openOrCreateDatabase(MyOpenHelper.DATABASE_NAME,
                MODE_PRIVATE, null);
        objSqLiteDatabase.delete(ManageTABLE.TABLE_USER, null, null);


    }   // deleteUser

}   // Main Class
