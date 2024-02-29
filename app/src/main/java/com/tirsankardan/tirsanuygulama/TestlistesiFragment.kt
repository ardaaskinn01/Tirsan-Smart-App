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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.tirsankardan.tirsanuygulama.data.DatabaseHelper
import com.tirsankardan.tirsanuygulama.data.DatabaseHelper.Table.TableEntry.COLUMN_CREATED_AT
import com.tirsankardan.tirsanuygulama.data.DatabaseHelper.Table.TableEntry.COLUMN_DESCRIPTION
import com.tirsankardan.tirsanuygulama.data.DatabaseHelper.Table.TableEntry.COLUMN_ELAPSED_TIME
import com.tirsankardan.tirsanuygulama.data.DatabaseHelper.Table.TableEntry.COLUMN_ID
import com.tirsankardan.tirsanuygulama.data.DatabaseHelper.Table.TableEntry.COLUMN_STATUS
import com.tirsankardan.tirsanuygulama.data.DatabaseHelper.Table.TableEntry.COLUMN_TABLE_NAME
import com.tirsankardan.tirsanuygulama.data.DatabaseHelper.Table.TableEntry.COLUMN_TEST_NAME
import com.tirsankardan.tirsanuygulama.data.DatabaseHelper.Table.TableEntry.COLUMN_USER
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class TestlistesiFragment : Fragment() {
    private lateinit var tableLayout: TableLayout
    private lateinit var imageButton: ImageButton
    private lateinit var clearButton: Button
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var database: SQLiteDatabase
    private lateinit var evetButton: Button
    private lateinit var hayirButton: Button

    var deleting = false

    // Buton işlevlerinin tanımlanması
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_testlistesi, container, false)
        val soruView = view.findViewById<ConstraintLayout>(R.id.questionView)
        imageButton = view.findViewById(R.id.imageButton)
        clearButton = view.findViewById(R.id.button5)
        evetButton = view.findViewById(R.id.button12)
        hayirButton = view.findViewById(R.id.button13)
        imageButton.setOnClickListener {
            findNavController().popBackStack()
        }
        clearButton.setOnClickListener {
            soruView.visibility = View.VISIBLE
        }
        evetButton.setOnClickListener {
            databaseHelper = DatabaseHelper(requireContext())
            database = databaseHelper.writableDatabase
            database.delete(COLUMN_TABLE_NAME, null, null)
            val rowCount = tableLayout.childCount
            if (rowCount > 1) {
                tableLayout.removeViews(1, rowCount - 1)
            }
            soruView.visibility = View.GONE
        }
        hayirButton.setOnClickListener {
            soruView.visibility = View.GONE
        }
        return view
    }

    // Veritabanına kaydedilmiş testleri tabloya yükleyen fonksiyon
    @SuppressLint("Range", "SuspiciousIndentation")
    fun loadTests(context: Context) {
        tableLayout = requireView().findViewById(R.id.tableLayout)
        databaseHelper = DatabaseHelper(requireContext())
        database = databaseHelper.readableDatabase
        val projection = arrayOf(COLUMN_ID, COLUMN_TEST_NAME, COLUMN_DESCRIPTION, COLUMN_USER, COLUMN_STATUS, COLUMN_CREATED_AT, COLUMN_ELAPSED_TIME)
        val dbHelper = DatabaseHelper(context)
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            COLUMN_TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )

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
                            weight = 0.12f
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
                    val testDataDescription = TextView(context).apply {
                        text = description
                        layoutParams = TableRow.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT).apply {
                            weight = 0.16f
                        }
                        gravity = Gravity.CENTER
                        setTextColor(ContextCompat.getColor(context, android.R.color.white))
                        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
                            this,
                            5,  // minTextSize
                            12, // maxTextSize
                            1F.toInt(), // stepGranularity
                            TypedValue.COMPLEX_UNIT_SP // unit
                        )
                    }
                    val testDataUser = TextView(context).apply {
                        text = user
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
                    val testDataStatus = TextView(context).apply {
                        text = status
                        layoutParams = TableRow.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT).apply {
                            weight = 0.16f
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
                    val testDataCreatedAt = TextView(context).apply {
                        text = createdAt
                        layoutParams = TableRow.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT).apply {
                            weight = 0.20f
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
                            weight = 0.185f
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

                    val testDataSave = Button(context).apply {
                        // Save butonu kullanıldığında gerçekleşen olay
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
                    }

                    val testDataDelete = Button(context).apply {
                        // Delete butonu kullanıldığında gerçekleşen olay
                        setOnClickListener {
                            val rowIndex = tableLayout.indexOfChild(dataRow)
                            if (rowIndex != -1) {
                                // Veritabanından silme işlemi
                                val id = testDataId.text.toString()
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
                    }

                    tableLayout.dividerDrawable = dividerDrawable
                    val showDividersValue = TableLayout.SHOW_DIVIDER_BEGINNING or
                            TableLayout.SHOW_DIVIDER_MIDDLE or
                            TableLayout.SHOW_DIVIDER_END
                    tableLayout.showDividers = showDividersValue

                    dataRow.addView(testDataId)
                    dataRow.addView(testDataName)
                    dataRow.addView(testDataDescription)
                    dataRow.addView(testDataUser)
                    dataRow.addView(testDataStatus)
                    dataRow.addView(testDataCreatedAt)
                    dataRow.addView(testDataElapsedTime)
                    dataRow.addView(testDataSave)
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

    // Save butonuna tıklandığında çalışan indirme fonksiyonu
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

    // Uygulamada çıkan mesajlar/uyarılar için oluşturulmuş bir fonksiyon
    private fun showToast(message: String) {
        lifecycleScope.launch {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}

