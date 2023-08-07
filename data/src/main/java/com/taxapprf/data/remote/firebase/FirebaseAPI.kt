package com.taxapprf.data.remote.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.taxapprf.data.error.AuthError
import com.taxapprf.domain.FirebasePathModel
import javax.inject.Inject


class FirebaseAPI @Inject constructor() {
    private val database = FirebaseDatabase.getInstance().apply { setPersistenceEnabled(true) }
    val reference = database.reference

    val auth = FirebaseAuth.getInstance()
    val uid: String
        get() = auth.currentUser?.uid ?: throw AuthError()

    fun getAccountsPath() = reference
        .child(USERS)
        .child(uid)
        .child(ACCOUNTS)

    fun getReportsPath(accountKey: String) = reference
        .child(USERS)
        .child(uid)
        .child(REPORTS)
        .child(accountKey)

    fun getReportPath(firebasePathModel: FirebasePathModel) = reference
        .child(USERS)
        .child(uid)
        .child(REPORTS)
        .child(firebasePathModel.accountName)
        .child(firebasePathModel.year)

    fun getTransactionsPath(firebasePathModel: FirebasePathModel) = reference
        .child(USERS)
        .child(uid)
        .child(TRANSACTIONS)
        .child(firebasePathModel.accountName)
        .child(firebasePathModel.year)

    companion object {
        const val USERS = "users"
        const val ACCOUNTS = "accounts"
        const val REPORTS = "reports"
        const val TRANSACTIONS = "transactions"
    }
}