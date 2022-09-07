package com.jrrobo.juniorroboapp.data.emailLogin

data class EmailRegisterData(
    val pk_id: Int? = null,
    val first_name: String? = null,
    val last_name: String? = null,
    val email: String,
    val password: String,
    val is_active: Boolean = true,
    val country_name: String? = null,
    val city: String? = null,
    val mobile: String?,
    val user_image: String? = null
)
