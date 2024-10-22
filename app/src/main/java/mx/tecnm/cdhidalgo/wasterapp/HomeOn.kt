package mx.tecnm.cdhidalgo.wasterapp

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject

class HomeOn : AppCompatActivity() {

    lateinit var btnOff: ImageButton
    lateinit var btnStadisticsOn: ImageButton
    lateinit var btnLogOutOn: ImageButton
    lateinit var tvNivel: TextView
    lateinit var sesion: SharedPreferences
    private var valor: String = ""
    private var estado: String = ""
    lateinit var waterAnimationView: WaterAnimationView

    private val handler = Handler(Looper.getMainLooper())
    private val delayMillis: Long = 3000 // 3 segundos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_on)

        btnOff = findViewById(R.id.btnHomeOnOff)
        btnStadisticsOn = findViewById(R.id.btnHomeOnStadistics)
        btnLogOutOn = findViewById(R.id.btnHomeOnLogOut)
        tvNivel = findViewById(R.id.tvNivelOn)
        sesion = getSharedPreferences("sesion", 0)
        waterAnimationView = findViewById(R.id.water_animation_view_On)

        if(sesion.getString("jwt", "").toString()== ""){
            startActivity(Intent(this, Login::class.java))
            finish()
        }

        setActuator()
        startRepeatingTask()

        btnStadisticsOn.setOnClickListener { startActivity(Intent(this, Stadistics::class.java)) }
        btnOff.setOnClickListener {
            setActuatorOff()
            TimeStorage.horaFin = System.currentTimeMillis()
            startActivity(Intent(this, HomeOff::class.java))
        }
        btnLogOutOn.setOnClickListener { startActivity(Intent(this, LogOut::class.java))  }
    }
    override fun onResume() {
        super.onResume()
        // Continúa la ejecución periódica cuando la actividad vuelve a estar en primer plano
        startRepeatingTask()
    }

    override fun onPause() {
        super.onPause()
        // Detiene la ejecución periódica cuando la actividad se pausa
        stopRepeatingTask()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Detiene la ejecución periódica cuando la actividad se destruye
        stopRepeatingTask()
    }

    private val sensorRunnable = object : Runnable {
        override fun run() {
            getSensor()
            handler.postDelayed(this, delayMillis)
        }
    }

    private val actuatorRunnable = object : Runnable {
        override fun run() {
            getActuator()
            handler.postDelayed(this, delayMillis)
        }
    }

    private fun startRepeatingTask() {
        sensorRunnable.run()
        actuatorRunnable.run()
    }

    private fun stopRepeatingTask() {
        handler.removeCallbacks(sensorRunnable)
        handler.removeCallbacks(actuatorRunnable)
    }

    private fun getSensor(){
        val url = Uri.parse(Config.URL + "sensors/1")
            .buildUpon()
            .build().toString()
        val peticion = object: JsonObjectRequest(Request.Method.GET, url, null, {
                response ->
            valor = response.getString("value")
            // Altura del contenedor en centímetros (25 cm)
            val alturaContenedorCm = 28

            // Altura del contenedor en píxeles (500dp)
            val alturaContenedorPx = waterAnimationView.height
            val containerWidth = waterAnimationView.width
            waterAnimationView.setContainerWidth(containerWidth.toFloat())

            // Calcular la relación entre los centímetros y los píxeles
            val relacionCmPx = alturaContenedorPx.toFloat() / alturaContenedorCm.toFloat()

            // Valor del sensor en centímetros (ejemplo)
            val nivelSensorCm = valor.toFloat()

            // Calcular la altura del agua en píxeles
            // Invertir el valor del sensor y escalarlo al rango del contenedor
            val alturaAguaPx = (28 - nivelSensorCm) * relacionCmPx

            // Establecer el nivel de agua en WaterAnimationView

            waterAnimationView.setWaterLevel(alturaAguaPx)

            var porcentaje = waterAnimationView.getWaterLevelPercentage()
            if(porcentaje <= 0.0f){
                porcentaje = 0f
            }else if(porcentaje >= 100.0f){
                porcentaje = 100f
        }
            val porcentajeFormateado = String.format("%.2f", porcentaje)
            tvNivel.text = "$porcentajeFormateado%"
            if (porcentajeFormateado == "80.00"){
                setActuatorOff()
            }

            if (porcentajeFormateado == "15.00"){
                //mensaje para que se active la bomba
            }
        }, {
                error -> Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()
        }){
            override fun getHeaders(): Map<String, String>{
                val body: MutableMap<String, String> = HashMap()
                body["Authorization"] = sesion.getString("jwt", "").toString()
                return body
            }
        }
        MySingleton.getInstance(applicationContext).addToRequestQueue(peticion)
    }

    private fun getActuator(){
        val url = Uri.parse(Config.URL + "actuators/1")
            .buildUpon()
            .build().toString()
        val peticion = object: JsonObjectRequest(Request.Method.GET, url, null, {
                response ->
            estado = response.getString("value_status")
            if(estado == "0"){
                startActivity(Intent(this, HomeOff::class.java))
            }
        }, {
                error -> Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()
        }){
            override fun getHeaders(): Map<String, String>{
                val body: MutableMap<String, String> = HashMap()
                body["Authorization"] = sesion.getString("jwt", "").toString()
                return body
            }
        }
        MySingleton.getInstance(applicationContext).addToRequestQueue(peticion)
    }

    private fun setActuatorOff(){
        val url = Uri.parse(Config.URL + "actuators/1")
            .buildUpon()
            .build().toString()

        val body = JSONObject()
        body.put("status","Apagado")
        body.put("value_status","0")

        val peticion = object: JsonObjectRequest(Request.Method.PUT, url, body, {
                response ->
        }, {
                error -> Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()
        }){
            override fun getHeaders(): Map<String, String>{
                val body: MutableMap<String, String> = HashMap()
                body["Authorization"] = sesion.getString("jwt", "").toString()
                return body
            }
        }
        MySingleton.getInstance(applicationContext).addToRequestQueue(peticion)
    }

    private fun setActuator(){
        val url = Uri.parse(Config.URL + "actuators/1")
            .buildUpon()
            .build().toString()

        val body = JSONObject()
        body.put("status","Encendido")
        body.put("value_status","1")

        val peticion = object: JsonObjectRequest(Request.Method.PUT, url, body, {
                response ->

        }, {
                error -> Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()
        }){
            override fun getHeaders(): Map<String, String>{
                val body: MutableMap<String, String> = HashMap()
                body["Authorization"] = sesion.getString("jwt", "").toString()
                return body
            }
        }
        MySingleton.getInstance(applicationContext).addToRequestQueue(peticion)
    }

}