package com.taxapprf.taxapp.activities

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.taxapprf.domain.activity.GetAccountModelUseCase
import com.taxapprf.domain.activity.AccountModel
import com.taxapprf.taxapp.firebase.UserLivaData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getAccountModel: GetAccountModelUseCase
) : ViewModel() {
    val account = getAccountModel.execute()
        .asLiveData(viewModelScope.coroutineContext)

    private var _userName = MutableLiveData<String>()
    val userName: LiveData<String> = _userName

    private var _userEmail = MutableLiveData<String>()
    val userEmail: LiveData<String> = _userEmail

    init {
        // Здесь желательно сделать один запрос к фаербейз и получить все разом
        // и создать одну модель
        // и ее одну наблюдать в активити
//        val user = UserLivaData().firebaseUser
//        println("@@@@@@@@@@@@@2 " + user.uid)
        //Firebase.database.setPersistenceEnabled(true)
//        val reference = Firebase.database.getReference("Users").child(user.uid)

//        FirebaseUserName(reference).readName { name ->
//            _userName.postValue(name)
//        }

        /*        FirebaseUserEmail(UserLivaData().firebaseUser).readEmail { email ->
                    _userEmail.postValue(email)
                }*/
    }

}