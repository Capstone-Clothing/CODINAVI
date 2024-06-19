package com.example.capstonecodinavi.Camera

data class AnalysisResult(
    val result: List<ItemResult>
)

data class ItemResult(
    val 색상: String,
    val 종류: String,
    val 무늬정보: String,
    val 상의or하의: String,
    val 어울리는상의or하의추천: String
)
