package com.example.csespot

import android.database.Cursor
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.csespot.utils.DatabaseHelper
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main);
        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottom_nav_view)
        val frameMain = findViewById<FrameLayout>(R.id.main)
        val test = getSharedPreferences("test", MODE_PRIVATE)

        bottomNavView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menu_item_running -> {
                    // Respond to navigation item 1 click
                    loadFragment(RunningFragment())
                    Toast.makeText(this, "Running", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.menu_item_cycling -> {
                    // Respond to navigation item 2 click
                    Toast.makeText(this, "Cycling", Toast.LENGTH_SHORT).show()
                    loadFragment(CyclingFragment())
                    true

                }
                else -> false
            }
        }

        var db = DatabaseHelper(this)
        //val contacts = db.getAllContacts()
        db.insertContact("chiena", "abcdd")
        val cursor: Cursor = db.getAllContacts()
        displayContacts(cursor)
        cursor.close()
        db.close()

    }

    private fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.main, fragment)
        transaction.commit()
    }
    private fun displayContacts(cursor: Cursor?) {
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME))
                val phone = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PHONE))

                Log.d("Contact", "ID: $id, Name: $name, Phone: $phone")
            }
        }
    }
}

