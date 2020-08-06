package jp.co.soramitsu.feature_onboarding_impl.presentation.import_account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import jp.co.soramitsu.common.base.BaseViewModel
import jp.co.soramitsu.common.utils.Event
import jp.co.soramitsu.feature_account_api.domain.model.CryptoType
import jp.co.soramitsu.feature_account_api.domain.model.Node
import jp.co.soramitsu.feature_account_api.domain.model.SourceType
import jp.co.soramitsu.feature_onboarding_api.domain.OnboardingInteractor
import jp.co.soramitsu.feature_onboarding_impl.OnboardingRouter
import jp.co.soramitsu.feature_onboarding_impl.presentation.import_account.dialog.model.EncryptionTypeChooserDialogData
import jp.co.soramitsu.feature_onboarding_impl.presentation.import_account.dialog.model.NetworkTypeChooserDialogData
import jp.co.soramitsu.feature_onboarding_impl.presentation.import_account.dialog.model.SourceTypeChooserDialogData

class ImportAccountViewmodel(
    private val interactor: OnboardingInteractor,
    private val router: OnboardingRouter
) : BaseViewModel() {

    private val _usernameVisibilityLiveData = MutableLiveData<Boolean>()
    val usernameVisibilityLiveData: LiveData<Boolean> = _usernameVisibilityLiveData

    private val _passwordVisibilityLiveData = MutableLiveData<Boolean>()
    val passwordVisibilityLiveData: LiveData<Boolean> = _passwordVisibilityLiveData

    private val _jsonInputVisibilityLiveData = MutableLiveData<Boolean>()
    val jsonInputVisibilityLiveData: LiveData<Boolean> = _jsonInputVisibilityLiveData

    private val _advancedVisibilityLiveData = MutableLiveData<Boolean>()
    val advancedVisibilityLiveData: LiveData<Boolean> = _advancedVisibilityLiveData

    private val _qrScanStartLiveData = MutableLiveData<Event<Unit>>()
    val qrScanStartLiveData: LiveData<Event<Unit>> = _qrScanStartLiveData

    private val _nextButtonEnabledLiveData = MutableLiveData<Boolean>()
    val nextButtonEnabledLiveData: LiveData<Boolean> = _nextButtonEnabledLiveData

    private val _sourceTypeChooserDialogInitialData = MutableLiveData<SourceTypeChooserDialogData>()
    val sourceTypeChooserDialogInitialData: LiveData<SourceTypeChooserDialogData> = _sourceTypeChooserDialogInitialData

    private val _encryptionTypeChooserDialogInitialData = MutableLiveData<EncryptionTypeChooserDialogData>()
    val encryptionTypeChooserDialogInitialData: LiveData<EncryptionTypeChooserDialogData> = _encryptionTypeChooserDialogInitialData

    private val _networkTypeChooserDialogInitialData = MutableLiveData<NetworkTypeChooserDialogData>()
    val networkTypeChooserDialogInitialData: LiveData<NetworkTypeChooserDialogData> = _networkTypeChooserDialogInitialData

    private val _selectedSourceTypeText = MutableLiveData<String>()
    val selectedSourceTypeText: LiveData<String> = _selectedSourceTypeText

    private val _selectedEncryptionTypeText = MutableLiveData<String>()
    val selectedEncryptionTypeText: LiveData<String> = _selectedEncryptionTypeText

    private val _selectedNodeText = MutableLiveData<String>()
    val selectedNodeText: LiveData<String> = _selectedNodeText

    private var selectedNode: Node? = null
    private var selectedSourceType: SourceType = SourceType.MNEMONIC_PASSPHRASE
    private var selectedEncryptionType: CryptoType = CryptoType.SR25519

    fun homeButtonClicked() {
        router.backToWelcomeScreen()
    }

    fun advancedButtonClicked() {
        _advancedVisibilityLiveData.value = _advancedVisibilityLiveData.value?.not() ?: true
    }

    fun sourceTypeInputClicked() {
        disposables.add(
            interactor.getSourceTypes()
                .map { mapSourceTypesToSourceTypeDialogData(it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _sourceTypeChooserDialogInitialData.value = it
                }, {
                    it.printStackTrace()
                })
        )
    }

    fun sourceTypeChanged(it: SourceType) {
        selectedSourceType = it
        _selectedSourceTypeText.value =  when(selectedSourceType) {
            SourceType.MNEMONIC_PASSPHRASE -> {
                _usernameVisibilityLiveData.value = true
                _passwordVisibilityLiveData.value = false
                _jsonInputVisibilityLiveData.value = false
                "Mnemonic passphrase"
            }
            SourceType.RAW_SEED -> {
                _usernameVisibilityLiveData.value = true
                _passwordVisibilityLiveData.value = false
                _jsonInputVisibilityLiveData.value = false
                "Raw seed"
            }
            SourceType.KEYSTORE -> {
                _usernameVisibilityLiveData.value = false
                _passwordVisibilityLiveData.value = true
                _jsonInputVisibilityLiveData.value = true
                "Recovery JSON"
            }
        }

        _advancedVisibilityLiveData.value = false
    }

    fun encryptionTypeInputClicked() {
        disposables.add(
            interactor.getEncryptionTypes()
                .map { mapEncryptionTypesToEncryptionTypeDialogData(it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _encryptionTypeChooserDialogInitialData.value = it
                }, {
                    it.printStackTrace()
                })
        )
    }

    fun encryptionTypeChanged(it: CryptoType) {
        selectedEncryptionType = it

        _selectedEncryptionTypeText.value =  when(selectedEncryptionType) {
            CryptoType.SR25519 -> "Schnorrkel | sr25519 (recommended) "
            CryptoType.ED25519 -> "Edwards | ed25519 (alternative)"
            CryptoType.ECDSA -> "ECDSA | (BTC/ETH compatible)"
        }
    }

    fun networkTypeInputClicked() {
        disposables.add(
            interactor.getDefaultNodes()
                .map { mapEncryptionTypesToEncryptionTypeDialogData(it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _networkTypeChooserDialogInitialData.value = it
                }, {
                    it.printStackTrace()
                })
        )
    }

    fun networkTypeChanged(it: Node) {
        selectedNode = it
        _selectedNodeText.value = it.name
    }

    private fun mapSourceTypesToSourceTypeDialogData(sourceTypes: List<SourceType>): SourceTypeChooserDialogData {
        return SourceTypeChooserDialogData(selectedSourceType, sourceTypes)
    }

    private fun mapEncryptionTypesToEncryptionTypeDialogData(encryptionTypes: List<CryptoType>): EncryptionTypeChooserDialogData {
        return EncryptionTypeChooserDialogData(selectedEncryptionType, encryptionTypes)
    }

    private fun mapEncryptionTypesToEncryptionTypeDialogData(nodes: List<Node>): NetworkTypeChooserDialogData {
        return NetworkTypeChooserDialogData(selectedNode ?: nodes.first(), nodes)
    }

    fun qrScanClicked() {
        _qrScanStartLiveData.value = Event(Unit)
    }

    fun nextBtnClicked(keyString: String, username: String, password: String, json: String, derivationPath: String) {
        selectedNode?.let {  node ->
            val disposable = when (selectedSourceType) {
                SourceType.MNEMONIC_PASSPHRASE -> interactor.importFromMnemonic(keyString, username, derivationPath, selectedEncryptionType, node)
                SourceType.RAW_SEED -> interactor.importFromSeed(keyString, username, derivationPath, selectedEncryptionType, node)
                SourceType.KEYSTORE -> interactor.importFromJson(json, password, node)
            }
        }
    }

    fun inputChanges(input1: String, input2: String) {
        _nextButtonEnabledLiveData.value = input1.isNotEmpty() && input2.isNotEmpty()
    }
}