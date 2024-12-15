package com.farhan.readsms

import android.app.ListActivity
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.CursorAdapter
import android.widget.TextView
import android.Manifest
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.farhan.readsms.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

//For best practice use recyclerView
//ListActivity untuk tunjukkan SMS di dalam SMS
class MainActivity : ListActivity() {

    private lateinit var binding: ActivityMainBinding

    //Content provider to open SMS
    val SMS = Uri.parse("content://sms")
    val PERMISSION_REQUEST_READ_SMS = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
        if (permissionCheck == PackageManager.PERMISSION_GRANTED)
        else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_SMS),
                PERMISSIONS_REQUEST_READ_SMS
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode){
            PERMISSIONS_REQUEST_READ_SMS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    readSMS()

                } else {
                    Snackbar.make(binding.root, "Permission not granted", Snackbar.LENGTH_LONG)
                        .show()
                }
                return
            }
        }
    }

//Column di dalma table yg akan di akses melalui content provider
    object SmsColumns {
        val ID = "_id"
        val ADDRESS = "address"
        val DATE = "date"
        val BODY = "body"
    }

    private inner class SmsCursorAdapter(context: Context, c: Cursor, autorequery: Boolean) :
        CursorAdapter(context, c, autorequery) {
        override fun newView(context: Context?, cursor: Cursor?, parent: ViewGroup?): View {
            return View.inflate(context, R.layout.custom_row, null)
        }

        override fun bindView(view: View?, context: Context?, cursor: Cursor?) {
            view!!.findViewById<TextView>(R.id.sms_origin).text =
                cursor!!.getString(cursor.getColumnIndexOrThrow(SmsColumns.ADDRESS))
            view!!.findViewById<TextView>(R.id.sms_body).text =
                cursor!!.getString(cursor.getColumnIndexOrThrow(SmsColumns.BODY))
            view!!.findViewById<TextView>(R.id.sms_date).text =
                cursor!!.getString(cursor.getColumnIndexOrThrow(SmsColumns.DATE))
        }
    }

    private fun readSMS() {
        //Baca database date content provider
        //Retrieve the SMS DB, specify the required column and order by date desce
        val cursor = contentResolver.query(
            SMS, arrayOf(
                SmsColumns.ID, SmsColumns.ADDRESS,
                SmsColumns.DATE, SmsColumns.BODY
            ), null, null, SmsColumns.DATE + " DESC"
        )
        val adapter = SmsCursorAdapter(this, cursor!!, true)
        listAdapter = adapter
    }
    }