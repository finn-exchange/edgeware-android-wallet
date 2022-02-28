package io.novafoundation.nova.core_db.di

import android.content.Context
import dagger.Module
import dagger.Provides
import io.novafoundation.nova.common.di.scope.ApplicationScope
import io.novafoundation.nova.core_db.AppDatabase
import io.novafoundation.nova.core_db.dao.AccountDao
import io.novafoundation.nova.core_db.dao.AccountStakingDao
import io.novafoundation.nova.core_db.dao.AssetDao
import io.novafoundation.nova.core_db.dao.ChainDao
import io.novafoundation.nova.core_db.dao.DappAuthorizationDao
import io.novafoundation.nova.core_db.dao.MetaAccountDao
import io.novafoundation.nova.core_db.dao.NodeDao
import io.novafoundation.nova.core_db.dao.OperationDao
import io.novafoundation.nova.core_db.dao.PhishingAddressDao
import io.novafoundation.nova.core_db.dao.StakingTotalRewardDao
import io.novafoundation.nova.core_db.dao.StorageDao
import io.novafoundation.nova.core_db.dao.TokenDao

@Module
class DbModule {

    @Provides
    @ApplicationScope
    fun provideAppDatabase(
        context: Context
    ): AppDatabase {
        return AppDatabase.get(context)
    }

    @Provides
    @ApplicationScope
    fun provideUserDao(appDatabase: AppDatabase): AccountDao {
        return appDatabase.userDao()
    }

    @Provides
    @ApplicationScope
    fun provideNodeDao(appDatabase: AppDatabase): NodeDao {
        return appDatabase.nodeDao()
    }

    @Provides
    @ApplicationScope
    fun provideAssetDao(appDatabase: AppDatabase): AssetDao {
        return appDatabase.assetDao()
    }

    @Provides
    @ApplicationScope
    fun provideOperationHistoryDao(appDatabase: AppDatabase): OperationDao {
        return appDatabase.operationDao()
    }

    @Provides
    @ApplicationScope
    fun providePhishingAddressDao(appDatabase: AppDatabase): PhishingAddressDao {
        return appDatabase.phishingAddressesDao()
    }

    @Provides
    @ApplicationScope
    fun provideStorageDao(appDatabase: AppDatabase): StorageDao {
        return appDatabase.storageDao()
    }

    @Provides
    @ApplicationScope
    fun provideTokenDao(appDatabase: AppDatabase): TokenDao {
        return appDatabase.tokenDao()
    }

    @Provides
    @ApplicationScope
    fun provideAccountStakingDao(appDatabase: AppDatabase): AccountStakingDao {
        return appDatabase.accountStakingDao()
    }

    @Provides
    @ApplicationScope
    fun provideStakingTotalRewardDao(appDatabase: AppDatabase): StakingTotalRewardDao {
        return appDatabase.stakingTotalRewardDao()
    }

    @Provides
    @ApplicationScope
    fun provideChainDao(appDatabase: AppDatabase): ChainDao {
        return appDatabase.chainDao()
    }

    @Provides
    @ApplicationScope
    fun provideMetaAccountDao(appDatabase: AppDatabase): MetaAccountDao {
        return appDatabase.metaAccountDao()
    }

    @Provides
    @ApplicationScope
    fun provideDappAuthorizationDao(appDatabase: AppDatabase): DappAuthorizationDao {
        return appDatabase.dAppAuthorizationDao()
    }
}