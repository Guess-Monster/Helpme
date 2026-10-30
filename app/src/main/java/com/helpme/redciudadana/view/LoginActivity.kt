
package com.helpme.redciudadana.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.helpme.redciudadana.R
import com.helpme.redciudadana.controller.AuthController

class LoginActivity : AppCompatActivity() {

    private val controller = AuthController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etCorreo =
            findViewById<TextInputEditText>(R.id.etCorreo)

        val etPassword =
            findViewById<TextInputEditText>(R.id.etPassword)

        val btnLogin =
            findViewById<MaterialButton>(R.id.btnLogin)

        val btnRegistro =
            findViewById<MaterialButton>(R.id.btnRegistro)

        btnLogin.setOnClickListener {

            val correo = etCorreo.text.toString()
            val password = etPassword.text.toString()

            btnLogin.isEnabled = false

            controller.iniciarSesion(
                correo,
                password
            ) { exitoso, mensaje ->

                btnLogin.isEnabled = true

                Toast.makeText(
                    this,
                    mensaje,
                    Toast.LENGTH_SHORT
                ).show()

                if (exitoso) {
                    startActivity(
                        Intent(this, HomeActivity::class.java)
                    )
                    finish()
                }
            }
        }

        btnRegistro.setOnClickListener {
            startActivity(
                Intent(this, RegistroActivity::class.java)
            )
        }
    }
}