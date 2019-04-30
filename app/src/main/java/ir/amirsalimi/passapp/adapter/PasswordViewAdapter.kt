package ir.amirsalimi.passapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ir.amirsalimi.passapp.R
import ir.amirsalimi.passapp.model.Password
import android.support.v7.widget.RecyclerView
import android.widget.RelativeLayout
import android.widget.TextView
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.widget.ImageView
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.widget.Toast
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import ir.amirsalimi.passapp.activity.Main
import ir.amirsalimi.passapp.app.AppConfig
import ir.amirsalimi.passapp.fragment.Fragment_newPass
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_register.*
import se.simbio.encryption.Encryption


class PasswordViewAdapter(_context: Context, val passwordList: ArrayList<Password>) :
    RecyclerView.Adapter<PasswordViewAdapter.ViewHolder>() {
    var context: Context? = null
    private var myClipboard: ClipboardManager? = null
    private var myClip: ClipData? = null
    init {
        context = _context
        myClipboard = _context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: Password = passwordList[position]
        holder.title.text = item.title
        holder.showPassword.setOnClickListener {
            val more = holder.more
            if (more.visibility == View.GONE) {
                togglePasswordBox(more, true)
            } else {
                togglePasswordBox(more, false)
            }
        }
        holder.copy.setOnClickListener {
            copyText(item.password)
        }
        val parent=context as Main

        val key = AppConfig.APP_KEY +"_pass"
        val salt = AppConfig.APP_KEY
        val iv = ByteArray(16)
        val encryption = Encryption.getDefault(key, salt, iv)
        holder.password.text=encryption.decryptOrNull(item.password)

        holder.edit.setOnClickListener {
            parent.modalAction="edit"
            parent.modalTitle.text = "ویرایش ${item.title}"
            parent.modalValue= arrayOf(item.id.toString(),item.title,holder.password.text.toString(),position.toString())
            parent.showAddPass()
        }
        setScaleAnimation(holder.itemView)
    }

    override fun getItemCount(): Int {
        return passwordList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.password_view_item, parent, false)
        return ViewHolder(v)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.title)
        val showPassword = itemView.findViewById<RelativeLayout>(R.id.show)
        val edit = itemView.findViewById<RelativeLayout>(R.id.edit)
        val delete = itemView.findViewById<RelativeLayout>(R.id.delete)
        val more = itemView.findViewById<RelativeLayout>(R.id.more)
        val copy = itemView.findViewById<ImageView>(R.id.copyIcon)
        val password = itemView.findViewById<TextView>(R.id.password)

    }

    fun togglePasswordBox(view: View, show: Boolean) {
        if (show) {
            view.visibility = View.VISIBLE
            view.alpha = 0.0f
            view.translationX = -20f
            view.animate()
                .translationY(0f)
                .alpha(1.0f)
                .setListener(null)
        } else {
            view.animate()
                .translationY(-20f)
                .alpha(0.0f)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        view.visibility = View.GONE
                    }
                })
        }

    }

    private fun setScaleAnimation(view: View) {
        val anim =
            ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        anim.duration = 500
        view.startAnimation(anim)
    }
    private fun copyText(text: String) {
        myClip = ClipData.newPlainText("password", text)
        myClipboard?.primaryClip = myClip;
        Toast.makeText(context, "کلمه عبور کپی شد!", Toast.LENGTH_SHORT).show()
    }
}