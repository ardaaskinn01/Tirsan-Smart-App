package com.tirsankardan.tirsanuygulama.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import com.tirsankardan.tirsanuygulama.data.DatabaseHelper.Table.SQL_DELETE_ENTRIES
import com.tirsankardan.tirsanuygulama.data.DatabaseHelper.Table.TableEntry.COLUMN_DESCRIPTION
import com.tirsankardan.tirsanuygulama.data.DatabaseHelper.Table.TableEntry.COLUMN_CREATED_AT
import com.tirsankardan.tirsanuygulama.data.DatabaseHelper.Table.TableEntry.COLUMN_ELAPSED_TIME
import com.tirsankardan.tirsanuygulama.data.DatabaseHelper.Table.TableEntry.COLUMN_ID
import com.tirsankardan.tirsanuygulama.data.DatabaseHelper.Table.TableEntry.COLUMN_STATUS
import com.tirsankardan.tirsanuygulama.data.DatabaseHelper.Table.TableEntry.COLUMN_TABLE_NAME
import com.tirsankardan.tirsanuygulama.data.DatabaseHelper.Table.TableEntry.COLUMN_TEST_NAME
import com.tirsankardan.tirsanuygulama.data.DatabaseHelper.Table.TableEntry.COLUMN_USER
import com.tirsankardan.tirsanuygulama.data.DatabaseHelper.Table.createTableQuery

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {


    object Table {
        object TableEntry: BaseColumns {
            const val COLUMN_ID = "ID"
            const val COLUMN_TABLE_NAME = "Tests"
            const val COLUMN_TEST_NAME = "Test_Name"
            const val COLUMN_DESCRIPTION = "Description"
            const val COLUMN_USER = "User"
            const val COLUMN_STATUS = "Status"
            const val COLUMN_CREATED_AT = "Created"
            const val COLUMN_ELAPSED_TIME = "Time"
        }
        const val createTableQuery = "CREATE TABLE $COLUMN_TABLE_NAME (" +
                "$COLUMN_ID TEXT PRIMARY KEY," +
                "$COLUMN_TEST_NAME TEXT," +
                "$COLUMN_DESCRIPTION TEXT," +
                "$COLUMN_USER TEXT," +
                "$COLUMN_STATUS TEXT," +
                "$COLUMN_CREATED_AT TEXT," +
                "$COLUMN_ELAPSED_TIME TEXT)"

        const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS $COLUMN_TABLE_NAME"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    companion object {
        private const val DATABASE_NAME = "test.db"
        private const val DATABASE_VERSION = 1
    }
}
