package io.novafoundation.nova.feature_staking_impl.di

import dagger.Module
import dagger.Provides
import io.novafoundation.nova.common.address.AddressIconGenerator
import io.novafoundation.nova.common.data.memory.ComputationalCache
import io.novafoundation.nova.common.data.network.AppLinksProvider
import io.novafoundation.nova.common.data.network.NetworkApiCreator
import io.novafoundation.nova.common.data.network.rpc.BulkRetriever
import io.novafoundation.nova.common.data.storage.Preferences
import io.novafoundation.nova.common.di.scope.FeatureScope
import io.novafoundation.nova.common.mixin.MixinFactory
import io.novafoundation.nova.common.resources.ResourceManager
import io.novafoundation.nova.core.storage.StorageCache
import io.novafoundation.nova.core_db.dao.AccountStakingDao
import io.novafoundation.nova.core_db.dao.StakingTotalRewardDao
import io.novafoundation.nova.feature_account_api.data.extrinsic.ExtrinsicService
import io.novafoundation.nova.feature_account_api.domain.interfaces.AccountRepository
import io.novafoundation.nova.feature_account_api.presenatation.account.AddressDisplayUseCase
import io.novafoundation.nova.feature_staking_api.domain.api.EraTimeCalculatorFactory
import io.novafoundation.nova.feature_staking_api.domain.api.IdentityRepository
import io.novafoundation.nova.feature_staking_api.domain.api.StakingRepository
import io.novafoundation.nova.feature_staking_impl.data.StakingSharedState
import io.novafoundation.nova.feature_staking_impl.data.network.subquery.StakingApi
import io.novafoundation.nova.feature_staking_impl.data.network.subquery.SubQueryValidatorSetFetcher
import io.novafoundation.nova.feature_staking_impl.data.repository.IdentityRepositoryImpl
import io.novafoundation.nova.feature_staking_impl.data.repository.PayoutRepository
import io.novafoundation.nova.feature_staking_impl.data.repository.StakingConstantsRepository
import io.novafoundation.nova.feature_staking_impl.data.repository.StakingRepositoryImpl
import io.novafoundation.nova.feature_staking_impl.data.repository.StakingRewardsRepository
import io.novafoundation.nova.feature_staking_impl.data.repository.datasource.StakingRewardsDataSource
import io.novafoundation.nova.feature_staking_impl.data.repository.datasource.StakingStoriesDataSource
import io.novafoundation.nova.feature_staking_impl.data.repository.datasource.StakingStoriesDataSourceImpl
import io.novafoundation.nova.feature_staking_impl.data.repository.datasource.SubqueryStakingRewardsDataSource
import io.novafoundation.nova.feature_staking_impl.domain.StakingInteractor
import io.novafoundation.nova.feature_staking_impl.domain.alerts.AlertsInteractor
import io.novafoundation.nova.feature_staking_impl.domain.payout.PayoutInteractor
import io.novafoundation.nova.feature_staking_impl.domain.recommendations.ValidatorRecommendatorFactory
import io.novafoundation.nova.feature_staking_impl.domain.recommendations.settings.RecommendationSettingsProviderFactory
import io.novafoundation.nova.feature_staking_impl.domain.rewards.RewardCalculatorFactory
import io.novafoundation.nova.feature_staking_impl.domain.setup.SetupStakingInteractor
import io.novafoundation.nova.feature_staking_impl.domain.staking.bond.BondMoreInteractor
import io.novafoundation.nova.feature_staking_impl.domain.staking.controller.ControllerInteractor
import io.novafoundation.nova.feature_staking_impl.domain.staking.rebond.RebondInteractor
import io.novafoundation.nova.feature_staking_impl.domain.staking.redeem.RedeemInteractor
import io.novafoundation.nova.feature_staking_impl.domain.staking.rewardDestination.ChangeRewardDestinationInteractor
import io.novafoundation.nova.feature_staking_impl.domain.staking.unbond.UnbondInteractor
import io.novafoundation.nova.feature_staking_impl.domain.validators.ValidatorProvider
import io.novafoundation.nova.feature_staking_impl.domain.validators.current.CurrentValidatorsInteractor
import io.novafoundation.nova.feature_staking_impl.domain.validators.current.search.SearchCustomValidatorsInteractor
import io.novafoundation.nova.feature_staking_impl.presentation.common.SetupStakingSharedState
import io.novafoundation.nova.feature_staking_impl.presentation.common.rewardDestination.RewardDestinationMixin
import io.novafoundation.nova.feature_staking_impl.presentation.common.rewardDestination.RewardDestinationProvider
import io.novafoundation.nova.feature_wallet_api.domain.AssetUseCase
import io.novafoundation.nova.feature_wallet_api.domain.TokenUseCase
import io.novafoundation.nova.feature_wallet_api.domain.implementations.AssetUseCaseImpl
import io.novafoundation.nova.feature_wallet_api.domain.implementations.SharedStateTokenUseCase
import io.novafoundation.nova.feature_wallet_api.domain.interfaces.TokenRepository
import io.novafoundation.nova.feature_wallet_api.domain.interfaces.WalletConstants
import io.novafoundation.nova.feature_wallet_api.domain.interfaces.WalletRepository
import io.novafoundation.nova.feature_wallet_api.presentation.mixin.assetSelector.AssetSelectorFactory
import io.novafoundation.nova.feature_wallet_api.presentation.mixin.assetSelector.AssetSelectorMixin
import io.novafoundation.nova.feature_wallet_api.presentation.mixin.fee.FeeLoaderMixin
import io.novafoundation.nova.feature_wallet_api.presentation.mixin.fee.create
import io.novafoundation.nova.runtime.di.LOCAL_STORAGE_SOURCE
import io.novafoundation.nova.runtime.di.REMOTE_STORAGE_SOURCE
import io.novafoundation.nova.runtime.multiNetwork.ChainRegistry
import io.novafoundation.nova.runtime.storage.source.StorageDataSource
import javax.inject.Named

