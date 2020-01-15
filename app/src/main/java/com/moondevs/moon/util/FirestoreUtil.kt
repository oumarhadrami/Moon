package com.moondevs.moon.util

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.moondevs.moon.address_screens.addresses_database.Address
import com.moondevs.moon.login_screens.User
import timber.log.Timber
import java.sql.RowId

object FirestoreUtil {
    val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val currentUserDocRef: DocumentReference
        get() = firestoreInstance.document(
            "Users/${FirebaseAuth.getInstance().currentUser?.uid
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


    fun insertAddress(address: Address, rowId: Long){
        val currentUserAddressDoc= firestoreInstance.collection("Users")
                                                        .document(FirebaseAuth.getInstance().currentUser!!.uid)
                                                        .collection("Addresses")
                                                        .document("Address $rowId")
        val addressHashMap = hashMapOf(
            "addressId" to rowId,
            "name" to address.Name,
            "phoneNumber" to address.PhoneNumber,
            "latitude" to address.Latitude,
            "longitude" to address.Longitude
        )


        currentUserAddressDoc.set(addressHashMap).addOnSuccessListener { Timber.d("DocumentSnapshot successfully written!") }
           .addOnFailureListener { e -> Timber.d("Error writing document" + e) }

    }




}