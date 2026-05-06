package com.example.mychartsapp.presentation.ui.models

import java.io.RandomAccessFile

data class PDAFile(
    val header: PDAHeader,
    val channels: List<PDAChannel>,
    val channelData: Map<Int, ChannelData>
)

data class PDAHeader(
    val clock: Double,
    val type: String,
    val startTime: String,
    val frames: Int,
    val version: String,
    val modules: Map<Int, String>,
    val groups: Map<Int, String>
)

data class PDAChannel(
    val id: Int,
    val name: String,
    val unit: String,
    val dataType: String,
    val minScale: Double,
    val maxScale: Double,
    val group: String,
    val timeBase: Double
)

data class ChannelData(
    val channelId: Int,
    val timestamps: List<Double>,
    val values: List<Double>
)

data class DataPoint(
    val timestamp: Double,
    val value: Double
)
