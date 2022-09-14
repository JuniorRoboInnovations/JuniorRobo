package com.jrrobo.juniorroboapp.data.booking

import java.util.*

data class BookingItem(
    val PkBookingId: Int,
    val BookingAmount : String,
    val Discount: String,
    val FkGradeId: Int?,
    val FkUserId: Int?,
    val FkVoucherId: Int?,
    val BookingDatetime: Date?,
    val Status:String,
)
