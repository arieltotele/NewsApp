package com.example.newsapp.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapp.R
import com.example.newsapp.adapters.NewsAdapter
import com.example.newsapp.data.db.ArticleDatabase
import com.example.newsapp.databinding.FragmentSearchNewsBinding
import com.example.newsapp.repository.NewsRepository
import com.example.newsapp.ui.viewmodel.NewsViewModel
import com.example.newsapp.ui.viewmodel.NewsViewModelProviderFactory
import com.example.newsapp.util.Constants
import com.example.newsapp.util.Resource
import kotlinx.coroutines.*

class SearchNewsFragment : Fragment(R.layout.fragment_search_news) {

    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    lateinit var fragmentSearchNewsFragment: FragmentSearchNewsBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentSearchNewsBinding.bind(view)
        fragmentSearchNewsFragment = binding

        val newsRepository = NewsRepository(ArticleDatabase(requireContext()))
        val viewModelProviderFactory = NewsViewModelProviderFactory(requireActivity().application, newsRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(NewsViewModel::class.java)

        setupRecyclerView()

        newsAdapter.setOnItemClickListener {
            val action = SearchNewsFragmentDirections.toArticleNewsFragment(it)
            findNavController().navigate(action)
        }

        var job: Job? = null

        //TODO: Manage the onChangeListener with viewBinding or dataBinding
        fragmentSearchNewsFragment.etSearch.addTextChangedListener { searchText ->
            job?.cancel()
            job = MainScope().launch {
                delay(Constants.TIME_SEARCH_NEWS_DELAY)
                if (searchText.toString().isNotEmpty()){
                    viewModel.searchNews(searchText.toString())
                }
            }
        }

        viewModel.searchNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles)
                    }
                }

                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Toast.makeText(activity, "An error have occurred: $message",
                            Toast.LENGTH_LONG).show()
                    }
                }

                is Resource.Loading -> showProgressBar()
            }
        })
    }

    //TODO: Use bindingAdapters for progress bar
    private fun hideProgressBar() {
        fragmentSearchNewsFragment.paginationProgressBar.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
       fragmentSearchNewsFragment.paginationProgressBar.visibility = View.VISIBLE
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter()
        //TODO: Use ViewBing
       fragmentSearchNewsFragment.rvSearchNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)

        }
    }
}