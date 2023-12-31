package com.example.wtwear

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.DialogFragment

class HomeFragment : Fragment() {

    private val hatImages = listOf(R.drawable.hat1_n, R.drawable.hat2_f, R.drawable.hat3_n)
    private val coatImages = listOf(R.drawable.coat1_n, R.drawable.coat2_f, R.drawable.coat3_n)
    private val trousersImages = listOf(R.drawable.trousers1_n, R.drawable.trousers2_n, R.drawable.trousers3_n)
    private val shoesImages = listOf(R.drawable.shoes1_n, R.drawable.shoes2_n, R.drawable.shoes3_n)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val hatImage = view.findViewById<ImageView>(R.id.hatImage)
        val topImage = view.findViewById<ImageView>(R.id.topImage)
        val trousersImage = view.findViewById<ImageView>(R.id.trousersImage)
        val shoesImage = view.findViewById<ImageView>(R.id.shoesImage)

        val leftButton1 = view.findViewById<ImageView>(R.id.leftButton1)
        val rightButton1 = view.findViewById<ImageView>(R.id.rightButton1)

        val leftButton2 = view.findViewById<ImageView>(R.id.leftButton2)
        val rightButton2 = view.findViewById<ImageView>(R.id.rightButton2)

        val leftButton3 = view.findViewById<ImageView>(R.id.leftButton3)
        val rightButton3 = view.findViewById<ImageView>(R.id.rightButton3)

        val leftButton4 = view.findViewById<ImageView>(R.id.leftButton4)
        val rightButton4 = view.findViewById<ImageView>(R.id.rightButton4)

        setOnClickListenerForImage(leftButton1, rightButton1, hatImage, hatImages)
        setOnClickListenerForImage(leftButton2, rightButton2, topImage, coatImages)
        setOnClickListenerForImage(leftButton3, rightButton3, trousersImage, trousersImages)
        setOnClickListenerForImage(leftButton4, rightButton4, shoesImage, shoesImages)

        setClickListenerForPopup(hatImage)
        setClickListenerForPopup(topImage)
        setClickListenerForPopup(trousersImage)
        setClickListenerForPopup(shoesImage)

        return view
    }

    private fun setOnClickListenerForImage(leftButton: ImageView, rightButton: ImageView, middleImage: ImageView, imageList: List<Int>) {
        leftButton.setOnClickListener {
            rotateImage(middleImage, imageList, backward = true)
        }

        rightButton.setOnClickListener {
            rotateImage(middleImage, imageList, backward = false)
        }
    }
    private fun rotateImage(middleImage: ImageView, imageList: List<Int>, backward: Boolean) {
        // Get the current index of the middle image
        val currentIndex = middleImage.tag as? Int ?: 0

        // Calculate the new index based on the rotation direction
        val newIndex = if (backward) {
            (currentIndex - 1 + imageList.size) % imageList.size
        } else {
            (currentIndex + 1) % imageList.size
        }

        // Update the middle image with the new resource
        middleImage.setImageResource(imageList[newIndex])

        // Save the new index as a tag for the middle image
        middleImage.tag = newIndex

        // Save the resource ID as a tag for the middle image
        middleImage.tag = imageList[newIndex]
    }
    private fun setClickListenerForPopup(imageView: ImageView) {
        imageView.setOnClickListener {
            // Get the drawable resource ID from the tag of the clicked ImageView
            val imageResource = imageView.tag as? Int ?: 0

            // Create an instance of the PopUpFragment with the selected drawable resource ID
            val popUpFragment = PopUpFragment.newInstance(imageResource)

            // Show the PopUpFragment as a dialog
            popUpFragment.show(requireActivity().supportFragmentManager, "popUpFragment")
        }
    }
}
