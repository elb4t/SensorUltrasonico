package es.elb4t.sensorultrasonico

import android.app.Activity
import android.content.ContentValues.TAG
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.PeripheralManager
import java.io.IOException


/**
 * Skeleton of an Android Things activity.
 *
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 *
 * <pre>{@code
 * val service = PeripheralManagerService()
 * val mLedGpio = service.openGpio("BCM6")
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
 * mLedGpio.value = true
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 *
 */
class MainActivity : Activity() {
    private val ECHO_PIN_NAME = "BCM20"
    private val TRIGGER_PIN_NAME = "BCM16"
    private val INTERVALO_ENTRE_LECTURAS:Long = 3000
    private var mEcho: Gpio? = null
    private var mTrigger: Gpio? = null
    var hazAlgo: Int = 0
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val manager = PeripheralManager.getInstance()

        try {
            mEcho = manager.openGpio(ECHO_PIN_NAME)
            mEcho?.setDirection(Gpio.DIRECTION_IN)
            mTrigger = manager.openGpio(TRIGGER_PIN_NAME)
            mTrigger?.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
            handler.post(runnable)
        } catch (e: IOException) {
            Log.e(TAG, "Error en al acceder a salida", e)
        }
    }

    private val runnable: Runnable = object: Runnable {
        override fun run() {
            try {
            leerDistancia()
            handler.postDelayed(this, INTERVALO_ENTRE_LECTURAS)
            } catch (e: IOException) {
                Log.e(TAG, "Error en PeripheralIO API", e)
            }
        }
    }

    @Throws(IOException::class, InterruptedException::class)
    protected fun leerDistancia(): Double {
        mTrigger?.value = false
        Thread.sleep(0, 2000) // 2 mseg
        mTrigger?.value = true
        Thread.sleep(0, 10000) //10 msec
        mTrigger?.setValue(false);
        while (mEcho?.value === false) {
            hazAlgo = 0
        }
        val tiempoIni = System.nanoTime()
        while (mEcho?.value === true) {
            hazAlgo = 1
        }
        val tiempoFin = System.nanoTime()
        val anchoPulso = tiempoFin - tiempoIni
        val distancia = anchoPulso / 1000.0 / 58.23 //cm
        Log.i(TAG, "distancia (Android Things): $distancia cm")
        return distancia
    }
}
