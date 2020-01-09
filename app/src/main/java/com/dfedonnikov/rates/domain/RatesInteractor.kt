package com.dfedonnikov.rates.domain

import com.dfedonnikov.rates.data.RatesRepository
import com.dfedonnikov.rates.ui.RateItem
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

interface RatesInteractor {

    fun getRates(): Observable<RatesState>
    fun changeBase(item: RateItem)
    fun recalculatedRates(amount: String)
    fun destroy()
}

class RatesInteractorImpl @Inject constructor(private val repository: RatesRepository) : RatesInteractor {

    private val compositeDisposable = CompositeDisposable()

    init {
        startLoadingRates()
    }

    private fun startLoadingRates() {
        val disposable = Observable.interval(1000, TimeUnit.MILLISECONDS)
            .startWith(1)
            .flatMapSingle {
                val base = when (val state = repository.getState()) {
                    null -> Currency.EUR.title
                    else -> state.currentCurrency.title
                }
                updateRates(base)
            }
            .subscribeOn(Schedulers.io())
            .subscribe({

            }, {})
        compositeDisposable.add(disposable)
    }

    private fun updateRates(base: String): Single<Unit> {
        return repository.updateRates(base)
            .doOnSuccess {
                when (val state = repository.getState()) {
                    null -> initState(it)
                    else -> updateState(it, state)
                }
            }
            .map { Unit }
    }

    private fun initState(data: RatesData) {
        val list = data.currencies.mapTo(LinkedList()) { RateStateItem(it.key, BigDecimal.ZERO, it.value) }
        list.addFirst(RateStateItem(data.base, BigDecimal.ZERO, BigDecimal.ONE))
        val ratesState = RatesState(data.base, BigDecimal.ZERO, list)
        repository.updateState(ratesState)

    }

    private fun updateState(data: RatesData, state: RatesState) {
        val list = state.getRates().mapIndexedTo(LinkedList()) { index, it ->
            when (index) {
                0 -> it.copy(ratioToBase = BigDecimal.ONE)
                else -> {
                    val factor = data.currencies[it.currency] ?: return
                    val amount = state.currentAmount.multiply(factor).setScale(2, RoundingMode.HALF_UP)
                    RateStateItem(it.currency, amount, factor)
                }
            }

        }
        val ratesState = RatesState(data.base, state.currentAmount, list)
        repository.updateState(ratesState)
    }

    override fun getRates(): Observable<RatesState> = repository.getLastRates()

    override fun changeBase(item: RateItem) {
        val state = repository.getState() ?: return
        val list = state.getRates()
        val currentCurrency = Currency.valueOf(item.title)
        val element = list.find { it.currency == currentCurrency }
        list.remove(element)
        list.addFirst(element)
        val ratesState = state.copy(currentCurrency = currentCurrency, currentAmount = item.amount.toBigDecimal(), rates = list)
        repository.updateState(ratesState)
        val disposable = repository.updateRates(item.title)
            .subscribeOn(Schedulers.io())
            .subscribe({}, {})
        compositeDisposable.add(disposable)
    }

    override fun recalculatedRates(amount: String) {
        val currentAmount = try {
            BigDecimal(amount)
        } catch (e: Exception) {
            BigDecimal.ZERO
        }
        val state = repository.getState() ?: return
        val list = state.getRates()
            .mapTo(LinkedList()) {
                val newAmount = currentAmount.multiply(it.ratioToBase).setScale(2, RoundingMode.HALF_UP)
                RateStateItem(it.currency, newAmount, it.ratioToBase)
            }
        val ratesState = state.copy(currentAmount = currentAmount, rates = list)
        repository.updateState(ratesState)
    }

    override fun destroy() {
        compositeDisposable.clear()
    }
}