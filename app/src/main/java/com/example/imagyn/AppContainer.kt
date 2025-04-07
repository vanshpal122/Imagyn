package com.example.imagyn

import android.content.Context
import com.example.imagyn.data.ImagynRepository
import com.example.imagyn.data.database.ImagynDatabase

interface AppContainer {
    val imagynRepository: ImagynRepository
}

class AppDataContainer(context: Context): AppContainer {
    override val imagynRepository: ImagynRepository by lazy {
        ImagynRepository(ImagynDatabase.getDatabase(context).cardDao())
    }
}