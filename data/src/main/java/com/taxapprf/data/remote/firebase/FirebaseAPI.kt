package com.taxapprf.data.remote.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.taxapprf.data.error.AuthError
import javax.inject.Inject


class FirebaseAPI @Inject constructor() {
    private val database = FirebaseDatabase.getInstance()//.apply { setPersistenceEnabled(true) }
    val auth = FirebaseAuth.getInstance()
    val reference = database.reference
    val uid: String
        get() = auth.currentUser?.uid ?: throw AuthError()
    val accountsPath
        get() = reference
            .child(USERS)
            .child(uid)
            .child(ACCOUNTS)

    companion object {
        const val USERS = "users"
        const val ACCOUNTS = "accounts"

        const val ACCOUNT_NAME = "name"

        const val DEFAULT_ACCOUNT = "default"
    }
}