package com.pasosync.pasosyncconnect.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.onNavDestinationSelected
import com.pasosync.pasosyncconnect.R
import kotlinx.android.synthetic.main.fragment_pdf.*

class PdfFragment:Fragment(R.layout.fragment_pdf) {
    private  val TAG = "PdfFragment"
val args:PdfFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: ${args.lec.lecturePdfUrl}")
val pdf=args.lec.lecturePdfUrl
        (activity as AppCompatActivity).supportActionBar?.title = " "

       // pdf_view.loadUrl("https://docs.google.com/gview?embedded=true&url=${args.lec.lecturePdfUrl}");
        pdf_view.apply {
            webViewClient = WebViewClient()
            Log.d(TAG, "onViewCreated: $pdf")
            loadUrl(args.lec.lecturePdfUrl)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.fragment_pdf, container, false)
        setHasOptionsMenu(true)

        return v
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

}