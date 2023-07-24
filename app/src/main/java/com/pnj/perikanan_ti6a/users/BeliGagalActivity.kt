package com.pnj.perikanan_ti6a.users

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.TextView
import com.google.firebase.firestore.*
import com.pnj.perikanan_ti6a.R
import com.pnj.perikanan_ti6a.ikan.Ikan
import com.pnj.perikanan_ti6a.ikan.IkanAdapter
import com.pnj.perikanan_ti6a.databinding.ActivityBeliGagalBinding
import java.util.Timer

class BeliGagalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBeliGagalBinding

    private lateinit var ikanArrayList: ArrayList<Ikan>
    private lateinit var ikanAdapter: IkanAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var timer: Timer


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBeliGagalBinding.inflate(layoutInflater)
        setContentView(binding.root)


        ikanArrayList = arrayListOf()
        ikanAdapter = IkanAdapter(ikanArrayList, "hasil_belanja")
        val intent = Intent(this, UsersMainActivity::class.java)
        val textView = findViewById<TextView>(R.id.TVCountdown)
        object : CountDownTimer(11000, 1000) {

            // Callback function, fired on regular interval
            override fun onTick(millisUntilFinished: Long) {
                val count = millisUntilFinished / 1000
                textView.setText("Beralih ke halaman utama dalam (" + count + ") detik")
            }

            // Callback function, fired
            // when the time is up
            override fun onFinish() {
                textView.setText("Beralih!")
                startActivity(intent)
            }
        }.start()

        // TextChangedListener

        binding.BtnCofirm.setOnClickListener {
            startActivity(intent)
        }


    }








}