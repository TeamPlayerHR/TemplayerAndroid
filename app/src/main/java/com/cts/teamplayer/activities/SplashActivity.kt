package com.cts.teamplayer.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.cts.teamplayer.R
import com.cts.teamplayer.util.TeamPlayerSharedPrefrence

class SplashActivity: AppCompatActivity() {
    private var mpref: TeamPlayerSharedPrefrence? = null
    var bundle: Bundle? = null
    var str : String? =  null
    var str1 : String? =  null
    var str2 : String? =  null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        mpref = TeamPlayerSharedPrefrence.getInstance(this)
        bundle = intent.extras
        handlerCodeSplash()

    }

    private fun handlerCodeSplash() {
        Handler().postDelayed({
            val token = mpref!!.getAccessToken("")
            val isLogin = mpref!!.getLogin("")

            if(token!!.equals("")){
                startActivity(Intent(this@SplashActivity, WelcomeActivity::class.java))
                finish()
            }else{
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()

            }
        }, 2000)
    }



}
