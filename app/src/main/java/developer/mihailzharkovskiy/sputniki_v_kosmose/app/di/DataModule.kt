package developer.mihailzharkovskiy.sputniki_v_kosmose.app.di

import android.content.Context
import androidx.annotation.Keep
import androidx.room.Room
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.data.repository.SatellitesLocalSourse
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.data.repository.SatellitesRemoteSource
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.data.repository.SatellitesRepositoryImpl
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.data.sources.local_source.SatellitesLocalSourceImpl
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.data.sources.local_source.database.SatelliteDao
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.data.sources.local_source.database.SatelliteDataBase
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.data.sources.remote_sorce.SatellitesRemoteSourceImpl
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.data.sources.remote_sorce.network.SatellitesRetrofitService
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.domain.SatelliteRepository
import kotlinx.coroutines.CoroutineDispatcher
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    //ROOM
    @Provides
    @Singleton
    fun provideSatelliteDataBase(@ApplicationContext context: Context): SatelliteDataBase =
        Room.databaseBuilder(context, SatelliteDataBase::class.java, "SatelliteDb").build()

    @Provides
    @Singleton
    fun provideSatelliteEntriesDao(dataBase: SatelliteDataBase): SatelliteDao =
        dataBase.entriesDao()

    //RETROFIT
    @Keep
    @Provides
    @Singleton
    fun provideSatellitesRetrofitService(@ApplicationContext context: Context): SatellitesRetrofitService {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .baseUrl("https://celestrak.com/")
            .build()
        return retrofit.create(SatellitesRetrofitService::class.java)
    }

    //REPOSITORY
    @Provides
    @Singleton
    fun provideSatelliteRepository(
        localSource: SatellitesLocalSourse,
        remoteSource: SatellitesRemoteSource,
    ): SatelliteRepository {
        return SatellitesRepositoryImpl(localSource, remoteSource)
    }

    //SOURCES
    @Provides
    @Singleton
    fun provideLocalSource(
        entriesDao: SatelliteDao,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): SatellitesLocalSourse {
        return SatellitesLocalSourceImpl(entriesDao, ioDispatcher)
    }

    @Provides
    @Singleton
    fun provideRemoteSource(
        retrofitService: SatellitesRetrofitService,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): SatellitesRemoteSource {
        return SatellitesRemoteSourceImpl(retrofitService, ioDispatcher)
    }
}