package ir.amirsalimi.passapp.activity

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.widget.LinearLayout
import ir.amirsalimi.passapp.R
import ir.amirsalimi.passapp.adapter.PasswordViewAdapter
import ir.amirsalimi.passapp.model.Password
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import ir.amirsalimi.passapp.helper.MyDatabaseOpenHelper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import ir.amirsalimi.passapp.model.Settings
import org.jetbrains.anko.db.*
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Handler
import android.support.v4.app.FragmentTransaction
import android.view.KeyEvent
import ir.amirsalimi.passapp.fragment.Fragment_newPass
import java.util.*


class Main : BaseActivity() {
    var passwordsAdapter: PasswordViewAdapter? = null
    var database: MyDatabaseOpenHelper? = null
    var passwordsInList: ArrayList<Password> = ArrayList<Password>()
    var modalAction:String=""
    val modalValue = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = MyDatabaseOpenHelper.getInstance(this)
        passwordsList.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false).apply{
            reverseLayout=true
        }
        passwordsAdapter = PasswordViewAdapter(this, passwordsInList)
        passwordsList.adapter = passwordsAdapter
        database?.use {

            //get user name from settings
            select("Settings").whereSimple("name = ?", "name").exec {
                val rowParser = classParser<Settings>()
                val settings = parseSingle(rowParser)
                name.text = settings.value
            }

            select("Passwords").exec {
                numberSaved.text = "+${this.count}"
               /* if (this.count < 5) {
                    insert(
                        "Passwords",
                        "title" to "تست یک",
                        "password" to "gdfgdfgddgfj"
                    )
                    insert(
                        "Passwords",
                        "title" to "تست دومین",
                        "password" to "gdfgdfgddgfj"
                    )
                    insert(
                        "Passwords",
                        "title" to "تست سومی",
                        "password" to "gdfgdfgddgfj"
                    )
                    insert(
                        "Passwords",
                        "title" to "تست چارمییی",
                        "password" to "gdfgdfgddgfj"
                    )
                    insert(
                        "Passwords",
                        "title" to "تست پنج",
                        "password" to "gdfgdfgddgfj"
                    )
                    insert(
                        "Passwords",
                        "title" to "ببب شششششیش",
                        "password" to "gdfgdfgddgfj"
                    )
                }*/
                val parser = rowParser { id: Int, title: String, password: String ->
                    Triple(id, title, password)
                }
                passwordsInList.clear()
                for (triple in parseList(parser)) {
                    passwordsInList.add(Password(title = triple.second, password = triple.third))
                }
                passwordsAdapter!!.notifyDataSetChanged()
            }
        }
        addPassword.setOnClickListener {
            startActivity(Intent(this, modal::class.java))
        }
        search.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int,
                count: Int
            ) {
                filterPasswords(search.text.toString())
            }
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int,
                after: Int
            ) {
            }
            override fun afterTextChanged(s: Editable) {}
        })
        searchBtn.setOnClickListener {
            filterPasswords(search.text.toString())
        }
        addPassword.setOnClickListener {
            modalTitle.text = "افزودن پسورد"
            frgmngr().replace(R.id.mainFrg, Fragment_newPass.newInstance()).commit()
            showModal(R.layout.newpassword_modal)

        }
        hideSheet.setOnClickListener {
            hideModal()
        }
        //init models code
    }
    fun frgmngr(): FragmentTransaction {
         return supportFragmentManager
            .beginTransaction()
    }

    public fun filterPasswords(text: String) {
        database?.use {
            select("Passwords").exec {
                val parser = rowParser { id: Int, title: String, password: String ->
                    Triple(id, title, password)
                }
                passwordsInList.clear()
                for (triple in parseList(parser)) {
                    if (text.isEmpty()) {
                        passwordsInList.add(Password(title = triple.second, password = triple.third))
                    } else {
                        if (triple.second.matches(Regex(".*$text.*"))) {
                            passwordsInList.add(Password(title = triple.second, password = triple.third))
                        }
                    }
                    passwordsAdapter!!.notifyDataSetChanged()
                }
            }
        }
    }
    fun showModal(layout: Int){
        val view = modal
        view.visibility = View.VISIBLE
        view.alpha = 0.0f
        view.translationY =900f
        view.animate().apply { duration = 500L }
            .translationY(0f)
            .alpha(1.0f)
            .setListener(null)
     }
    fun hideModal(time:Long=500L,onEnd:()->Unit?={}) {
        val view = modal
        view!!.animate().apply { duration = time }
            .translationY(900f)
            .alpha(0.0f)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    view.visibility = View.GONE
                    onEnd()
                }
            })
    }

    var doubleBackToExitPressedOnce = false

    override
    fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {

        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
            && keyCode == KeyEvent.KEYCODE_BACK
            && event.getRepeatCount() === 0
        ) {
            onBackPressed()
            return true
        }
         return super.onKeyDown(keyCode, event)
    }


    override fun onBackPressed() {
        hideModal()

        if (doubleBackToExitPressedOnce) {
            finish()
            return
        }
        this.doubleBackToExitPressedOnce = true
        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 1000)
    }
}