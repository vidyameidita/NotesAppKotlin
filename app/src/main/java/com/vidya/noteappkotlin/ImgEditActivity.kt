package com.vidya.noteappkotlin

import android.app.AlertDialog
import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.vidya.noteappkotlin.databinding.ActivityImgEditBinding

class ImgEditActivity : AppCompatActivity() {

    //view binding
    private lateinit var binding: ActivityImgEditBinding

    private companion object {
        private const val TAG = "IMG_EDIT_TAG"
    }

    //book id get from intent started from adapterImgAdmin
    private var bookId = ""

    //progress dialog
    private lateinit var progressDialog: ProgressDialog

    //arraylist to hold category titles
    private lateinit var categoryTitleArrayList: ArrayList<String>

    //arraylist to hold category id
    private lateinit var categoryIdArrayList: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImgEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get book id to edit the book info
        bookId = intent.getStringExtra("bookId")!!

        //setup progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        loadcategories()
        loadBookInfo()

        //handle click, goback
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        //handle click, pick category
        binding.categoryTv.setOnClickListener {
            categoryDialog()
        }

        //handle click,
        binding.submitBtn.setOnClickListener {
            validateData()
        }

    }

    private fun loadBookInfo() {
        Log.d(TAG, "loadBookInfo: Loading book info")

        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.child(bookId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    //get book info
                    selectedCategoyId = snapshot.child("categoryId").value.toString()
                    val description = snapshot.child("description").value.toString()
                    val title = snapshot.child("title").value.toString()


                    //set to views
                    binding.titleEt.setText(title)
                    binding.descriptionEt.setText(description)


                    //load book category info using categoryId
                    Log.d(TAG, "onDataChange: Loading notes category info")
                    val refBookcategory = FirebaseDatabase.getInstance().getReference("Categories")
                    refBookcategory.child(selectedCategoyId)
                        .addListenerForSingleValueEvent(object :ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                //get category
                                val category = snapshot.child("category").value
                                //set to textview
                                binding.categoryTv.text = category.toString()
                            }

                            override fun onCancelled(error: DatabaseError) {

                            }
                        })
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    private var title =""
    private var description =""
    private fun validateData() {
        //get data
        title = binding.titleEt.text.toString().trim()
        description = binding.descriptionEt.text.toString().trim()

        //validate data
        if (title.isEmpty()) {
            Toast.makeText(this, "Enter Title", Toast.LENGTH_SHORT).show()
        }
        else if (description.isEmpty()) {
            Toast.makeText(this, "Enter Description", Toast.LENGTH_SHORT).show()
        }

        else if (selectedCategoyId.isEmpty()) {
            Toast.makeText(this, "Pick category", Toast.LENGTH_SHORT).show()
        }
        else{
            updateImg()
        }

    }

    private fun updateImg() {
        Log.d(TAG, "updateImg: Starting updating img info...")

        //show progress
        progressDialog.setMessage("Updating Notes info")
        progressDialog.show()

        //setup data to update to db, spellings of keys must be same as in firebase
        val hashMap = HashMap<String, Any> ()
        hashMap["title"] = "$title"
        hashMap["desciption"] = "$description"
        hashMap["categoryId"] = "$selectedCategoyId"

        //start updating
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.child(bookId)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Log.d(TAG, "updateImg: Updated successfully...")
                Toast.makeText(this, "Updated successfully...", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {e->
                Log.d(TAG, "updateImg: Failed to update due to ${e.message}")
                progressDialog.dismiss()
                Toast.makeText(this, "Failed to update due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private var selectedCategoyId = ""
    private var selectedcategoryTitle = ""

    private fun categoryDialog() {
        //show dialog to pick the category img we already got the categories

        //make string array from arraylis of string
        val categoriesArray = arrayOfNulls<String>(categoryTitleArrayList.size)
        for (i in categoryTitleArrayList.indices){
            categoriesArray[i] = categoryTitleArrayList[i]
        }

        //alert dialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose category")
            .setItems(categoriesArray){dialog, position->
                //handle click, save click clicked category id and title
                selectedCategoyId = categoryIdArrayList[position]
                selectedcategoryTitle = categoryTitleArrayList[position]

                //set to textview
                binding.categoryTv.text = selectedcategoryTitle

            }
            .show()//show dialog

    }


    private fun loadcategories() {
        Log.d(TAG, "loadcategories: loading categories...")

        categoryTitleArrayList = ArrayList()
        categoryIdArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //clear list before starting adding data into then
                categoryIdArrayList.clear()
                categoryTitleArrayList.clear()

                for (ds in snapshot.children) {
                    val id = "${ds.child("id").value}"
                    val category = "${ds.child("category").value}"

                    categoryIdArrayList.add(id)
                    categoryTitleArrayList.add(category)

                    Log.d(TAG, "onDataChange: Category ID $id")
                    Log.d(TAG, "onDataChange: Category $category")

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}