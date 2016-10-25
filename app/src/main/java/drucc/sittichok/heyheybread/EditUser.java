package drucc.sittichok.heyheybread;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class EditUser extends AppCompatActivity {
    // Explicit


    private EditText passwordEditText,
            nameEditText,surnameEditText,addressEditText,
            phoneEditText;
    private TextView userTextView;
    private String strID;
    private String passwordString,nameString,surnameString,addressString, phoneString;
    private static final String urlSTRING = "http://www.fourchokcodding.com/mos/php_edit_user.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        deleteSynUser();

        synEditUserTABLE();

        // Bind Widget
        bindWidget();

        //Show View
        showView();
    } // Main Method

    private void deleteSynUser() {
        SQLiteDatabase objSqLiteDatabase = openOrCreateDatabase(MyOpenHelper.DATABASE_NAME,
                MODE_PRIVATE, null);
        objSqLiteDatabase.delete(ManageTABLE.TABLE_USER, null, null);

    }   // deleteSynUser

    private void synEditUserTABLE() {
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


    private void showView() {
        strID = getIntent().getStringExtra("ID");
        SQLiteDatabase sqLiteDatabase = openOrCreateDatabase(MyOpenHelper.DATABASE_NAME,
                MODE_PRIVATE,null);
        Cursor cursor = sqLiteDatabase
                .rawQuery("SELECT * FROM userTABLE WHERE _id = " + "'"+strID +"'", null);
        cursor.moveToFirst();
        String[] resultStrings = new String[cursor.getColumnCount()];
        for (int i=0; i<cursor.getColumnCount(); i++) {
            resultStrings[i] = cursor.getString(i);
        }   //for
        cursor.close();

        userTextView.setText(resultStrings[1]);
        passwordEditText.setText(resultStrings[2]);
        nameEditText.setText(resultStrings[3]);
        surnameEditText.setText(resultStrings[4]);
        addressEditText.setText(resultStrings[5]);
        phoneEditText.setText(resultStrings[6]);
    }   // showView

    public void clickSaveEdit(View view) {
        passwordString = passwordEditText.getText().toString().trim();
        nameString = nameEditText.getText().toString().trim();
        surnameString = surnameEditText.getText().toString().trim();
        addressString = addressEditText.getText().toString().trim();
        phoneString = phoneEditText.getText().toString().trim();

        // Check Space
        if (checkSpace()) {
            // ถ้ามีช่องว่าง
            MyAlertDialog myAlertDialog = new MyAlertDialog();
            myAlertDialog.errorDialog(this, "มีช่องว่าง","กรุณากรอกให้ครบทุกช่อง");
        }  else {
            // OK  On space
            updateValueToServer();

        }
    } // clickSaveEdit

    private void updateValueToServer() {
        OkHttpClient okHttpClient = new OkHttpClient();
        final RequestBody requestBody = new FormEncodingBuilder()
                .add("isAdd", "true")
                .add("id", strID)
                .add("Password", passwordString)
                .add("Name", nameString)
                .add("Surname", surnameString)
                .add("Address", addressString)
                .add("Phone", phoneString)
                .build();
        Request.Builder builder = new Request.Builder();
        final Request request = builder.url(urlSTRING).post(requestBody).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
            }
            @Override
            public void onResponse(Response response) throws IOException {
                try {
                    if (response.isSuccessful()) {
                        Toast.makeText(EditUser.this,"แก้ไขข้อมูลสำเร็จ",Toast.LENGTH_SHORT).show();
                        finish();

                    } else {
                        Toast.makeText(EditUser.this,"แก้ไขข้อมูลล้มเหลว",Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        finish();
        Toast.makeText(EditUser.this,"แก้ไขข้อมูลสำเร็จ",Toast.LENGTH_SHORT).show();
    }   // updateValueToServer

    private boolean checkSpace() {
        return
                passwordString.equals("") ||
                nameString.equals("") ||
                surnameString.equals("") ||
                addressString.equals("") ||
                phoneString.equals("");
    }   // checkSpace

    public void clickCancelEdit(View view) {
        finish();
    } // clickCancelEdit

    private void bindWidget() {
        userTextView = (TextView) findViewById(R.id.editText3);
        passwordEditText = (EditText) findViewById(R.id.editText4);
        nameEditText = (EditText) findViewById(R.id.editText5);
        surnameEditText = (EditText) findViewById(R.id.editText6);
        addressEditText = (EditText) findViewById(R.id.editText7);
        phoneEditText = (EditText) findViewById(R.id.editText8);
    }
}   // Main Class
