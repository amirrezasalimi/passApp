package ir.amirsalimi.passapp.fragment

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import ir.amirsalimi.passapp.R
import ir.amirsalimi.passapp.activity.Main
import ir.amirsalimi.passapp.app.AppConfig
import ir.amirsalimi.passapp.model.Password
import ir.amirsalimi.passapp.view.passwordCheck
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.fragment_new_password.view.*
import kotlinx.android.synthetic.main.input_view.view.*
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.update
import org.jetbrains.anko.toast
import se.simbio.encryption.Encryption
import java.util.*


class Fragment_newPass : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val ctx = context as FragmentActivity
        val parent: Main = activity as Main
        val view = inflater.inflate(R.layout.fragment_new_password, container, false)

        view.randomPass.setOnClickListener {
            view.password.setText(randomCharacter(9))
        }
        val password = view.password

        val passView = passwordCheck(ctx)
        val viewpass = ImageView(ctx)
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
                val state = checkPasswordState(password.getText())
                passView.state = state[0]
                passView.stateColor = state[1]
                passView.invalidate()
            }
        })
        var editId = 0
        if (parent.modalAction == "edit") {
            view._title.setText(parent.modalValue[1])
            view.password.setText(parent.modalValue[2])
            editId = parent.modalValue[0].toInt()

        }
        view.submitPassword.setOnClickListener {
            hideKeyboard(parent)

            if (view._title.text().isEmpty()) {
                view._title.setMessage("عنوان نمیتواند خالی باشد", resources.getColor(R.color.danger))
                return@setOnClickListener
            } else {
                view._title.setMessage("")
            }
            if (view.password.text().isEmpty()) {
                view.password.setMessage("رمز عبور نمیتواند خالی باشد", resources.getColor(R.color.danger))
                return@setOnClickListener
            } else {
                view.password.setMessage("")
            }
            parent.hideModal(300, fun() {
                val key = AppConfig.APP_KEY + "_pass"
                val salt = AppConfig.APP_KEY
                val iv = ByteArray(16)
                val encryption = Encryption.getDefault(key, salt, iv)
                parent.database?.use {

                    if (editId == 0) {
                        val id = insert(
                            "Passwords",
                            "title" to view._title.getText(),
                            "password" to encryption.encryptOrNull(view.password.text())
                        )
                        parent.passwordsInList.add(
                            Password(
                                id.toInt(),
                                view._title.text(),
                                encryption.encryptOrNull(view.password.text())
                            )
                        )
                        parent.passwordsList.smoothScrollToPosition(parent.passwordsInList.size - 1)
                        parent.passwordsAdapter!!.notifyItemInserted(parent.passwordsInList.size - 1)
                        parent.numberSaved.setText("+" + parent.passwordsInList.size)
                    } else {
                        update(
                            "Passwords",
                            "title" to view._title.text(),
                            "password" to encryption.encryptOrNull(view.password.getText())
                        ).whereSimple("id = ?", editId.toString())
                            .exec()

                        var pos=parent.modalValue[3]

                        parent.filterPasswords("")
                        parent.passwordsList.smoothScrollToPosition(pos.toInt())

                        parent.toast("ویرایش شد")

                    }
                }

            })

        }
        return view
    }

    fun randomCharacter(len: Int): String {
        fun IntRange.random() =
            Random().nextInt((endInclusive + 1) - start) + start

        val cc = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_+$%^*@#!"
        var o = ""
        for (i in 0..len) {
            val r_i = (0..(cc.length - 1)).random()
            o += cc[r_i]
        }
        return o
    }

    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    companion object {
        fun newInstance(): Fragment_newPass {
            return Fragment_newPass()
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


}// Required empty public constructor