package com.pnj.perikanan_ti6a.users

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.pnj.perikanan_ti6a.R
import com.pnj.perikanan_ti6a.databinding.ActivityDetilProdukKeranjangBinding
import com.pnj.perikanan_ti6a.ikan.Ikan
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.NumberFormat
import java.util.*
import java.time.LocalDateTime

class DetailIkanKeranjangActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetilProdukKeranjangBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var firebaseAuth: FirebaseAuth

    private var dataGambar: Bitmap? = null
    private val REQ_CAM = 101
    private lateinit var imgUri: Uri

    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        binding = ActivityDetilProdukKeranjangBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val (curr_ikan) = setDefaultValue()
        val intent = intent
        val harga_ikan = intent.getStringExtra("harga_ikan").toString()

        val editText = findViewById<EditText>(R.id.TxtJumlah)
        val textView = findViewById<TextView>(R.id.TVTotal)
        val time = intent.getStringExtra("time").toString()

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val teks = p0.toString()
                if (teks.isNullOrEmpty()) {
                    textView.setText("Rp 0")
                } else {
                    val total = harga_ikan.toInt() * teks.toInt()
                    textView.setText(format_duit(total))
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })


        binding.BtnBeli.setOnClickListener {
            if (binding.TxtJumlah.text.toString().isNullOrEmpty()) {
                Toast.makeText(this, "Kosong", Toast.LENGTH_SHORT).show()
            } else{
            val jumlahBeli = binding.TxtJumlah.text.toString().toInt()
            val stok_ikan = binding.TxtEditStok.text.toString().toInt()
            val nama_ikan = binding.TxtEditNama.text.toString()
            val harga_ikan = binding.TxtEditHarga.text.toString()
            val total_harga = (harga_ikan.toInt() * jumlahBeli).toString()


            if (jumlahBeli <= stok_ikan) {
                val email = firebaseAuth.currentUser!!.email.toString()
//            val updating =
//            updating.update("barusan", "0")
                val ids = ArrayList<String>()
                            CoroutineScope(Dispatchers.IO).launch {
                                val personQuery = db.collection("data_ikan")
                                    .whereEqualTo("nama_ikan", nama_ikan.toString())
                                    .whereEqualTo("stok_ikan", stok_ikan.toString())
                                    .get()
                                    .await()

                                for (document in personQuery) {
                                    ids.add(document.id)
                                }
//                Toast.makeText(this@BeliSuksesActivity, id, Toast.LENGTH_LONG).show()
                                for (id in ids) {
//                    if (db.collection("riwayat_belanja").document(id)!=null){
//                        Log.e("id", id)
//                    }
                                    val updating = db.collection("data_ikan").document(id)
                                    updating.update("stok_ikan", (stok_ikan-jumlahBeli).toString()).addOnSuccessListener {
                                        Toast.makeText(this@DetailIkanKeranjangActivity,"sukses update stok", Toast.LENGTH_LONG).show()

                                    }
                                }
                            }
                val ids2 = ArrayList<String>()
                CoroutineScope(Dispatchers.IO).launch {
                    val personQuery = db.collection("riwayat_belanja")
                        .whereEqualTo("time", time)
                        .get()
                        .await()

                    for (document in personQuery) {
                        ids2.add(document.id)
                    }
//                Toast.makeText(this@BeliSuksesActivity, id, Toast.LENGTH_LONG).show()
                    for (id in ids2) {
//                    if (db.collection("riwayat_belanja").document(id)!=null){
//                        Log.e("id", id)
//                    }
                        val updating = db.collection("riwayat_belanja").document(id)
                        updating.update("barusan", 0).addOnSuccessListener {
                            Toast.makeText(this@DetailIkanKeranjangActivity,"sukses update riwayat", Toast.LENGTH_LONG).show()

                        }
                    }
                }
                val intent = Intent(this, UsersMainActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "gagal", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, BeliGagalActivity::class.java)
                startActivity(intent)
            }

        }
    }

        showFoto()
    }

    private fun format_duit(number: Int): String{
        val localeId = Locale("IND", "ID")
        val numFormat = NumberFormat.getCurrencyInstance(localeId)
        val formatRupiah = numFormat.format(number).toString()
        return formatRupiah
    }

    fun setDefaultValue(): Array<Any> {
        val intent = intent
        val nama_ikan = intent.getStringExtra("nama_ikan").toString()
        val stok_ikan = intent.getStringExtra("stok_ikan").toString()
        val deskripsi_ikan = intent.getStringExtra("deskripsi_ikan").toString()
        val harga_ikan = intent.getStringExtra("harga_ikan").toString()
        val jumlah = intent.getStringExtra("jumlah").toString()
        val total = intent.getStringExtra("total").toString()

        binding.TxtEditDeskripsi.setText(deskripsi_ikan)
        binding.TxtEditNama.setText(nama_ikan)
        binding.TxtEditStok.setText(stok_ikan)
        binding.TxtEditHarga.setText(harga_ikan)
        binding.TxtJumlah.setText(jumlah)
        binding.TVTotal.setText(format_duit(total.toInt()).toString())
//        binding.TxtJumlah.setText("1")




        val curr_ikan = Ikan(nama_ikan, deskripsi_ikan, stok_ikan, harga_ikan)
        return arrayOf(curr_ikan)

    }

    fun newIkan(): RiwayatBelanja {
        var deskripsi_ikan : String = binding.TxtEditDeskripsi.text.toString()
        var nama_ikan : String = binding.TxtEditNama.text.toString()
        var stok_ikan : String = binding.TxtEditStok.text.toString()
        var harga_ikan : String = binding.TxtEditHarga.text.toString()
        var jumlah : String = binding.TxtJumlah.text.toString()
        val total_harga = (harga_ikan.toInt() * jumlah.toInt()).toString()


        if (dataGambar != null) {
            uploadPictFirebase(dataGambar!!, "foto_${nama_ikan}")

        }

        val ikan = RiwayatBelanja()
        ikan.nama_ikan = nama_ikan
        ikan.harga_ikan = harga_ikan
        ikan.jumlah = jumlah
        ikan.total = total_harga


        return ikan

    }

    private fun updateIkan(ikan: Ikan, newIkanMap: Map<String, Any>) =
        CoroutineScope(Dispatchers.IO).launch {
            val personQuery = db.collection("data_ikan")
                .whereEqualTo("nama_ikan", ikan.nama_ikan)
                .whereEqualTo("deskripsi_ikan", ikan.deskripsi_ikan)
                .whereEqualTo("stok_ikan", ikan.stok_ikan)
                .whereEqualTo("harga_ikan", ikan.harga_ikan)
                .get()
                .await()

            if (personQuery.documents.isNotEmpty()) {
                for (document in personQuery) {
                    try {
                        db.collection("data_ikan").document(document.id).set(
                            newIkanMap,
                            SetOptions.merge()
                        )
                    }
                    // Exception kotlin
                    catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@DetailIkanKeranjangActivity,
                                e.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
            else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@DetailIkanKeranjangActivity,
                        "No fishes matched the query.", Toast.LENGTH_LONG).show()
                }
            }
        }

    fun showFoto() {
        val intent = intent
        val nama_ikan =intent.getStringExtra("nama_ikan").toString()

        val storageRef = FirebaseStorage.getInstance().reference.child("foto_ikan/foto_${nama_ikan}.jpg")
        val localfile = File.createTempFile("tempImage", "jpg")
        storageRef.getFile(localfile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            binding.BtnImgIkan.setImageBitmap(bitmap)
        }.addOnFailureListener {
            Log.e("foto ?", "gagal")
        }
    }
    fun getFoto() {
        val intent = intent
        val nama_ikan =intent.getStringExtra("nama_ikan").toString()
        val storageRef = FirebaseStorage.getInstance().reference.child("foto_ikan/foto_${nama_ikan}.jpg")
        val localfile = File.createTempFile("tempImage", "jpg")
        storageRef.getFile(localfile).addOnSuccessListener {
            var bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
//            Toast.makeText(this, "get foto sukses", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Log.e("foto ?", "gagal")
        }

    }

    private fun openCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            this.packageManager?.let {
                intent?.resolveActivity(it).also {
                    startActivityForResult(intent, REQ_CAM)
                }
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_CAM && resultCode == RESULT_OK) {
            dataGambar = data?.extras?.get("data") as Bitmap
            binding.BtnImgIkan.setImageBitmap(dataGambar)
        }
    }

    private fun uploadPictFirebase(img_bitmap: Bitmap, file_name: String) {
        val baos = ByteArrayOutputStream()
        val ref = FirebaseStorage.getInstance().reference.child("foto_ikan_riwayat/${file_name}.jpg")
        img_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)

        val img = baos.toByteArray()
        ref.putBytes(img)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    ref.downloadUrl.addOnCompleteListener { Task ->
                        Task.result.let { Uri ->
                            imgUri = Uri
                            binding.BtnImgIkan.setImageBitmap(img_bitmap)
                        }
                    }
                }
            }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun addIkan() {
        var nama_ikan: String = binding.TxtEditNama.text.toString()
        var stok: String = binding.TxtEditStok.text.toString()
        var harga: String = binding.TxtEditHarga.text.toString()
        var jumlah: String = binding.TxtJumlah.text.toString()
        var total: String = (harga.toInt() * jumlah.toInt()).toString()
        val currentDT = LocalDateTime.now()

        firebaseAuth = FirebaseAuth.getInstance()
        val ikan: MutableMap<String, Any> = HashMap()
        ikan["nama_ikan"] = nama_ikan
        ikan["harga_ikan"] = harga
        ikan["jumlah"] = jumlah
        ikan["total"] = total
        ikan["user_id"] = firebaseAuth.currentUser!!.email.toString()
        ikan["time"] = currentDT.toString()
        ikan["barusan"] = "1"
        val storageRef = FirebaseStorage.getInstance().reference.child("img_ikan/foto_${nama_ikan}.jpg")
        val localfile = File.createTempFile("tempImage", "jpg")
        val updating = db.collection("data_ikan").document(nama_ikan)
        storageRef.getFile(localfile).addOnSuccessListener {
            var bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            dataGambar = bitmap
            if (dataGambar != null) {
                uploadPictFirebase(bitmap, "foto_ikan_riwayat_${nama_ikan}_${ikan["user_id"]}_${ikan["time"]}")

                val addOnSuccessListener = db.collection("riwayat_belanja").add(ikan)
                    .addOnSuccessListener {
                        updating.update("stok_ikan",(stok.toInt()-jumlah.toInt()).toString())
//                    val intentMain = Intent(this, MainActivity::class.java)
//                    startActivity(intentMain)
                    }

//                val personQuery = db.collection("riwayat_belanja")
//                    .whereEqualTo("barusan", "1")
//                    .get()
//                val id = personQuery.documents.id
//            db.collection("data_ikan").document(nama_ikan).set(ikan)
//                .addOnSuccessListener {
//                    val intentMain = Intent(this, MainActivity::class.java)
//                    startActivity(intentMain)
//                }
            }
//            Toast.makeText(this, "get foto sukses", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Log.e("foto ?", "gagal")
        }

    }



}
