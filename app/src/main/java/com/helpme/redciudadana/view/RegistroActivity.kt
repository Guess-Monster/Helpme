
package com.helpme.redciudadana.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.helpme.redciudadana.R
import com.helpme.redciudadana.controller.AuthController

class RegistroActivity : AppCompatActivity() {

    private val controller = AuthController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        val etNombre =
            findViewById<TextInputEditText>(
                R.id.etNombreRegistro
            )

        val etCorreo =
            findViewById<TextInputEditText>(
                R.id.etCorreoRegistro
            )

        val etPassword =
            findViewById<TextInputEditText>(
                R.id.etPasswordRegistro
            )

        val etConfirmar =
            findViewById<TextInputEditText>(
                R.id.etConfirmarPassword
            )

        val btnCrearCuenta =
            findViewById<MaterialButton>(
                R.id.btnCrearCuenta
            )

        val btnVolver =
            findViewById<MaterialButton>(
                R.id.btnVolverLogin
            )

        btnCrearCuenta.setOnClickListener {

            val nombre = etNombre.text.toString()
            val correo = etCorreo.text.toString()
            val password = etPassword.text.toString()
            val confirmacion = etConfirmar.text.toString()

            btnCrearCuenta.isEnabled = false

            controller.registrar(
                nombre,
                correo,
                password,
                confirmacion
            ) { exitoso, mensaje ->

                btnCrearCuenta.isEnabled = true

                Toast.makeText(
                    this,
                    mensaje,
                    Toast.LENGTH_LONG
                ).show()

                if (exitoso) {

                    val intent = Intent(
                        this,
                        HomeActivity::class.java
                    )

                    startActivity(intent)
                    finish()
                }
            }
        }

        btnVolver.setOnClickListener {
            finish()
        }
    }
}