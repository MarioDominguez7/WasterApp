package mx.tecnm.cdhidalgo.wasterapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import mx.tecnm.cdhidalgo.wasterapp.databinding.ActivityLogOutBinding

class LogOut : AppCompatActivity() {

    private lateinit var binding: ActivityLogOutBinding
    lateinit var btnLogOutButton: ImageButton
    lateinit var btnLogOutStadistics: ImageButton
    lateinit var btnLogOutLogOut: Button
    lateinit var etLogOutName: TextInputEditText


    lateinit var sesion: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_out)
        /* enableEdgeToEdge()
        binding = ActivityLogOutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Cargar el GIF usando Glide
        Glide.with(this)
            .asGif()
            .load(R.drawable.saludando)
            .into(binding.ivLogOut)*/

        btnLogOutButton = findViewById(R.id.btnLogOutHomeOff)
        btnLogOutStadistics = findViewById(R.id.btnLogOutStadistics)
        btnLogOutLogOut = findViewById(R.id.btnLogOutLogOut)
        etLogOutName = findViewById(R.id.etLogOutName)
        sesion = getSharedPreferences("sesion", 0)

        etLogOutName.setText(sesion.getString("email","0"))

        if(sesion.getString("jwt", "").toString()== ""){
            startActivity(Intent(this, Login::class.java))
            finish()
        }

        btnLogOutStadistics.setOnClickListener { startActivity(Intent(this, Stadistics::class.java)) }
        btnLogOutButton.setOnClickListener { startActivity(Intent(this, HomeOff::class.java)) }

        btnLogOutLogOut.setOnClickListener {
            with(sesion.edit()) {
                putString("jwt", "")
                apply()
            }
            startActivity(Intent(this, Login::class.java))
            finish()
        }

        /*ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/
    }
}
