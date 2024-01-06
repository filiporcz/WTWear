package com.example.wtwear

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

class HomeFragment : Fragment() {

    //private val hatImages = listOf(R.drawable.hat1_n, R.drawable.hat2_f, R.drawable.hat3_n)
    //private val coatImages = listOf(R.drawable.coat1_n, R.drawable.coat2_f, R.drawable.coat3_n)
    //private val trousersImages = listOf(R.drawable.trousers1_n, R.drawable.trousers2_n, R.drawable.trousers3_n)
    //private val shoesImages = listOf(R.drawable.shoes1_n, R.drawable.shoes2_n, R.drawable.shoes3_n)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val temperature = view.findViewById<TextView>(R.id.temperatureText)
        val feelsLike = view.findViewById<TextView>(R.id.feelsLikeText)

        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val unitPref = sharedPref?.getString("unit", "metric")
        Log.d("Current Unit Test:", unitPref.toString())

        userViewModel.weather.observe(viewLifecycleOwner) {
            if (unitPref == "metric") {
                temperature.text = it.temperature.toString()
                feelsLike.text = it.feelsLike.toString()
            } else if (unitPref == "imperial") {
                temperature.text = celsiusToFahrenheit(it.temperature).toString()
                feelsLike.text = celsiusToFahrenheit(it.feelsLike).toString()
            }
        }

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

        userViewModel.clothes.observe(viewLifecycleOwner) {
            Log.d("userViewModel Test Clothes:", it.toString())

            val hat = it.hat
            val top = it.top
            val trousers = it.trousers
            val shoes = it.shoes

            val hatImages = imageToXMLTag(it.images(hat))
            val topImages = imageToXMLTag(it.images(top))
            val trousersImages = imageToXMLTag(it.images(trousers))
            val shoesImages = imageToXMLTag(it.images(shoes))

            val hatDescriptions = it.descriptions(hat)
            val topDescriptions = it.descriptions(top)
            val trousersDescriptions = it.descriptions(trousers)
            val shoesDescriptions = it.descriptions(shoes)

            Log.d("Test Images Hat:", hatImages.toString())
            Log.d("Test Images Top:", topImages.toString())
            Log.d("Test Images Trousers:", trousersImages.toString())
            Log.d("Test Images Shoes:", shoesImages.toString())

            changeClothingXMLImage(hatImage, hatImages)
            changeClothingXMLImage(topImage, topImages)
            changeClothingXMLImage(trousersImage, trousersImages)
            changeClothingXMLImage(shoesImage, shoesImages)

            Log.d("Test Descriptions Hat:", hatDescriptions.toString())
            Log.d("Test Descriptions Top:", topDescriptions.toString())
            Log.d("Test Descriptions Trousers:", trousersDescriptions.toString())
            Log.d("Test Descriptions Shoes:", shoesDescriptions.toString())

            changeClothingXMLDescription(hatImage, hatDescriptions)
            changeClothingXMLDescription(topImage, topDescriptions)
            changeClothingXMLDescription(trousersImage, trousersDescriptions)
            changeClothingXMLDescription(shoesImage, shoesDescriptions)

            setOnClickListenerForImage(leftButton1, rightButton1, hatImage, hatImages)
            setOnClickListenerForImage(leftButton2, rightButton2, topImage, topImages)
            setOnClickListenerForImage(leftButton3, rightButton3, trousersImage, trousersImages)
            setOnClickListenerForImage(leftButton4, rightButton4, shoesImage, shoesImages)

            setClickListenerForPopup(hatImage)
            setClickListenerForPopup(topImage)
            setClickListenerForPopup(trousersImage)
            setClickListenerForPopup(shoesImage)
        }

        //setOnClickListenerForImage(leftButton1, rightButton1, hatImage, hatImages)
        //setOnClickListenerForImage(leftButton2, rightButton2, topImage, coatImages)
        //setOnClickListenerForImage(leftButton3, rightButton3, trousersImage, trousersImages)
        //setOnClickListenerForImage(leftButton4, rightButton4, shoesImage, shoesImages)

        //setClickListenerForPopup(hatImage)
        //setClickListenerForPopup(topImage)
        //setClickListenerForPopup(trousersImage)
        //setClickListenerForPopup(shoesImage)

        return view
    }

    private fun imageToXMLTag(list: List<String?>): List<Int> {
        return list.map { resources.getIdentifier(it, "drawable", context?.packageName)  }
    }

    private fun changeClothingXMLImage(clothing: ImageView, clothes: List<Int>) {
        if (clothes.isEmpty()) {
            clothing.setImageResource(android.R.color.transparent)
        } else {
            clothing.setImageResource(clothes[0])
            clothing.tag = clothes[0] // Save the resource ID as a tag for getting the image ID later
        }
    }

    private fun changeClothingXMLDescription(clothing: ImageView, descriptions: List<String?>) {
        if (descriptions.isEmpty() or (descriptions[0].isNullOrEmpty())) {
            clothing.contentDescription = "No description"
        } else {
            clothing.contentDescription = descriptions[0]
        }
    }

    private fun setOnClickListenerForImage(
        leftButton: ImageView,
        rightButton: ImageView,
        middleImage: ImageView,
        imageList: List<Int>,
    ) {
        leftButton.setOnClickListener {
            rotateImage(middleImage, imageList, backward = true)
        }

        rightButton.setOnClickListener {
            rotateImage(middleImage, imageList, backward = false)
        }
    }

    private fun rotateImage(
        middleImage: ImageView,
        imageList: List<Int>,
        backward: Boolean) {
        // Get the current index of the middle image
        val currentRes = middleImage.tag as? Int ?: 0
        val currentIndex = imageList.indexOf(currentRes)
        //val currentIndex = imageList.indexOf(currentImage)
        Log.d("Test rotateImage tag", currentIndex.toString())

        // Calculate the new index based on the rotation direction
        val newIndex = if (backward) {
            (currentIndex - 1 + imageList.size) % imageList.size
        } else {
            (currentIndex + 1) % imageList.size
        }

        // Update the middle image with the new resource
        middleImage.setImageResource(imageList[newIndex])

        // Save the new index as a tag for the middle image
        //middleImage.tag = newIndex

        // Save the resource ID as a tag for the middle image
        middleImage.tag = imageList[newIndex]
    }
    private fun setClickListenerForPopup(imageView: ImageView) {
        imageView.setOnClickListener {
            // Get the drawable resource ID from the tag of the clicked ImageView
            val imageResource = imageView.tag as? Int ?: 0
            Log.d("Test imageResource tag", imageResource.toString())

            // Get the resource description from the tag of the clicked ImageView
            val descriptionResource = imageView.contentDescription

            // Create an instance of the PopUpFragment with the selected drawable resource ID
            val popUpFragment = PopUpFragment.newInstance(imageResource, descriptionResource)

            // Show the PopUpFragment as a dialog
            popUpFragment.show(requireActivity().supportFragmentManager, "popUpFragment")
        }
    }
}
