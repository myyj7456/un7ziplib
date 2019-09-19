package com.hzy.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import java.util.Map;

public class ProvinceManager {
    private static ProvinceManager instance;

    public SQLiteDatabase sqliteDB;
    public DatabaseDAO dao;

    private Context mContext;

    private ProvinceManager(Context context)
    {
        this.mContext = context;
    }

    public static synchronized ProvinceManager getInstance(Context context)
    {
        if (instance == null)
        {
            instance = new ProvinceManager(context.getApplicationContext());
        }
        return instance;

    }

    public void init() {
        AssetsDatabaseManager.initManager(mContext);
        AssetsDatabaseManager mg = AssetsDatabaseManager.getAssetsDatabaseManager();
        sqliteDB = mg.getDatabase("number_location.7z");
        dao = new DatabaseDAO(sqliteDB);
    }

    public String getProvince(String phoneNum) {
        String prefix, center;
        Map<String, String> map = null;

        if (isZeroStarted(phoneNum) && getNumLength(phoneNum) > 2) {
            prefix = getAreaCodePrefix(phoneNum);
            map = dao.queryAeraCode(prefix);

        } else if (!isZeroStarted(phoneNum) && getNumLength(phoneNum) > 6) {
            prefix = getMobilePrefix(phoneNum);
            center = getCenterNumber(phoneNum);
            map = dao.queryNumber(prefix, center);
        }

        if (map == null) {
                        setNotFoundText();
        } else {
            String province = map.get("province");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                if (province == null || province.isEmpty()) {
                    {
                              setNotFoundText();
                    }
                } else
                    return province;
            }
        }
        return null;
    }

    /**
     * 得到输入区号中的前三位数字或前四位数字去掉首位为零后的数字。
     */
    public String getAreaCodePrefix(String number) {
        if (number.charAt(1) == '1' || number.charAt(1) == '2')
            return number.substring(1, 3);
        return number.substring(1, 4);
    }

    /**
     * 得到输入手机号码的前三位数字。
     */
    public String getMobilePrefix(String number) {
        return number.substring(0, 3);
    }

    /**
     * 得到输入号码的中间四位号码，用来判断手机号码归属地。
     */
    public String getCenterNumber(String number) {
        return number.substring(3, 7);
    }


    /**
     * 判断号码是否以零开头
     */
    public boolean isZeroStarted(String number) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            if (number == null || number.isEmpty()) {
                return false;
            }
        }
        return number.charAt(0) == '0';
    }

    /**
     * 得到号码的长度
     */
    public int getNumLength(String number) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            if (number == null || number.isEmpty())
                return 0;
        }
        return number.length();
    }

    /**
     * 查询数据库中无匹配记录。
     */
    private static void setNotFoundText() {
        System.out.println("查询省份：数据库无匹配记录");
    }

}
