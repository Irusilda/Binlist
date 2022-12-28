package com.example.bin1

data class Data(
    val number_length: Int?,
    val number_luhn: Boolean?,
    val scheme: String?,
    val type: String?,
    val brand: String?,
    val prepaid: Boolean?,
    val country_name: String?,
    val country_latitude: Int?,
    val country_longitude: Int?,
    val bank_name: String? = "",
    val bank_url: String?,
    val bank_phone: String?,
    val bank_city: String?
)
