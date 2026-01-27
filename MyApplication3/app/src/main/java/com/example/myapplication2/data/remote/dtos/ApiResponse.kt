package com.example.myapplication.data.remote.dtos

data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T?,
    val timestamp: String
)