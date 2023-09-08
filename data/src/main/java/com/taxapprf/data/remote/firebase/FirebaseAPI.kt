package com.taxapprf.data.remote.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.taxapprf.data.error.DataErrorUser
import javax.inject.Inject


class FirebaseAPI @Inject constructor() {
    private val database =
        FirebaseDatabase.getInstance()

    private val reference = database.reference

    val auth = FirebaseAuth.getInstance()

    val isUserAuth
        get() = auth.currentUser != null

    private val uid: String
        get() = auth.currentUser?.uid ?: throw DataErrorUser()

    fun getAccountsPath() = reference
        .child(ACCOUNTS)
        .child(uid)

    fun getReportsPath(accountKey: String) = reference
        .child(REPORTS)
        .child(uid)
        .child(accountKey)

    fun getReportPath(accountKey: String, reportKey: String) = reference
        .child(REPORTS)
        .child(uid)
        .child(accountKey)
        .child(reportKey)

    fun getTransactionsPath(accountKey: String, reportKey: String) = reference
        .child(TRANSACTIONS)
        .child(uid)
        .child(accountKey)
        .child(reportKey)

    companion object {
        const val ACCOUNTS = "accounts"
        const val REPORTS = "reports"
        const val TRANSACTIONS = "transactions"
    }
}