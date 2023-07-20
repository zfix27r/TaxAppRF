package com.taxapprf.taxapp.firebase

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class FirebaseUserEmail(user: FirebaseUser) {
    private val database: FirebaseDatabase
    private val referenceUser: DatabaseReference
    private val referenceUserName: DatabaseReference
    private var userEmail: String? = null

    interface DataStatus {
        fun DataIsLoaded(userName: String?)
    }

    init {
        database = FirebaseDatabase.getInstance()
        referenceUser = database.getReference("Users").child(user.uid)
        referenceUserName = referenceUser.child("email")
    }

/*    fun readEmail(dataStatus: FirebaseUserName.DataStatus) {
        referenceUserName.get().addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                //...
            } else {
                userEmail = task.result!!.value.toString()
                dataStatus.DataIsLoaded(userEmail)
            }
        }
    }*/
}