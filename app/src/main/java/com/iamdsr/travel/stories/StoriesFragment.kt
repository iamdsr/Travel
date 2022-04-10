package com.iamdsr.travel.stories;

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.iamdsr.travel.R
import com.iamdsr.travel.customRecyclerViewAdapters.PostsRecyclerAdapter
import com.iamdsr.travel.interfaces.RecyclerViewActionsInterface
import com.iamdsr.travel.models.PostsModel

class StoriesFragment: Fragment(), RecyclerViewActionsInterface {

    // Widgets
    private lateinit var mPostsRecyclerView: RecyclerView
    private lateinit var mAddPostButton: ExtendedFloatingActionButton

    // Utils
    private lateinit var postsRecyclerAdapter: PostsRecyclerAdapter
    private lateinit var postsList: MutableList<PostsModel>

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
        mAddPostButton.setOnClickListener(View.OnClickListener {
            findNavController().navigate(R.id.action_storiesFragment_to_addNewPostFragment)
        })
        postsList = arrayListOf()
        postsList.add(PostsModel("","","", arrayListOf(), "", "", "", "", arrayListOf()))
        postsList.add(PostsModel("","","", arrayListOf(), "", "", "", "", arrayListOf()))
        postsList.add(PostsModel("","","", arrayListOf(), "", "", "", "", arrayListOf()))
        postsList.add(PostsModel("","","", arrayListOf(), "", "", "", "", arrayListOf()))
        postsList.add(PostsModel("","","", arrayListOf(), "", "", "", "", arrayListOf()))
        postsList.add(PostsModel("","","", arrayListOf(), "", "", "", "", arrayListOf()))
        postsList.add(PostsModel("","","", arrayListOf(), "", "", "", "", arrayListOf()))
        postsList.add(PostsModel("","","", arrayListOf(), "", "", "", "", arrayListOf()))
        postsRecyclerAdapter.submitList(postsList)
    }

    private fun initRecyclerView() {
        mPostsRecyclerView.layoutManager = LinearLayoutManager(context)
        mPostsRecyclerView.setHasFixedSize(true)
        postsRecyclerAdapter = PostsRecyclerAdapter(this)
        mPostsRecyclerView.adapter = postsRecyclerAdapter
    }

    private fun setUpWidgets() {
        if (view != null){
            mPostsRecyclerView = view!!.findViewById(R.id.posts_recycler_view)
            mAddPostButton = view!!.findViewById(R.id.add_new_post)
        }
    }

    override fun onItemClick(view: View, position: Int) {

    }
}