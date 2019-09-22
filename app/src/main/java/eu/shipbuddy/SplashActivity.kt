package eu.shipbuddy

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import eu.shipbuddy.login.LoginActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}