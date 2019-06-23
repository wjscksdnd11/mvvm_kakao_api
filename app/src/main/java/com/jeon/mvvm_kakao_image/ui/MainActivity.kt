package com.jeon.mvvm_kakao_image.ui

import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.facebook.drawee.backends.pipeline.Fresco
import com.jeon.mvvm_kakao_image.*

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*


class MainActivity : AppCompatActivity(), DetailFragment.OnFragmentInteractionListener {
    override fun onFragmentInteraction(uri: Uri) {

    }

    lateinit var viewmodel: ImageViewModel
    private lateinit var imageAdapter: ImageListAdapter
    var scheduler: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Fresco.initialize(this)

        viewmodel = ViewModelProviders.of(this).get(ImageViewModel::class.java)
        initAdapter()
        initState()

        viewmodel.imagesList.observe(this, Observer {
            imageAdapter.currentList?.let { currentList ->
                if (currentList.size > it.size) {
                    imageAdapter.notifyDataSetChanged()
                }
                txt_empty.visibility = if (it.size == 0) View.VISIBLE else View.GONE
            }
            imageAdapter.submitList(it)
        })
        viewmodel.getState().observe(this, Observer { state ->
            txt_error.visibility = if (viewmodel.listIsEmpty() && state == State.ERROR) View.VISIBLE else View.GONE
            if (!viewmodel.listIsEmpty()) {
                imageAdapter.setState(state ?: State.DONE)
            }
        })

        viewmodel.imageUrl.observe(this, Observer { url ->
            Navigator.addFragmentToActivity(
                fragmentManager = supportFragmentManager,
                fragment = DetailFragment.newInstance(url),
                frameId = R.id.container,
                isBackStack = true
            )
        })
    }

    override fun onBackPressed() {

        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)

        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                cancelJob()
                registerJob { viewmodel.setQuery(newText) }
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {


                return false
            }

        })

        return true
    }

    fun registerJob(job: () -> Unit) {
        scheduler = GlobalScope.launch(Dispatchers.Default) {
            delay(1000L)
            job()
        }
    }

    fun cancelJob() {
        scheduler?.let {
            runBlocking {
                it.cancel()
            }
        }
    }

    private fun initAdapter() {
        imageAdapter = ImageListAdapter({ viewmodel.retry() },
            { imageUrl -> viewmodel.itemClick(imageUrl) })
        recycler_view.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recycler_view.adapter = imageAdapter

    }

    private fun initState() {
        txt_error.setOnClickListener { viewmodel.retry() }

    }
}
