package com.pasosync.pasosyncconnect.ui.fragment


import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs


import com.google.android.material.snackbar.Snackbar
import com.pasosync.pasosyncconnect.R
import com.pasosync.pasosyncconnect.ui.MainActivity
import com.pasosync.pasosyncconnect.ui.viewmodels.MainViewModels
import kotlinx.android.synthetic.main.fragment_article.*

class ArticleFragment : Fragment(R.layout.fragment_article) {

    private lateinit var viewModel: MainViewModels
    val args: ArticleFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewmodel
        val article = args.article
        webView.apply {
            webViewClient = WebViewClient()
            loadUrl(article.url)
        }


    }
}