package io.novafoundation.nova.feature_staking_impl.presentation.staking.main

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import io.novafoundation.nova.common.view.bottomSheet.list.fixed.FixedListBottomSheet
import io.novafoundation.nova.feature_staking_impl.R
import io.novafoundation.nova.feature_staking_impl.domain.main.ManageStakeAction
import kotlinx.android.synthetic.main.item_sheet_staking_action.view.itemSheetStakingActionImage
import kotlinx.android.synthetic.main.item_sheet_staking_action.view.itemSheetStakingActionText

class ManageStakingBottomSheet(
    context: Context,
    private val payload: Payload,
    private val onItemChosen: (ManageStakeAction) -> Unit,
) : FixedListBottomSheet(context) {

    class Payload(val availableActions: Set<ManageStakeAction>)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTitle(R.string.staking_manage_title)

        manageItem(R.drawable.ic_staking_operations, R.string.staking_balance_title_v2_2_0, ManageStakeAction.BALANCE)
        manageItem(R.drawable.ic_unpaid_rewards, R.string.staking_reward_payouts_title_v2_2_0, ManageStakeAction.PAYOUTS)
        manageItem(R.drawable.ic_wallet_24, R.string.staking_rewards_destination_title_v2_0_0, ManageStakeAction.REWARD_DESTINATION)
        manageItem(R.drawable.ic_validators_outline, R.string.staking_your_validators, ManageStakeAction.VALIDATORS)
        manageItem(R.drawable.ic_people_outline, R.string.staking_controller_account, ManageStakeAction.CONTROLLER)
    }

    private inline fun manageItem(
        @DrawableRes iconRes: Int,
        @StringRes titleRes: Int,
        action: ManageStakeAction,
        crossinline extraBuilder: (View) -> Unit = {},
    ) {
        if (action in payload.availableActions) {
            item(R.layout.item_sheet_staking_action) {
                it.itemSheetStakingActionImage.setImageResource(iconRes)
                it.itemSheetStakingActionText.setText(titleRes)

                extraBuilder(it)

                it.setDismissingClickListener {
                    onItemChosen(action)
                }
            }
        }
    }
}