package com.taxapprf.taxapp.firebase

import com.google.firebase.database.DatabaseReference

class FirebaseUserName(
    private val reference: DatabaseReference,
) {
    fun readName(name: (String) -> Unit) {

        reference
            .child("name")
            .get()
            .addOnCompleteListener{
                if (it.isSuccessful) {
                    println("SUCCCCCC")
                    println("!!!!!!!!! " + it.result?.children)
                }

                if (it.isCanceled)
                    println("CANCEL")

                if (it.isComplete) {
                    println("COMPLE")
                    println("!!!!!!!!! " + it.result!!.value.toString())
                }
/*                if (!task.isSuccessful) {
                } else {
                    name.invoke(task.result!!.value.toString())
                }*/
            }
    }
}