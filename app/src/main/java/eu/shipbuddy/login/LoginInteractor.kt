package eu.shipbuddy.login

import eu.shipbuddy.model.User
import eu.shipbuddy.repository.UserRepository

class LoginInteractor {

    interface OnLoginFinishedListener {
        fun onSuccess()
    }

    fun login(username: String, password: String, listener: OnLoginFinishedListener) {
        UserRepository.instance.setUser(User(username))

        listener.onSuccess()
    }
}