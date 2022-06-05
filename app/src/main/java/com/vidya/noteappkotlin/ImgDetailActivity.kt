package com.vidya.noteappkotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.vidya.noteappkotlin.databinding.ActivityImgDetailBinding

class ImgDetailActivity : AppCompatActivity() {

    //view binding
    private lateinit var binding: ActivityImgDetailBinding

    //book id
    private var bookId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImgDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get notes id from intent
        bookId = intent.getStringExtra("bookId")!!

        //incremenet book view count, whenever this page starts
        MyApplication.incrementImg(bookId)


        loadBookDetails()

        //handle backbutton click, goback
        binding.backBtn.setOnClickListener{
            onBackPressed()
        }
    }

    private fun loadBookDetails() {
        //books > bookid > details
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.child(bookId)
            .addListenerForSingleValueEvent(object :ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    //get data
                    val categoryId = "${snapshot.child("categoryId").value}"
                    val description = "${snapshot.child("description").value}"
                    val timestamp = "${snapshot.child("timestamp").value}"
                    val title = "${snapshot.child("title").value}"
                    val uid = "${snapshot.child("uid").value}"

                    //format date
                    val date = MyApplication.formatTimeStamp(timestamp.toLong())

                    //load img category
                    MyApplication.loadCategory(categoryId, binding.categoryTv)
                    //set data
                    binding.titleTv.text = title
                    binding.categoryTv.text = categoryId
                    binding.descriptionTv.text = description
                    binding.dateTv.text = date



                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }
}