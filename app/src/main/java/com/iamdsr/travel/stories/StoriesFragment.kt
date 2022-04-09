package com.iamdsr.travel.stories;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.iamdsr.travel.R;
import com.iamdsr.travel.customRecyclerViewAdapters.PlannedTripRecyclerAdapter
import com.iamdsr.travel.customRecyclerViewAdapters.StoriesRecyclerAdapter
import com.iamdsr.travel.interfaces.RecyclerViewActionsInterface
import com.iamdsr.travel.models.StoriesModel

class StoriesFragment: Fragment(), RecyclerViewActionsInterface {

    // Widgets
    private var mStoriesRecyclerView: RecyclerView? = null

    // Utils
    private lateinit var storiesRecyclerAdapter: StoriesRecyclerAdapter
    private lateinit var storiesList: MutableList<StoriesModel>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_stories, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpWidgets()
        initRecyclerView()
        storiesList = arrayListOf()
        storiesList.add(StoriesModel("1"))
        storiesList.add(StoriesModel("2"))
        storiesList.add(StoriesModel("3"))
        storiesList.add(StoriesModel("4"))
        storiesRecyclerAdapter.submitList(storiesList)
    }

    private fun initRecyclerView() {
        mStoriesRecyclerView?.layoutManager = LinearLayoutManager(context)
        mStoriesRecyclerView?.setHasFixedSize(true)
        storiesRecyclerAdapter = StoriesRecyclerAdapter(this)
        mStoriesRecyclerView?.adapter = storiesRecyclerAdapter
    }

    private fun setUpWidgets() {
        if (view != null){
            mStoriesRecyclerView = view!!.findViewById(R.id.stories_recycler_view)
        }
    }

    override fun onItemClick(view: View, position: Int) {

    }
}