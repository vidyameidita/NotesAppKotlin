package com.vidya.noteappkotlin

import java.util.logging.Filter
import java.util.logging.LogRecord

class FilterImgAdmin : android.widget.Filter{

    //arraylist in which we want
    var filterList: ArrayList<ModelImg>
    //adapter in which filter need to be implemented
    var adapterImgAdmin: AdapterImgAdmin

    //constructur
    constructor(filterList: ArrayList<ModelImg>, adapterImgAdmin: AdapterImgAdmin) {
        this.filterList = filterList
        this.adapterImgAdmin = adapterImgAdmin
    }

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        var constraint: CharSequence? = constraint
        val results = FilterResults()
        //vaue to should be searched should not be nulland nit wmpty
        if (constraint !=null && constraint.isNotEmpty()) {
            //cahenge to upper case, or lowercase to avoid case sensitivity
            constraint = constraint.toString().lowercase()
            var  filteredModels = ArrayList<ModelImg>()
            for (i in filterList.indices) {
                //validate if match
                if (filterList[i].title.lowercase().contains(constraint)) {
                    //searched value issimiliar to value in list, add to filtered list
                    filteredModels.add(filterList[i])
                }
            }
                results.count = filteredModels.size
                results.values = filteredModels
            }
            else {
                //searched value is either null or empty, return all data
                results.count = filterList.size
                results.values = filterList

            }
            return results // dont miss
        }

    override fun publishResults(constraint: CharSequence, results: FilterResults) {
        adapterImgAdmin.imgArrayList = results.values as ArrayList<ModelImg>

        //notif changes
        adapterImgAdmin.notifyDataSetChanged()


    }
}
