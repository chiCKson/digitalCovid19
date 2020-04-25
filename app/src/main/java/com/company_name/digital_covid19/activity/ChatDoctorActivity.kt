package com.company_name.digital_covid19.activity

import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressPie
import com.company_name.digital_covid19.R
import com.company_name.digital_covid19.adapter.MessageAdapter

import com.company_name.digital_covid19.databinding.ActivityChatDoctorBinding
import com.company_name.digital_covid19.models.Chat
import com.company_name.digital_covid19.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sdsmdg.tastytoast.TastyToast

class ChatDoctorActivity : AppCompatActivity() {
    private lateinit var binding:ActivityChatDoctorBinding
    private lateinit var docId:String
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var chatList:ArrayList<Chat>

    var ref = FirebaseDatabase.getInstance().getReference()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this, R.layout.activity_chat_doctor)
        setSupportActionBar(binding.toolbarChatlist)
        sharedPreferences=this.getSharedPreferences("digitalCovidPrefs",0)
        binding.recyclerViewChat.setHasFixedSize(true)
        val linearLayoutManager=LinearLayoutManager(applicationContext)
        linearLayoutManager.stackFromEnd=true
        binding.recyclerViewChat.layoutManager=linearLayoutManager
        supportActionBar!!.title=""
        val iin = intent
        val b = iin.extras
        if (b != null) {
            docId = b.getString("docId")!!
            getDocter(docId)
            val nic=sharedPreferences.getString("currentUserNic","null")
            readMessage(nic,docId)
            //sendVerificationCode(mobile)

        }
        binding.btnSendChat.setOnClickListener {
            val nic=sharedPreferences.getString("currentUserNic","null")
            if (binding.etChat.text.toString()!="" && nic!="null")
                sendMessage(nic,docId,binding.etChat.text.toString())
            binding.etChat.setText("")
        }
        binding.backChatlis.setOnClickListener {
            finish()
        }
    }
    private fun getDocter(id:String){
        val dialog = ACProgressPie.Builder(this)
                .ringColor(Color.WHITE)
                .pieColor(Color.WHITE)
                .updateType(ACProgressConstant.PIE_AUTO_UPDATE)
                .build()
        dialog.show()
        var dRef = FirebaseDatabase.getInstance().getReference("admin")
        dRef.child(id).addValueEventListener( object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val doctor=dataSnapshot.getValue(Doctor::class.java)
                binding.docNameChat.text=doctor!!.username
                dialog.dismiss()
            }
            override fun onCancelled(error: DatabaseError) {
                dialog.dismiss()
                TastyToast.makeText(applicationContext, resources.getString(R.string.error_db),
                        TastyToast.LENGTH_LONG, TastyToast.ERROR)
            }
        })
    }
    private fun sendMessage(sender:String,receiver:String,message:String){
        val nic=sharedPreferences.getString("currentUserNic","null")
        var hashMap:HashMap<String,String> = HashMap()
        hashMap.put("sender",sender)
        hashMap.put("receiver",receiver)
        hashMap.put("message",message)
        ref.child("messages").push().setValue(hashMap)
        ref.child("chatListDoctorWise").child(receiver).child(FirebaseAuth.getInstance().currentUser!!.uid).setValue(sender)
        ref.child("chatReadByDoctor").child(receiver).child(nic).setValue("false")
    }
    private fun readMessage(myId:String,userId:String){
        chatList= ArrayList()
        val chatListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                chatList.clear()
                for (child in dataSnapshot.children){
                    val chat=child.getValue(Chat::class.java)
                    if (chat!!.receiver.equals(myId) && chat.sender.equals(userId) ||
                            chat.receiver.equals(userId) && chat.sender.equals(myId)){
                        chatList.add(chat)
                    }
                }

                messageAdapter= MessageAdapter(this@ChatDoctorActivity,chatList)
                binding.recyclerViewChat.adapter=messageAdapter


            }
            override fun onCancelled(databaseError: DatabaseError) {
                val e=databaseError.toException()

                TastyToast.makeText(applicationContext, resources.getString(R.string.error_db),
                        TastyToast.LENGTH_LONG, TastyToast.ERROR)
            }
        }
        ref.child("messages").addValueEventListener(chatListener)

    }
}
