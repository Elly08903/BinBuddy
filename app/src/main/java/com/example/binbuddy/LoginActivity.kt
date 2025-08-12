package com.example.binbuddy

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.binbuddy.data.AppDatabase
import com.example.binbuddy.data.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    private lateinit var session: SessionManager
    private val db by lazy { AppDatabase.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        session = SessionManager(this)
        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin  = findViewById<Button>(R.id.btnLogin)
        val btnGuest  = findViewById<Button>(R.id.btnGuest)

        btnLogin.setOnClickListener {
            val user = etUsername.text.toString()
            val pass = etPassword.text.toString()
            lifecycleScope.launch {
                val dbUser: UserEntity? = withContext(Dispatchers.IO) {
                    db.userDao().getUserByUsername(user)
                }
                if (dbUser != null && dbUser.password == pass) {
                    session.login(dbUser)
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, "Invalid credentials", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnGuest.setOnClickListener {
            lifecycleScope.launch {
                val user = withContext(Dispatchers.IO) {
                    db.userDao().getUserByUsername("user")
                        ?: UserEntity(username = "user", password = "userpass", isAdmin = false).also {
                            db.userDao().insertUser(it)
                        }.let { db.userDao().getUserByUsername("user") }
                }
                SessionManager(this@LoginActivity).guestLogin(usingUserId = user!!.id)
                startActivity(Intent(this@LoginActivity, StoreActivity::class.java))
                finish()
            }
        }

    }
}
