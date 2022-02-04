package info.softweb.techbullspractical.ui.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AbsListView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import info.softweb.techbullspractical.R
import info.softweb.techbullspractical.adapters.MoviesAdapter
import info.softweb.techbullspractical.databinding.FragmentMoviesBinding
import info.softweb.techbullspractical.utils.NetworkResult
import info.softweb.techbullspractical.viewmodels.MainViewModel

@AndroidEntryPoint
class MoviesFragment : Fragment(), SearchView.OnQueryTextListener {

    private var layoutManager: LinearLayoutManager?=null
    private var _binding: FragmentMoviesBinding? = null
    private val binding get() = _binding!!
    var isScrolling = false
    var currentItems = 0
    var totalItems = 0
    var scrollOutItems = 0
    private lateinit var mainViewModel: MainViewModel
    private val mAdapter by lazy { MoviesAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMoviesBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.mainViewModel = mainViewModel
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(false);
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false);
        setHasOptionsMenu(true)
        setupRecyclerView()
        requestApiData()
        loadMore()
        return binding.root
    }

    private fun loadMore() {
        binding.recyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true
                }
            }

            @RequiresApi(Build.VERSION_CODES.M)
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                currentItems = layoutManager!!.childCount
                totalItems = layoutManager!!.itemCount
                scrollOutItems = layoutManager!!.findFirstVisibleItemPosition()
                if (isScrolling && currentItems + scrollOutItems == totalItems) {
                    isScrolling = false
                    requestApiData()
                }
            }
        })
    }


    private fun setupRecyclerView() {
        binding.recyclerview.adapter = mAdapter
        layoutManager=LinearLayoutManager(requireContext())
        binding.recyclerview.layoutManager = layoutManager
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.movie_menu, menu)

        val search = menu.findItem(R.id.menu_search)
        val searchView = search.actionView as? SearchView
        searchView?.isSubmitButtonEnabled = true
        searchView?.setIconifiedByDefault(false);
        searchView?.setOnQueryTextListener(this)
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            searchApiData(query)
        }
        return true
    }



    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestApiData() {
        mainViewModel.getMovies(mainViewModel.applyQueries())
        mainViewModel.moviesResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is NetworkResult.Success -> {
                    mainViewModel.isLoading.value=false
                    response.data?.let { it.search?.let { it1 -> mAdapter.setData(it1) } }
                }
                is NetworkResult.Error -> {
                    mainViewModel.isLoading.value=false
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is NetworkResult.Loading -> {
                    mainViewModel.isLoading.value=true
                }
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun searchApiData(searchQuery: String) {
        mainViewModel.searchMovies(mainViewModel.applySearchQuery(searchQuery))
        mainViewModel.searchedRecipesResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is NetworkResult.Success -> {
                    val foodRecipe = response.data
                    foodRecipe?.let { it.search?.let { it1 -> mAdapter.setData(it1) } }
                }
                is NetworkResult.Error -> {
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is NetworkResult.Loading -> {
                }
            }
        })
    }

    override fun onQueryTextChange(p0: String?): Boolean {
        return true
    }

}