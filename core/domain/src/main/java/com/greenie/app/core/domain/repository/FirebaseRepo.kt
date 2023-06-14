package com.greenie.app.core.domain.repository

interface FirebaseRepo {
    fun setToken(token: String)
    fun getToken(): String
}