package com.dfinn.wallet.feature_crowdloan_impl.presentation.contribute.custom.model

import android.os.Parcelable
import com.dfinn.wallet.feature_crowdloan_api.data.network.blockhain.binding.ParaId
import com.dfinn.wallet.feature_crowdloan_impl.presentation.contribute.custom.BonusPayload
import com.dfinn.wallet.feature_crowdloan_impl.presentation.contribute.select.parcel.ParachainMetadataParcelModel
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal

@Parcelize
class CustomContributePayload(
    val paraId: ParaId,
    val parachainMetadata: ParachainMetadataParcelModel,
    val amount: BigDecimal,
    val previousBonusPayload: BonusPayload?
) : Parcelable