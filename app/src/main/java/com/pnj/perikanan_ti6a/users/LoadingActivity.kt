package com.pnj.perikanan_ti6a.users

import android.R
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pnj.perikanan_ti6a.auth.SignInActivity
import com.pnj.perikanan_ti6a.databinding.ActivityAwalBinding
import com.pnj.perikanan_ti6a.ikan.Ikan
import com.pnj.perikanan_ti6a.ikan.IkanAdapter

class LoadingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAwalBinding

    private lateinit var ikanArrayList: ArrayList<Ikan>
    private lateinit var ikanAdapter: IkanAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAwalBinding.inflate(layoutInflater)
        setContentView(binding.root)


        ikanArrayList = arrayListOf()
        ikanAdapter = IkanAdapter(ikanArrayList, "hasil_belanja")
        val intent = Intent(this, SignInActivity::class.java)
        binding.IVLoad.alpha = 0f
        binding.IVLoad.animate().setDuration(3000).alpha(1f).withEndAction{
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        // TextChangedListener



    }








}