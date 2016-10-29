package drucc.sittichok.heyheybread;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

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

public class MainActivity extends AppCompatActivity {
    //Explicit
    private ManageTABLE objManageTABLE;
    private EditText userEditText , passwordEditText;
    private String userString  , passwordString;
    public String TAG = "hey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Bind Widget
        bindWidget();

        //Connected Database ทำให้สามารถเรียกใช้ เมดตอด ที่ อยู่ ใน ManageTABLE ได้
        objManageTABLE = new ManageTABLE(this);

        //Delete All SQLite
        deleteAllSQLite();

        //Synchronize JSON to SQLite
        synJSONtoSQLite();
    }   // OnCreate

    public void onBackPressed() {
        android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(this);
        dialog.setIcon(R.drawable.icon_question);
        dialog.setCancelable(true);
        dialog.setMessage("คุณต้องการปิดแอพพลิเคชัน?");
        dialog.setPositiveButton("ใช่", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        dialog.setNegativeButton("ไม่", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialog.show();
    }

    private void bindWidget() {
        userEditText = (EditText) findViewById(R.id.editText);
        passwordEditText = (EditText) findViewById(R.id.editText2);
    }   // bindWidget

    public void clickLogin(View view) {
        // Check Space เช็คว่า ถ้า ช่องที่กรอกข้อมูล อันใด อันหนึ่งว่าง ให้ โชว์ ข้อความ  "มีช่องว่าง","กรุณากรอกให้ครบ" ที่หน้า MainActivity.this
        userString = userEditText.getText().toString().trim(); // รับค่าเป็น text แปลงเป็น String ,trim ตัดช่องว่าง
        passwordString = passwordEditText.getText().toString().trim();
        if (userString.equals("") || passwordString.equals("")) {  //อีคั่ว
            //มีช่องว่าง
            MyAlertDialog objMyAlertDialog = new MyAlertDialog();
            objMyAlertDialog.errorDialog(MainActivity.this,"มีช่องว่าง","กรุณากรอกข้อมูลให้ครบ");
        } else {
            //ไม่มีช่องว่าง
            checkUser();
        }
    }   // clickLogin

    private void checkUser() {
        try {
            String[] resultStrings = objManageTABLE.searchUser(userString);  //userString คือ ค่าที่รับมาจากลูกค้ากรอก
            if (passwordString.equals(resultStrings[2])) {
                // equals คือ = เปรียบเทียบ PasswordString ที่ลูกค้ากรอกมา ถ้า ตรงกับ Pass ที่อยู่ในฐานข้อมูล
                Intent objIntent = new Intent(MainActivity.this, HubActivity.class);
                objIntent.putExtra("ID", resultStrings[0]);
                startActivity(objIntent);
                finish();
            } else {
                MyAlertDialog objMyAlertDialog = new MyAlertDialog();
                objMyAlertDialog.errorDialog(MainActivity.this,"รหัสผ่านผิด","กรุณากรอกรหัสผ่านใหม่");
            }
        } catch (Exception e) {
            MyAlertDialog objMyAlertDialog = new MyAlertDialog();
            objMyAlertDialog.errorDialog(MainActivity.this,"ชื่อผู้ใช้ไม่ถูกต้อง","ไม่มี "+ userString + " ในฐานข้อมูล" );
        }
    }   // checkUser

    public void synJSONtoSQLite() {
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
                Log.d(TAG, "InputStream ==> " + e.toString());
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
                Log.d(TAG, "strJSON ==> " + e.toString());
            }

            //3. Update JSON String to SQLite
            try {
                JSONArray objJsonArray = new JSONArray(strJSON);
                for (int i=0; i<objJsonArray.length();i++) {
                    JSONObject object = objJsonArray.getJSONObject(i);
                    switch (intTimes) {
                        case 1: // userTABLE
                            String strID7 = object.getString("id");
                            String strUser = object.getString(ManageTABLE.COLUMN_User);
                            String strPassword = object.getString(ManageTABLE.COLUMN_Password);
                            String strName = object.getString(ManageTABLE.COLUMN_Name);
                            String strSurname = object.getString(ManageTABLE.COLUMN_Surname);
                            String strAddress = object.getString(ManageTABLE.COLUMN_Address);
                            String strPhone = object.getString(ManageTABLE.COLUMN_Phone);
                            String strBalance = object.getString(ManageTABLE.COLUMN_Balance);
                            objManageTABLE.addNewUser(strID7, strUser, strPassword, strName, strSurname,
                                    strAddress, strPhone, strBalance);
                            break;

                    }   //switch
                }
            } catch (Exception e) {
                Log.d(TAG, "Update ==> " + e.toString());
            }
            intTimes += 1;
        }   //while
    }   // synJSONtoSQLite

    public void clickNewRegister(View view) {
        startActivity(new Intent(MainActivity.this,RegisterActivity.class));
        finish();
    }   // clickNewRegister

    private void deleteAllSQLite() {
        SQLiteDatabase objSqLiteDatabase = openOrCreateDatabase(MyOpenHelper.DATABASE_NAME,
                MODE_PRIVATE, null); // MODE_PRIVATE คือ ลบข้อมูลในตาราง แต่ไม่ลบตารางออก
        objSqLiteDatabase.delete(ManageTABLE.TABLE_USER, null, null);

    }   // deleteAllSQLite

}   // Main class
