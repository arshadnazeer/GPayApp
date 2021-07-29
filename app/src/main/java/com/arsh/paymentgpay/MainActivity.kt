package com.arsh.paymentgpay

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.arsh.paymentgpay.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var amount : String
    lateinit var uri : Uri
    val GOOGLE_PAY_PACKAGE_NAME = "com.google.android.apps.nbu.paisa.user"
    val GOOGLE_REQUEST_CODE = 123


    var name : String = "Arsh"
    var upiID : String = "nazeer.arsh-1@okicici"
    var transactionNote : String = "Test Pay"
    lateinit var status : String


    private fun isAppInstalled(context: Context, packageName: String): Boolean {
        return try {
            context.packageManager.getApplicationInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    private fun getUPIPaymentUri(
        name: String,
        upiId: String,
        transactionNote: String,
        amount: String
    ): Uri? {
        return Uri.Builder()
            .scheme("upi")
            .authority("pay")
            .appendQueryParameter("pa", upiId)
            .appendQueryParameter("pn", name)
            .appendQueryParameter("tn", transactionNote)
            .appendQueryParameter("am", amount)
            .appendQueryParameter("cu", "INR")
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.gpayButton.setOnClickListener(View.OnClickListener {
            amount = binding.gpayButton.getText().toString()
            if (amount.isNotEmpty()){
                uri = getUPIPaymentUri(name,upiID,transactionNote,amount)!!
                payWithGPay()
            } else{
                binding.etAmount.setError("Anount is required")
                binding.etAmount.requestFocus()
            }
        })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            status = data.getStringExtra("Status")!!.lowercase()
        }
        if (RESULT_OK == resultCode && status == "success") {
            Toast.makeText(this@MainActivity, "Transaction Successful", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this@MainActivity, "Transaction Failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun payWithGPay() {
        if (isAppInstalled(this,GOOGLE_PAY_PACKAGE_NAME)){
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = uri
            intent.setPackage(GOOGLE_PAY_PACKAGE_NAME)
            startActivityForResult(intent, GOOGLE_REQUEST_CODE)
        } else {
            Toast.makeText(this, "Please Install GPay", Toast.LENGTH_SHORT).show()
        }
    }

}