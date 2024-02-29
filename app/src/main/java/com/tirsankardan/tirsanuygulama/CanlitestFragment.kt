@file:Suppress("DEPRECATION")

package com.tirsankardan.tirsanuygulama

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.*
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteException
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.addCallback
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.tirsankardan.tirsanuygulama.data.DatabaseHelper
import com.tirsankardan.tirsanuygulama.data.DatabaseHelper.Table.TableEntry.COLUMN_CREATED_AT
import com.tirsankardan.tirsanuygulama.data.DatabaseHelper.Table.TableEntry.COLUMN_DESCRIPTION
import com.tirsankardan.tirsanuygulama.data.DatabaseHelper.Table.TableEntry.COLUMN_ELAPSED_TIME
import com.tirsankardan.tirsanuygulama.data.DatabaseHelper.Table.TableEntry.COLUMN_ID
import com.tirsankardan.tirsanuygulama.data.DatabaseHelper.Table.TableEntry.COLUMN_STATUS
import com.tirsankardan.tirsanuygulama.data.DatabaseHelper.Table.TableEntry.COLUMN_TEST_NAME
import com.tirsankardan.tirsanuygulama.data.DatabaseHelper.Table.TableEntry.COLUMN_USER
import kotlinx.coroutines.*
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.*
import java.io.*
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class CanlitestFragment : Fragment() {
    private var stopStatus = false
    private var stopStatus2 = false
    private var bluetoothSocket2: BluetoothSocket? = null
    private val maxDataPoints = 90  // Maksimum gösterilecek veri noktası sayısı
    private val updateIntervalMillis = 1000L // Saniye güncelleme aralığı
    private val torqueDataList1 = mutableListOf<Entry>() // İlk cihaz için torque veri listesi
    private val torqueDataList2 = mutableListOf<Entry>() // İkinci cihaz için torque veri listesi
    private val tempDataList1 = mutableListOf<Entry>() // İlk cihaz için temp veri listesi
    private val tempDataList2 = mutableListOf<Entry>() // İkinci cihaz için temp veri listesi
    private var xAxisValue = 0f // X eksenindeki son değer
    private var isChartOpen = false
    private var isChart2Open = false
    private lateinit var editOffset: EditText
    private var offset = false
    private var offset2 = false
    private var div = 1
    private var div2 = 1
    private var pressed: Int? = null
    private val MAX_CONNECTION_ATTEMPTS = 3
    private val MAX_CONNECTION_ATTEMPTS2 = 10000000000000
    private var connectionAttempts = 0
    private var connectionAttempts2 = 0
    private var isBluetoothConnected = false
    private lateinit var tickButton: ImageButton
    private lateinit var restoreButton: ImageButton
    private lateinit var cancelButton: ImageButton
    private lateinit var lineChart: LineChart
    private lateinit var lineChart2: LineChart
    private lateinit var lineChart3: LineChart
    private lateinit var lineChart4: LineChart
    var lowhigh = false
    var lowhigh2 = false
    private var back = false
    private var back2 = false
    private var myCoroutine2: Job? = null
    private var myCoroutine: Job? = null
    private var myCoroutine3: Job? = null
    private var isConnectedDevice1 = false
    private var isConnectedDevice2 = false
    private lateinit var imageButton: ImageButton
    private lateinit var testListAdapter: ArrayAdapter<String>
    private lateinit var button: Button
    private lateinit var button2: Button
    private lateinit var button3: Button
    private lateinit var button4: Button
    private lateinit var offsetbutton: Button
    private lateinit var offsetbutton2: Button
    private lateinit var torquebutton1: Button
    private lateinit var torquebutton2: Button
    private lateinit var tempbutton1: Button
    private lateinit var tempbutton2: Button
    private lateinit var textViewTorque: TextView
    private lateinit var textViewTorque2: TextView
    private lateinit var textViewTemp: TextView
    private lateinit var textViewTemp2: TextView
    private lateinit var textViewName: TextView
    private lateinit var textViewName2: TextView
    private lateinit var textViewTime: TextView
    private lateinit var textViewTime2: TextView
    private lateinit var textViewHTorque: TextView
    private lateinit var textViewHTorque2: TextView
    private lateinit var textViewHTemp: TextView
    private lateinit var textViewHTemp2: TextView
    private lateinit var textViewLTorque: TextView
    private lateinit var textViewLTorque2: TextView
    private lateinit var textViewLTemp: TextView
    private lateinit var textViewLTemp2: TextView
    private lateinit var textViewDiff: TextView
    private lateinit var textViewDiff2: TextView
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothAdapter2: BluetoothAdapter? = null
    private var bluetoothDevice: BluetoothDevice? = null
    private var bluetoothDevice2: BluetoothDevice? = null
    private var bluetoothSocket: BluetoothSocket? = null
    private var inputStream: InputStream? = null
    private var stopReading = false
    private var stopReading2 = false
    private var lastUpdateTime = 0L
    var retryConnection = false
    private val temperatureUpdateInterval = 125 // 0.125 saniye (ms)
    private var highestTorque: Int = Int.MIN_VALUE
    private var lowestTorque: Int = Int.MAX_VALUE
    private var highestTemperature: Double = Double.MIN_VALUE
    private var lowestTemperature: Double = Double.MAX_VALUE
    private var highestTorque2: Int = Int.MIN_VALUE
    private var lowestTorque2: Int = Int.MAX_VALUE
    private var highestTemperature2: Double = Double.MIN_VALUE
    private var lowestTemperature2: Double = Double.MAX_VALUE
    private var testFinished = false
    private var testFinished2 = false
    private var isOpen = false
    private var isOpen2 = false
    private lateinit var sharedViewModel: SharedViewModel
    var outputFile: String? = null
    private var hours = 0
    private var minutes = 0
    private var seconds = 0
    private var milliseconds = 0
    var dataCountTorque = 0
    var dataCountTorque2 = 0
    var dataCountTemp = 0
    var dataCountTemp2 = 0
    private var formattedTorque: Int? = null
    private var formattedTorque2: Int? = null
    private var sabitTorque: Int? = null
    private var sabitTorque2: Int? = null
    private var createdAt = "00-00-0000 00:00:00"
    var id = SimpleDateFormat("ddMMHHmmss", Locale.getDefault()).format(Date())
    private val workbook = HSSFWorkbook()
    private val sheetTorque = workbook.createSheet("Cihaz 1 Tork Verileri")
    private val sheetTemp = workbook.createSheet("Cihaz 1 Temp Verileri")
    private val sheetTorque2 = workbook.createSheet("Cihaz 2 Tork Verileri")
    private val sheetTemp2 = workbook.createSheet("Cihaz 2 Temp Verileri")
    var startTime = SystemClock.elapsedRealtime()
    val handler = Handler()

    // Timerlar
    private val runnable = object : Runnable {
        override fun run() {
            if (!isOpen) {
                createdAt = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
            }
            isOpen = true
            val elapsedTime = SystemClock.elapsedRealtime() - startTime
            hours = (elapsedTime / (1000 * 60 * 60)).toInt()
            minutes = (elapsedTime / 1000 / 60).toInt()
            seconds = (elapsedTime / 1000 % 60).toInt()
            milliseconds = (elapsedTime % 1000).toInt()
            // İlgili TextView'e zamanı güncelle
            textViewTime.text = String.format("%02d:%02d:%02d",hours, minutes, seconds)
            handler.post(this)
        }
    }
    private val runnable2 = object : Runnable {
        override fun run() {
            if (!isOpen2) {
                createdAt = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
            }
            isOpen2 = true
            val elapsedTime = SystemClock.elapsedRealtime() - startTime
            hours = (elapsedTime / (1000 * 60 * 60)).toInt()
            minutes = (elapsedTime / 1000 / 60).toInt()
            seconds = (elapsedTime / 1000 % 60).toInt()
            milliseconds = (elapsedTime % 1000).toInt()
            // İlgili TextView'e zamanı güncelle
            textViewTime2.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)
            handler.post(this)
        }
    }

    // Buton işlevlerinin tanımlanması ve grafik kurma
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_canlitest, container, false)
        val offsetView = view.findViewById<ConstraintLayout>(R.id.offsetView)
        val cardView = view.findViewById<CardView>(R.id.card1)
        val cardView2 = view.findViewById<CardView>(R.id.card2)
        val cardView3 = view.findViewById<CardView>(R.id.card3)
        val cardView4 = view.findViewById<CardView>(R.id.card4)
        tickButton = offsetView.findViewById(R.id.imageButton2)
        restoreButton = offsetView.findViewById(R.id.imageButton3)
        cancelButton = offsetView.findViewById(R.id.imageButton4)
        editOffset = offsetView.findViewById(R.id.editText)
        lineChart = cardView.findViewById(R.id.lineChart)
        lineChart2 = cardView2.findViewById(R.id.lineChart2)
        lineChart3 = cardView3.findViewById(R.id.lineChart3)
        lineChart4 = cardView4.findViewById(R.id.lineChart4)
        offsetbutton = view.findViewById(R.id.button10)
        offsetbutton2 = view.findViewById(R.id.button11)
        torquebutton1 = view.findViewById(R.id.button6)
        torquebutton2 = view.findViewById(R.id.button8)
        tempbutton1 = view.findViewById(R.id.button7)
        tempbutton2 = view.findViewById(R.id.button9)
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        textViewTorque = view.findViewById(R.id.textView)
        imageButton = view.findViewById(R.id.imageButton)
        textViewTorque2 = view.findViewById(R.id.textView21)
        textViewTemp = view.findViewById(R.id.textView2)
        textViewTemp2 = view.findViewById(R.id.textView22)
        textViewName = view.findViewById(R.id.textView11)
        textViewName2 = view.findViewById(R.id.textView60)
        textViewTime = view.findViewById(R.id.textView12)
        textViewTime2 = view.findViewById(R.id.textView61)
        textViewHTorque = view.findViewById(R.id.textView53)
        textViewHTorque2 = view.findViewById(R.id.textView33)
        textViewHTemp = view.findViewById(R.id.textView55)
        textViewHTemp2 = view.findViewById(R.id.textView35)
        textViewLTorque = view.findViewById(R.id.textView54)
        textViewLTorque2 = view.findViewById(R.id.textView34)
        textViewLTemp = view.findViewById(R.id.textView56)
        textViewLTemp2 = view.findViewById(R.id.textView36)
        textViewDiff = view.findViewById(R.id.textView15)
        textViewDiff2 = view.findViewById(R.id.textView17)
        button = view.findViewById(R.id.button)
        button2 = view.findViewById(R.id.button2)
        button3 = view.findViewById(R.id.button3)
        button4 = view.findViewById(R.id.button4)

        // Buton işlev tanımlamaları
        button.setOnClickListener {
            finishTest()
        }
        button4.setOnClickListener {
            finishTest2()
        }
        // Testi kaydeden fonksiyonu çağıran Buton
        button2.setOnClickListener {
            // Veritabanına özellikleri kaydetme işlemini burada yapabilirsiniz
            savePropertiesToDatabase()
        }

        button3.setOnClickListener {
            // Veritabanına özellikleri kaydetme işlemini burada yapabilirsiniz
            savePropertiesToDatabase()
        }

        torquebutton1.setOnClickListener {

            if (cardView.isVisible) {
                cardView.isVisible = false
            }
            else if (!cardView.isVisible) {
                cardView.isVisible = true
            }
        }

        torquebutton2.setOnClickListener {
            if (cardView3.isVisible) {
                cardView3.isVisible = false
            }
            else if (!cardView3.isVisible) {
                cardView3.isVisible = true
            }
        }

        tempbutton1.setOnClickListener {
            if (cardView2.isVisible) {
                cardView2.isVisible = false
            }
            else if (!cardView2.isVisible) {
                cardView2.isVisible = true
            }
        }

        tempbutton2.setOnClickListener {
            if (cardView4.isVisible) {
                cardView4.isVisible = false
            }
            else if (!cardView4.isVisible) {
                cardView4.isVisible = true
            }
        }

        offsetbutton.setOnClickListener {
            pressed = 1
            if (cardView.visibility == View.VISIBLE){
                isChartOpen = true
                cardView.visibility = View.GONE
            }
            else {
                isChartOpen = false
            }
            if (cardView2.visibility == View.VISIBLE){
                isChart2Open = true
                cardView2.visibility = View.GONE
            }
            else {
                isChart2Open = false
            }
            sabitTorque = if (!offset) {
                formattedTorque
            } else {
                sabitTorque
            }
            offsetView.visibility = View.VISIBLE
            div = 1
            lowhigh = true
            offset = true
        }

        offsetbutton2.setOnClickListener {
            pressed = 2
            if (cardView.visibility == View.VISIBLE){
                isChartOpen = true
                cardView.visibility = View.GONE
            }
            else {
                isChartOpen = false
            }
            if (cardView2.visibility == View.VISIBLE){
                isChart2Open = true
                cardView2.visibility = View.GONE
            }
            else {
                isChart2Open = false
            }
            sabitTorque2 = if (!offset2) {
                formattedTorque2
            } else {
                sabitTorque2
            }
            offsetView.visibility = View.VISIBLE
            div2 = 1
            lowhigh2 = true
            offset2 = true
        }

        tickButton.setOnClickListener {
            if (editOffset.text.toString().isEmpty()) {
                offsetView.visibility = View.GONE
            }
            else {
                val newDiv = editOffset.text.toString().toInt()
                if (pressed == 1) {
                    div = newDiv
                }
                else if (pressed == 2) {
                    div2 = newDiv
                }
                offsetView.visibility = View.GONE
            }
            if (isChartOpen) {
                cardView.visibility = View.VISIBLE
            }
            if (isChart2Open) {
                cardView2.visibility = View.VISIBLE
            }
        }

        restoreButton.setOnClickListener {
            if (pressed == 1) {
                div = 1
            }
            else if (pressed == 2) {
                div2 = 1
            }
            offsetView.visibility = View.GONE
            if (isChartOpen) {
                cardView.visibility = View.VISIBLE
            }
            if (isChart2Open) {
                cardView2.visibility = View.VISIBLE
            }
        }

        cancelButton.setOnClickListener {
            if (pressed == 1) {
                div = 1
                offset = false
                lowhigh = false
            }
            else if (pressed == 2) {
                div2 = 1
                offset2 = false
                lowhigh2 = false
            }
            offsetView.visibility = View.GONE
            if (isChartOpen) {
                cardView.visibility = View.VISIBLE
            }
            if (isChart2Open) {
                cardView2.visibility = View.VISIBLE
            }
        }

        //grafikler kuruluyor
        setupLineChart(lineChart, "Nm")
        setupLineChart(lineChart2, "°C")
        setupLineChart(lineChart3, "Nm")
        setupLineChart(lineChart4, "°C")
        return view
    }

    // Testi kaydeden fonksiyon
    private fun savePropertiesToDatabase() {
        val dbHelper = context?.let { DatabaseHelper(it) }
        val testName = arguments?.getString("Test_Name")
        val testDescription = arguments?.getString("Test_Description")
        val user = if (sharedViewModel.isAdmin.value == true) "Admin" else "User"
        val status = if (testFinished && testFinished2) "Passive" else "Active"
        val elapsedTime = String.format("%02d:%02d", minutes, seconds)
        val createdAt = createdAt
        val id = SimpleDateFormat("ddMMHHmmss", Locale.getDefault()).format(Date())

        dbHelper?.let { helper ->
            try {
                val db = helper.writableDatabase
                val values = ContentValues().apply {
                    put(COLUMN_ID, id)
                    put(COLUMN_TEST_NAME, testName)
                    put(COLUMN_DESCRIPTION, testDescription)
                    put(COLUMN_USER, user)
                    put(COLUMN_STATUS, status)
                    put(COLUMN_CREATED_AT, createdAt)
                    put(COLUMN_ELAPSED_TIME, elapsedTime)
                }
                val result = db.insertOrThrow(DatabaseHelper.Table.TableEntry.COLUMN_TABLE_NAME, null, values)

                // Diğer işlemler
            } catch (e: SQLiteException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                dbHelper.close()
            }
        }
        showToast("Başarıyla kaydedildi")
        //testlistesine excel kaydı gönderir
        saveExcelFile(id.toString())
    }

    // Geri dönüş butonları ve bluetooth olayları
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        testListAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, mutableListOf<String>())

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (!back && !back2) {
                isEnabled = false
            }
            else {
                findNavController().popBackStack()
            }
            isEnabled = true
        }

        imageButton.setOnClickListener {
            if (!back && !back2) {
                imageButton.isEnabled = false
            }
            else {
                findNavController().popBackStack()
            }
            imageButton.isEnabled = true
        }

        // Bluetooth aygıtını kontrol etmek için BluetoothAdapter'ı al
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            showToast("Bluetooth desteklenmiyor")
            return
        }
        if (!bluetoothAdapter!!.isEnabled) {
            showToast("Bluetooth'u açın")
            return
        }
        if (checkBluetoothPermission()) {
            // Bluetooth iznini kontrol et ve bağlantıyı başlat
            connectBluetoothDevice()
        } else {
            // Bluetooth iznini iste
            requestBluetoothPermission()
        }
    }

    // Başka bir ekrana geçildiğinde arkaplan senaryoları
    override fun onStop() {
        super.onStop()
        // Bluetooth bağlantısını kapat ve kaynakları temizle
        try {
            // Runnable'ları durdur
            myCoroutine?.cancel()
            inputStream?.close()
            bluetoothSocket?.close()
            bluetoothSocket2?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    // Bluetooth izin kontrolü
    private fun checkBluetoothPermission(): Boolean {
        val permission = Manifest.permission.BLUETOOTH
        val result = ContextCompat.checkSelfPermission(requireContext(), permission)
        return result == PackageManager.PERMISSION_GRANTED
    }

    // Bluetooth izni isteme
    private fun requestBluetoothPermission() {
        val permission = Manifest.permission.BLUETOOTH
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(permission), PERMISSION_REQUEST_CODE)
    }

    // İkinci Bluetooth kontrolü
    private fun checkPermission(): Boolean {
        val permission = Manifest.permission.BLUETOOTH_ADMIN
        val result = ContextCompat.checkSelfPermission(requireContext(), permission)
        return result == PackageManager.PERMISSION_GRANTED
    }

    // Stop Test butonuna basıldığında çalışan fonksiyon
    private fun finishTest() {
        if (stopStatus) {
            back = false
            stopStatus = false
            stopReading = false
            testFinished = false
            handler.post(runnable)
            button.setBackgroundColor(Color.RED)
            // Buton metnini güncelleyin
            button.text = "STOP THE TEST"
            myCoroutine = lifecycleScope.launch(Dispatchers.IO) {
                readingData(stopReading, stopStatus, bluetoothSocket, 1)
            }
        } else {
            stopStatus = true
            stopReading = true
            testFinished = true

            handler.removeCallbacks(runnable)
            button.setBackgroundColor(Color.GREEN)
            // Buton metnini güncelleyin
            button.text = "START THE TEST"
            myCoroutine?.cancel()
            myCoroutine3?.cancel()
            back = true
        }
    }

    // İkinci cihaz için Stop Test fonksiyonu
    private fun finishTest2() {
        if (stopStatus2) {
            back2 = false
            stopStatus2 = false
            stopReading2 = false // stopReading'i false olarak ayarla
            testFinished2 = false // testFinished'ı false olarak ayarla
            handler.post(runnable2) // Daha önce durdurulan runnable2'yi yeniden başlat
            button4.setBackgroundColor(Color.RED)
            button4.text = "STOP THE TEST"
            myCoroutine2 = lifecycleScope.launch(Dispatchers.IO) {
                readingData(stopReading2, stopStatus2, bluetoothSocket2, 2)
            }
        } else {
            stopStatus2 = true
            stopReading2 = true // stopReading zaten true ise, tekrar true olarak ayarla
            testFinished2 = true // testFinished'ı true olarak ayarla
            handler.removeCallbacks(runnable2) // Daha önce başlatılan runnable2'yi durdur
            button4.setBackgroundColor(Color.GREEN)
            button4.text = "START THE TEST"
            myCoroutine2?.cancel()
            myCoroutine3?.cancel()
            back2 = true
        }
    }

    override fun onPause() {
        super.onPause()
        // Uygulama arka plana alındığında bağlantıyı kapat ve tekrar bağlantı denemesini durdur.
        bluetoothSocket?.close()
        bluetoothSocket2?.close()
        retryConnection = false
    }

    override fun onResume() {
        super.onResume()
        // Uygulama tekrar ön plana geçtiğinde tekrar bağlantı denemesine devam etmek için retryConnection'ı true olarak ayarla.
        retryConnection = false
    }

    // İzinleri onaylayarak cihazlarla bağlantıyı gerçekleştirme
    private fun connectBluetoothDevice() {
        val deviceAddress1 = arguments?.getString("DEVICE_ADDRESS1")
        val deviceAddress2 = arguments?.getString("DEVICE_ADDRESS2")
        val hasPermission = checkPermission()
        if (!hasPermission) {
            showToast("Bluetooth izni reddedildi")
            return
        }
        if (deviceAddress1.isNullOrEmpty() && deviceAddress2.isNullOrEmpty()) {
            showToast("Bağlanacak Bluetooth aygıtı bulunamadı.")
            return
        }

        try {
            if (!deviceAddress1.isNullOrEmpty()) {
                connectWithRetry(deviceAddress1, 1)
            }

            if (!deviceAddress2.isNullOrEmpty()) {
                connectWithRetry(deviceAddress2, 2)
            }

            // Cihazlar bağlanmış ise veri okunmaya başlanır.
            if (isConnectedDevice1 && isConnectedDevice2) {
                showToast("Her iki Bluetooth aygıtına da bağlandı")
                startReadingData(deviceAddress1, deviceAddress2, false, false)
            } else if (isConnectedDevice1 && !isConnectedDevice2) {
                startReadingData(deviceAddress1, null, false, true)
            } else if (!isConnectedDevice1 && isConnectedDevice2) {
                startReadingData(null, deviceAddress2, true, false)
            } else {
                showToast("İki cihaz da bağlanamadı.")
                cancelCanliTestFragment()
            }
        } catch (e: Exception) {
            showToast("Hata oluştu: ${e.localizedMessage}")
            e.printStackTrace()
        }
    }

    // Bağlantıyı ilk denemede olmama ihtimaline karşı birkaç kez deneyen fonksiyon
    @SuppressLint("MissingPermission")
    private fun connectWithRetry(deviceAddress: String, deviceIndex: Int) {
        if (deviceAddress == null) {
            // Device address null ise, hata mesajı göster ve işlemi sonlandır
            showToast("Device address null olduğu için bağlantı yapılamadı")
            return
        }
        connectionAttempts = 0
        isBluetoothConnected = false
        while (!isBluetoothConnected && connectionAttempts < MAX_CONNECTION_ATTEMPTS) {
            try {
                val uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")
                if (deviceIndex == 1) {
                    bluetoothDevice = bluetoothAdapter?.getRemoteDevice(deviceAddress)
                    bluetoothSocket = bluetoothDevice?.createInsecureRfcommSocketToServiceRecord(uuid)
                    if (bluetoothSocket != null) {
                        bluetoothSocket!!.connect()
                        inputStream = bluetoothSocket?.inputStream
                        isConnectedDevice1 = true
                        isBluetoothConnected = true
                        showToast("1. Cihaz Bağlandı.")
                    }
                    else {
                        connectionAttempts++
                        showToast("Bağlantı kurulamadı. Tekrar bağlantı deneniyor...")
                        if (connectionAttempts >= MAX_CONNECTION_ATTEMPTS) {
                            return
                        }
                    }
                }
                else {
                    bluetoothDevice2 = bluetoothAdapter?.getRemoteDevice(deviceAddress)
                    bluetoothSocket2 = bluetoothDevice2?.createInsecureRfcommSocketToServiceRecord(uuid)
                    if (bluetoothSocket2 != null) {
                        bluetoothSocket2!!.connect()
                        inputStream = bluetoothSocket2?.inputStream
                        isConnectedDevice2 = true
                        isBluetoothConnected = true
                        showToast("2. Cihaz Bağlandı.")
                    }
                    else {
                        connectionAttempts++
                        showToast("Bağlantı kurulamadı. Tekrar bağlantı deneniyor...")
                        if (connectionAttempts >= MAX_CONNECTION_ATTEMPTS) {
                            return
                        }
                    }
                }
                Thread.sleep(1500) // İstediğiniz bekleme süresini ayarlayı
            } catch (e: IOException) {
                e.printStackTrace()
                connectionAttempts++
                if (connectionAttempts >= MAX_CONNECTION_ATTEMPTS) {
                    break
                }
            }
        }
    }

    private fun cancelCanliTestFragment() {
        findNavController().popBackStack()
    }

    // Bluetooth bağlantısı koptuğunda yeniden bağlanma
    @SuppressLint("MissingPermission")
    private suspend fun connectWithRetry2(index: Int) {
        val deviceAddress1 = arguments?.getString("DEVICE_ADDRESS1")
        val deviceAddress2 = arguments?.getString("DEVICE_ADDRESS2")
        myCoroutine3 = lifecycleScope.launch(Dispatchers.IO) {
            while (retryConnection) {
                try {
                    // Tekrar bağlantı denemesi yap.
                    val uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")
                    if (index == 1) {
                        showToast("1. cihaza tekrar bağlantı deneniyor")
                        bluetoothSocket = bluetoothDevice?.createInsecureRfcommSocketToServiceRecord(uuid)
                        bluetoothSocket?.connect()
                        inputStream = bluetoothSocket?.inputStream
                        retryConnection = false // Bağlantı başarılı olduğunda döngüden çık
                    } else if (index == 2) {
                        showToast("2. cihaza tekrar bağlantı deneniyor")
                        bluetoothSocket2 = bluetoothDevice2?.createInsecureRfcommSocketToServiceRecord(uuid)
                        bluetoothSocket2?.connect()
                        inputStream = bluetoothSocket2?.inputStream
                        retryConnection = false // Bağlantı başarılı olduğunda döngüden çık
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    connectionAttempts2++
                    if (connectionAttempts2 >= MAX_CONNECTION_ATTEMPTS2) {
                        retryConnection = false // MAX_CONNECTION_ATTEMPTS2 ulaşılamaz bir sayı olduğundan sonsuza kadar deneme yapılır.
                    }
                }

                if (!retryConnection && connectionAttempts2 < MAX_CONNECTION_ATTEMPTS2) {
                    // Veri alma işlemini başlat
                    withContext(Dispatchers.Main) {
                        if (index == 1) {
                            myCoroutine = lifecycleScope.launch(Dispatchers.IO) {
                                readingData(stopReading, stopStatus, bluetoothSocket, 1)
                            }
                            showToast("Bluetooth aygıtına tekrar bağlandı")
                        } else if (index == 2) {
                            myCoroutine2 = lifecycleScope.launch(Dispatchers.IO) {
                                readingData(stopReading2, stopStatus2, bluetoothSocket2, 2)
                            }
                            showToast("Bluetooth aygıtına tekrar bağlandı")
                        }
                    }
                }
                // Tekrar bağlantı denemeleri arasında uygun bir bekleme süresi ekleyin.
                Thread.sleep(1500) // İstediğiniz bekleme süresini ayarlayın
            }
        }
    }

    // Veri okumak için hazırlık yapan fonksiyon
    private fun startReadingData(deviceAddress1: String?, deviceAddress2: String?, stopReading: Boolean, stopReading2: Boolean) {
        val selectedDevice = arguments?.getString("Selected_Device")
        val selectedDevice2 = arguments?.getString("Selected_Device2")
        if (deviceAddress1 != null && !testFinished) {
            textViewName.text = "$selectedDevice"
            handler.post(runnable)
        }
        if (deviceAddress2 != null && !testFinished2) {
            textViewName2.text = "$selectedDevice2"
            handler.post(runnable2)
        }
        if (deviceAddress1 != null && deviceAddress2 != null) {
                myCoroutine = lifecycleScope.launch(Dispatchers.IO) {
                    readingData(stopReading, stopStatus, bluetoothSocket, 1)
                }
                myCoroutine2 = lifecycleScope.launch(Dispatchers.IO) {
                    readingData(stopReading2, stopStatus2, bluetoothSocket2, 2)
                }
        } else if (deviceAddress1 != null && deviceAddress2 == null) {
            myCoroutine = lifecycleScope.launch(Dispatchers.IO) {
                readingData(stopReading, stopStatus, bluetoothSocket, 1)
            }
        } else if (deviceAddress1 == null && deviceAddress2 != null) {
            myCoroutine2 = lifecycleScope.launch(Dispatchers.IO) {
                readingData(stopReading2, stopStatus2, bluetoothSocket2, 2)
            }
        } else {
            showToast("Bağlanacak Bluetooth aygıtı bulunamadı.")
        }
    }

    // Veri okuma işlemi
    private suspend fun readingData(stopReading: Boolean, stopStatus: Boolean, bluetoothSocket: BluetoothSocket?, index: Int) {
        val selectedData = arguments?.getString("Selected_Data")
        val selectedData2 = arguments?.getString("Selected_Data2")
        val selectedData3 = arguments?.getString("Selected_Data3")
        val selectedData4 = arguments?.getString("Selected_Data4")
        if (bluetoothSocket != null) {
            try {
                val inputStream = bluetoothSocket.inputStream
                val reader = BufferedReader(InputStreamReader(inputStream, Charset.forName("UTF-8")))
                while (!stopReading && bluetoothSocket != null) { // stopReading kontrolünü kullanarak döngüyü durdur
                    try {
                        val stringBuilder = StringBuilder()
                        val buffer = ByteArray(1024) // Okunacak verinin maksimum boyutunu belirleyin
                        val line = reader.readLine()
                        if (line != null) {
                            stringBuilder.append(line)
                        } else {
                            break // Okuma tamamlandı
                        }
                        val bytesRead = inputStream?.read(buffer)
                        if (bytesRead != null && bytesRead > 0) {
                            val receivedData = stringBuilder.toString()
                            // Alınan veriyi kullanın veya işleyin
                            withContext(Dispatchers.Main) {
                                // Gelen veriyi işlemek için processReceivedData() fonksiyonunu çağır
                                try {
                                    val torqueData = processReceivedData(receivedData, DataType.Torque, index)?.toInt()
                                    val temperatureData = processReceivedData(receivedData, DataType.Temperature, index)
                                    if (torqueData != null)  {
                                        if (index == 1 && (selectedData3 == "Torque" || selectedData4 == "Torque")) {
                                            try {
                                                updateTorqueData(torqueData, index)
                                            } catch (e: Exception) {
                                                showToast("UpdateTorqueData, Error updating torque data: ${e.message}")
                                                e.printStackTrace()
                                                // Hata durumunda yapılması gereken işlemleri buraya ekleyebilirsiniz
                                            }
                                        }
                                        else if (index == 2 && (selectedData == "Torque" || selectedData2 == "Torque")) {
                                            try {
                                                updateTorqueData(torqueData, index)
                                            } catch (e: Exception) {
                                                showToast("UpdateTorqueData, Error updating torque data: ${e.message}")
                                                e.printStackTrace()
                                                // Hata durumunda yapılması gereken işlemleri buraya ekleyebilirsiniz
                                            }
                                        }
                                    }
                                    if (temperatureData != null) {
                                        if (index == 1 && (selectedData3 == "Temperature" || selectedData4 == "Temperature")) {
                                            try {
                                                updateTemperatureData(temperatureData, index)
                                            } catch (e: Exception) {
                                                showToast("UpdateTemperatureData, Error updating temperature data: ${e.message}")
                                                e.printStackTrace()
                                                // Hata durumunda yapılması gereken işlemleri buraya ekleyebilirsiniz
                                            }
                                        }
                                        else if (index == 2 && (selectedData == "Temperature" || selectedData2 == "Temperature")) {
                                            try {
                                                updateTemperatureData(temperatureData, index)
                                            } catch (e: Exception) {
                                                showToast("UpdateTemperatureData, Error updating temperature data: ${e.message}")
                                                e.printStackTrace()
                                                // Hata durumunda yapılması gereken işlemleri buraya ekleyebilirsiniz
                                            }
                                        }
                                    }
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    } catch (e: IOException) {
                        if (stopStatus) {
                            break
                        }
                        else {
                            e.printStackTrace()
                            // Bluetooth bağlantısı kesildiğinde buraya gelecek
                            // Bağlantıyı kapat ve yeniden bağlanma işlemlerini yap
                            savePropertiesToDatabase()
                            bluetoothSocket.close()
                            retryConnection = true
                            connectWithRetry2(index)
                            break
                        }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                // Veri alımı sırasında hata oluştuğunda bir log mesajı göster
                Log.e("Bluetooth", "Error reading data: ${e.message}")
            }
        }
    }

// ... (Diğer fonksiyonlarınızı buraya ekleyin)


    // Veri türü tanımlamaları
    private enum class DataType {
        Torque,
        Temperature
    }

    private fun processReceivedData(receivedData: String, dataType: DataType, index: Int): String? {
        val torquePrefix = "Torque="
        val tempPrefix = "Temp="
        val prefix = when (dataType) {
            DataType.Torque -> torquePrefix
            DataType.Temperature -> tempPrefix
        }
        val dataIndex = receivedData.indexOf(prefix)
        return if (dataIndex >= 0) {
            val dataEndIndex = receivedData.indexOf('N', dataIndex + prefix.length)
            val data = if (dataEndIndex >= 0) {
                receivedData.substring(dataIndex + prefix.length, dataEndIndex)
            } else {
                receivedData.substring(dataIndex + prefix.length)
            }
            when (dataType) {
                DataType.Torque -> {
                    var numericValue = ""
                    if ('.' in data) {
                        numericValue = data.substringBefore(".").trim()
                    }
                    else {
                        numericValue = data.trim()
                    }
                    numericValue.toIntOrNull()?.toString()
                }
                DataType.Temperature -> {
                    val numericValue = extractNumericTempValue(data)
                    numericValue
                }
            }
        } else {
            null
        }
    }

    // Verinin işlendiği fonksiyondan çağrılan Temp verisinin numerik hale getirildiği fonksiyon
    private fun extractNumericTempValue(data: String): String? {
        var numericPart = ""
        var startIndex = -1
        var endIndex = -1
        var isNumericStarted = false

        for (i in data.indices) {
            val char = data[i]

            if (char.isDigit() || char == '-' || char == '.') {
                if (!isNumericStarted) {
                    startIndex = i
                    isNumericStarted = true
                }
                endIndex = i
            } else if (isNumericStarted) {
                break
            }
        }

        if (startIndex != -1 && endIndex != -1) {
            numericPart = data.substring(startIndex, endIndex + 1)
        }
        return numericPart.trim()
    }

    // Tork verisinin güncellendiği fonksiyon
    private fun updateTorqueData(torque: Int, index: Int) {
        if (index == 1) {
            formattedTorque = torque
            val deviceIndex1 = 1
            if (!offset) {
                textViewTorque.text = formattedTorque.toString()
                formattedTorque.toString().let { veriIslemleri(it, deviceIndex1) }
                addTorqueEntry(formattedTorque, 1)
            }
            else {
                offsetTorqueValue(formattedTorque.toString(), sabitTorque.toString().toInt(), deviceIndex1)
            }
            val numericTorque = formattedTorque.toString().toIntOrNull()
            try {
                veriIslemleri(numericTorque?.toString(), deviceIndex1)
            } catch (e: Exception) {
                e.printStackTrace()
                // Hata durumunda yapılması gereken işlemleri buraya ekleyebilirsiniz
            }

        } else if (index == 2) {
            val deviceIndex2 = 2
            formattedTorque2 = torque
            if (!offset2) {
                textViewTorque2.text = formattedTorque2.toString()
                formattedTorque2.toString().let { veriIslemleri(it, deviceIndex2) }
                addTorqueEntry(formattedTorque2, 2)
            }
            else {
                offsetTorqueValue(formattedTorque2.toString(), sabitTorque2.toString().toInt(), deviceIndex2)
            }
            val numericTorque2 = formattedTorque2.toString().toIntOrNull()
            try {
                veriIslemleri(numericTorque2?.toString(), deviceIndex2)
            } catch (e: Exception) {
                e.printStackTrace()
                // Hata durumunda yapılması gereken işlemleri buraya ekleyebilirsiniz
            }
        }
    }

    // Tork verisinin ölçeklendiği fonksiyon
    private fun offsetTorqueValue(offsetTorque: String, sabitTork: Int, deviceIndex: Int) {
        var graphTorque: Int?
        if (offsetTorque.isNotEmpty()) {
            if (deviceIndex == 1) {
                //textViewDiff.text = ("$offsetTorque, $sabitTork")
                val offsetTorque1 = ((offsetTorque.toInt() - sabitTork) / div).toString()
                textViewTorque.text = offsetTorque1
                graphTorque = textViewTorque.text.toString().toInt()
                if (offsetTorque1 != null && lowhigh) {
                    if (offsetTorque1.toInt() > highestTorque) {
                        highestTorque = offsetTorque1.toInt()
                        textViewHTorque.text = highestTorque.toString()
                    }
                    if (offsetTorque1.toInt() < lowestTorque) {
                        lowestTorque = offsetTorque1.toInt()
                        textViewLTorque.text = lowestTorque.toString()
                    }
                }
            }
            else {
                //textViewDiff2.text = ("$offsetTorque, $sabitTork")
                val offsetTorque2 = ((offsetTorque.toInt() - sabitTork) / div2).toString()
                textViewTorque2.text = offsetTorque2
                graphTorque = textViewTorque2.text.toString().toInt()
                if (offsetTorque2 != null) {
                    if (offsetTorque2.toInt() > highestTorque2) {
                        highestTorque2 = offsetTorque2.toInt()
                        textViewHTorque2.text = highestTorque2.toString()
                    }
                    if (offsetTorque2.toInt() < lowestTorque2) {
                        lowestTorque2 = offsetTorque2.toInt()
                        textViewLTorque2.text = lowestTorque2.toString()
                    }
                }
            }
            veriIslemleri(offsetTorque, deviceIndex)
            addTorqueEntry(graphTorque, deviceIndex)
        }
    }

    // Sıcaklık verisinin güncellendiği Fonksiyon
    private fun updateTemperatureData(temp: String, index: Int) {
        val currentTime = System.currentTimeMillis()
        if (index == 1) {
            val deviceIndex1 = 3
            if (temp.isNotEmpty()) {
                if (currentTime - lastUpdateTime >= temperatureUpdateInterval) {
                    textViewTemp.text = "$temp"
                    try {
                        veriIslemleri(temp, deviceIndex1)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        // Hata durumunda yapılması gereken işlemleri buraya ekleyebilirsiniz
                    }
                    lastUpdateTime = currentTime
                    val numericTemperature = temp.toDoubleOrNull()
                    addTempEntry(numericTemperature, 1)
                    if (numericTemperature != null) {
                        // Sıcaklık değerini karşılaştır ve en yüksek/en düşük değeri güncelle
                        if (numericTemperature > highestTemperature) {
                            highestTemperature = numericTemperature
                            textViewHTemp.text = "$highestTemperature"
                        }
                        if (numericTemperature < lowestTemperature) {
                            lowestTemperature = numericTemperature
                            textViewLTemp.text = "$lowestTemperature"
                        }
                    }
                }
            }
        }
        else if (index == 2) {
            val deviceIndex2 = 4
            if (temp.isNotEmpty()) {
                if (currentTime - lastUpdateTime >= temperatureUpdateInterval) {
                    textViewTemp2.text = "$temp"
                    try {
                        veriIslemleri(temp, deviceIndex2)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        // Hata durumunda yapılması gereken işlemleri buraya ekleyebilirsiniz
                    }
                    lastUpdateTime = currentTime
                    val numericTemperature2 = temp.toDoubleOrNull()
                    addTempEntry(numericTemperature2, 2)
                    if (numericTemperature2 != null) {
                        // Sıcaklık değerini karşılaştır ve en yüksek/en düşük değeri güncelle
                        if (numericTemperature2 > highestTemperature2) {
                            highestTemperature2 = numericTemperature2
                            textViewHTemp2.text = "$highestTemperature2"
                        }
                        if (numericTemperature2 < lowestTemperature2) {
                            lowestTemperature2 = numericTemperature2
                            textViewLTemp2.text = "$lowestTemperature2"
                        }
                    }
                }
            }
        }
    }

    // Grafik kurulumlarının yapıldığı fonksiyon
    private fun setupLineChart(lineChart: LineChart, unit: String) {
        lineChart.apply {
            setTouchEnabled(false)
            description = Description().apply { text = "" }
            setNoDataText("No data available")
            legend.isEnabled = false
            setDrawGridBackground(false)
            setDrawBorders(true)
            setBorderColor(Color.BLACK)
            setBorderWidth(0.5f)

            val xAxis = xAxis
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)
            xAxis.granularity = 5f

            val yAxisRight = axisRight
            yAxisRight.isEnabled = false

            val yAxisLeft = lineChart.axisLeft
            yAxisLeft.setDrawGridLines(true)
            yAxisLeft.gridColor = Color.WHITE
            val elapsedTimeValues = listOf(0L, 1L, 2L, 3L, 4L, 5L) // Geçen süre değerlerinizi burada tanımlayın
            val valueFormatter = MyValueFormatter(elapsedTimeValues, unit)
            xAxis.valueFormatter = valueFormatter
            yAxisLeft.valueFormatter = null
            yAxisRight.valueFormatter = null
        }
    }

    // Grafiğe tork verisinin eklendiği fonksiyon
    private fun addTorqueEntry(torque: Int?, deviceIndex: Int) {
        torque?.let {
            val entry = Entry(xAxisValue, torque.toFloat())
            if (deviceIndex == 1) {
                torqueDataList1.add(entry)
                startDataUpdate()
                if (torqueDataList1.size > maxDataPoints) {
                    torqueDataList1.removeAt(0)
                }
                val lineDataSet = LineDataSet(torqueDataList1, "Torque")
                lineDataSet.color = Color.CYAN
                lineDataSet.setDrawCircles(false)
                lineDataSet.setDrawValues(false)
                val newLineData = LineData(lineDataSet)
                lineChart.data = newLineData
                lineChart.notifyDataSetChanged()
                lineChart.invalidate()
            } else if (deviceIndex == 2) {
                torqueDataList2.add(entry)
                startDataUpdate()
                if (torqueDataList2.size > maxDataPoints) {
                    torqueDataList2.removeAt(0)
                }
                val lineDataSet = LineDataSet(torqueDataList2, "Torque")
                lineDataSet.color = Color.CYAN
                lineDataSet.setDrawCircles(false)
                lineDataSet.setDrawValues(false)
                val newLineData = LineData(lineDataSet)
                lineChart3.data = newLineData
                lineChart3.notifyDataSetChanged()
                lineChart3.invalidate()
            }
        }
    }

    // Grafiğe sıcaklık verisinin eklendiği fonksiyon
    private fun addTempEntry(temp: Double?, deviceIndex: Int) {
        temp?.let {
            val entry = Entry(xAxisValue, temp.toFloat())
            if (deviceIndex == 1) {
                tempDataList1.add(entry)
                startDataUpdate()
                if (tempDataList1.size > maxDataPoints) {
                    tempDataList1.removeAt(0)
                }
                val lineDataSet = LineDataSet(tempDataList1, "Temp")
                lineDataSet.color = Color.RED
                lineDataSet.setDrawCircles(false)
                lineDataSet.setDrawValues(false)
                val newLineData = LineData(lineDataSet)
                lineChart2.data = newLineData
                lineChart2.notifyDataSetChanged()
                lineChart2.invalidate()
            } else if (deviceIndex == 2) {
                tempDataList2.add(entry)
                startDataUpdate()
                if (tempDataList2.size > maxDataPoints) {
                    tempDataList2.removeAt(0)
                }
                val lineDataSet = LineDataSet(tempDataList2, "Temp")
                lineDataSet.color = Color.RED
                lineDataSet.setDrawCircles(false)
                lineDataSet.setDrawValues(false)
                val newLineData = LineData(lineDataSet)
                lineChart4.data = newLineData
                lineChart4.notifyDataSetChanged()
                lineChart4.invalidate()
            }
        }
    }

    // Grafiği güncel tutan fonksiyon
    private fun startDataUpdate() {
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                xAxisValue += 1f // X eksenindeki değeri güncelle
                handler.postDelayed(this, updateIntervalMillis)
            }
        }, updateIntervalMillis)
    }

    // Grafikte eksen değerlerini düzenleyen sınıf
    class MyValueFormatter(private val elapsedTimeValues: List<Long>, private val unit: String) : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            return if (value >= 0 && value < elapsedTimeValues.size) {
                "${elapsedTimeValues[value.toInt()]} $unit"
            } else {
                "" // Geçersiz bir değer için boş bir dize döndürülür
            }
        }

        private fun formatElapsedTime(elapsedTime: Long): String {
            // Elapsed time'ı istediğiniz biçimde biçimlendirin ve döndürün
            // Örneğin, dakika ve saniye olarak biçimlendirmek için aşağıdaki gibi bir yöntem kullanabilirsiniz
            val minutes = elapsedTime / 60
            val seconds = elapsedTime % 60
            return String.format("%02d:%02d", minutes, seconds)
        }
    }

    // Excelde sayfaların oluşturulduğu fonksiyon
    private fun veriIslemleri(data: String?, deviceIndex: Int) {
        try {
            when (deviceIndex) {
                1 -> {
                    if (dataCountTorque == 0) {
                        val headerRow = sheetTorque.createRow(0)
                        headerRow.createCell(0).setCellValue("Elapsed Time")
                        headerRow.createCell(1).setCellValue("Torque")
                        dataCountTorque++
                    } else {
                        addTorqueDataToExcel(data ?: "", deviceIndex)
                    }
                }
                2 -> {
                    if (dataCountTorque2 == 0) {
                        val headerRow = sheetTorque2.createRow(0)
                        headerRow.createCell(0).setCellValue("Elapsed Time")
                        headerRow.createCell(1).setCellValue("Torque")
                        dataCountTorque2++
                    } else {
                        addTorqueDataToExcel(data ?: "", deviceIndex)
                    }
                }
                3 -> {
                    if (dataCountTemp == 0) {
                        val headerRow = sheetTemp.createRow(0)
                        headerRow.createCell(0).setCellValue("Elapsed Time")
                        headerRow.createCell(1).setCellValue("Temp")
                        dataCountTemp++
                    } else {
                        addTempDataToExcel(data ?: "", deviceIndex)
                    }
                }
                4 -> {
                    if (dataCountTemp2 == 0) {
                        val headerRow = sheetTemp2.createRow(0)
                        headerRow.createCell(0).setCellValue("Elapsed Time")
                        headerRow.createCell(1).setCellValue("Temp")
                        dataCountTemp2++
                    } else {
                        addTempDataToExcel(data ?: "", deviceIndex)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            showToast("Hata oluştu: ${e.localizedMessage}")
        }
    }

    // Excelde sayfalara tork verisinin eklenmesi
    private fun addTorqueDataToExcel(data: String, deviceIndex: Int) {
        try {
            val elapsedTime = String.format("%02d:%02d:%02d.%02d", hours, minutes, seconds, milliseconds)
            // Yeni veri satırı oluşturma
            if (deviceIndex == 1) {
                val dataRow = sheetTorque.createRow(dataCountTorque)
                dataRow.createCell(0).setCellValue(elapsedTime)
                if (data != null) {
                    dataRow.createCell(1).setCellValue(data)
                } else {
                    dataRow.createCell(1).setCellValue("")
                }
                dataCountTorque++
            } else if (deviceIndex == 2) {
                val dataRow = sheetTorque2.createRow(dataCountTorque2)
                dataRow.createCell(0).setCellValue(elapsedTime)
                if (data != null) {
                    dataRow.createCell(1).setCellValue(data)
                } else {
                    dataRow.createCell(1).setCellValue("")
                }
                dataCountTorque2++
            }
            // Excel dosyasını kaydet
        } catch (e: Exception) {
            e.printStackTrace()
            showToast("Hata oluştu: ${e.localizedMessage}")
        }
    }

    // Excelde sayfalara sıcaklık verisinin eklenmesi
    private fun addTempDataToExcel(data: String, deviceIndex: Int) {
        try {
            val elapsedTime = String.format("%02d:%02d:%02d.%02d", hours, minutes, seconds, milliseconds)
            // Yeni veri satırı oluşturma
            if (deviceIndex == 3) {
                val dataRow = sheetTemp.createRow(dataCountTemp)
                dataRow.createCell(0).setCellValue(elapsedTime)
                if (data != null) {
                    dataRow.createCell(1).setCellValue(data)
                } else {
                    dataRow.createCell(1).setCellValue("")
                }

                dataCountTemp++
            } else if (deviceIndex == 4) {
                val dataRow = sheetTemp2.createRow(dataCountTemp2)
                dataRow.createCell(0).setCellValue(elapsedTime)
                if (data != null) {
                    dataRow.createCell(1).setCellValue(data)
                } else {
                    dataRow.createCell(1).setCellValue("")
                }
                dataCountTemp2++
            }
            // Excel dosyasını kaydet
        } catch (e: Exception) {
            e.printStackTrace()
            showToast("Hata oluştu: ${e.localizedMessage}")
        }
    }

    // Excel dosyasının güncellendiği fonksiyon
    private fun saveExcelFile(id: String) {
        try {
            val outputDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            outputFile = File(outputDirectory, "$id.xls").toString()
            val outputStream = FileOutputStream(outputFile)
            workbook.write(outputStream)
            outputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Uygulamada çıkan mesajlar/uyarılar için oluşturulmuş bir fonksiyon
    private fun showToast(message: String) {
        lifecycleScope.launch {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    // İzin istek kodu
    companion object {
        private const val PERMISSION_REQUEST_CODE = 1
    }
}


