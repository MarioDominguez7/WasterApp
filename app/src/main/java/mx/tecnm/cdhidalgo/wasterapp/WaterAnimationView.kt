package mx.tecnm.cdhidalgo.wasterapp

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator

class WaterAnimationView : View {

    private val paint: Paint = Paint()
    private var centerX = 0f
    private var centerY = 0f
    private var waterLevel = 0f
    private var maxWaterLevel = 0f
    private var minWaterLevel = 0f
    private var lastWaterLevel = 0f // Para mantener el último nivel de agua conocido
    private var maxRectWidth = 0f // Ancho máximo del rectángulo


    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        paint.color = Color.parseColor("#4388F0") // Color del agua
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerX = w / 2f
        centerY = h.toFloat()
        maxWaterLevel = centerY * 0.8f // Establecer el nivel máximo del agua al 80% del centro en la vista
        minWaterLevel = centerY * 0.25f // Establecer el nivel mínimo del agua al 25% del centro en la vista
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Dibujar el agua solo si el nivel de agua ha cambiado
        if (waterLevel != lastWaterLevel) {
            // Dibujar el agua
            paint.color = Color.parseColor("#4388F0") // Color del agua
            canvas.drawRect(
                centerX - maxRectWidth / 2, // Izquierda
                centerY - waterLevel, // Arriba
                centerX + maxRectWidth / 2, // Derecha
                centerY, // Abajo
                paint
            )
            lastWaterLevel = waterLevel // Actualizar el último nivel de agua conocido
        }
    }

    fun setWaterLevel(level: Float) {
        // Iniciar la animación solo si el nivel ha cambiado
        if (level != waterLevel) {
            // Crear un animador de valor
            val animator = ValueAnimator.ofFloat(waterLevel, level)
            animator.duration = 1000 // Duración de la animación en milisegundos
            animator.interpolator = DecelerateInterpolator() // Interpolador para suavizar la animación

            // Asignar un oyente de actualización al animador
            animator.addUpdateListener { animation ->
                // Actualizar el nivel de agua animado y solicitar una nueva dibujar
                waterLevel = animation.animatedValue as Float
                invalidate()
            }

            // Iniciar la animación
            animator.start()
        }
    }


    fun setContainerWidth(width: Float) {
        maxRectWidth = width - 100f // Ajuste opcional para dejar espacio en los bordes
    }

    fun getWaterLevelPercentage(): Float {
        return (waterLevel / maxWaterLevel) * 100 // Calcular el porcentaje del nivel de agua actual
    }
}
