package com.example.wtwear

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment


class PopUpFragment : DialogFragment() {

    companion object {
        private const val ARG_IMAGE_RESOURCE = "imageResource"
        private const val ARG_IMAGE_DESCRIPTION = "imageDescription"

        fun newInstance(imageResource: Int, imageDescription: CharSequence): PopUpFragment {
            val fragment = PopUpFragment()
            val args = Bundle()
            args.putInt(ARG_IMAGE_RESOURCE, imageResource)
            args.putCharSequence(ARG_IMAGE_DESCRIPTION, imageDescription)
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

        // Retrieve the image description from arguments
        val imageDescription = arguments?.getCharSequence(ARG_IMAGE_DESCRIPTION) ?: 0

        // Set the image in your PopUpFragment layout
        val popUpImageView = view.findViewById<ImageView>(R.id.imageView)

        // Set the text in your PopUpFragment layout
        val popUpTextView = view.findViewById<TextView>(R.id.textView)

        popUpImageView.setImageResource(imageResource)
        popUpTextView.text = imageDescription.toString()

    }
}