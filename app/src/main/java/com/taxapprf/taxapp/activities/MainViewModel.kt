package com.taxapprf.taxapp.activities

import android.app.Application
import android.content.Context
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.taxapprf.taxapp.firebase.FirebaseUserEmail
import com.taxapprf.taxapp.firebase.FirebaseUserName
import com.taxapprf.taxapp.firebase.UserLivaData
import com.taxapprf.taxapp.usersdata.Settings

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private var _userName = MutableLiveData<String>()
    val userName: LiveData<String> = _userName

    private var _userEmail = MutableLiveData<String>()
    val userEmail: LiveData<String> = _userEmail

    init {
        // Здесь желательно сделать один запрос к фаербейз и получить все разом
        // и создать одну модель
        // и ее одну наблюдать в активити
        application
            .getSharedPreferences(Settings.SETTINGSFILE.name, Context.MODE_PRIVATE)
            .edit {
                FirebaseUserName(UserLivaData().firebaseUser).readName { name ->
                    _userName.postValue(name)
                }
                FirebaseUserEmail(UserLivaData().firebaseUser).readEmail { email ->
                    _userEmail.postValue(email)
                }
            }
    }
}