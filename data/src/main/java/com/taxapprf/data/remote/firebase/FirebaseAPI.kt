package com.taxapprf.data.remote.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.taxapprf.data.error.AuthError
import com.taxapprf.domain.transaction.GetTransactionModel
import com.taxapprf.domain.transaction.GetTransactionsModel
import javax.inject.Inject


class FirebaseAPI @Inject constructor() {
    private val database = FirebaseDatabase.getInstance()//.apply { setPersistenceEnabled(true) }
    private val reference = database.reference

    val auth = FirebaseAuth.getInstance()
    private val uid: String
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

    fun getReportPath(accountKey: String, yearKey: String) = reference
        .child(USERS)
        .child(uid)
        .child(REPORTS)
        .child(accountKey)
        .child(yearKey)

    fun getTransactionsPath(getTransactionsModel: GetTransactionsModel) = reference
        .child(USERS)
        .child(uid)
        .child(TRANSACTIONS)
        .child(getTransactionsModel.accountKey)
        .child(getTransactionsModel.reportKey)

    fun getTransactionPath(getTransactionModel: GetTransactionModel) = reference
        .child(USERS)
        .child(uid)
        .child(TRANSACTIONS)
        .child(getTransactionModel.accountKey)
        .child(getTransactionModel.reportKey)
        .child(getTransactionModel.transactionKey)

    companion object {
        const val USERS = "users"
        const val ACCOUNTS = "accounts"
        const val REPORTS = "reports"
        const val TRANSACTIONS = "transactions"
    }
}