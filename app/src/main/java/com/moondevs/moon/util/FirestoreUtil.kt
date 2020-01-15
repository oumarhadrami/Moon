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
    /**Adding a reference to Users collection in the firebase with userID as the document*/
    private val currentUserDocRef: DocumentReference
        get() = firestoreInstance.document(
            "Users/${FirebaseAuth.getInstance().currentUser?.uid
                ?: throw NullPointerException("UID is null.")}"
        )

    /**If the user data in not in the firestore database, then we create one and add the data */
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

    /**Update the phone number and the name fields in the database*/
    fun updateCurrentUser(phoneNumber: String = "", name: String = "", address: String = "") {
        val userFieldMap = mutableMapOf<String, Any>()
        if (phoneNumber.isNotBlank()) userFieldMap["phoneNumber"] = name
        currentUserDocRef.update(userFieldMap)
    }

    /**Get the info of the current logged in user*/
    fun getCurrentUser(onComplete: (User) -> Unit) {
        currentUserDocRef.get()
            .addOnSuccessListener {
                onComplete(it.toObject(User::class.java)!!)
            }
    }

    /**Add the address set by the user as address 1, address2 ,.. in the addresses collection in Users/UID document*/
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

        //add the address to the database
        currentUserAddressDoc.set(addressHashMap).addOnSuccessListener { Timber.d("DocumentSnapshot successfully written!") }
           .addOnFailureListener { e -> Timber.d("Error writing document $e") }

    }




}