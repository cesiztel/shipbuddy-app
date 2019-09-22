package eu.shipbuddy.login

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import eu.shipbuddy.MainActivity
import eu.shipbuddy.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), LoginView {

    private val presenter = LoginPresenter(this, LoginInteractor())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setupListeners()
    }

    private fun setupListeners() {
        sign_in_submit_button.setOnClickListener { validateCredentials() }
    }

    private fun validateCredentials() {
        presenter.validateCredentials(username.editText!!.text.toString(), password.editText!!.text.toString())
    }

    override fun navigateToHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
