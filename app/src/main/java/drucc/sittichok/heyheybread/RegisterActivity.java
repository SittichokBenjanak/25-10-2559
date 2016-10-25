package drucc.sittichok.heyheybread;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity {
    //Explicit
    private EditText UserEditText,PasswordEditText,NameEditText,
    SurnameEditText,AddressEditText, PhonEditText;
    private String userString,passwordString, nameString,
    surnameString,addressString, phoneString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //Bind Widget
        bindWidget();
    }   // onCreate

    private boolean checkUser() {
        try {
            // Have This User in my Database ถ้ามี User ในฐานข้อมูล
            ManageTABLE objManageTABLE = new ManageTABLE(this);
            String[] resultStrings = objManageTABLE.searchUser(userString);
            Log.d("hey", "Name ==> " + resultStrings[3]);
            return true;
        } catch (Exception e) {
            //No This User in my Database
            return false;
        }
    }   // checkUser

    public void clickSave(View view) {
        //Check Space รับค่าที่ลูกค้ากรอกมาเช็คช่องว่าง ทุกอัน ที่ลูกค้า กรอกมา
        userString = UserEditText.getText().toString().trim(); //trim คือตัดช่องว่างทิ้ง
        passwordString = PasswordEditText.getText().toString().trim();
        nameString = NameEditText.getText().toString().trim();
        surnameString = SurnameEditText.getText().toString().trim();
        addressString = AddressEditText.getText().toString().trim();
        phoneString = PhonEditText.getText().toString().trim();

        if (userString.equals("") || // ถ้ามี ช่องไหนว่าง ให้ โชว์ข้อความว่า "มีช่องว่าง", "กรุณากรอกให้ครบทุกช่อง"
                passwordString.equals("") ||
                nameString.equals("") ||
                surnameString.equals("") ||   // equals อีคั่ว เหมือนเท่ากับ i="" ใช้เพราะเป็น String
                addressString.equals("") ||
                phoneString.equals("")) {
            //Have Space  ถ้ามีช่องว่างให้ทำ
            MyAlertDialog objMyAlertDialog = new MyAlertDialog();
            objMyAlertDialog.errorDialog(RegisterActivity.this, "มีช่องว่าง", "กรุณากรอกข้อมูลให้ครบทุกช่อง");
        } else {
            //No Space
            if (checkUser()) {
                MyAlertDialog objMyAlertDialog = new MyAlertDialog();
                objMyAlertDialog.errorDialog(RegisterActivity.this,"ชื่อผู้ใช้นี้มีคนใช้แล้ว","กรุณาเปลี่ยนชื่อผู้ใช้ใหม่");
            } else {
                confirmRegister();
            }
        } // if
    }   //clickSave

    private void confirmRegister() {
        // เมื่อกดบันทึก โชว์ กล่องข้อความ แบบ Builder
        AlertDialog.Builder objBuilder = new AlertDialog.Builder(this);
        objBuilder.setIcon(R.drawable.icon_myaccount);  // ตั้งค่า รูป
        objBuilder.setTitle("ตรวจสอบข้อมูลการสมัคร");  // หัวข้อ
        objBuilder.setMessage("ชื่อผู้ใช้ = " + userString + "\n" +  // ข้อความที่จะโชว์ ทั้ง หมด รับค่าจากที่ ลูกค้ากรอกมา
                "รหัสผ่าน = " + passwordString + "\n" +
                "ชื่อ = " + nameString + "\n" +
                "นามสกุล = " + surnameString + "\n" +
                "ที่อยู่ = " + addressString + "\n" +
                "เบอร์โทรศัพท์ = " + phoneString + "\n");
        objBuilder.setPositiveButton("ยืนยันการสมัคร", new DialogInterface.OnClickListener() {  // ถ้ากดตกลง ให้อัฟเดทเข้าฐานข้อมูล
            @Override
            public void onClick(DialogInterface dialog, int which) {
                upDateMySQL();
                dialog.dismiss();  // dialog.dismiss ให้ dialog หายไป
            }
        });
        objBuilder.setNegativeButton("ยกเลิกการสมัคร", new DialogInterface.OnClickListener() {  // ถ้ายกเลิก ให้ปิดข้อความลงเฉยๆ อยู่หน้าเดิม
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();  // dialog.dismiss ให้ dialog หายไป
            }
        });
        objBuilder.show();  //ให้ โชว์ กล่องข้อความ ที่ลูกค้ากรอกมา
    }   // confirmRegister
    private void upDateMySQL() {
        StrictMode.ThreadPolicy myPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(myPolicy);
        try {
            String strURL = "http://www.fourchokcodding.com/mos/php_add_data.php";
            ArrayList<NameValuePair> objNameValuePairs = new ArrayList<NameValuePair>();
            objNameValuePairs.add(new BasicNameValuePair("isAdd","true"));
            objNameValuePairs.add(new BasicNameValuePair(ManageTABLE.COLUMN_User,userString));
            objNameValuePairs.add(new BasicNameValuePair(ManageTABLE.COLUMN_Password, passwordString));
            objNameValuePairs.add(new BasicNameValuePair(ManageTABLE.COLUMN_Name, nameString));
            objNameValuePairs.add(new BasicNameValuePair(ManageTABLE.COLUMN_Surname, surnameString));
            objNameValuePairs.add(new BasicNameValuePair(ManageTABLE.COLUMN_Address, addressString));
            objNameValuePairs.add(new BasicNameValuePair(ManageTABLE.COLUMN_Phone, phoneString));
            objNameValuePairs.add(new BasicNameValuePair(ManageTABLE.COLUMN_Balance,"0"));

            HttpClient objHttpClient = new DefaultHttpClient(); //เปิด เซอวิท ให้ สามารถเรียกใช้ไฟล์บนเซิฟเวอร์ได้
            HttpPost objHttpPost = new HttpPost(strURL);
            objHttpPost.setEntity(new UrlEncodedFormEntity(objNameValuePairs,"UTF-8"));
            objHttpClient.execute(objHttpPost);

            Toast.makeText(RegisterActivity.this, "สมัครสมาชิกสำเร็จ", Toast.LENGTH_SHORT).show();
            // โชว์ ข้อความ ว่า บันทึกสำเร็จ แล้วหายไป 3.5วื
        } catch (Exception e) {
            Toast.makeText(RegisterActivity.this,"\n" + "สมัครสมาชิกไม่สำเร็จ", Toast.LENGTH_SHORT).show();
        }
        // Intent To MainActivity
        startActivity(new Intent(RegisterActivity.this,MainActivity.class));  // กลับไปหน้า Main หรือ หน้า Login
    }   // upDateMySQL

    private void bindWidget() {
        UserEditText = (EditText) findViewById(R.id.edtUser);
        PasswordEditText= (EditText) findViewById(R.id.edtPass);
        NameEditText = (EditText) findViewById(R.id.edtName);
        SurnameEditText = (EditText) findViewById(R.id.edtSurname);
        AddressEditText = (EditText) findViewById(R.id.edtAddress);
        PhonEditText = (EditText) findViewById(R.id.edtPhone);
    }
}   // Main class
