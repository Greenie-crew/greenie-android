package com.greenie.app.core.domain.usecase.firebase

import com.greenie.app.core.domain.repository.FirebaseRepo
import javax.inject.Inject

class SaveFirebaseToken @Inject constructor(
    private val firebaseRepo: FirebaseRepo
){
    operator fun invoke(token: String) {
        firebaseRepo.setToken(token)
    }
}