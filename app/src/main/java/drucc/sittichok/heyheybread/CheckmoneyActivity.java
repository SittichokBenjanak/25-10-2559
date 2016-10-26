package drucc.sittichok.heyheybread;

import android.database.sqlite.SQLiteDatabase;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

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

public class CheckmoneyActivity extends AppCompatActivity {

    private String strID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkmoney);

        strID = getIntent().getStringExtra("ID");

        // deletesynUserTable
        deleteUser();

        // synUserTable
        synUserTABLE();



    }

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

    }   // synUserTABLE

    private void deleteUser() {

        SQLiteDatabase objSqLiteDatabase = openOrCreateDatabase(MyOpenHelper.DATABASE_NAME,
                MODE_PRIVATE, null);
        objSqLiteDatabase.delete(ManageTABLE.TABLE_USER, null, null);


    }   // deleteUser

}   // Main Class
