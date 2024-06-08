package mx.tecnm.cdhidalgo.wasterapp

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.google.android.material.textfield.TextInputEditText

class Login : AppCompatActivity() {

    lateinit var btnLogin:Button
    lateinit var btnLoginRegistrarse:Button
    lateinit var etLoginCorreo:TextInputEditText
    lateinit var etLoginPassword:TextInputEditText
    lateinit var sesion: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //Match con las variables
        btnLogin = findViewById(R.id.btnLoginLogin)
        btnLoginRegistrarse = findViewById(R.id.btnLoginRegistrarse)
        etLoginCorreo = findViewById(R.id.etLoginCorreo)
        etLoginPassword = findViewById(R.id.etLoginPassword)
        sesion = getSharedPreferences("sesion", 0)

        btnLoginRegistrarse.setOnClickListener {
            startActivity(Intent(this, SignIn::class.java))
        }

        btnLogin.setOnClickListener {
            login()
        }
    }

    private fun login() {
        val url = Uri.parse(Config.URL + "login")
            .buildUpon()
            .build().toString()

        val peticion = object:StringRequest(Request.Method.POST, url, {
                response -> with(sesion.edit()){
            putString("jwt", response)
            putString("email", etLoginCorreo.text.toString())
            apply()
        }
            startActivity(Intent(this, HomeOff::class.java))

        }, {
                error -> Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()
        }){
            override fun getParams(): Map<String, String>{
                val body: MutableMap<String, String> = HashMap()
                body["email"] = etLoginCorreo.text.toString()
                body.put("password", etLoginPassword.text.toString())
                return body
            }
        }
        MySingleton.getInstance(applicationContext).addToRequestQueue(peticion)
    }
}
