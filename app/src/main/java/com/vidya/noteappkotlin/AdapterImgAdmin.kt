package com.vidya.noteappkotlin

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.vidya.noteappkotlin.databinding.RowPdfAdminBinding

class AdapterImgAdmin :RecyclerView.Adapter<AdapterImgAdmin.HolderImgAdmin>, Filterable {

    //context
    private var context: Context
    //arrrayList to hold pdf
    public var imgArrayList: ArrayList<ModelImg>
    private val filterList: ArrayList<ModelImg>

    //view binding
    private lateinit var binding: RowPdfAdminBinding

    //filter object
    private var filter : FilterImgAdmin? = null

    //constructur
    constructor(context: Context, imgArrayList: ArrayList<ModelImg>) : super() {
        this.context = context
        this.imgArrayList = imgArrayList
        this.filterList = imgArrayList
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderImgAdmin {
        binding = RowPdfAdminBinding.inflate(LayoutInflater.from(context), parent, false)

        return HolderImgAdmin(binding.root)

    }

    override fun onBindViewHolder(holder: HolderImgAdmin, position: Int) {
        //get data, set data, handle click ets

        //get data
        val model = imgArrayList[position]
//        val imgId = model.id
        val categoryId = model.categoryId
        val title = model.title
        val description = model.description
//        val imgUrl = model.url
        val timestamp = model.timestamp
        //convert timestamp to dd/MM/yyyy format
        val formattedDate = MyApplication.formatTimeStamp(timestamp)

        //set data
        holder.titleTv.text = title
        holder.descriptionTV.text = description
        holder.dateTv.text = formattedDate
        holder.categoryTv.text = categoryId

        //
//        holder.itemView.setOnClickListener {
//            val intent = Intent(context, ImgDetailActivity::class.java)
//            intent.putExtra("categoryId", categoryId)
//            intent.putExtra("title", title)
//            intent.putExtra("description", description)
//            intent.putExtra("timestamp", timestamp)
//            intent.putExtra("formattedDate", formattedDate)
//            context.startActivity(intent)
//        }

        //load further details like category,img from url

        //category id
//        MyApplication.loadCategory(categoryId = categoryId, holder.categoryTv)

        //we dont need page number here. pas null for page number || load img thimbnail
//        MyApplication.loadImgFromUrlSinglePage(imgUrl, title, holder.progressBar, null)

        //load img size
//        MyApplication.loadImgSize(imgUrl, title, holder.sizeTv)

        //lets create an apllication class that will contain the functions that will be used multiple places in app
        holder.moreBtn.setOnClickListener {
            moreOptionDialog(model, holder)
        }

        //handle item click, open imgdetailsactivity, lets create
        holder.itemView.setOnClickListener {
            //intent with bookId
            val intent = Intent(context, ImgDetailActivity::class.java)
            intent.putExtra("bookId", categoryId)
            context.startActivity(intent)
        }
    }

    private fun moreOptionDialog(model: ModelImg, holder: AdapterImgAdmin.HolderImgAdmin) {
        //get id,title, of book
        val bookId = model.id
        val bookTitle = model.title

        //options to show in dialog
        val options = arrayOf("Edit", "Delete")

        //alert dialog
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Choose Option")
            .setItems(options) { dialog, position ->
                //handle item click
                if (position == 0){
                //edit is clicked
                    val intent = Intent(context, ImgEditActivity::class.java)
                    intent.putExtra("bookId", bookId) //passed bookid
                    context.startActivity(intent)


            }
        else if (position ==  1) {
            //deleted is clicked, lets create fnction in MyApplication class for this

                //show confirmation dialog first if you can need
            MyApplication.deleteBook(context, bookId, bookTitle )

        }
    }
            .show()

    }

    override fun getItemCount(): Int {
        return imgArrayList.size //items count

    }



    override fun getFilter(): Filter {
        if (filter == null){
            filter = FilterImgAdmin(filterList, this)
        }
        return filter as FilterImgAdmin
    }

    inner class HolderImgAdmin(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //UI Views o
//        val progressBar = binding.progressBar
        val titleTv = binding.titleTv
        val descriptionTV = binding.descriptionTv
        val categoryTv = binding.categoryTv
//        val sizeTv = binding.sizeTv
        val dateTv = binding.dateTv
        val moreBtn = binding.moreBtn
    }


}