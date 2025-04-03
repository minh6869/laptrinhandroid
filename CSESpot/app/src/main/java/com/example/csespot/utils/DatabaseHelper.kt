package com.example.csespot.utils

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.csespot.DBHelper
import android.database.Cursor

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    // Companion object để chứa các hằng số (constants)
    companion object {
        // Thông tin cơ sở dữ liệu
        private const val DATABASE_NAME = "TLUContact.db"  // Tên file cơ sở dữ liệu
        private const val DATABASE_VERSION = 1          // Phiên bản cơ sở dữ liệu

        // Tên bảng và các cột
        const val TABLE_NAME = "Contact"
        const  val COLUMN_ID = "_id"  // Cột khóa chính, nên bắt đầu bằng "_"
        const val COLUMN_NAME = "name"
        const val COLUMN_PHONE = "phone"

        // Câu lệnh SQL tạo bảng
        private const val SQL_CREATE_ENTRIES =
            "CREATE TABLE $TABLE_NAME (" +
                    "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," + // Khóa chính, tự động tăng
                    "$COLUMN_NAME TEXT," +
                    "$COLUMN_PHONE TEXT)"

        // Câu lệnh SQL xóa bảng
        private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS $TABLE_NAME"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Được gọi khi cơ sở dữ liệu được tạo lần đầu tiên
        db.execSQL(SQL_CREATE_ENTRIES)  // Thực thi câu lệnh tạo bảng
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Được gọi khi phiên bản cơ sở dữ liệu thay đổi
        // Xử lý việc nâng cấp (ví dụ: thêm cột, thay đổi cấu trúc bảng)
        // Thường thì bạn sẽ xóa bảng cũ và tạo lại
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    // Có thể thêm onDowngrade nếu cần thiết, nhưng ít khi dùng.
    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }
    fun insertContact(name: String, phone: String): Long {
        val db = writableDatabase // Lấy database để ghi

        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_PHONE, phone)
        }

        val newRowId = db.insert(TABLE_NAME, null, values) // Thêm dữ liệu
        db.close() // Đóng kết nối database.  Luôn đóng sau khi thao tác.
        return newRowId // Trả về ID của dòng mới được thêm (hoặc -1 nếu lỗi)
    }

    public fun getAllContacts(): Cursor {
        var db: SQLiteDatabase = getReadableDatabase(); // Lấy database để đọc
        var projection = arrayOf(  // Các cột cần lấy
                COLUMN_ID,
                COLUMN_NAME,
                COLUMN_PHONE
        )
        val cursor = db.query(TABLE_NAME, projection, null, null, null, null, null)
        return cursor;

    }


}