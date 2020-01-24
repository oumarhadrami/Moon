package com.moondevs.moon.home_screens.account.rating


import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar

import com.moondevs.moon.R
import com.moondevs.moon.databinding.FragmentFeedbackBinding
import com.moondevs.moon.util.FirestoreUtil
import timber.log.Timber


class FeedbackFragment : Fragment() {

    private lateinit var args: FeedbackFragmentArgs
    private lateinit var binding: FragmentFeedbackBinding
    private var deliveryRating = 0.0
    private var shopRatingFeedBack = 0.0
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_feedback, container, false)
        args = FeedbackFragmentArgs.fromBundle(arguments!!)

        FirestoreUtil.firestoreInstance.document(args.orderPath)
            .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                binding.shopNameInFeedback.text = documentSnapshot!!.get("shopName").toString()
                binding.orderAmountToPayInFeedback.text =
                    documentSnapshot.get("amountToPay").toString() + " MRU"
                binding.orderDateInFeedback.text = documentSnapshot.get("orderDate").toString()
            }


        /**On Submit Button Click*/
        binding.submitFeedback.setOnClickListener {
            deliveryRating = binding.deliveryRating.rating.toDouble()
            shopRatingFeedBack = binding.shopRating.rating.toDouble()
            if (deliveryRating > 0 && shopRatingFeedBack > 0){
                updateDeliveryRatingInOrder()
            }
            else {
                Snackbar.make(
                    it,
                    getString(R.string.please_rate_both_delivery_and_shop),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }







        return binding.root
    }

    private fun updateDeliveryRatingInOrder() {
        FirestoreUtil.firestoreInstance.document(args.orderPath).update(
            "deliveryExperienceRating", deliveryRating
        ).addOnSuccessListener {
            Snackbar.make(binding.root, getString(R.string.submitting_your_feedback), Snackbar.LENGTH_SHORT).show()
            Timber.i("Delivery rated successfully")
            updateShopRatingInOrder()
        }
    }

    private fun updateShopRatingInOrder() {
        FirestoreUtil.firestoreInstance.document(args.orderPath).update(
            "shopRating", shopRatingFeedBack
        ).addOnSuccessListener {
            Timber.i("Shop rated successfully")
            getShopRatingAndNumberOfRatings()
        }
    }

    private fun getShopRatingAndNumberOfRatings() {
        FirestoreUtil.firestoreInstance.document(args.orderPath).get()
            .addOnSuccessListener { DocumentSnapshot ->
                val itemsHashMap =
                    DocumentSnapshot.get("Items") as HashMap<String, HashMap<String, Any>>
                val shopId = itemsHashMap["0"]!!["shopId"].toString()
                FirestoreUtil.firestoreInstance.collection("Shops")
                    .document(shopId).get().addOnSuccessListener { DocumentSnapshot ->
                        val shopRating = DocumentSnapshot.getDouble("shopRating")
                        val numberOfRatings = DocumentSnapshot.getDouble("numberOfRatings")
                        updateOverallShopRating(shopRating, numberOfRatings, shopId)
                    }
            }
    }

    private fun updateOverallShopRating(
        shopRating: Double?,
        numberOfRatings: Double?,
        shopId: String
    ) {
        if (numberOfRatings != 0.0){
        FirestoreUtil.firestoreInstance.collection("Shops").document(shopId).update(
            "shopRating", (shopRating!!.plus((shopRatingFeedBack.minus(shopRating))/ numberOfRatings!!.plus(1)) )
        ).addOnSuccessListener {
            Timber.i("Overall shop rated successfully")
            updateNumberOfRatings(shopId,numberOfRatings)
        }
        }
        else{
            FirestoreUtil.firestoreInstance.collection("Shops").document(shopId).update(
                "shopRating", shopRatingFeedBack
            ).addOnSuccessListener {
                Timber.i("Overall shop rated successfully")
                updateNumberOfRatings(shopId, numberOfRatings) }
        }


    }

    private fun updateNumberOfRatings(shopId: String, numberOfRatings: Double?) {
        FirestoreUtil.firestoreInstance.collection("Shops").document(shopId).update(
            "numberOfRatings", (numberOfRatings!!.plus(1))
            ).addOnSuccessListener {
            Timber.i("Overall shop numberOfRatings updated successfully")
            updateOrderRatingStatus()
        }
    }

    private fun updateOrderRatingStatus() {
        FirestoreUtil.firestoreInstance.document(args.orderPath).update(
            "isOrderRated", true
        ).addOnSuccessListener {
            Snackbar.make(binding.root, getString(R.string.thank_you), Snackbar.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }
    }
}