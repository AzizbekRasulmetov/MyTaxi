package uz.ravshanbaxranov.mytaxi.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uz.ravshanbaxranov.mytaxi.data.db.LocationsDatabase
import uz.ravshanbaxranov.mytaxi.data.repository.TrackingRepositoryImpl
import uz.ravshanbaxranov.mytaxi.domain.repository.TrackingRepository
import uz.ravshanbaxranov.mytaxi.domain.use_case.AddTrack
import uz.ravshanbaxranov.mytaxi.domain.use_case.TrackUseCases
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @[Provides Singleton]
    fun provideWordInfoDatabase(app: Application): LocationsDatabase {
        return Room
            .databaseBuilder(app, LocationsDatabase::class.java, "locations_db")
            .allowMainThreadQueries()
            .build()
    }

    @[Provides Singleton]
    fun provideTrackingRepository(db: LocationsDatabase): TrackingRepository =
        TrackingRepositoryImpl(db.dao)

    @[Provides Singleton]
    fun provideTrackUseCases(repository: TrackingRepository): TrackUseCases {
        return TrackUseCases(
            addTrack = AddTrack(repository)
        )
    }

}