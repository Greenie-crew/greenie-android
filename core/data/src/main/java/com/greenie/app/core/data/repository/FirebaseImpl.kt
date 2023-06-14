package com.greenie.app.core.data.repository

import com.greenie.app.core.domain.repository.FirebaseRepo
import com.greenie.app.core.sharedpreference.AppSharedPreferenceManager
import javax.inject.Inject

class FirebaseImpl @Inject constructor(
    private val appSharedPreferenceManager: AppSharedPreferenceManager
): FirebaseRepo {
    override fun setToken(token: String) {
        appSharedPreferenceManager.setToken(token)
    }

    override fun getToken(): String {
        return appSharedPreferenceManager.getToken()
    }
}