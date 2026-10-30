
package com.helpme.redciudadana.model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()

    private val database = FirebaseDatabase
        .getInstance()
        .getReference("usuarios")

    fun registrar(
        nombre: String,
        correo: String,
        password: String,
        callback: (Boolean, String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(correo, password)
            .addOnCompleteListener { task ->

                if (!task.isSuccessful) {
                    callback(
                        false,
                        task.exception?.localizedMessage
                            ?: "Error al registrar usuario"
                    )
                    return@addOnCompleteListener
                }

                val uid = auth.currentUser?.uid

                if (uid == null) {
                    callback(false, "No se obtuvo el UID")
                    return@addOnCompleteListener
                }

                val usuario = Usuario(
                    uid = uid,
                    nombre = nombre,
                    correo = correo,
                    rol = "ciudadano",
                    fechaRegistro = System.currentTimeMillis()
                )

                database.child(uid)
                    .setValue(usuario)
                    .addOnCompleteListener { dbTask ->

                        if (dbTask.isSuccessful) {
                            callback(true, "Registro exitoso")
                        } else {
                            auth.signOut()
                            callback(
                                false,
                                "Cuenta creada, pero no se pudo guardar " +
                                        "el perfil. Intenta iniciar sesión."
                            )
                        }
                    }
            }
    }

    fun iniciarSesion(
        correo: String,
        password: String,
        callback: (Boolean, String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(correo, password)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    callback(true, "Inicio de sesión exitoso")
                } else {
                    callback(
                        false,
                        task.exception?.localizedMessage
                            ?: "No se pudo iniciar sesión"
                    )
                }
            }
    }

    fun cerrarSesion() {
        auth.signOut()
    }

    fun usuarioActual() = auth.currentUser
}
