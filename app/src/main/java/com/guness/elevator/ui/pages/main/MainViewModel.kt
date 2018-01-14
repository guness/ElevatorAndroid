package com.guness.elevator.ui.pages.main

import android.app.Application
import android.arch.lifecycle.LiveData
import com.guness.core.SGViewModel
import com.guness.elevator.db.ElevatorEntity
import com.guness.elevator.db.FavoriteEntity
import com.guness.elevator.db.FavoriteEntity.KeyDef
import com.guness.elevator.db.FavoriteEntity.TypeDef
import com.guness.elevator.db.GroupWithDevices
import com.guness.elevator.ui.pages.panel.PanelActivity
import com.guness.elevator.ui.pages.scan.ScanActivity
import com.guness.utils.SingleLiveEvent
import io.reactivex.Single
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

class MainViewModel(application: Application) : SGViewModel(application) {

    var groups: LiveData<List<GroupWithDevices>> = getApp().getDatabase().dao().getAllDevices()
    var favorites: LiveData<List<FavoriteEntity>> = getApp().getDatabase().dao().getFavorites()
    var showElevatorPickerCommand: SingleLiveEvent<Pair<String, String?>> = SingleLiveEvent()
    var showFloorPickerCommand: SingleLiveEvent<Triple<String, ElevatorEntity, Int?>> = SingleLiveEvent()

    override fun onStart() {
        super.onStart()
    }

    fun onElevatorSelected(groupId: Int, itemId: Int) {
        val device = groups.value?.find { it.group?.id?.toInt() == groupId }
                ?.elevators?.find { it.id.toInt() == itemId }
                ?.device
        if (device != null) {
            launchCommand.value = Pair(false, PanelActivity.newIntent(getAppContext(), device))
        }
    }

    fun onAddElevatorClicked(): Boolean {
        launchCommand.value = Pair(false, ScanActivity.newIntent(getAppContext()))
        return true
    }

    fun onDeleteElevatorClicked(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun onFavoriteClicked(@KeyDef key: String) {
        val favorite = favorites.value?.find { key == it.key }
        if (favorite == null) {
            showElevatorPickerCommand.value = Pair(key, null)
        } else {
            launchCommand.value = Pair(false, PanelActivity.newIntent(getAppContext(), favorite.device, favorite.floor))
        }
    }

    fun onFavoriteLongClicked(@KeyDef key: String) {
        showElevatorPickerCommand.value = when (getType(key)) {
            FavoriteEntity.TYPE_ELEVATOR -> {
                val favorite = favorites.value?.find { key == it.key }
                Pair(key, favorite?.device)
            }
            FavoriteEntity.TYPE_FLOOR -> {
                Pair(key, null)
            }
            else -> {
                throw IllegalArgumentException("Unsupported type")
            }
        }
    }

    fun onFavoriteElevatorPicked(@KeyDef key: String, entity: ElevatorEntity) {
        when (getType(key)) {
            FavoriteEntity.TYPE_ELEVATOR -> {
                Single.just(FavoriteEntity())
                        .observeOn(Schedulers.io())
                        .subscribe(Consumer {
                            it.key = key
                            it.description = entity.description
                            it.device = entity.device
                            it.type = FavoriteEntity.TYPE_ELEVATOR
                            getApp().getDatabase().dao().insert(it)
                        })
            }
            FavoriteEntity.TYPE_FLOOR -> {
                val favorite = favorites.value?.find { key == it.key }
                val floor = if (favorite?.device == entity.device) {
                    favorite.floor
                } else {
                    null
                }
                showFloorPickerCommand.postValue(Triple(key, entity, floor))
            }
            else -> {
                throw IllegalArgumentException("Unsupported type")
            }
        }
    }

    fun onFavoriteFloorPicked(@KeyDef key: String, entity: ElevatorEntity, floor: Int) {
        Single.just(FavoriteEntity())
                .observeOn(Schedulers.io())
                .subscribe(Consumer {
                    it.key = key
                    it.description = entity.description
                    it.device = entity.device
                    it.type = FavoriteEntity.TYPE_FLOOR
                    it.floor = floor
                    getApp().getDatabase().dao().insert(it)
                })
    }

    fun onFavoriteRemoved(key: String) {
        Single.just(key)
                .observeOn(Schedulers.io())
                .subscribe(Consumer {
                    getApp().getDatabase().dao().deleteFavorite(key)
                })
    }

    @TypeDef
    private fun getType(@KeyDef key: String): String {
        return when (key) {
            FavoriteEntity.L1, FavoriteEntity.L2, FavoriteEntity.L3, FavoriteEntity.L4 -> {
                FavoriteEntity.TYPE_ELEVATOR
            }
            FavoriteEntity.R1, FavoriteEntity.R2, FavoriteEntity.R3, FavoriteEntity.R4 -> {
                FavoriteEntity.TYPE_FLOOR
            }
            else -> {
                throw IllegalArgumentException("this is not a supported key: " + key)
            }
        }
    }
}