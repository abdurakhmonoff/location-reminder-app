package com.udacity.project4.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.map

class AuthenticationViewModel : ViewModel() {
    enum class AuthenticationState { AUTHORIZED, UNAUTHORIZED }

    val authenticationState = FirebaseUserLiveData().map { user ->
        if (user != null) {
            AuthenticationState.AUTHORIZED
        } else {
            AuthenticationState.UNAUTHORIZED
        }
    }

}