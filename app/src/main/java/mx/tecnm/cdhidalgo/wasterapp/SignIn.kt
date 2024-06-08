package mx.tecnm.cdhidalgo.wasterapp

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject

class SignIn : AppCompatActivity() {

    lateinit var btnRegistrarse:Button
    lateinit var btnSignInLogin:Button
    lateinit var etSignInName: TextInputEditText
    lateinit var etSignInEmail: TextInputEditText
    lateinit var etSignInPassword: TextInputEditText
    lateinit var etSignInCompPassword: TextInputEditText

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        //Match con las variables
        btnRegistrarse = findViewById(R.id.btnSignInSignIn)
        btnSignInLogin = findViewById(R.id.btnSignInLogin)
        etSignInName = findViewById(R.id.etSignInName)
        etSignInEmail = findViewById(R.id.etSignInEmail)
        etSignInPassword = findViewById(R.id.etSignInPassword)
        etSignInCompPassword = findViewById(R.id.etSignInCompPassword)

        btnSignInLogin.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
        }

        btnRegistrarse.setOnClickListener { register() }
    }

    private fun register(){
        val url = Uri.parse(Config.URL + "register")
            .buildUpon()
            .build().toString()

        val body = JSONObject()
        body.put("username",etSignInName.text.toString())
        body.put("email",etSignInEmail.text.toString())
        body.put("password",etSignInPassword.text.toString())

        if(etSignInPassword.text.toString() == etSignInCompPassword.text.toString()) {

            val peticion = object : JsonObjectRequest(Request.Method.POST, url, body, { response ->
                Toast.makeText(this, "Usuario Creado", Toast.LENGTH_LONG).show()
                finish()
            }, { error ->
                Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()
            }) {
                override fun getHeaders(): Map<String, String> {
                    val body: MutableMap<String, String> = HashMap()
                    return body
                }
            }
            MySingleton.getInstance(applicationContext).addToRequestQueue(peticion)
        } else{
            Toast.makeText(this, "Las Contraseñas NO Coinciden", Toast.LENGTH_LONG).show()
        }

    }
}