package com.moondevs.moon.util

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.moondevs.moon.login_screens.User

object FirestoreUtil {
    val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val currentUserDocRef: DocumentReference
        get() = firestoreInstance.document(
            "users/${FirebaseAuth.getInstance().currentUser?.uid
                ?: throw NullPointerException("UID is null.")}"
        )

    fun initCurrentUserIfFirstTime(onComplete: () -> Unit) {
        currentUserDocRef.get().addOnSuccessListener { documentSnapshot ->
            if (!documentSnapshot.exists()) {
                val newUser = User(FirebaseAuth.getInstance().currentUser?.phoneNumber ?: "",
                    FirebaseAuth.getInstance().uid.toString())
                currentUserDocRef.set(newUser).addOnSuccessListener {
                    onComplete()
                }
            }
            else
                onComplete()
        }
    }


    fun updateCurrentUser(phoneNumber: String = "", name: String = "", address: String = "") {
        val userFieldMap = mutableMapOf<String, Any>()
        if (phoneNumber.isNotBlank()) userFieldMap["phoneNumber"] = name
        currentUserDocRef.update(userFieldMap)
    }


    fun getCurrentUser(onComplete: (User) -> Unit) {
        currentUserDocRef.get()
            .addOnSuccessListener {
                onComplete(it.toObject(User::class.java)!!)
            }
    }

}