package com.tirsankardan.tirsanuygulama

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Environment
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.tirsankardan.tirsanuygulama.data.DatabaseHelper
import com.tirsankardan.tirsanuygulama.data.DatabaseHelper.Table.TableEntry.COLUMN_CREATED_AT
import com.tirsankardan.tirsanuygulama.data.DatabaseHelper.Table.TableEntry.COLUMN_DESCRIPTION
import com.tirsankardan.tirsanuygulama.data.DatabaseHelper.Table.TableEntry.COLUMN_ELAPSED_TIME
import com.tirsankardan.tirsanuygulama.data.DatabaseHelper.Table.TableEntry.COLUMN_ID
import com.tirsankardan.tirsanuygulama.data.DatabaseHelper.Table.TableEntry.COLUMN_STATUS
import com.tirsankardan.tirsanuygulama.data.DatabaseHelper.Table.TableEntry.COLUMN_TABLE_NAME
import com.tirsankardan.tirsanuygulama.data.DatabaseHelper.Table.TableEntry.COLUMN_TEST_NAME
import com.tirsankardan.tirsanuygulama.data.DatabaseHelper.Table.TableEntry.COLUMN_USER
import com.tirsankardan.tirsanuygulama.databinding.FragmentAnasayfaBinding
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class AnasayfaFragment : Fragment() {
    private lateinit var tableLayout: TableLayout
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var database: SQLiteDatabase
    private lateinit var binding: FragmentAnasayfaBinding

    // Buton işlevlerinin tanımlanması
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentAnasayfaBinding.inflate(inflater, container, false)

        // Test oluşturma ve test listesi ekranlarına geçişi sağlayan kod bloğu
        val listebuttonBinding = binding.testListButton
        val olusturbuttonBinding = binding.testOlusturButton
        olusturbuttonBinding.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_anasayfa_to_testolusturFragment)
        }
        listebuttonBinding.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_anasayfa_to_testlistesiFragment)
        }
        return binding.root
    }

    // Testleri tablo şeklinde gösteren fonksiyon
    @SuppressLint("SuspiciousIndentation")
    private fun loadTests(context: Context) {
        tableLayout = requireView().findViewById(R.id.tableLayout2)
        databaseHelper = DatabaseHelper(requireContext())
        database = databaseHelper.readableDatabase
        val projection = arrayOf(
            COLUMN_ID,
            COLUMN_TEST_NAME,
            COLUMN_DESCRIPTION,
            COLUMN_USER,
            COLUMN_STATUS,
            COLUMN_CREATED_AT,
            COLUMN_ELAPSED_TIME
        )
        val dbHelper = DatabaseHelper(context)
        val db = dbHelper.readableDatabase
        val query = "SELECT * FROM ${COLUMN_TABLE_NAME} ORDER BY ROWID ASC LIMIT 6 OFFSET (SELECT COUNT(*) FROM ${COLUMN_TABLE_NAME}) - 6"
        val cursor: Cursor = db.rawQuery(query, null)

        try {
            with(cursor) {
                while (moveToNext()) {
                    val id = getString(getColumnIndexOrThrow(COLUMN_ID))
                    val testName = getString(getColumnIndexOrThrow(COLUMN_TEST_NAME))
                    val description = getString(getColumnIndexOrThrow(COLUMN_DESCRIPTION))
                    val user = getString(getColumnIndexOrThrow(COLUMN_USER))
                    val status = getString(getColumnIndexOrThrow(COLUMN_STATUS))
                    val createdAt = getString(getColumnIndexOrThrow(COLUMN_CREATED_AT))
                    val elapsedTime = getString(getColumnIndexOrThrow(COLUMN_ELAPSED_TIME))

                    val dividerDrawable = ColorDrawable(Color.WHITE)
                    val dataRow = TableRow(context).apply {
                        // LayoutParams'leri ayarlayın
                        layoutParams = TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT)

                        // TableRow'a ayraç çizgisini ekleyin
                        dividerDrawable?.let { drawable ->
                            setShowDividers(TableLayout.SHOW_DIVIDER_MIDDLE or TableLayout.SHOW_DIVIDER_BEGINNING or TableLayout.SHOW_DIVIDER_END)
                            setDividerDrawable(drawable)
                        }
                    }

                    val testDataId = TextView(context).apply {
                        text = id
                        layoutParams = TableRow.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT).apply {
                            weight = 0.123f
                        }
                        gravity = Gravity.CENTER
                        setTextColor(ContextCompat.getColor(context, android.R.color.white))
                        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
                            this,
                            3,  // minTextSize
                            8, // maxTextSize
                            1F.toInt(), // stepGranularity
                            TypedValue.COMPLEX_UNIT_SP // unit
                        )
                    }
                    val testDataName = TextView(context).apply {
                        text = testName
                        layoutParams = TableRow.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT).apply {
                            weight = 0.14f
                        }
                        gravity = Gravity.CENTER
                        setTextColor(ContextCompat.getColor(context, android.R.color.white))
                        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
                            this,
                            5,  // minTextSize
                            11, // maxTextSize
                            1F.toInt(), // stepGranularity
                            TypedValue.COMPLEX_UNIT_SP // unit
                        )
                    }
                    val testDataUser = TextView(context).apply {
                        text = user
                        layoutParams = TableRow.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT).apply {
                            weight = 0.135f
                        }
                        gravity = Gravity.CENTER
                        setTextColor(ContextCompat.getColor(context, android.R.color.white))
                        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
                            this,
                            2,  // minTextSize
                            11, // maxTextSize
                            1F.toInt(), // stepGranularity
                            TypedValue.COMPLEX_UNIT_SP // unit
                        )
                    }
                    val testDataCreatedAt = TextView(context).apply {
                        text = createdAt
                        layoutParams = TableRow.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT).apply {
                            weight = 0.195f
                        }
                        gravity = Gravity.CENTER
                        setTextColor(ContextCompat.getColor(context, android.R.color.white))
                        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
                            this,
                            5,  // minTextSize
                            10, // maxTextSize
                            1F.toInt(), // stepGranularity
                            TypedValue.COMPLEX_UNIT_SP // unit
                        )
                    }
                    val testDataElapsedTime = TextView(context).apply {
                        text = elapsedTime
                        layoutParams = TableRow.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT).apply {
                            weight = 0.18f
                        }
                        gravity = Gravity.CENTER
                        setTextColor(ContextCompat.getColor(context, android.R.color.white))
                        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
                            this,
                            5,  // minTextSize
                            11, // maxTextSize
                            1F.toInt(), // stepGranularity
                            TypedValue.COMPLEX_UNIT_SP // unit
                        )
                    }

                    val testDataDetails = Button(context).apply {
                        text = "SAVE"
                        layoutParams = TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                            weight = 0.205f
                        }
                        gravity = Gravity.CENTER
                        setTextColor(ContextCompat.getColor(context, android.R.color.white))
                        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
                            this,
                            4,  // minTextSize
                            9, // maxTextSize
                            1F.toInt(), // stepGranularity
                            TypedValue.COMPLEX_UNIT_SP // unit
                        )
                        backgroundTintList = ContextCompat.getColorStateList(context, R.color.button_background_color2)
                        setOnClickListener {
                            try {
                                val tableRow = this.parent as TableRow
                                val testDataIdTextView = tableRow.getChildAt(0) as TextView
                                val id = testDataIdTextView.text.toString()

                                downloadExcelFile(id)
                            } catch (e: Exception) {
                                e.printStackTrace()
                                showToast("Hata oluştu: ${e.localizedMessage}")
                            }
                        }
                    }

                    val testDataDelete = Button(context).apply {
                        text = "DELETE"
                        layoutParams = TableRow.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT).apply {
                            weight = 0.205f
                        }
                        gravity = Gravity.CENTER
                        setTextColor(ContextCompat.getColor(context, android.R.color.white))
                        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
                            this,
                            4,  // minTextSize
                            9, // maxTextSize
                            1F.toInt(), // stepGranularity
                            TypedValue.COMPLEX_UNIT_SP // unit
                        )
                        backgroundTintList = ContextCompat.getColorStateList(context, R.color.button_background_color)
                        setOnClickListener {
                            val rowIndex = tableLayout.indexOfChild(dataRow)
                            if (rowIndex != -1) {
                                // Veritabanından silme işlemi
                                val id = testDataId.text.toString() // Silinecek testin ID'sini alın

                                val dbHelper = DatabaseHelper(context)
                                val db = dbHelper.writableDatabase

                                val selection = "$COLUMN_ID = ?"
                                val selectionArgs = arrayOf(id)

                                db.delete(COLUMN_TABLE_NAME, selection, selectionArgs)

                                db.close()

                                // Arayüzden silme işlemi
                                tableLayout.removeViewAt(rowIndex)
                            }
                        }
                    }

                    tableLayout.dividerDrawable = dividerDrawable
                    val showDividersValue = TableLayout.SHOW_DIVIDER_BEGINNING or
                            TableLayout.SHOW_DIVIDER_MIDDLE or
                            TableLayout.SHOW_DIVIDER_END
                    tableLayout.showDividers = showDividersValue

                    dataRow.addView(testDataId)
                    dataRow.addView(testDataName)
                    dataRow.addView(testDataUser)
                    dataRow.addView(testDataCreatedAt)
                    dataRow.addView(testDataElapsedTime)
                    dataRow.addView(testDataDetails)
                    dataRow.addView(testDataDelete)
                    tableLayout.addView(dataRow, 1)
                }
            }
        } catch (e: Exception) {
            val errorMessage = "Error: ${e.message}"
            showToast(errorMessage)
        } finally {
            cursor.close()
            db.close()
        }
    }

    // Save butonuna basıldığında çalışan indirme fonksiyonu
    private fun downloadExcelFile(id: String) {
        try {
            val inputDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            val inputFile = File(inputDirectory, "$id.xls")

            if (inputFile.exists()) {
                val outputDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val outputFile = File(outputDirectory, "$id.xls")

                val inputStream = FileInputStream(inputFile)
                val outputStream = FileOutputStream(outputFile)

                val buffer = ByteArray(1024)
                var read: Int
                while (inputStream.read(buffer).also { read = it } != -1) {
                    outputStream.write(buffer, 0, read)
                }

                inputStream.close()
                outputStream.flush()
                outputStream.close()

                showToast("Excel dosyası indirme işlemi tamamlandı")
            } else {
                showToast("Excel dosyası bulunamadı")
            }

        } catch (e: Exception) {
            e.printStackTrace()
            showToast("Excel dosyasını indirirken hata oluştu: ${e.localizedMessage}")
        }
    }

    // Ekran açıldığında Tablo yükleme fonksiyonunu çalıştıran fonksiyon
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadTests(requireContext())
    }

    // Uyarılar ve mesajlar için oluşturulmuş fonksiyon
    private fun showToast(message: String) {
        lifecycleScope.launch {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}
