package com.maghribpress.torrebook.ui;

import android.app.SearchManager;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.maghribpress.torrebook.ProgressListener;
import com.maghribpress.torrebook.R;
import com.maghribpress.torrebook.classes.ApiTokenObject;
import com.maghribpress.torrebook.db.entity.Book;
import com.maghribpress.torrebook.viewmodel.BookSearchViewModel;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment implements ProgressListener {
    BookSearchViewModel model;
    private BooksAdapter recyclerViewAdapter;
    private RecyclerView recyclerView;
    private RelativeLayout progressLayout;
    private SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;
    public static final String SEARCH_DIRECT="SEARCH_DIRECT";
    public static final String SEARCH_QUERY="SEARCH_QUERY";

    private EndlessRecyclerViewScrollListener scrollListener;

    @Override
    public void stopProgress() {
        showProgress(false);
    }

    static class ViewLifecycleOwner implements LifecycleOwner {
        private final LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);

        @Override
        public LifecycleRegistry getLifecycle() {
            return lifecycleRegistry;
        }
    }
    @Nullable
    private ViewLifecycleOwner viewLifecycleOwner;

    /**
     * @return the Lifecycle owner of the current view hierarchy,
     * or null if there is no current view hierarchy.
     */
    @Nullable
    public LifecycleOwner getViewLifecycleOwner() {
        return viewLifecycleOwner;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        //setRetainInstance(true);
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_main, container, false);
        viewLifecycleOwner = new ViewLifecycleOwner();
        viewLifecycleOwner.getLifecycle().handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
        recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        progressLayout= (RelativeLayout) view.findViewById(R.id.progressViewLayout_search);
        //recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        recyclerViewAdapter = new BooksAdapter(new ArrayList<Book>());
        LinearLayoutManager llayout = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(llayout);
        recyclerView.setAdapter(recyclerViewAdapter);
        scrollListener = new EndlessRecyclerViewScrollListener(llayout) {

            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                model.getOnlineBooksByPage(page);
            }
        };
        recyclerView.addOnScrollListener(scrollListener);
        scrollListener.setStartingPage(1);
        Log.d("ROTATIONTAG","onCreateView SearchFragment Called");
        /*BookListViewModel.Factory factory = new BookListViewModel.Factory(
                getActivity().getApplication(), SearchFragment.this);

         model = ViewModelProviders.of(this, factory)
                .get(BookListViewModel.class);*/
        showProgress(true);
        // Observe product data
        boolean isSearch = false;
        String searchQueryFromArgs = "";
        if(getArguments() != null)
            if(getArguments().containsKey(SEARCH_DIRECT)) {
                isSearch = getArguments().getBoolean(SEARCH_DIRECT);
                searchQueryFromArgs = getArguments().getString(SEARCH_QUERY);
            }
        setupModel(searchQueryFromArgs,isSearch);
        if(getActivity()!=null) {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.requestWriteExtPermission();
            mainActivity.setDrawerItemSelected(1);
        }
        return  view;
    }
    private void showProgress(boolean show) {
        if(show) {
            progressLayout.setVisibility(View.VISIBLE);
            progressLayout.bringToFront();
        }else {
            progressLayout.setVisibility(View.INVISIBLE);
        }
    }
    private void setupModel(String query, boolean isSearch) {
        BookSearchViewModel.Factory factory = new BookSearchViewModel.Factory(
                getActivity().getApplication(), query,"en", ApiTokenObject.getInstance().getToken(),1,isSearch,SearchFragment.this);

        model = ViewModelProviders.of(this, factory)
                .get(BookSearchViewModel.class);

        // Observe product data
        model.getSearchedBook().observe(getViewLifecycleOwner(), new Observer<List<Book>>() {
            @Override
            public void onChanged(@Nullable List<Book> books) {
                recyclerViewAdapter.addItems(books);
            }
        });
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem searchItem = menu.findItem(R.id.search_book_view);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

            queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                    Log.i("onQueryText", newText);
                    return true;
                }
                @Override
                public boolean onQueryTextSubmit(String query) {
                    showProgress(true);
                    //showProgress(true);
                    //DataRepository mRepository = ((BasicApp) getActivity().getApplication()).getRepository();
                    //setupModel(query,true);
                    model.getSearchedBooksOnline(query,"en",SearchFragment.this);
                    return true;
                }
            };
            searchView.setOnQueryTextListener(queryTextListener);
        }
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_book_view:
                // Not implemented here
                return false;
            default:
                break;
        }
        searchView.setOnQueryTextListener(queryTextListener);
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onStart() {
        super.onStart();
        if (viewLifecycleOwner != null) {
            viewLifecycleOwner.getLifecycle().handleLifecycleEvent(Lifecycle.Event.ON_START);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (viewLifecycleOwner != null) {
            viewLifecycleOwner.getLifecycle().handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
        }
    }

    @Override
    public void onPause() {
        if (viewLifecycleOwner != null) {
            viewLifecycleOwner.getLifecycle().handleLifecycleEvent(Lifecycle.Event.ON_PAUSE);
        }
        super.onPause();
    }

    @Override
    public void onStop() {
        if (viewLifecycleOwner != null) {
            viewLifecycleOwner.getLifecycle().handleLifecycleEvent(Lifecycle.Event.ON_STOP);
        }
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        if (viewLifecycleOwner != null) {
            viewLifecycleOwner.getLifecycle().handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);
            viewLifecycleOwner = null;
        }
        super.onDestroyView();
    }
}
