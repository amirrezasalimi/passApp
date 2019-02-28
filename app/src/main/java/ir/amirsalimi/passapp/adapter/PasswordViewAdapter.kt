package ir.amirsalimi.passapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ir.amirsalimi.passapp.R
import ir.amirsalimi.passapp.model.Password
import android.support.v7.widget.RecyclerView

class PasswordViewAdapter(private val passwordList:ArrayList<Password>) : RecyclerView.Adapter<PasswordViewAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item:Password=passwordList[position]

    }
    override fun getItemCount(): Int {
        return passwordList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.password_view_item, parent, false)
        return ViewHolder(v)
    }
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){


    }
}