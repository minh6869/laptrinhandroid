package com.example.csespot;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ContactDAO { // Data Access Object

    private DBHelper dbHelper;

    public ContactDAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    // Thêm một contact mới
    public long insertContact(String name, String phone) {
        SQLiteDatabase db = dbHelper.getWritableDatabase(); // Lấy database để ghi

        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_NAME, name);
        values.put(DBHelper.COLUMN_PHONE, phone);

        long newRowId = db.insert(DBHelper.TABLE_NAME, null, values); // Thêm dữ liệu
        db.close(); // Đóng kết nối database.  Luôn đóng sau khi thao tác.
        return newRowId; // Trả về ID của dòng mới được thêm (hoặc -1 nếu lỗi)
    }

    // Đọc tất cả các contact
    public Cursor getAllContacts() {
        SQLiteDatabase db = dbHelper.getReadableDatabase(); // Lấy database để đọc

        String[] projection = {  // Các cột cần lấy
                DBHelper.COLUMN_ID,
                DBHelper.COLUMN_NAME,
                DBHelper.COLUMN_PHONE
        };

        // Sắp xếp kết quả (tùy chọn)
        String sortOrder = DBHelper.COLUMN_NAME + " ASC"; // Sắp xếp theo tên tăng dần

        Cursor cursor = db.query(
                DBHelper.TABLE_NAME,   // Tên bảng
                projection,             // Các cột cần lấy
                null,                   // Điều kiện WHERE (null = lấy tất cả)
                null,                   // Giá trị cho điều kiện WHERE
                null,                   // GROUP BY (không nhóm)
                null,                   // HAVING (không có)
                sortOrder               // Sắp xếp
        );

        // Không đóng database ở đây vì Cursor cần nó mở. Sẽ đóng trong Activity.
        return cursor;
    }


    // Cập nhật thông tin một contact
    public int updateContact(int id, String newName, String newPhone) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_NAME, newName);
        values.put(DBHelper.COLUMN_PHONE, newPhone);

        String selection = DBHelper.COLUMN_ID + " = ?"; // Điều kiện WHERE
        String[] selectionArgs = { String.valueOf(id) };   // Giá trị cho điều kiện

        int count = db.update(
                DBHelper.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );
        db.close();
        return count; // Trả về số dòng bị ảnh hưởng
    }

    // Xóa một contact
    public int deleteContact(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String selection = DBHelper.COLUMN_ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };

        int deletedRows = db.delete(DBHelper.TABLE_NAME, selection, selectionArgs);
        db.close();
        return deletedRows; // Trả về số dòng bị xóa
    }

    // Tìm kiếm theo tên
    public Cursor searchContacts(String query) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {DBHelper.COLUMN_ID, DBHelper.COLUMN_NAME, DBHelper.COLUMN_PHONE};

        // Sử dụng LIKE để tìm kiếm gần đúng
        String selection = DBHelper.COLUMN_NAME + " LIKE ?";
        String[] selectionArgs = { "%" + query + "%" }; // Dấu % đại diện cho bất kỳ ký tự nào

        Cursor cursor = db.query(
                DBHelper.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        return cursor;
    }
    //Có thể tạo các hàm query, insert, delete, update khác, tuỳ theo logic ứng dụng.
}
