
package com.helpme.redciudadana.controller

import android.util.Patterns
import com.helpme.redciudadana.model.AuthRepository

class AuthController {

    private val repository = AuthRepository()

    fun registrar(
        nombre: String,
        correo: String,
        password: String,
        confirmacion: String,
        callback: (Boolean, String) -> Unit
    ) {
        when {
            nombre.isBlank() -> {
                callback(false, "Ingresa tu nombre")
            }

            !Patterns.EMAIL_ADDRESS.matcher(correo).matches() -> {
                callback(false, "Correo electrónico inválido")
            }

            password.length < 8 -> {
                callback(
                    false,
                    "La contraseña debe tener al menos 8 caracteres"
                )
            }

            password != confirmacion -> {
                callback(false, "Las contraseñas no coinciden")
            }

            else -> {
                repository.registrar(
                    nombre.trim(),
                    correo.trim(),
                    password,
                    callback
                )
            }
        }
    }

    fun iniciarSesion(
        correo: String,
        password: String,
        callback: (Boolean, String) -> Unit
    ) {
        if (correo.isBlank() || password.isBlank()) {
            callback(false, "Completa todos los campos")
            return
        }

        repository.iniciarSesion(
            correo.trim(),
            password,
            callback
        )
    }

    fun cerrarSesion() {
        repository.cerrarSesion()
    }

    fun haySesion(): Boolean {
        return repository.usuarioActual() != null
    }
}