package com.example.capstonecodinavi.User

import androidx.recyclerview.widget.RecyclerView
import com.example.capstonecodinavi.databinding.ItemResultBinding

class ResultSummaryViewHolder(val binding: ItemResultBinding):RecyclerView.ViewHolder(binding.root) {
    fun bindData(resultSummary: ResultSummary) {
        binding.colorTv.text = resultSummary.color
        binding.patternTv.text = resultSummary.pattern
        binding.typeTv.text = resultSummary.type
    }
}