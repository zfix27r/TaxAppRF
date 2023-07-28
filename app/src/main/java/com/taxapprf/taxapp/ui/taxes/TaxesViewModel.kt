package com.taxapprf.taxapp.ui.taxes

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.taxapprf.domain.transaction.SaveTransactionUseCase
import com.taxapprf.domain.taxes.GetTaxesUseCase
import com.taxapprf.taxapp.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class TaxesViewModel @Inject constructor(
    private val getYearsUseCase: GetTaxesUseCase,
    private val saveTransactionUseCase: SaveTransactionUseCase,
) : BaseViewModel() {

    val taxes = getYearsUseCase.execute()
        .asLiveData(viewModelScope.coroutineContext)


/*    @Throws(IOException::class)
    fun addTransactions(filePath: String?) {
        val settings: SharedPreferences = getApplication<Application>()
            .getSharedPreferences(Settings.SETTINGSFILE.name, Context.MODE_PRIVATE)
        val account = settings.getString(Settings.ACCOUNT.name, "")
        val task = Runnable {
            var transactions: List<Transaction>? = null
            transactions = try {
                ParseExcel(filePath).parse()
            } catch (e: IOException) {
                //...обработать
                return@Runnable
            }
            for (transaction in transactions) {
                val year = DateCheck(transaction.date).year
                val ctrl = Controller(transaction.date)
                val currenciesCall = ctrl.prepareCurrenciesCall()
                currenciesCall.enqueue(object : Callback<Currencies?> {
                    override fun onResponse(
                        call: Call<Currencies?>,
                        response: Response<Currencies?>
                    ) {
                        if (response.isSuccessful) {
                            val cur = response.body()
                            val rateCentralBankDouble = cur!!.getCurrencyRate(transaction.currency)
                            transaction.rateCentralBank = rateCentralBankDouble
                            var sum = transaction.sum
                            val k: Int
                            when (transaction.type) {
                                "TRADE" -> k = 1
                                "FUNDING/WITHDRAWAL" -> k = 0
                                "COMMISSION" -> {
                                    sum = Math.abs(sum)
                                    k = -1
                                }

                                else -> k = 1
                            }
                            var sumRubBigDecimal =
                                BigDecimal(sum * rateCentralBankDouble * 0.13 * k)
                            sumRubBigDecimal = sumRubBigDecimal.setScale(2, RoundingMode.HALF_UP)
                            val sumRubDouble = sumRubBigDecimal.toDouble()
                            transaction.sumRub = sumRubDouble
                            addToFirebase(account, year, transaction)
                        } else {
                            //...
                        }
                    }

                    override fun onFailure(call: Call<Currencies?>, t: Throwable) {
                        //message.setValue("Не удалось загрузить курс валюты. Сделка не добавлена!");
                    }
                })
            }
            //calculateSum(years);
        }
        val thread = Thread(task)
        thread.start()
    }

    private fun addToFirebase(account: String?, year: String, transaction: Transaction) {
        FirebaseTransactions(UserLivaData().firebaseUser, account)
            .addTransaction(year, transaction, object : FirebaseTransactions.DataStatus {
                override fun DataIsLoaded(transactions: List<Transaction>) {}
                override fun DataIsInserted() {
                    //message.setValue("Сделка добавлена!");
                    calculateSum(account, year)
                }

                override fun DataIsUpdated() {}
                override fun DataIsDeleted() {}
            })
    }

    fun calculateSum(account: String?, year: String?) {
        FirebaseTransactions(UserLivaData().firebaseUser, account).sumTransaction(year)
    }*/
}