@Module
class StakingFeatureModule {

    @Provides
    @FeatureScope
    fun provideAssetUseCase(
        walletRepository: WalletRepository,
        accountRepository: AccountRepository,
        sharedState: StakingSharedState,
    ): AssetUseCase = AssetUseCaseImpl(
        walletRepository,
        accountRepository,
        sharedState
    )

    @Provides
    fun provideAssetSelectorMixinFactory(
        assetUseCase: AssetUseCase,
        singleAssetSharedState: StakingSharedState,
        resourceManager: ResourceManager
    ): MixinFactory<AssetSelectorMixin.Presentation> = AssetSelectorFactory(
        assetUseCase,
        singleAssetSharedState,
        resourceManager
    )

    @Provides
    @FeatureScope
    fun provideTokenUseCase(
        tokenRepository: TokenRepository,
        sharedState: StakingSharedState,
    ): TokenUseCase = SharedStateTokenUseCase(
        tokenRepository,
        sharedState
    )

    @Provides
    @FeatureScope
    fun provideFeeLoaderMixin(
        feeLoaderMixinFactory: FeeLoaderMixin.Factory,
        tokenUseCase: TokenUseCase,
    ): FeeLoaderMixin.Presentation = feeLoaderMixinFactory.create(tokenUseCase)

    @Provides
    @FeatureScope
    fun provideStakingSharedState(
        chainRegistry: ChainRegistry,
        preferences: Preferences
    ) = StakingSharedState(chainRegistry, preferences)

    @Provides
    @FeatureScope
    fun provideStakingStoriesDataSource(): StakingStoriesDataSource = StakingStoriesDataSourceImpl()

    @Provides
    @FeatureScope
    fun provideStakingRewardsSubqueryDataSource(
        stakingApi: StakingApi,
        stakingTotalRewardDao: StakingTotalRewardDao,
    ): StakingRewardsDataSource = SubqueryStakingRewardsDataSource(
        stakingApi = stakingApi,
        stakingTotalRewardDao = stakingTotalRewardDao
    )

