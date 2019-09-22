package eu.shipbuddy.login

class LoginPresenter(var loginView: LoginView?, val loginInteractor: LoginInteractor) :
        LoginInteractor.OnLoginFinishedListener {

    fun validateCredentials(username: String, password: String) {
        loginInteractor.login(username, password, this)
    }

    fun onDestroy() {
        loginView = null
    }

    override fun onSuccess() {
        loginView?.navigateToHome()
    }
}