package com.example.wtwear

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment


class PopUpFragment : DialogFragment() {

    companion object {
        private const val ARG_IMAGE_RESOURCE = "imageResource"

        fun newInstance(imageResource: Int): PopUpFragment {
            val fragment = PopUpFragment()
            val args = Bundle()
            args.putInt(ARG_IMAGE_RESOURCE, imageResource)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pop_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve the image resource ID from arguments
        val imageResource = arguments?.getInt(ARG_IMAGE_RESOURCE) ?: 0

        // Set the image in your PopUpFragment layout
        val popUpImageView = view.findViewById<ImageView>(R.id.imageView)
        popUpImageView.setImageResource(imageResource)
    }
}