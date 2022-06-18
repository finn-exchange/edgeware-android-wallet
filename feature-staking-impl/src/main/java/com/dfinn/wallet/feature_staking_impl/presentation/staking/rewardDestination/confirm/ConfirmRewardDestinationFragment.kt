package com.dfinn.wallet.feature_staking_impl.presentation.staking.rewardDestination.confirm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dfinn.wallet.common.base.BaseFragment
import com.dfinn.wallet.common.di.FeatureUtils
import com.dfinn.wallet.common.mixin.impl.observeValidations
import com.dfinn.wallet.common.utils.applyStatusBarInsets
import com.dfinn.wallet.common.view.setProgress
import com.dfinn.wallet.feature_account_api.presenatation.actions.setupExternalActions
import com.dfinn.wallet.feature_staking_api.di.StakingFeatureApi
import com.dfinn.wallet.feature_staking_impl.R
import com.dfinn.wallet.feature_staking_impl.di.StakingFeatureComponent
import com.dfinn.wallet.feature_staking_impl.presentation.staking.rewardDestination.confirm.parcel.ConfirmRewardDestinationPayload
import kotlinx.android.synthetic.main.fragment_confirm_reward_destination.confirmRewardDestinationConfirm
import kotlinx.android.synthetic.main.fragment_confirm_reward_destination.confirmRewardDestinationContainer
import kotlinx.android.synthetic.main.fragment_confirm_reward_destination.confirmRewardDestinationExtrinsicInformation
import kotlinx.android.synthetic.main.fragment_confirm_reward_destination.confirmRewardDestinationRewardDestination
import kotlinx.android.synthetic.main.fragment_confirm_reward_destination.confirmRewardDestinationToolbar

private const val KEY_PAYLOAD = "KEY_PAYLOAD"

class ConfirmRewardDestinationFragment : BaseFragment<ConfirmRewardDestinationViewModel>() {

    companion object {

        fun getBundle(payload: ConfirmRewardDestinationPayload) = Bundle().apply {
            putParcelable(KEY_PAYLOAD, payload)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_confirm_reward_destination, container, false)
    }

    override fun initViews() {
        confirmRewardDestinationContainer.applyStatusBarInsets()

        confirmRewardDestinationToolbar.setHomeButtonListener { viewModel.backClicked() }

        confirmRewardDestinationExtrinsicInformation.setOnAccountClickedListener { viewModel.originAccountClicked() }

        confirmRewardDestinationConfirm.prepareForProgress(viewLifecycleOwner)
        confirmRewardDestinationConfirm.setOnClickListener { viewModel.confirmClicked() }

        confirmRewardDestinationRewardDestination.setPayoutAccountClickListener { viewModel.payoutAccountClicked() }
    }

    override fun inject() {
        val payload = argument<ConfirmRewardDestinationPayload>(KEY_PAYLOAD)

        FeatureUtils.getFeature<StakingFeatureComponent>(
            requireContext(),
            StakingFeatureApi::class.java
        )
            .confirmRewardDestinationFactory()
            .create(this, payload)
            .inject(this)
    }

    override fun subscribe(viewModel: ConfirmRewardDestinationViewModel) {
        observeValidations(viewModel)
        setupExternalActions(viewModel)

        viewModel.showNextProgress.observe(confirmRewardDestinationConfirm::setProgress)

        viewModel.rewardDestinationFlow.observe(confirmRewardDestinationRewardDestination::showRewardDestination)

        viewModel.walletUiFlow.observe(confirmRewardDestinationExtrinsicInformation::setWallet)
        viewModel.feeStatusFlow.observe(confirmRewardDestinationExtrinsicInformation::setFeeStatus)
        viewModel.originAccountModelFlow.observe(confirmRewardDestinationExtrinsicInformation::setAccount)
    }
}