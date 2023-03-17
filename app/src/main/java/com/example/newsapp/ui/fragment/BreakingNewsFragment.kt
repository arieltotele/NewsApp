package com.example.newsapp.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapp.R
import com.example.newsapp.adapters.NewsAdapter
import com.example.newsapp.data.db.ArticleDatabase
import com.example.newsapp.databinding.FragmentBreakingNewsBinding
import com.example.newsapp.repository.NewsRepository
import com.example.newsapp.ui.viewmodel.NewsViewModel
import com.example.newsapp.ui.viewmodel.NewsViewModelProviderFactory
import com.example.newsapp.util.Resource
import kotlinx.android.synthetic.main.fragment_breaking_news.*

class BreakingNewsFragment : Fragment(R.layout.fragment_breaking_news) {

    //TODO: Create a Base Fragment
    //TODO: Use a util for full the data in the recyclerview
    private lateinit var viewModel: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter
    private var fragmentBreakingNews: FragmentBreakingNewsBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentBreakingNewsBinding.bind(view)
        fragmentBreakingNews = binding

        val newsRepository = NewsRepository(ArticleDatabase(requireContext()))
        val viewModelProviderFactory = NewsViewModelProviderFactory(requireActivity().application, newsRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(NewsViewModel::class.java)

        viewModel.getBreakingNews();
        //TODO: Hacer que actualice la busqueda haciendo swipe up
        setupRecyclerView()

        newsAdapter.setOnItemClickListener {
            val action = BreakingNewsFragmentDirections.toArticleNewsFragment(it)
            findNavController().navigate(action)
        }

        //binding.rvBreakingNews.setSc

        viewModel.breakingNews.observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles)
                    }
                }

                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Toast.makeText(activity, "An error have ocurred: $message",
                            Toast.LENGTH_LONG).show()
                    }
                }

                is Resource.Loading -> showProgressBar()

            }
        })
    }

    //TODO: Use bindingAdapters for progress bar
    private fun hideProgressBar() {
        paginationProgressBar.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
        paginationProgressBar.visibility = View.VISIBLE
    }

    private fun setupRecyclerView(){
        newsAdapter = NewsAdapter()
        fragmentBreakingNews!!.rvBreakingNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)

        }
    }

    override fun onDestroy() {
        //Is not necessary save the instance at this point
        fragmentBreakingNews = null
        super.onDestroy()
    }

}