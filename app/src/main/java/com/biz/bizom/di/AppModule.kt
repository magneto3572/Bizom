package com.biz.bizom.di

import android.content.Context
import com.biz.bizom.domain.utils.CShowProgress
import com.biz.bizom.domain.utils.NetworkUtil
import com.biz.bizom.data.sources.RemoteDataSource
import com.biz.bizom.data.sources.apicall.UserApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun providesAuthApi(remoteDataSource: RemoteDataSource): UserApi {
        return remoteDataSource.buildApi(UserApi::class.java)
    }

    @Provides
    fun provideProgressDialog(@ApplicationContext context: Context) : CShowProgress {
        return CShowProgress(context)
    }

    @Provides
    fun provideNetworkUtil(@ApplicationContext context: Context) : NetworkUtil {
        return NetworkUtil(context)
    }
}