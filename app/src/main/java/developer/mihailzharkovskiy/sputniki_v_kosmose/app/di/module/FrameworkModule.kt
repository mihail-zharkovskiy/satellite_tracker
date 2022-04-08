package developer.mihailzharkovskiy.sputniki_v_kosmose.app.di.module

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.framework.compas.CompassImpl
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.framework.user_location.UserLocationSourceImpl
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.common.resource.Resource
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.framework.Compass
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.framework.UserLocationSource
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FrameworkModule {

    @Provides
    @Singleton
    fun provideCompass(@ApplicationContext context: Context, resource: Resource): Compass =
        CompassImpl(resource, context)

    @Provides
    @Singleton
    fun provideUserLocationSource(@ApplicationContext context: Context): UserLocationSource {
        return UserLocationSourceImpl(context)
    }

}