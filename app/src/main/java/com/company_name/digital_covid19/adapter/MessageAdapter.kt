package com.company_name.digital_covid19.adapter

import android.content.Context
import android.content.SharedPreferences
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.company_name.digital_covid19.R

import com.company_name.digital_covid19.models.Chat
import com.company_name.digital_covid19.models.Event
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MessageAdapter:RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private val MESSAGE_TYPE_LEFT=0
    private  val MESSAGE_TYPE_RIGHT=1
    private lateinit var sharedPreferences: SharedPreferences
    private var chatList = ArrayList<Chat>()
    private var context: Context? = null
    private lateinit var fuser:FirebaseUser

    constructor(context: Context, chatList: ArrayList<Chat>) : super() {
        this.chatList = chatList
        this.context = context
        sharedPreferences=context.getSharedPreferences("digitalCovidPrefs",0)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType==MESSAGE_TYPE_RIGHT){
            val view=LayoutInflater.from(context).inflate(R.layout.chat_layout_right,parent,false)
            return MessageAdapter.ViewHolder(view)
        }else{
            val view=LayoutInflater.from(context).inflate(R.layout.chat_layout_left,parent,false)
            return MessageAdapter.ViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat=chatList[position]
        holder.showMessage.text=chat.message
    }

    override fun getItemViewType(position: Int): Int {
        val nic=sharedPreferences.getString("currentUserNic","null")
        if (chatList[position].sender!!.equals(nic) && nic!=null){
            return MESSAGE_TYPE_RIGHT
        }else{
            return MESSAGE_TYPE_LEFT
        }
    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val showMessage: TextView
        val ivProfileImage: ImageView


        init {

            this.showMessage = itemView?.findViewById(R.id.show_message)
            this.ivProfileImage = itemView?.findViewById(R.id.profile_image)
        }

    }
}