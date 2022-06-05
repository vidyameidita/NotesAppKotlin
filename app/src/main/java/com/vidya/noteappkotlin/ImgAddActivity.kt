package com.vidya.noteappkotlin

import android.app.AlertDialog
import android.app.Application
import android.app.Instrumentation
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.vidya.noteappkotlin.databinding.ActivityImgAddBinding

class ImgAddActivity : AppCompatActivity() {

    //setup view binding activity_img_add --> ActivityImgBinding
    private lateinit var binding: ActivityImgAddBinding

    //firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    //progress dialog (show while upload pdf)
    private lateinit var progressDialog: ProgressDialog

    //arraylist to hold img categories
    private lateinit var categoryArrayList: ArrayList<ModelCategory>

//    //url of picked img
//    private var imgUri: Uri? = null

    //TAG
    private val TAG = "IMG_ADD_TAG"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImgAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        loadImgCategories()

        //setup progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        //handle click goback
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

//        handle click, show category pick dialog
        binding.categoryTv.setOnClickListener {
            categoryPickDialog()
        }

        //handle click, pick img intent
//        binding.attachImgBtn.setOnClickListener {
//            imgPickIntent()
//        }

        //handle click, start uploading img/book
        binding.submitBtn.setOnClickListener {
            //STEP 1: validate Data
            //STEP 2: Upload img to firebase storage
            //STEP 3: get url pg uploaded img
            //STEP 4: Upload Img info to firebase db

            validateData()
        }


    }

    private var title = ""
    private var description = ""
    private var category = ""

    private fun validateData() {
        //STEP 1: Validate Data
//        Log.d(TAG, "validateData: validating data")

        //get data
        title = binding.titleEt.text.toString().trim()
        description = binding.descriptionEt.text.toString().trim()
        category = binding.categoryTv.text.toString().trim()

        //validate data
        if (title.isEmpty()) {
            Toast.makeText(this, "Masukkan Judul...", Toast.LENGTH_SHORT).show()
        }
        else if (description.isEmpty()) {
            Toast.makeText(this, "Masukkan deskripsi...", Toast.LENGTH_SHORT).show()
        }
        else if (category.isEmpty()) {
            Toast.makeText(this, "Pilih Category...", Toast.LENGTH_SHORT).show()
        }
//        else if (imgUri == null){
//            Toast.makeText(this, "Pilih Gambar", Toast.LENGTH_SHORT).show()
//        }
        else {
            //data validated, begin upload
            uploadImgToStorage()
        }
    }

    private fun uploadImgToStorage() {
//        //SETP 2: Upload img to firebase storage
//        Log.d(TAG, "uploadImgToStorage: uploading to storage...")
//
//        //show progress dialog
//        progressDialog.setMessage("Upload IMG ...")
//        progressDialog.show()
//
//        //timestamp
        val timestamp = System.currentTimeMillis()
//
//        //path of img in firebase storage
//        val filePathAndname = "Books/$timestamp"
//        //storage reference
//        val storageReference = FirebaseStorage.getInstance().getReference(filePathAndname)
//        storageReference.putFile(imgUri!!)
//            .addOnSuccessListener { taskSnapshot ->
//                Log.d(TAG, "uploadImgToStorage: IMG uploaded now getting url...")
//
//                //STEP 3: get url of uploaded img
//                val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
//                while (!uriTask.isSuccessful);
//                val uploadedImgUrl = "${uriTask.result}"
//
                uploadImgInfoToDb(timestamp)
//
//            }
//            .addOnFailureListener {e->
//                Log.d(TAG, "uploadImgToStorage: failed to upload due to ${e.message}")
//                progressDialog.dismiss()
//                Toast.makeText(this, "failed to upload due to ${e.message}", Toast.LENGTH_SHORT).show()
//            }
    }

    private fun uploadImgInfoToDb(timestamp: Long) {
        //STEP 4 : Upload IMG Info to firebase db
        Log.d(TAG, "uploadImgInfoToDb: uploading to db")
        progressDialog.setMessage("Uploading img info...")

        //uid to current user
        val uid = firebaseAuth.uid

        //setup data to upload
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["uid"] = "$uid"
        hashMap["id"] = "$timestamp"
        hashMap["title"] = "$title"
        hashMap ["description"] = "$description"
        hashMap ["categoryId"] = "$category"
//        hashMap ["url"] = "$uploadedImgUrl"
        hashMap ["timestamp"] = timestamp
        hashMap ["viewsCount"] = 0
        hashMap ["downloadsCount"] = 0

        //db reference DB > Books > BookId > (Book Info)
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.child("$timestamp")
            .setValue(hashMap)
            .addOnSuccessListener {
                Log.d(TAG, "uploadImgInfoToDb: upload to db")
                progressDialog.dismiss()
                Toast.makeText(this, "Uploaded...", Toast.LENGTH_SHORT).show()
//                imgUri = null
            }
            .addOnFailureListener {e->
                Log.d(TAG, "uploadImgInfoToDb: failed to upload due to ${e.message}")
                progressDialog.dismiss()
                Toast.makeText(this, "failed to upload due to ${e.message}", Toast.LENGTH_SHORT).show()

            }

    }

    private fun loadImgCategories() {
        Log.d(TAG, "LoadImgcategories: Loading Img categories")
        //init arraylist
        categoryArrayList = ArrayList()

        //db reference to load categories MG > Categories
        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //clear list before adding data
                categoryArrayList.clear()
                for (ds in snapshot.children){
                    //get data
                    val model = ds.getValue(ModelCategory::class.java)
                    //add to arrayList
                    categoryArrayList.add(model!!)
                    Log.d(TAG, "onDataChage: ${model.category}")
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private var selectedCategoryId = ""
    private var selectedCategoryTitle = ""

    private fun categoryPickDialog() {
        Log.d(TAG, "categoryPickDialog: Showing img category pick dialog")

        //get string array of categories from arraylist
        val categoriesArray = arrayOfNulls<String>(categoryArrayList.size)
        for (i in categoryArrayList.indices){
            categoriesArray[i] = categoryArrayList[i].category

        }

        //alert dialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pick Category")
            .setItems(categoriesArray) {dialog, which ->
                //handle items click
                //get click item
                selectedCategoryTitle = categoryArrayList[which].category
                selectedCategoryId = categoryArrayList[which].id
                //set category to textview
                binding.categoryTv.text = selectedCategoryTitle

                Log.d(TAG, "categoryPickDialog: Selected Category ID: $selectedCategoryId")
                Log.d(TAG, "categoryPickDialog: Selected category Title: $selectedCategoryTitle")
            }
            .show()
    }

//    private fun imgPickIntent() {
//
//        Log.d(TAG, "imgPickIntent: starting img pick intent")
//
//        val intent = Intent(Intent.ACTION_PICK)
//        intent.type = "image/*"
//        intent.action = Intent.ACTION_GET_CONTENT
//        imgActivityResultLauncher.launch(intent)
//    }
//
//    val imgActivityResultLauncher = registerForActivityResult(
//        ActivityResultContracts.StartActivityForResult(),
//        ActivityResultCallback<ActivityResult>{result ->
//            if (result.resultCode == RESULT_OK) {
//                Log.d(TAG, "IMG Picked")
//                imgUri = result.data!!.data
//            }
//            else{
//                Log.d(TAG, "IMG Pick cancelled")
//                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
//            }
//
//        }
//    )
}