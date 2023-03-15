package uz.ravshanbaxranov.mytaxi.di

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import uz.ravshanbaxranov.mytaxi.R
import uz.ravshanbaxranov.mytaxi.presentation.MainActivity
import uz.ravshanbaxranov.mytaxi.util.Constants
import uz.ravshanbaxranov.mytaxi.util.Constants.NOTIFICATION_CHANNEL_ID
import uz.ravshanbaxranov.mytaxi.util.Constants.PENDING_INTENT_REQUEST_CODE


@Module
@InstallIn(ServiceComponent::class)
object NotificationModule {


    @SuppressLint("UnspecifiedImmutableFlag")
    @ServiceScoped
    @Provides
    fun providePendingIntent(
        @ApplicationContext context: Context
    ): PendingIntent {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(
                context,
                PENDING_INTENT_REQUEST_CODE,
                Intent(context, MainActivity::class.java).also {
                    it.action = Constants.ACTION_SHOW_TRACKING_FRAGMENT
                },
                PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        } else {
            PendingIntent.getActivity(
                context,
                PENDING_INTENT_REQUEST_CODE,
                Intent(context, MainActivity::class.java).also {
                    it.action = Constants.ACTION_SHOW_TRACKING_FRAGMENT
                },
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
    }

    @[Provides ServiceScoped]
    fun provideNotificationBuilder(
        @ApplicationContext context: Context, pendingIntent: PendingIntent
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.baseline_local_taxi_24)
            .setContentIntent(pendingIntent)
            .setContentTitle("MyTaxi")
            .setContentText("Starting...")
    }

    @[Provides ServiceScoped]
    fun provideNotificationManager(
        @ApplicationContext context: Context
    ): NotificationManager {
        return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

}