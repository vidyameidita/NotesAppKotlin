package com.vidya.noteappkotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.vidya.noteappkotlin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    //view binding
    private lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //handle click, begin
        binding.loginBtn.setOnClickListener{
            //will do later
            startActivity(Intent(this,LoginActivity::class.java))

        }

        //hand click,skip and continue to main screen
        binding.skipBtn.setOnClickListener{
            //wil do later
            startActivity(Intent(this, DashboardAdminActivity::class.java))
            finish()

        }

        //now lets connect with firebase
    }
}