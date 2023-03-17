package com.example.newsapp.ui.fragment

import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.newsapp.R
import com.example.newsapp.data.db.ArticleDatabase
import com.example.newsapp.databinding.FragmentArticleNewsBinding
import com.example.newsapp.repository.NewsRepository
import com.example.newsapp.ui.viewmodel.NewsViewModel
import com.example.newsapp.ui.viewmodel.NewsViewModelProviderFactory
import com.google.android.material.snackbar.Snackbar

class ArticleNewsFragment : Fragment(R.layout.fragment_article_news) {

    private lateinit var viewModel: NewsViewModel
    private val args: ArticleNewsFragmentArgs by navArgs()
    private var fragmentArticlesNewsBinding: FragmentArticleNewsBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentArticleNewsBinding.bind(view)
        fragmentArticlesNewsBinding = binding

        val newsRepository = NewsRepository(ArticleDatabase(requireContext()))
        val viewModelProviderFactory = NewsViewModelProviderFactory(requireActivity().application, newsRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory)[NewsViewModel::class.java]

        val article = args.articleArg

        binding.webView.apply {
            webViewClient = WebViewClient()
            loadUrl(article.url!!)
        }

        binding.fab.setOnClickListener {
            viewModel.saveArticle(article)
            Snackbar.make(view, getString(R.string.article_saved_successfully), Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        //Is not necessary save the instance at this point
        fragmentArticlesNewsBinding = null
        super.onDestroy()
    }

}