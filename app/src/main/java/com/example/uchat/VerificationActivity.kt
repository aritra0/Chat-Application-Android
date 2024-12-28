package com.example.uchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.uchat.databinding.ActivityVerificationBinding
import com.google.firebase.auth.FirebaseAuth

class VerificationActivity : AppCompatActivity() {
    var binding: ActivityVerificationBinding? = null

    var auth:FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification)
    }
}
