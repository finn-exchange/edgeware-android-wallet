package jp.co.soramitsu.feature_onboarding_impl.presentation.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import jp.co.soramitsu.common.base.BaseViewModel
import jp.co.soramitsu.common.utils.Event
import jp.co.soramitsu.feature_onboarding_api.domain.OnboardingInteractor
import jp.co.soramitsu.feature_onboarding_impl.OnboardingRouter

class CreateAccountViewModel(
    private val interactor: OnboardingInteractor,
    private val router: OnboardingRouter
) : BaseViewModel() {

    private val _nextButtonEnabledLiveData = MutableLiveData<Boolean>()
    val nextButtonEnabledLiveData: LiveData<Boolean> = _nextButtonEnabledLiveData

    private val _showScreenshotsWarningEvent = MutableLiveData<Event<Unit>>()
    val showScreenshotsWarningEvent: LiveData<Event<Unit>> = _showScreenshotsWarningEvent

    fun homeButtonClicked() {
        router.backToWelcomeScreen()
    }

    fun accountNameChanged(accountName: CharSequence?) {
        accountName?.let {
            _nextButtonEnabledLiveData.value = it.isNotEmpty()
        }
    }

    fun nextClicked() {
        _showScreenshotsWarningEvent.value = Event(Unit)
    }

    fun screenshotWarningConfirmed(accountName: String) {
        router.openMnemonicScreen(accountName)
    }
}