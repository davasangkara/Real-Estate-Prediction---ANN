package com.example.houselearning

import android.content.res.AssetManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class MainActivity : AppCompatActivity() {
    private lateinit var interpreter: Interpreter
    private val mModel = "Home.tflite"

    private lateinit var resultTextView: TextView
    private lateinit var date: EditText
    private lateinit var house_age: EditText
    private lateinit var mrt_station: EditText
    private lateinit var stores: EditText
    private lateinit var latitude: EditText
    private lateinit var longitude: EditText
    private lateinit var house_price: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Set the back button drawable
        val backDrawable = resources.getDrawable(R.drawable.back, null)
        toolbar.setNavigationIcon(backDrawable)

        // Set the navigation click listener
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        resultTextView = findViewById(R.id.txtResult)
        date = findViewById(R.id.date)
        house_age = findViewById(R.id.house_age)
        mrt_station = findViewById(R.id.mrt_station)
        stores = findViewById(R.id.stores)
        latitude = findViewById(R.id.latitude)
        longitude = findViewById(R.id.longitude)
        house_price = findViewById(R.id.house_price)

        findViewById<Button>(R.id.btnCheck).setOnClickListener {
            val result = doInterference(
                date.text.toString(),
                house_age.text.toString(),
                mrt_station.text.toString(),
                stores.text.toString(),
                latitude.text.toString(),
                longitude.text.toString(),
                house_price.text.toString()
            )
            resultTextView.text = result
        }

        initInterpreter()
    }

    private fun initInterpreter() {
        val option = Interpreter.Options()
        option.setNumThreads(7)
        option.setUseNNAPI(true)
        interpreter = Interpreter(loadModelFile(assets, mModel), option)
    }

    private fun doInterference(
        input1: String,
        input2: String,
        input3: String,
        input4: String,
        input5: String,
        input6: String,
        input7: String
    ): String {
        val inputVal = FloatArray(7)
        inputVal[0] = input1.toFloatOrNull() ?: 0f
        inputVal[1] = input2.toFloatOrNull() ?: 0f
        inputVal[2] = input3.toFloatOrNull() ?: 0f
        inputVal[3] = input4.toFloatOrNull() ?: 0f
        inputVal[4] = input5.toFloatOrNull() ?: 0f
        inputVal[5] = input6.toFloatOrNull() ?: 0f
        inputVal[6] = input7.toFloatOrNull() ?: 0f
        val output = Array(1) { FloatArray(1) }
        interpreter.run(inputVal, output)

        val predictedHousePrice = output[0][0]


        val formattedPrice = String.format("%.0f", predictedHousePrice)

        val priceInBillion = predictedHousePrice / 1000
        val formattedPriceWithUnit = if (priceInBillion >= 1) {
            String.format("%.1f", priceInBillion) + " Milyar"
        } else {
            formattedPrice + " juta"
        }

        return "Harga Rumah: $formattedPriceWithUnit"
    }

    private fun loadModelFile(assetManager: AssetManager, modelPath: String): MappedByteBuffer {
        val fileDescriptor = assetManager.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }
}
