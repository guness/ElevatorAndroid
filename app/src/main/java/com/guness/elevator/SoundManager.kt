package com.guness.elevator

import android.annotation.TargetApi
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build

/**
 * Created by guness on 21.01.2018.
 */
class SoundManager(private val context: Context) {

    private lateinit var mSound: SoundPool
    private var mClickSound = 0
    private var mDingSound = 0

    fun createSoundPool() {

        mSound = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            createNewSoundPool()
        } else {
            createOldSoundPool()
        }
        mClickSound = mSound.load(context, R.raw.elevator_button, 1) // in 2nd param u have to pass your desire ringtone
        mDingSound = mSound.load(context, R.raw.elevator_ding, 1)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun createNewSoundPool(): SoundPool {
        val attributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        return SoundPool.Builder()
                .setAudioAttributes(attributes)
                .build()
    }

    @Suppress("DEPRECATION")
    private fun createOldSoundPool(): SoundPool {
        return SoundPool(5, AudioManager.STREAM_MUSIC, 0)
    }

    fun playDing() {
        mSound.play(mDingSound, 1f, 1f, 1, 0, 1f)
    }

    fun playClick() {
        mSound.play(mClickSound, 1f, 1f, 1, 0, 1f)
    }

    fun release() {
        mSound.release()
    }
}