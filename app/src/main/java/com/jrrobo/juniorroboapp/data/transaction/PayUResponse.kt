package com.jrrobo.juniorroboapp.data.transaction


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PayUResponse(
    @Json(name = "addedon")
    val addedon: String?,
    @Json(name = "amount")
    val amount: String?,
    @Json(name = "bank_ref_no")
    val bankRefNo: String?,
    @Json(name = "discount")
    val discount: String?,
    @Json(name = "email")
    val email: String?,
    @Json(name = "error_code")
    val errorCode: String?,
    @Json(name = "Error_Message")
    val errorMessage: String?,
    @Json(name = "field1")
    val field1: String?,
    @Json(name = "field2")
    val field2: String?,
    @Json(name = "field3")
    val field3: String?,
    @Json(name = "field4")
    val field4: String?,
    @Json(name = "field5")
    val field5: String?,
    @Json(name = "field6")
    val field6: String?,
    @Json(name = "field7")
    val field7: String?,
    @Json(name = "field9")
    val field9: String?,
    @Json(name = "firstname")
    val firstname: String?,
    @Json(name = "furl")
    val furl: String?,
    @Json(name = "hash")
    val hash: String?,
    @Json(name = "ibibo_code")
    val ibiboCode: String?,
    @Json(name = "id")
    val id: Long?,
    @Json(name = "is_seamless")
    val isSeamless: Int?,
    @Json(name = "key")
    val key: String?,
    @Json(name = "mode")
    val mode: String?,
    @Json(name = "PG_TYPE")
    val pGTYPE: String?,
    @Json(name = "payment_source")
    val paymentSource: String?,
    @Json(name = "phone")
    val phone: String?,
    @Json(name = "productinfo")
    val productinfo: String?,
    @Json(name = "status")
    val status: String?,
    @Json(name = "surl")
    val surl: String?,
    @Json(name = "transaction_fee")
    val transactionFee: String?,
    @Json(name = "txnid")
    val txnid: String?,
    @Json(name = "unmappedstatus")
    val unmappedstatus: String?
)