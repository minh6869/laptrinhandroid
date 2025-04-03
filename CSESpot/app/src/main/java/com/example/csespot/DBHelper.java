package com.example.csespot;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    // Thông tin cơ sở dữ liệu
    private static final String DATABASE_NAME = "Contact.db";  // Tên file cơ sở dữ liệu
    private static final int DATABASE_VERSION = 1;          // Phiên bản cơ sở dữ liệu

    // Tên bảng và các cột
    public static final String TABLE_NAME = "contacts";
    public static final String COLUMN_ID = "_id";  // Cột khóa chính, nên bắt đầu bằng "_"
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PHONE = "phone";

    // Câu lệnh SQL tạo bảng
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + // Khóa chính, tự động tăng
                    COLUMN_NAME + " TEXT," +
                    COLUMN_PHONE + " TEXT)";

    // Câu lệnh SQL xóa bảng
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Được gọi khi cơ sở dữ liệu được tạo lần đầu tiên
        db.execSQL(SQL_CREATE_ENTRIES);  // Thực thi câu lệnh tạo bảng
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Được gọi khi phiên bản cơ sở dữ liệu thay đổi
        // Xử lý việc nâng cấp (ví dụ: thêm cột, thay đổi cấu trúc bảng)
        // Thường thì bạn sẽ xóa bảng cũ và tạo lại
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    // Có thể thêm onDowngrade nếu cần thiết, nhưng ít khi dùng.
}