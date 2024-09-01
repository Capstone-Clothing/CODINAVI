package com.example.capstonecodinavi.User

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.capstonecodinavi.databinding.ItemResultSummaryBinding

class ResultSummaryAdapter(var resultSummaryList: ArrayList<ResultSummary>) : RecyclerView.Adapter<ResultSummaryViewHolder>() {

    val itemClicked = MutableLiveData<ResultSummary>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultSummaryViewHolder {
        val binding = ItemResultSummaryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ResultSummaryViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return resultSummaryList.size
    }

    override fun onBindViewHolder(holder: ResultSummaryViewHolder, position: Int) {
        val item = resultSummaryList[position]
        holder.bindData(item)
        holder.binding.parentLl.setOnClickListener {
            itemClicked.postValue(item)
        }
    }


}