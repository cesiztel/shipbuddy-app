package eu.shipbuddy.repository

import eu.shipbuddy.model.User

class UserRepository {

    lateinit var authenticatedUser: User

    fun setUser(user: User) {
        authenticatedUser = user
    }

    fun getUser(): User {
        return authenticatedUser
    }

    companion object {
        val instance = UserRepository()
    }
}