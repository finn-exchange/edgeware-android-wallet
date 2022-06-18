package com.dfinn.wallet.common.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dfinn.wallet.common.utils.Event
import com.dfinn.wallet.common.utils.WithCoroutineScopeExtensions
import com.dfinn.wallet.common.validation.*
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

typealias TitleAndMessage = Pair<String, String>

open class BaseViewModel : ViewModel(), CoroutineScope, WithCoroutineScopeExtensions {

    private val _errorLiveData = MutableLiveData<Event<String>>()
    val errorLiveData: LiveData<Event<String>> = _errorLiveData

    private val _errorWithTitleLiveData = MutableLiveData<Event<TitleAndMessage>>()
    val errorWithTitleLiveData: LiveData<Event<TitleAndMessage>> = _errorWithTitleLiveData

    private val _messageLiveData = MutableLiveData<Event<String>>()
    val messageLiveData: LiveData<Event<String>> = _messageLiveData

    fun showMessage(text: String) {
        _messageLiveData.value = Event(text)
    }

    fun showError(title: String, text: String) {
        _errorWithTitleLiveData.value = Event(title to text)
    }

    fun showError(text: String) {
        _errorLiveData.postValue(Event(text))
    }

    fun showError(throwable: Throwable) {
        throwable.message?.let(this::showError)
    }

    override val coroutineContext: CoroutineContext
        get() = viewModelScope.coroutineContext

    override val coroutineScope: CoroutineScope
        get() = this

    suspend fun <P, S> ValidationExecutor.requireValid(
        validationSystem: ValidationSystem<P, S>,
        payload: P,
        validationFailureTransformer: (S) -> TitleAndMessage,
        progressConsumer: ProgressConsumer? = null,
        autoFixPayload: (original: P, failureStatus: S) -> P = { original, _ -> original },
        block: (P) -> Unit,
    ) = requireValid(
        validationSystem = validationSystem,
        payload = payload,
        errorDisplayer = ::showError,
        validationFailureTransformerDefault = validationFailureTransformer,
        progressConsumer = progressConsumer,
        autoFixPayload = autoFixPayload,
        block = block
    )

    suspend fun <P, S> ValidationExecutor.requireValid(
        validationSystem: ValidationSystem<P, S>,
        payload: P,
        validationFailureTransformerCustom: (ValidationStatus.NotValid<S>, ValidationFlowActions) -> TransformedFailure,
        progressConsumer: ProgressConsumer? = null,
        block: (P) -> Unit,
    ) = requireValid(
        validationSystem = validationSystem,
        payload = payload,
        errorDisplayer = ::showError,
        validationFailureTransformerCustom = validationFailureTransformerCustom,
        progressConsumer = progressConsumer,
        block = block
    )
}