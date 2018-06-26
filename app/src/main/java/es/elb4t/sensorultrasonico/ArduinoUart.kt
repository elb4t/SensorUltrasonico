package es.elb4t.sensorultrasonico

import android.content.ContentValues.TAG
import android.util.Log
import com.google.android.things.pio.PeripheralManager
import com.google.android.things.pio.UartDevice
import java.io.IOException




class ArduinoUart {
    private var uart: UartDevice? = null

    constructor(nombre: String, baudios: Int) {
        try {
            uart = PeripheralManager.getInstance().openUartDevice(nombre)
            uart?.setBaudrate(baudios)
            uart?.setDataSize(8)
            uart?.setParity(UartDevice.PARITY_NONE)
            uart?.setStopBits(1)
        } catch (e: IOException) {
            Log.w(TAG, "Error iniciando UART", e)
        }
    }

    fun escribir(s: String) {
        try {
            val escritos = uart?.write(s.toByteArray(), s.length)
            Log.d(TAG, escritos.toString() + " bytes escritos en UART")
        } catch (e: IOException) {
            Log.w(TAG, "Error al escribir en UART", e)
        }
    }

    fun leer(): String {
        var s = ""
        var len: Int
        val maxCount = 8 // Máximo de datos leídos cada vez
        val buffer = ByteArray(maxCount)
        try {
            do {
                len = uart?.read(buffer, buffer.size)!!
                for (i in 0 until len) {
                    s += buffer[i].toChar()
                }
            } while (len > 0)
        } catch (e: IOException) {
            Log.w(TAG, "Error al leer de UART", e)
        }
        return s
    }

    fun cerrar(){
        if (uart != null){
            try {
                uart?.close()
                uart = null
            }catch (e: IOException){
                Log.w(TAG, "Error cerrando UART", e)
            }
        }
    }

    companion object {

        fun disponibles(): List<String> {
            return PeripheralManager.getInstance().uartDeviceList
        }
    }
}