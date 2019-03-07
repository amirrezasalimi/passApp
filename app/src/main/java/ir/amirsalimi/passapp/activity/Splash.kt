package ir.amirsalimi.passapp.activity

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import ir.amirsalimi.passapp.R
import android.support.v4.os.HandlerCompat.postDelayed
import android.util.Log
import android.content.Intent
import ir.amirsalimi.passapp.helper.MyDatabaseOpenHelper
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.parseList
import org.jetbrains.anko.db.rowParser
import org.jetbrains.anko.db.select
import java.lang.reflect.Array.get


class Splash : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val database: MyDatabaseOpenHelper = MyDatabaseOpenHelper.getInstance(getApplicationContext())

        val context: Context = this
        val handler = Handler()
        handler.postDelayed({

            database.use {
                select("Settings").exec {
                    Log.d("test:", this.count.toString())
                    Log.d("test:", this.columnNames.toString())

                    val parser = rowParser { id: Int, name: String, value: String ->
                        Triple(id, name, value)
                    }
                    for (triple in parseList(parser)) {
                        Log.d("test:",triple.first.toString()+" - "+triple.second+" - "+triple.third)
                    }

                    val nextActivity: Intent = if (this.count != 0) {
                        Intent(context, Main::class.java)
                    } else {
                        Intent(context, Register::class.java)
                    }

                    nextActivity.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

                    startActivity(nextActivity)
                    finish()

                }
            }

        }, 1500)
    }
}
