package com.vidya.noteappkotlin

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.vidya.noteappkotlin.databinding.RowCategoryBinding

class AdapterCategory : RecyclerView.Adapter<AdapterCategory.HolderCategory>, Filterable {

    private val context: Context
    public var categoryArrayList: ArrayList<ModelCategory>
    private var filterList: ArrayList<ModelCategory>
    private var filter: Filtercategory? = null

    private lateinit var binding:  RowCategoryBinding

    constructor(context: Context, categoryArrayList: ArrayList<ModelCategory>) {
        this.context = context
        this.categoryArrayList = categoryArrayList
        this.filterList = categoryArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderCategory {
        //inflate bind row_category
        binding = RowCategoryBinding.inflate(LayoutInflater.from(context), parent, false)

        return HolderCategory(binding.root)
    }

    override fun onBindViewHolder(holder: HolderCategory, position: Int) {
        //get data, set data, handle click etc

        //get data
        val model = categoryArrayList[position]
        val id = model.id
        val category = model.category
        val uid = model.uid
        val timestamp = model.timestamp

        //set data
        holder.categoryTv.text = category

        //handle click delete category
        holder.deleyeBtn.setOnClickListener {
            //confirm before delete
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Delete")
                .setMessage("Are You sure want to delete this category?")
                .setPositiveButton("Confirm") {a,  d->
                    Toast.makeText(context , "Deleting...", Toast.LENGTH_SHORT).show()
                    deleteCategory(model, holder)


                }
                .setNegativeButton("cancel") {a, d->
                    a.dismiss()

                }
                .show()

        }
//        handle click, start img list admin activity, also pas img id, title
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ImgListAdminActivity::class.java)
            intent.putExtra("categoryId", id)
            intent.putExtra("category", category)
            context.startActivity(intent)
        }
    }

    private fun deleteCategory(model: ModelCategory, holder: HolderCategory) {
        // get id category to delete
        val id = model.id
        //firebase DB > categories > categoriesid
        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.child(id)
            .removeValue()
            .addOnSuccessListener {
                Toast.makeText(context, "Deleted...", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{ e->
                Toast.makeText(context, "Unable to delete due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
}
    override fun getItemCount(): Int {
        return categoryArrayList.size
    }


    inner class HolderCategory(itemView: View): RecyclerView.ViewHolder(itemView) {
        //init ui views
        var categoryTv:TextView = binding.categoryTv
        var deleyeBtn:ImageButton = binding.deleteBtn
    }

    override fun getFilter(): Filter {
        if (filter == null) {
            filter = Filtercategory(filterList, this)
        }
        return filter as Filtercategory
    }

}