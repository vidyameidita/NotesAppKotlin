package com.vidya.noteappkotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.vidya.noteappkotlin.databinding.ActivityImgListAdminBinding
import java.lang.Exception
import java.util.Arrays.toString
import java.util.Objects.toString

class ImgListAdminActivity : AppCompatActivity() {

    //view bining
    private lateinit var binding: ActivityImgListAdminBinding

    private companion object{
        const val TAG = "IMG_LIST_ADMIN_TAG"
    }

    //category id, title
    private var categoryId = ""
    private var category = ""
//    private var title = ""
//    private var description = ""

    //arraylist tp hold img
    private lateinit var imgArrayList: ArrayList<ModelImg>

    //adapter
    private lateinit var adapterImgAdmin: AdapterImgAdmin

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImgListAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get from intent, that we passed from adapter
        val intent = intent
        categoryId = intent.getStringExtra("categoryId")!!
        category = intent.getStringExtra("category")!!
//        title = intent.getStringExtra("title")!!
//        description = intent.getStringExtra("description")!!

        //get from intent, that we pa
//        intent.putExtra("categoryId", categoryId)
//        intent.putExtra("title", title)
//        intent.putExtra("description", description)
//        intent.putExtra("timestamp", timestamp)
//        intent.putExtra("formattedDate", formattedDate)

        //set img category
        binding.subTitleTv.text = category

        //load img
        loadImgList()

        //search
        binding.searchEt.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence, p1: Int, p2: Int, p3: Int) {
                //filter data
                try {
                    adapterImgAdmin.filter!!.filter(s)
                }
                catch (e: Exception) {
                    Log.d(TAG, "onTextChanged: ${e.message}")

                }

            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        //handle click, goback
        binding.backBtn.setOnClickListener {

        }

    }

    private fun loadImgList() {
        //init arraylist
        imgArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Books")
        Log.e(TAG, "test: "+categoryId)
        ref.orderByChild("categoryId").equalTo(category)
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    //clear list before start adding data into it
                    imgArrayList.clear()
                    for (ds in snapshot.children){
                        Log.e(TAG, "test: "+ds.toString())
//                        get data
                        val model = ds.getValue(ModelImg::class.java)
                        //add to list
                        Log.d(TAG, "onDataChange: ${model?.title} ${model?.categoryId}")
                        if (model != null) {
                            imgArrayList.add(model)
//                            Log.d(TAG, "onDataChange: ${model.title} ${model.categoryId}")
                        }

                    }
                    //setup adapter
                    adapterImgAdmin = AdapterImgAdmin(this@ImgListAdminActivity, imgArrayList)
                    binding.booksRv.adapter = adapterImgAdmin
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(TAG, "onDataChange: error db")
                }
            })

    }
}