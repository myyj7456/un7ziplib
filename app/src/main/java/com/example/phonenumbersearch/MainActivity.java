package com.example.phonenumbersearch;

import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.phonenumbersearch.utils.AssetsDatabaseManager;
import com.example.phonenumbersearch.utils.DatabaseDAO;
import com.example.phonenumbersearch.utils.DialogUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public EditText mNumberEditText;
    public Button mSearchButton;
    public TextView mTextView;
    public SQLiteDatabase sqliteDB;
    public DatabaseDAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNumberEditText = findViewById(R.id.id_nummber_et);
        mNumberEditText.setInputType( InputType.TYPE_CLASS_NUMBER);
        mSearchButton = findViewById(R.id.id_search_bt);
        mTextView = findViewById(R.id.id_tv);
        mSearchButton.setOnClickListener(this);
        initDB();
    }

    private void initDB() {
        AssetsDatabaseManager.initManager(this.getApplicationContext());
        AssetsDatabaseManager mg = AssetsDatabaseManager.getAssetsDatabaseManager();
        sqliteDB = mg.getDatabase("number_location.7z");
        dao = new DatabaseDAO(sqliteDB);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        AssetsDatabaseManager.closeAllDatabase();
    }

    @Override
    public void onClick(View view) {
        String phoneNumber = mNumberEditText.getText().toString();
        String prefix, center;
        Map<String,String> map = null;

        if (isZeroStarted(phoneNumber) && getNumLength(phoneNumber) > 2){
            Log.d("caojingqi","phoneNumber= " + phoneNumber);
            prefix = getAreaCodePrefix(phoneNumber);
            map = dao.queryAeraCode(prefix);

        }else if (!isZeroStarted(phoneNumber) && getNumLength(phoneNumber) > 6){
            Log.d("caojingqi","phoneNumber000= " + phoneNumber);
            prefix = getMobilePrefix(phoneNumber);
            center = getCenterNumber(phoneNumber);
            map = dao.queryNumber(prefix, center);
        }

        DialogUtils.showNumberDialog(this, view, map, phoneNumber);
    }

    /**得到输入区号中的前三位数字或前四位数字去掉首位为零后的数字。*/
    public String getAreaCodePrefix(String number){
        if (number.charAt(1) == '1' || number.charAt(1) == '2')
            return number.substring(1,3);
        return number.substring(1,4);
    }

    /**得到输入手机号码的前三位数字。*/
    public String getMobilePrefix(String number){
        return number.substring(0,3);
    }

    /**得到输入号码的中间四位号码，用来判断手机号码归属地。*/
    public String getCenterNumber(String number){
        return number.substring(3,7);
    }

    /**判断号码是否以零开头*/
    public boolean isZeroStarted(String number){
        if (number == null || number.isEmpty()){
            return false;
        }
        return number.charAt(0) == '0';
    }

    /**得到号码的长度*/
    public int getNumLength(String number){
        if (number == null || number.isEmpty()  )
            return 0;
        return number.length();
    }
}
