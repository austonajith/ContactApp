package list.user.listingapp.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import list.user.listingapp.BASE_URL
import list.user.listingapp.BuildConfig
import list.user.listingapp.data.local.LocalHelper
import list.user.listingapp.data.local.LocalHelperImpl
import list.user.listingapp.data.local.dao.RemoteKeysDao
import list.user.listingapp.data.local.dao.UserDao
import list.user.listingapp.data.remote.api.ApiHelper
import list.user.listingapp.data.remote.api.ApiHelperImpl
import list.user.listingapp.data.remote.api.ApiService
import list.user.listingapp.db.UserDatabase
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideBaseUrl() = BASE_URL

    @Singleton
    @Provides
    fun provideOkHttpClient() = if (BuildConfig.DEBUG) {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    } else {
        OkHttpClient.Builder().build()
    }

    @Singleton
    @Provides
    fun providesRetrofit(okHttpClient: OkHttpClient, BASE_URL: String): Retrofit =
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .build()

    @Singleton
    @Provides
    fun providesUserApiService(retrofit: Retrofit) = retrofit.create(ApiService::class.java)


    @Singleton
    @Provides
    fun provideApiHelper(apiHelper: ApiHelperImpl):ApiHelper {
        return  apiHelper
    }

    @Singleton
    @Provides
    fun provideUserDb(@ApplicationContext context: Context):UserDatabase{
        return UserDatabase.getInstance(context)
    }

    @Singleton
    @Provides
    fun providUserDao(db: UserDatabase): UserDao{
        return db.userDao()
    }

    @Singleton
    @Provides
    fun providRemoteKeysDao(db: UserDatabase): RemoteKeysDao{
        return db.remoteKeysDao()
    }

    @Singleton
    @Provides
    fun provideRoomHelper(localHelper: LocalHelperImpl): LocalHelper{
        return localHelper
    }
}

