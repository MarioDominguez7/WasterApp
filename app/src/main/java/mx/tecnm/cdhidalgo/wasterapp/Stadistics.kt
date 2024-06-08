package mx.tecnm.cdhidalgo.wasterapp

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Stadistics : AppCompatActivity() {

    lateinit var btnStadisticsButton: ImageButton
    lateinit var btnLogOut: ImageButton

    lateinit var sesion: SharedPreferences

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_stadistics)

        btnStadisticsButton = findViewById(R.id.btnStadisticsButton)
        btnLogOut = findViewById(R.id.btnStadisticsLogOut)
        sesion = getSharedPreferences("sesion", 0)

        if(sesion.getString("jwt", "").toString()== ""){
            startActivity(Intent(this, Login::class.java))
            finish()
        }

        btnStadisticsButton.setOnClickListener { startActivity(Intent(this, HomeOff::class.java)) }
        btnLogOut.setOnClickListener { startActivity(Intent(this, LogOut::class.java)) }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}