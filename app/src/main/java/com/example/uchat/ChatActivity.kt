package com.example.uchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

package com.malkinfo.answerandquestion
import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database. FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.malkinfo.answerandquestion.adapter.MessagesAdapter
import com.malkinfo.answerandquestion.databinding.ActivityChatBinding
import com.malkinfo.answerandquestion.model.Message

class ChatActivity : AppCompatActivity() {

    val binding: ActivityChatBinding? = null
    val adapter: MessagesAdapter? = null
    var message: ArrayList<Message>? = null
    var senderRoom: String? = null var receiverRoom: String? = null
    var database: FirebaseDatabase? = null
    var storage: FirebaseStorage? = null
    var dialog: ProgressDialog? = null
    var senderUid: String? = null
    var receiver lid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        setSupportActionBar(binding!!.toolbar)
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        dialog = ProgressDialog(context: this@ChatActivity)
        dialog!!.setMessage("Uploading image...")
        dialog!!.setCancelable(false)
        message = ArrayList()
        val name = intent.getStringExtra(name: "name")
        val profile = intent.getStringExtra(name: "image")
        binding!!.name.text = name
        Glide.with(activity: this@ChatActivity).load(profile)
        .placeholder(R.drawable.placeholder)
            .into(binding!!.profile01)
        binding!!.imageView2.setOnClickListener { finish() }
        receiverUid = intent.getStringExtra(name: "uid")
        senderUid = FirebaseAuth.getInstance().uid
        database!!.reference.child(pathString: "Presence").child(receiverUid!!)
        .addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val status = snapshot.getValue(String::class.java)
                    if (status == "offline") {
                        binding!!.status.visibility =  View.GONE
                    } else {
                        binding!!.status.setText(status)
                        binding!!.status.visibility =  View.VISIBILE
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}

        })

        senderRoom = senderUid + receiverUid
        receiverRoom = receiverUid + senderUid
        adapter = Messages Adapter ( context: this@ChatActivity, message, senderRoom!!, receiverRoom
        binding!!. recyclerView.layoutManager = LinearLayoutManager(context: this@ChatActivity)
        binding!!. recyclerView.adapter = adapter
        database!!. reference.child( pathString: "chats")
            .child(senderRoom!!)
            .child(pathString: "message")
            .addValueEventListener(object : ValueEventListener{

            override fun onDataChange (snapshot: DataSnapshot) {
                messages!!.clear()
                for (snapshot1 in snapshot.children) {
                    val message :Message? = snapshot1.getValue(Message::class.java)
                    message!!.messageId = snapshot1.key
                    messages!!.add(message)
                }

                adapter!!.notifyDataSetChanged()
                override fun onCancelled (error: DatabaseError) {}
        })

            binding!!.sendBtn.setOnClickListener { it: View!
            }
            val messageTxt:String = binding!!.messageBox.text.toString()
            val date = Date()
            val message = Message (messageTxt, senderUid, date.time)
            binding!!.messageBox.setText("")
            val randomKey = database!!.reference.push().key
            val lastMsgObj = HashMap<String, Any>()
            lastMsgobj["lastMsg"]=message.message!!
            lastMsgObj ["lastMsgTime"]  = date.time

            database!!.reference.child( pathString: "chats").child(senderRoom!!)
                .updateChildren (lastMsgobj)
            database!!.reference.child( pathString: "chats").child(receiverRoom!!)
                .updateChildren (lastMsgobj)
            database!!.reference.child( pathString: "chats").child(senderRoom!!)
            .child(pathString: "messages")
            .child(randomKey!!)
            .setValue(message).addOnSuccessListener {it: Void!
                database!!.reference.child( pathString: "chats")
                    .child(receiverRoom!!)
                    .child(pathString: "message")
                    .child(randomKey)
                    .setValue(message)
                    .addOnSuccessListener { }

            }

            binding!!.attachment.setOnClickListener { it: View!
                val intent = Intent()
                intent.action = Intent.ACTION_GET_CONTENT
                intent.type = "image/*"
                startActivityForResult(intent, requestCode: 25)|
    }


            val handler = Handler()
            binding!!.messageBox.addTextChangedListener(object :TextWatcher{
                override fun beforeTextChanged (s: CharSequence?, start: Int, count: Int, after)
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count:
                override fun afterTextChanged(s: Editable?)
                {
                    database!!.reference.child(pathString:  "Presence")
                    .child(senderUid!!)
                    .setValue("typing...")
                    handler.removeCallbacksAndMessages(token: null)
                    handler.postDelayed(userStoppedTyping, delayMillis: 1000)
                }

                var userStoppedTyping = Runnable {
                    database!!.reference.child(pathString: "Presence")
                    .child(senderUid!!)
                    .setValue("Online")
                }
            })
        supportActionbar?.setDisplayShowTitleEnabled(false)

            override fun onResume() {
                super.onResume()
                val currentId = FirebaseAuth.getInstance().uid
                database!!.reference
                    .child( pathString: "Presence")
                    .child(currentId!!)
                    .setValue("On")
            }

            override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
                super.onActivityResult(requestCode, resultCode, data)
                if (requestCode == 25 ){
                    if (data != null){
                        if (data.data != null){
                            val selectedImage = data.data
                            val calendar = Calendar.getInstance()
                            var refence = storage!!.reference.child(pathString: "chats")
                                .child( pathString: calendar.timeInMillis.toString()+"")
                            dialog!!.show()
                            refence.putFile(selectedImage!!)

                                .addOnCompleteListener { task->
                                    dialog!!.dismiss()
                                    if (task.isSuccessful){
                                        refence.downloadUrl.addOnSuccessListener { uri->
                                            val filePath = uri.toString()
                                            val messageTxt :String = binding!!.messageBox.text.toString
                                            val date = Date()
                                            val message = Message (messageTxt, senderUid, date.time)
                                            message.message = "photo"
                                            message.imageUrl = filePath
                                            binding!!.messageBox.setText("")
                                            val randomkey = database!!.reference.push().key

                                            val lastMsgObj = HashMap<String, Any>()
                                            lastMsgObj ["lastMsg"]= message.message!!
                                            lastMsgObj ["lastMsgTime"] = date.time
                                            database!!.reference.child(pathString: "chats")
                                                .updateChildren (lastMsgobj)
                                            database!!.reference.child( pathString: "chats")
                                                .child(receiverRoom!!)
                                                .updateChildren(lastMsg0bj)
                                            database!!.reference.child( pathString: "chats")
                                                .child(senderRoom!!)
                                                .child(pathString: "messages")
                                                .child(randomkey!!)
                                                .setvalue(message).addunsuccessListener {

                                                database!!.reference.child( pathString: "chats")
                                                    .child(receiverRoom!!)
                                                    .child(pathString: "messages")
                                                    .child(randomkey)
                                                    .setValue(message)
                                                    .addOnSuccessListener {}
                                            }
                                        }
                            }
                    }
                }
            }

            override fun onPause() {
                super.onPause()
                val currentId = FirebaseAuth.getInstance().uid
                database!!.reference
                    .child( pathString: "Presence")
                    .child(currentId!!)
                    .setValue("offline")



}
}
