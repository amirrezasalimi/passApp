package ir.amirsalimi.passapp.helper

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import ir.amirsalimi.passapp.app.AppConfig
import org.jetbrains.anko.db.*
import org.w3c.dom.Text

class MyDatabaseOpenHelper(ctx: Context) : ManagedSQLiteOpenHelper(ctx, AppConfig.DATABASE, null, 1) {
    companion object {
        private var instance: MyDatabaseOpenHelper? = null

        @Synchronized
        fun getInstance(ctx: Context): MyDatabaseOpenHelper {
            if (instance == null) {
                instance = MyDatabaseOpenHelper(ctx.getApplicationContext())
            }
            return instance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Here you create tables
        db.createTable("Settings", true,
            "id" to INTEGER + PRIMARY_KEY + UNIQUE,
            "name" to TEXT,
            "value" to TEXT)
        db.createTable("Passwords", true,
            "id" to INTEGER + PRIMARY_KEY + UNIQUE,
            "title" to TEXT,
            "password" to TEXT)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Here you can upgrade tables, as usual
        db.dropTable("Settings", true)
        db.dropTable("Passwords", true)

    }
}