    @Provides
    @FeatureScope
    fun provideStakingRepository(
        accountStakingDao: AccountStakingDao,
        @Named(LOCAL_STORAGE_SOURCE) localStorageSource: StorageDataSource,
        @Named(REMOTE_STORAGE_SOURCE) remoteStorageSource: StorageDataSource,
        stakingStoriesDataSource: StakingStoriesDataSource,
        walletConstants: WalletConstants,
        chainRegistry: ChainRegistry,
    ): StakingRepository = StakingRepositoryImpl(
        accountStakingDao = accountStakingDao,
        remoteStorage = remoteStorageSource,
        localStorage = localStorageSource,
        stakingStoriesDataSource = stakingStoriesDataSource,
        walletConstants = walletConstants,
        chainRegistry = chainRegistry
    )

    @Provides
    @FeatureScope
    fun provideIdentityRepository(
        bulkRetriever: BulkRetriever,
        chainRegistry: ChainRegistry,
    ): IdentityRepository = IdentityRepositoryImpl(
        bulkRetriever,
        chainRegistry
    )

    @Provides
    @FeatureScope
    fun provideStakingInteractor(
        walletRepository: WalletRepository,
        accountRepository: AccountRepository,
        stakingRepository: StakingRepository,
        stakingRewardsRepository: StakingRewardsRepository,
        stakingConstantsRepository: StakingConstantsRepository,
        identityRepository: IdentityRepository,
        payoutRepository: PayoutRepository,
        stakingSharedState: StakingSharedState,
        assetUseCase: AssetUseCase,
        factory: EraTimeCalculatorFactory,
    ) = StakingInteractor(
        walletRepository,
        accountRepository,
        stakingRepository,
        stakingRewardsRepository,
        stakingConstantsRepository,
        identityRepository,
        stakingSharedState,
        payoutRepository,
        assetUseCase,
        factory
    )

    @Provides
    @FeatureScope
    fun provideEraTimeCalculatorFactory(
        stakingRepository: StakingRepository,
    ) = EraTimeCalculatorFactory(stakingRepository)

    @Provides
    @FeatureScope
    fun provideAlertsInteractor(
        stakingRepository: StakingRepository,
        stakingConstantsRepository: StakingConstantsRepository,
        sharedState: StakingSharedState,
        walletRepository: WalletRepository,
    ) = AlertsInteractor(
        stakingRepository,
        stakingConstantsRepository,
        sharedState,
        walletRepository
    )

    @Provides
    @FeatureScope
    fun provideRewardCalculatorFactory(
        repository: StakingRepository,
        sharedState: StakingSharedState
    ) = RewardCalculatorFactory(repository, sharedState)

    @Provides
    @FeatureScope
    fun provideValidatorRecommendatorFactory(
        validatorProvider: ValidatorProvider,
        computationalCache: ComputationalCache,
        sharedState: StakingSharedState,
    ) = ValidatorRecommendatorFactory(validatorProvider, sharedState, computationalCache)

    @Provides
    @FeatureScope
    fun provideValidatorProvider(
        stakingRepository: StakingRepository,
        identityRepository: IdentityRepository,
        rewardCalculatorFactory: RewardCalculatorFactory,
        stakingConstantsRepository: StakingConstantsRepository,
    ) = ValidatorProvider(
        stakingRepository,
        identityRepository,
        rewardCalculatorFactory,
        stakingConstantsRepository
    )

    @Provides
    @FeatureScope
    fun provideStakingConstantsRepository(
        chainRegistry: ChainRegistry,
    ) = StakingConstantsRepository(chainRegistry)

    @Provides
    @FeatureScope
    fun provideRecommendationSettingsProviderFactory(
        stakingConstantsRepository: StakingConstantsRepository,
        computationalCache: ComputationalCache,
        sharedState: StakingSharedState,
    ) = RecommendationSettingsProviderFactory(
        computationalCache,
        stakingConstantsRepository,
        sharedState
    )

    @Provides
    @FeatureScope
    fun provideSetupStakingInteractor(
        extrinsicService: ExtrinsicService,
        sharedState: StakingSharedState,
    ) = SetupStakingInteractor(extrinsicService, sharedState)

