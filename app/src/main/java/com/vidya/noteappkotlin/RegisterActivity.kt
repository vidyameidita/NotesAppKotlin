package com.vidya.noteappkotlin

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.vidya.noteappkotlin.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    //view binding
    private lateinit var binding: ActivityRegisterBinding

    //firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    //progress dialog
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()

        //init progress dialog, will show while creating account
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        //handle back button click
        binding.backBtn.setOnClickListener{
            onBackPressed()//goto previous screen
        }

        //handle click, begin register
        binding.registerBtn.setOnClickListener{
            /*Steps
             * 1) Input Data
             * 2) Validate data
             * 3)create Account - Firebase Auth
             * 4) Save User Info - firabse realtime databse*/
            validateData()
        }

    }

    private var name = ""
    private var email = ""
    private var password = ""

    private fun validateData() {
        //1 input data
        name = binding.nameEt.text.toString().trim()
        email = binding.emailEt.text.toString().trim()
        password = binding.passwordEt.text.toString().trim()
        val cPassword = binding.cPasswordEt.text.toString().trim()

        //2 validate Data
        if (name.isEmpty()){
            Toast.makeText(this, "Enter Your name...", Toast.LENGTH_SHORT).show()

        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            //invalid email pattern
            Toast.makeText(this, "Invalid Rmail pattern...", Toast.LENGTH_SHORT).show()
        }
        else if (password.isEmpty()){
            //empt password
            Toast.makeText(this, "Confirm Password...", Toast.LENGTH_SHORT).show()
        }
        else if (password !=cPassword) {
            Toast.makeText(this, "Password doesn't Match...", Toast.LENGTH_SHORT).show()
        }
        else {
            createAccount()
        }

    }

    private fun createAccount() {
        //3 create account - firebase auth

        //show progress
        progressDialog.setMessage("Please wait...")
        progressDialog.show()

        //create user in firebase auth
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                //acount created, now add user info in db
                updateUserInfo()
            }
            .addOnFailureListener { e->
                //failed creating account
                progressDialog.dismiss()
                Toast.makeText(this, "failed creating account due to ${e.message}", Toast.LENGTH_SHORT).show()
            }

    }

    private fun updateUserInfo(){
        //4 save user  info - firebase realtime database
        progressDialog.setMessage("Saving user info...")

        //timestamp
        val timestamp = System.currentTimeMillis()

        //get current user uid, since user is registered so we can get it now
        val uid = firebaseAuth.uid

        //setup data to add in db
        val hashMap: HashMap<String, Any?> = HashMap()
        hashMap["uid"] = uid
        hashMap["email"] = email
        hashMap["name"] = name
        hashMap["profileImage"] = "" //add empty, will do in profile edit
        hashMap["userType"] = "user" //posible values are user/admin, will change value to admin manually on firebase db
        hashMap["timestamp"] = timestamp

        //set data in db
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(uid!!)
            .setValue(hashMap)
            .addOnSuccessListener {
                //user info saved, open user dashboard
                progressDialog.dismiss()
                Toast.makeText(this, "Account created...", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@RegisterActivity, DashboardActivity::class.java))
                finish()

            }
            .addOnFailureListener {e->
                //failed adding data to db
                progressDialog.dismiss()
                Toast.makeText(this, "failed saving user info due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}