package com.example.imagyn

import android.content.Context
import com.example.imagyn.data.ImagynRepository
import com.example.imagyn.data.database.ImagynDatabase

//Interface that specifies what dependencies are needed throughout the app
interface AppContainer {
    val imagynRepository: ImagynRepository
}

//Actual Instantiation of the dependencies 
class AppDataContainer(context: Context): AppContainer {
    override val imagynRepository: ImagynRepository by lazy {
        ImagynRepository(ImagynDatabase.getDatabase(context).cardDao())
    }
}