    @Provides
    @FeatureScope
    fun provideSetupStakingSharedState() = SetupStakingSharedState()

    @Provides
    fun provideRewardDestinationChooserMixin(
        resourceManager: ResourceManager,
        appLinksProvider: AppLinksProvider,
        stakingInteractor: StakingInteractor,
        iconGenerator: AddressIconGenerator,
        accountDisplayUseCase: AddressDisplayUseCase,
        sharedState: StakingSharedState,
    ): RewardDestinationMixin.Presentation = RewardDestinationProvider(
        resourceManager, stakingInteractor, iconGenerator, appLinksProvider, sharedState, accountDisplayUseCase
    )

    @Provides
    @FeatureScope
    fun provideStakingRewardsApi(networkApiCreator: NetworkApiCreator): StakingApi {
        return networkApiCreator.create(StakingApi::class.java)
    }

    @Provides
    @FeatureScope
    fun provideStakingRewardsRepository(
        rewardDataSource: StakingRewardsDataSource,
    ): StakingRewardsRepository {
        return StakingRewardsRepository(rewardDataSource)
    }

    @Provides
    @FeatureScope
    fun provideValidatorSetFetcher(
        stakingApi: StakingApi,
        stakingRepository: StakingRepository,
    ): SubQueryValidatorSetFetcher {
        return SubQueryValidatorSetFetcher(
            stakingApi,
            stakingRepository
        )
    }

    @Provides
    @FeatureScope
    fun providePayoutRepository(
        stakingRepository: StakingRepository,
        validatorSetFetcher: SubQueryValidatorSetFetcher,
        bulkRetriever: BulkRetriever,
        storageCache: StorageCache,
        chainRegistry: ChainRegistry,
    ): PayoutRepository {
        return PayoutRepository(stakingRepository, bulkRetriever, validatorSetFetcher, storageCache, chainRegistry)
    }

    @Provides
    @FeatureScope
    fun providePayoutInteractor(
        sharedState: StakingSharedState,
        extrinsicService: ExtrinsicService,
    ) = PayoutInteractor(sharedState, extrinsicService)

    @Provides
    @FeatureScope
    fun provideBondMoreInteractor(
        sharedState: StakingSharedState,
        extrinsicService: ExtrinsicService,
    ) = BondMoreInteractor(extrinsicService, sharedState)

    @Provides
    @FeatureScope
    fun provideUnbondInteractor(
        extrinsicService: ExtrinsicService,
        stakingRepository: StakingRepository,
    ) = UnbondInteractor(extrinsicService, stakingRepository)

    @Provides
    @FeatureScope
    fun provideRedeemInteractor(
        extrinsicService: ExtrinsicService,
        stakingRepository: StakingRepository,
    ) = RedeemInteractor(extrinsicService, stakingRepository)

    @Provides
    @FeatureScope
    fun provideRebondInteractor(
        sharedState: StakingSharedState,
        extrinsicService: ExtrinsicService,
    ) = RebondInteractor(extrinsicService, sharedState)

    @Provides
    @FeatureScope
    fun provideControllerInteractor(
        sharedState: StakingSharedState,
        extrinsicService: ExtrinsicService,
    ) = ControllerInteractor(extrinsicService, sharedState)

    @Provides
    @FeatureScope
    fun provideCurrentValidatorsInteractor(
        stakingRepository: StakingRepository,
        stakingConstantsRepository: StakingConstantsRepository,
        validatorProvider: ValidatorProvider,
    ) = CurrentValidatorsInteractor(
        stakingRepository, stakingConstantsRepository, validatorProvider
    )

    @Provides
    @FeatureScope
    fun provideChangeRewardDestinationInteractor(
        extrinsicService: ExtrinsicService,
    ) = ChangeRewardDestinationInteractor(extrinsicService)

    @Provides
    @FeatureScope
    fun provideSearchCustomValidatorsInteractor(
        validatorProvider: ValidatorProvider,
        sharedState: StakingSharedState
    ) = SearchCustomValidatorsInteractor(validatorProvider, sharedState)
}