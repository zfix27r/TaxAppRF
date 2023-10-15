package com.taxapprf.domain

import com.taxapprf.domain.main.account.SwitchAccountModel
import com.taxapprf.domain.main.transaction.SaveTransactionModel
import com.taxapprf.domain.main.user.ObserveUserWithAccountsModel
import com.taxapprf.domain.main.user.SignInModel
import com.taxapprf.domain.main.user.SignUpModel
import com.taxapprf.domain.main.user.UserModel
import com.taxapprf.domain.main.user.UserWithAccountsModel
import kotlinx.coroutines.flow.Flow

interface MainRepository {
    fun observeUserWithAccounts(observeUserWithAccountsModel: ObserveUserWithAccountsModel): Flow<UserWithAccountsModel>
    suspend fun signUp(signUpModel: SignUpModel)
    suspend fun signIn(signInModel: SignInModel)
    suspend fun signOut()
    suspend fun saveUser(userModel: UserModel)
    suspend fun switchAccount(switchAccountModel: SwitchAccountModel)
    suspend fun saveTransaction(saveTransactionModel: SaveTransactionModel): Int?
}