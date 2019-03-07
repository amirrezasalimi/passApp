package ir.amirsalimi.passapp.activity

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.view.View
import ir.amirsalimi.passapp.R
import ir.amirsalimi.passapp.view.passwordCheck
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.input_view.view.*
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import ir.amirsalimi.passapp.app.AppConfig.Companion.APP_KEY
import ir.amirsalimi.passapp.helper.MyDatabaseOpenHelper
import org.jetbrains.anko.db.insert
import se.simbio.encryption.Encryption


class Register : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        val passView = passwordCheck(this)
        val viewpass = ImageView(this)
        var viewPassActive: Boolean = false


        password.addView(passView, "left")
        val params = LinearLayout.LayoutParams(40, 40)
        //params.setMargins(3,3,3,30)
        params.leftMargin = 5
        params.topMargin = 12
        viewpass.layoutParams = params
        viewpass.setImageResource(R.drawable.ic_hide_view)
        viewpass.setOnClickListener {
            if (viewPassActive) {
                viewpass.setImageResource(R.drawable.ic_hide_view)
                password.passwordMod(true)
                viewPassActive = false
            } else {
                viewpass.setImageResource(R.drawable.ic_view)
                password.passwordMod(false)
                viewPassActive = true
            }
        }
        password.addView(viewpass, "left")
        password.input.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val state = checkPasswordState(password.input.text.toString())
                passView.state = state[0]
                passView.stateColor = state[1]
                passView.invalidate()
            }
        })
        if (password.text().isEmpty()) {
            passView.visibility = View.VISIBLE
        }

        okBtn.setOnClickListener {
            okLoading.visibility = View.VISIBLE
            Handler().postDelayed({

                if (SubmitUser() == "ok") {
                    val database: MyDatabaseOpenHelper = MyDatabaseOpenHelper.getInstance(this)

                    val key = APP_KEY+"_pass"
                    val salt = APP_KEY
                    val iv = ByteArray(16)
                    val encryption = Encryption.getDefault(key, salt, iv)
                    database.use{
                        insert("Settings",
                            "name" to "name",
                                    "value" to name.text()
                            )
                        insert("Settings",
                            "name" to "password",
                            "value" to encryption.encryptOrNull(password.text())
                        )
                    }

                    val nextActivity = Intent(this, Main::class.java)
                    nextActivity.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(nextActivity)
                    finish()
                }
                okLoading.visibility = View.GONE
            }, 200)
        }
        //okLoading
        ValueAnimator.ofFloat(0f, 360f).apply {
            addUpdateListener {
                okLoading.rotation = it.animatedValue as Float
            }
            duration = 800L
            repeatMode = ValueAnimator.RESTART
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            start()
        }
    }

    fun SubmitUser(): Any {
        if (name.text().isEmpty()) {

            return name.setMessage("نام کاربری نمیتواند خالی باشد", resources.getColor(R.color.danger))
        } else {
            name.setMessage("")
        }
        if (password.text().isEmpty()) {

            return password.setMessage("رمز عبور نمیتواند خالی باشد", resources.getColor(R.color.danger))
        } else {
            password.setMessage("")
        }
        if (againPassword.text().isEmpty()) {

            return againPassword.setMessage("تکرار رمز عبور نمیتواند خالی باشد", resources.getColor(R.color.danger))
        } else {
            againPassword.setMessage("")
        }
        if (password.text() != againPassword.text()) {
            return againPassword.setMessage("تکرار رمز عبور مطابقت ندارد", resources.getColor(R.color.danger))
        } else {
            return "ok"
        }
    }

    fun checkPasswordState(text: String): Array<String> {
        var score = 0
        if (text.matches(Regex("^(?=.*[A-Z].*[A-Z])(?=.*[!@#\$&*])(?=.*[0-9].*[0-9])(?=.*[a-z].*[a-z].*[a-z]).{0,32}\$"))) {
            score += 9
        }
        if (text.length > 4) {
            score += 6
        }
        if (text.length > 8) {
            score += 2
        }
        Log.d("test:", score.toString())
        if (score <= 3) {
            return arrayOf("ضعیف", "#F63E50")
        }
        if (score <= 6) {
            return arrayOf("متوسط", "#F6C53E")
        }
        if (score > 6) {
            return arrayOf("امن", "#5BF63E")
        }
        return arrayOf("", "")
    }
}
