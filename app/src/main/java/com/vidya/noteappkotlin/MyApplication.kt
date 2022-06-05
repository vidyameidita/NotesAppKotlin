package com.vidya.noteappkotlin

import android.app.Application
import android.app.ProgressDialog
import android.content.Context
import android.text.format.DateFormat
import android.util.Log
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.sql.Timestamp
import java.util.*

class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()
    }

    companion object {
        //created a static method to convert timestamp to proper date format, so we can use it everywhere in project, no need to rewrite agan
        fun formatTimeStamp(timestamp: Long) : String{
            val cal = Calendar.getInstance(Locale.ENGLISH)
            cal.timeInMillis = timestamp
            //format dd/MM/YYYY
            return DateFormat.format("dd/MM/yyyy", cal).toString()
        }

        //function to get img size
//        fun loadImgSize(imgUrl: String?, imgTitle: String, sizeTv: TextView ) {
//            val TAG = "IMG_SIZE_TAG"
//            //using url we can get title get file and its metadata from firebase storage
//            val ref = FirebaseStorage.getInstance().getReferenceFromUrl(imgUrl)
//            ref.metadata
//                .addOnSuccessListener {storageMetaData ->
//                    Log.d(TAG, "loadImgSize: got metadata")
//                    val bytes = storageMetaData.sizeBytes.toDouble()
//                    Log.d(TAG, "loadImgSize: Size Bytes $bytes")
//
//                    //convert bytes to KB/MB
//                    val kb = bytes/1024
//                    val mb = kb/1024
//                    if (mb>=1) {
//                        sizeTv.text = "${String.format("$.2f", mb)} MB"
//                    }
//                    else if (kb>=1) {
//                        sizeTv.text = "${String.format("$.2f", kb)} MB"
//                    }
//                    else {
//                        sizeTv.text = "${String.format("$.2f", bytes)} MB"
//                    }
//                }
//                .addOnFailureListener {e->
//                    //failed to get metadata
//                    Log.d(TAG, "loadImgSize: Failed to get metadata to ${e.message}")
//
//                }
//        }

        fun loadImgFromUrlSinglePage(
//            imgUrl: String,
//      imgUrl: String,
        imgTitle: String,
        progressBar: ProgressBar,
        pagesTv: TextView
        ){
//            val TAG = "IMG_THUMBNAIL_TAG"
//            //using url we can get file and its metadata from firebase storage
//            val ref = FirebaseStorage.getInstance().getReferenceFromUrl(imgUrl)
//            ref.metadata
//                .addOnSuccessListener {storageMetaData ->
//                    Log.d(TAG, "loadImgSize: got metadata")
//                    val bytes = storageMetaData.sizeBytes.toDouble()
//                    Log.d(TAG, "loadImgSize: Size Bytes $bytes")
//
//                }
//                .addOnFailureListener {e->
//                    //failed to get metadata
//                    Log.d(TAG, "loadImgSize: Failed to get metadata to ${e.message}")
//
//                }
        }

        fun loadCategory(categoryId: String, categoryTv: TextView) {
            //load category using category id from firebase
            val ref = FirebaseDatabase.getInstance().getReference("Categories")
            ref.child(categoryId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        //getcategory
                        val category = "${snapshot.child("category").value}"

                        //set category
                        categoryTv.text = category
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
        }

        fun deleteBook(context: Context, bookId: String, bookTitle:String) {
            //param details
            //1 context, used when require e.g. for progressdialog
            //2 bookId, to delete book firebase from db
            //3 bookTitle, show in dialog etc

            val TAG = "DELETE_NOTES_TAG"

            Log.d(TAG, "deleteBook: deleting...")

            //progress dialog
            val progressDialog = ProgressDialog(context)
            progressDialog.setTitle("Please wait...")
            progressDialog.setMessage("Deleting $bookTitle...")
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.show()

            val ref =FirebaseDatabase.getInstance().getReference("Books")
            ref.child(bookId)
                .removeValue()
                .addOnSuccessListener {
                    progressDialog.dismiss()
                    Toast.makeText(context, "Successfully deleted...", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "deleteBook: Deleted from db...")
                }
                .addOnFailureListener { e->
                    progressDialog.dismiss()
                    Log.d(TAG, "deleteBook: Failed to delete from db due to ${e.message}")
                    Toast.makeText(context, "Failed to delete due to ${e.message}", Toast.LENGTH_SHORT).show()

                }
        }

        fun incrementImg (bookId: String) {
            val ref = FirebaseDatabase.getInstance().getReference("Books")
            ref.child(bookId)
                .addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        //set to db
                        val dbRef = FirebaseDatabase.getInstance().getReference("Books")
                        dbRef.child(bookId)

                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
        }
    }



}