package com.taxapprf.data.remote.firebase.model

data class FirebaseAccountModel(
    val name: String? = null,
    val active: Boolean? = null
) {
    constructor() : this("", false)
}