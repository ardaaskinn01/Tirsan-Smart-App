package com.tirsankardan.tirsanuygulama

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.tirsankardan.tirsanuygulama.databinding.FragmentTestolusturBinding
import java.util.*

class TestolusturFragment : Fragment() {
    private lateinit var binding: FragmentTestolusturBinding
    private val TAG = "TestolusturFragment"
    private lateinit var button:Button
    private lateinit var editText: EditText
    private lateinit var editText2: EditText
    private lateinit var spinner: Spinner
    private lateinit var spinner2: Spinner
    private lateinit var spinner3: Spinner
    private lateinit var spinner4: Spinner
    private lateinit var spinner5: Spinner
    private lateinit var spinner6: Spinner
    private lateinit var checkbox: CheckBox
    private lateinit var checkbox2: CheckBox
    private lateinit var devices: ArrayList<String>
    private lateinit var devices2: ArrayList<String>
    private val REQUEST_BLUETOOTH_PERMISSION = 1
    private val REQUEST_LOCATION_PERMISSION = 1
    private val PERMISSIONS_BLUETOOTH = arrayOf(
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.ACCESS_FINE_LOCATION // Buraya izin ekledik
    )

    // Değişken-obje eşleştirme
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTestolusturBinding.inflate(inflater, container, false)
        button = binding.testolustur
        spinner = binding.deviceSpinner
        spinner2 = binding.deviceSpinner2
        spinner3 = binding.deviceSpinner3
        spinner4 = binding.deviceSpinner4
        spinner5 = binding.deviceSpinner5
        spinner6 = binding.deviceSpinner6
        checkbox = binding.checkBox2
        checkbox2 = binding.checkBox
        editText = binding.editText
        editText2 = binding.editText2
        devices = ArrayList()
        devices2 = ArrayList() // devices2'yi başlatın
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, devices)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        val adapter2 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, devices2)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner2.adapter = adapter2
        spinner.isEnabled = false
        spinner2.isEnabled = false
        spinner3.isEnabled = false
        spinner4.isEnabled = false
        spinner5.isEnabled = false
        spinner6.isEnabled = false
        checkbox.setOnCheckedChangeListener { _, isChecked ->
            // Enable EditText and Button if CheckBox is checked
            spinner.isEnabled = isChecked
            spinner3.isEnabled = isChecked
            spinner4.isEnabled = isChecked
        }
        checkbox2.setOnCheckedChangeListener { _, isChecked ->
            // Enable EditText and Button if CheckBox is checked
            spinner2.isEnabled = isChecked
            spinner5.isEnabled = isChecked
            spinner6.isEnabled = isChecked
        }

        checkBluetoothPermissions()

        return binding.root
    }

    // Canlı test ekranına inputları gönderen fonksiyon
    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imageButton.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.testolustur.setOnClickListener {
            val selectedDevice1 = getDeviceName(spinner.selectedItem?.toString())
            val selectedDevice2 = getDeviceName(spinner2.selectedItem?.toString())
            val selectedData1 = spinner5.selectedItem?.toString()
            val selectedData2 = spinner6.selectedItem?.toString()
            val selectedData3 = spinner3.selectedItem?.toString()
            val selectedData4 = spinner4.selectedItem?.toString()
            val isCheckboxChecked = checkbox.isChecked
            val isCheckboxChecked2 = checkbox2.isChecked
            val testName1 = editText.text?.toString()
            val testName2 = editText2.text?.toString()
            val selectedDeviceAddress2 = getDeviceAddress(selectedDevice2)
            val selectedDeviceAddress1 = getDeviceAddress(selectedDevice1)

                if (isCheckboxChecked && isCheckboxChecked2) {
                    // Cihaz seçimi yapılması gerektiği için uyarı göster
                    if (editText.text.isEmpty() && editText2.text.isEmpty()) {
                        Toast.makeText(requireContext(), "Lütfen test için isim ve açıklama giriniz", Toast.LENGTH_SHORT).show()
                    }
                    else if (editText.text.isEmpty()) {
                        Toast.makeText(requireContext(), "Lütfen test için isim giriniz", Toast.LENGTH_SHORT).show()
                    }
                    else if (editText2.text.isEmpty()) {
                        Toast.makeText(requireContext(), "Lütfen test için açıklama giriniz", Toast.LENGTH_SHORT).show()
                    }
                    else if ((selectedData3 == "Not Applicable" && selectedData4 == "Not Applicable") || (selectedData1 == "Not Applicable" && selectedData2 == "Not Applicable")){
                        Toast.makeText(requireContext(), "Lütfen veri tipi seçiniz.", Toast.LENGTH_SHORT).show()
                    }
                    else {

                        if (selectedDeviceAddress1 == selectedDeviceAddress2) {
                            Toast.makeText(requireContext(), "Lütfen farklı cihazlar seçiniz.", Toast.LENGTH_SHORT).show()
                        }
                        else {
                            val bundle = Bundle()
                            bundle.putString("DEVICE_ADDRESS1", selectedDeviceAddress1.toString())
                            bundle.putString("DEVICE_ADDRESS2", selectedDeviceAddress2.toString())
                            bundle.putString("Selected_Data", selectedData1.toString())
                            bundle.putString("Selected_Data2", selectedData2.toString())
                            bundle.putString("Selected_Data3", selectedData3.toString())
                            bundle.putString("Selected_Data4", selectedData4.toString())
                            bundle.putString("Selected_Device", selectedDevice1.toString())
                            bundle.putString("Selected_Device2", selectedDevice2.toString())
                            bundle.putString("Test_Name", testName1.toString())
                            bundle.putString("Test_Description", testName2.toString())

                            try {
                                findNavController().navigate(
                                    R.id.action_testolusturFragment_to_canlitestFragment,
                                    bundle
                                )
                            } catch (e: Exception) {
                                if (e.message?.contains("getDeviceAddress") == true) {
                                    val deviceAddress = selectedDeviceAddress1
                                    val deviceAddress2 = selectedDeviceAddress2
                                    val errorMessage = "getDeviceAddress hatası. deviceAddress: $deviceAddress, $deviceAddress2, hata: ${e.message}"
                                    Log.e(TAG, errorMessage)
                                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                                } else {
                                    val errorMessage = "Bir hata oluştu: ${e.message}"
                                    Log.e(TAG, errorMessage)
                                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }

                }
                else if (isCheckboxChecked && !isCheckboxChecked2) {
                    // Cihaz seçimi yapılması gerektiği için uyarı göster
                    if (editText.text.isEmpty() && editText2.text.isEmpty()) {
                        Toast.makeText(requireContext(), "Lütfen test için isim ve açıklama giriniz", Toast.LENGTH_SHORT).show()
                    }
                    else if (editText.text.isEmpty()) {
                        Toast.makeText(requireContext(), "Lütfen test için isim giriniz", Toast.LENGTH_SHORT).show()
                    }
                    else if (editText2.text.isEmpty()) {
                        Toast.makeText(requireContext(), "Lütfen test için açıklama giriniz", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        if (selectedData3 == "Not Applicable" && selectedData4 == "Not Applicable") {
                            Toast.makeText(requireContext(), "Lütfen veri tipi seçin.", Toast.LENGTH_SHORT).show()
                        }

                        else {
                            val bundle = Bundle()
                            bundle.putString("DEVICE_ADDRESS1", selectedDeviceAddress1.toString())
                            bundle.putString("Selected_Device", selectedDevice1.toString())
                            bundle.putString("Selected_Data3", selectedData3.toString())
                            bundle.putString("Selected_Data4", selectedData4.toString())
                            bundle.putString("Test_Name", testName1.toString())
                            bundle.putString("Test_Description", testName2.toString())
                            try {
                                findNavController().navigate(
                                    R.id.action_testolusturFragment_to_canlitestFragment,
                                    bundle
                                )
                            } catch (e: Exception) {
                                if (e.message?.contains("getDeviceAddress") == true) {
                                    val deviceAddress = selectedDeviceAddress1
                                    val errorMessage =
                                        "getDeviceAddress hatası. deviceAddress: $deviceAddress, hata: ${e.message}"
                                    Log.e(TAG, errorMessage)
                                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT)
                                        .show()
                                } else {
                                    val errorMessage = "hata?: ${e.message}"
                                    Log.e(TAG, errorMessage, e) // Hata logunu detaylarıyla birlikte kaydet
                                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                        }
                    }
                }
                else if (isCheckboxChecked2 && !isCheckboxChecked) {
                    // Cihaz seçimi yapılması gerektiği için uyarı göster
                    if (editText.text.isEmpty() && editText2.text.isEmpty()) {
                        Toast.makeText(requireContext(), "Lütfen test için isim ve açıklama giriniz", Toast.LENGTH_SHORT).show()
                    }
                    else if (editText2.text.isEmpty()) {
                        Toast.makeText(requireContext(), "Lütfen test için isim giriniz", Toast.LENGTH_SHORT).show()
                    }
                    else if (editText2.text.isEmpty()) {
                        Toast.makeText(requireContext(), "Lütfen test için açıklama giriniz", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        if (selectedData1 == "Not Applicable" && selectedData2 == "Not Applicable") {
                            Toast.makeText(requireContext(), "Lütfen veri tipi seçin.", Toast.LENGTH_SHORT).show()
                        }
                        else {
                            val bundle = Bundle()
                            bundle.putString("DEVICE_ADDRESS2", selectedDeviceAddress2.toString())
                            bundle.putString("Selected_Data", selectedData1.toString())
                            bundle.putString("Selected_Data2", selectedData2.toString())
                            bundle.putString("Selected_Device2", selectedDevice2.toString())
                            bundle.putString("Test_Name", testName1.toString())
                            bundle.putString("Test_Description", testName2.toString())

                            try {
                                findNavController().navigate(R.id.action_testolusturFragment_to_canlitestFragment, bundle)
                            } catch (e: Exception) {
                                if (e.message?.contains("getDeviceAddress") == true) {
                                    val deviceAddress = selectedDeviceAddress2
                                    val errorMessage = "getDeviceAddress hatası. deviceAddress: $deviceAddress, hata: ${e.message}"
                                    Log.e(TAG, errorMessage)
                                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                                } else {
                                    val errorMessage = "Bir hata oluştu: ${e.message}"
                                    Log.e(TAG, errorMessage)
                                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }

            }
    }

    // Cihaz isimlerini düzgün şekilde yazan fonksiyon
    private fun getDeviceName(deviceName: String?): String? {
        return deviceName?.trim()
    }

    // Seçim kutusunda seçilen cihazların cihaz adreslerini elde eden fonksiyon
    private fun getDeviceAddress(deviceName: String?): String? {
        val bluetoothManager = requireContext().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter
        if (bluetoothAdapter == null || deviceName.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Bluetooth adapter or device name is invalid.", Toast.LENGTH_SHORT).show()
            return null
        }

        val pairedDevices = if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            null
        } else {
            bluetoothAdapter.bondedDevices
        }

        if (pairedDevices != null) {
            for (device in pairedDevices) {
                if (device.name?.toLowerCase(Locale.getDefault()) == deviceName?.toLowerCase(Locale.getDefault())) {
                    val deviceAddress = device.address
                    if (deviceAddress != null) {
                        return deviceAddress
                    } else {
                        Toast.makeText(requireContext(), "Device address could not be obtained: $deviceName", Toast.LENGTH_SHORT).show()
                        return null
                    }
                }
            }
        }

        Toast.makeText(requireContext(), "Device address could not be obtained: $deviceName", Toast.LENGTH_SHORT).show()
        return null
    }

    // Ekran açıldıktan sonra bluetooth kontrollerini başlatır
    override fun onStart() {
        super.onStart()
        spinner.contentDescription = "Bluetooth cihazları listesi"
    }

    // Bu fonksiyon, belirtilen izinlerin verilip verilmediğini kontrol eder
    private fun hasPermissions(vararg permissions: String): Boolean {
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    // Bluetooth başlatılmadan önceki son kontrol
    private fun checkBluetoothPermissions() {
        if (!hasPermissions(*PERMISSIONS_BLUETOOTH)) {
            // Bluetooth izni verilmemiş, izin isteği yap
            requestPermissions(PERMISSIONS_BLUETOOTH, REQUEST_BLUETOOTH_PERMISSION)
        } else if (!hasPermissions(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            // Konum izni verilmemiş, konum izni isteği yap
            requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_LOCATION_PERMISSION)
        } else {
            // Her iki izin de verilmiş, Bluetooth'ı başlat
            startBluetooth()
        }
    }


    // Adaptörler bağlanır ve Bluetooth bağlantısı için her şey hazırlanıp bluetooth başlatılır
    private fun startBluetooth() {
        val bluetoothManager = requireContext().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter
        if (bluetoothAdapter == null) {
            // Bluetooth desteklenmiyorsa işlem yapmayın
            return
        }

        devices.clear()
        devices2.clear()
        val adapter = spinner.adapter as ArrayAdapter<String>
        val adapter2 = spinner2.adapter as ArrayAdapter<String>
        val temperatureAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            listOf("Not Applicable", "Temperature", "Torque")
        )
        temperatureAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner3.adapter = temperatureAdapter

        val torqueAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            listOf("Not Applicable", "Temperature", "Torque")
        )
        torqueAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner4.adapter = torqueAdapter

        val temperatureAdapter2 = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            listOf("Not Applicable", "Temperature", "Torque")
        )
        temperatureAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner5.adapter = temperatureAdapter2

        val torqueAdapter2 = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            listOf("Not Applicable", "Temperature", "Torque")
        )
        torqueAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner6.adapter = torqueAdapter2

        val pairedDevices = bluetoothAdapter.bondedDevices
        for (device in pairedDevices) {
            val deviceName: String
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // İzin verilmediyse, deviceName'i boş bir dizeye ayarla
                deviceName = ""
            } else {
                // İzin verildiyse, deviceName'i cihaz adına ayarla
                deviceName = device.name ?: ""
            }
            val deviceAddress = device.address
            val deviceString = "$deviceName"
            devices.add(deviceString)
            devices2.add(deviceString) // İkinci spinner'a da cihazı ekle
        }

        adapter.notifyDataSetChanged() // adapter'ın güncellenen verilerle yenilenmesi
        adapter2.notifyDataSetChanged()
        Log.d("TestOlusturFragment", "Paired devices: ${devices.joinToString()}")
    }

    //  Bluetooth izin isteğinin sonucunu işlemek için kullanılıyor
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_BLUETOOTH_PERMISSION) {
            try {
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    // Bluetooth ve konum izinleri verilmiş, Bluetooth'ı başlat
                    startBluetooth()
                } else {
                    // Bluetooth ve/veya konum izinleri verilmemiş, kullanıcıyı bilgilendirin ve izin isteği yapın
                    // Bluetooth izni
                    if (ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.BLUETOOTH_CONNECT
                        ) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(
                            requireActivity(),
                            arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                            REQUEST_BLUETOOTH_PERMISSION
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
