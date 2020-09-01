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

import com.stone.vega.library.VegaLayoutManager;
import com.maghribpress.torrebook.ProgressListener;
import com.maghribpress.torrebook.R;
import com.maghribpress.torrebook.classes.TorrentFile;
import com.maghribpress.torrebook.db.entity.Book;
import com.maghribpress.torrebook.viewmodel.TorrentListViewModel;

import java.util.ArrayList;
import java.util.List;

public class TorrentFragment extends Fragment implements ProgressListener {
    public final static String TORRENT_TITLE = "TORRENT_TITLE";
    public final static String TORRENT_GOOGLE_ID="TORRENT_GOOGLE_ID";
    public final static String TORRENT_PAGES="TORRENT_PAGES";
    public final static String TORRENT_LANGUAGE="TORRENT_LANGUAGE";
    public final static String TORRENT_COVERURL="TORRENT_COVERURL";
    public final static String TORRENT_ISBN10="TORRENT_ISBN10";
    public final static String TORRENT_ISBN13="TORRENT_ISBN13";
    public final static String TORRENT_RELEASE_DATE="TORRENT_RELEASE_DATE";
    public final static String TORRENT_RATING="TORRENT_RATING";
    public final static String TORRENT_RATING_COUNT="TORRENT_RATING_COUNT";
    public final static String TORRENT_PUBLISHER="TORRENT_PUBLISHER";
    public final static String TORRENT_DESCRIPTION="TORRENT_DESCRIPTION";
    public final static String TORRENT_AUTHORS_NAMES="TORRENT_AUTHORS_NAMES";
    public final static String TORRENT_CATEGORIES_NAMES="TORRENT_CATEGORIES_NAMES";
    public final static String TORRENT_AUTHORS="TORRENT_AUTHORS";
    public final static String TORRENT_CATEGORIES="TORRENT_CATEGORIES";
    public final static String TORRENT_AUTHOR_LASTNAME = "TORRENT_AUTHOR_LASTNAME";

    private TorrentAdapter recyclerViewAdapter;
    private RecyclerView recyclerView;
    private SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;
    private RelativeLayout progressLayout;
    List<TorrentFile> mtorrentsList;
    Book mBook;
    List<String> authorsNames;
    List<String> categoriesNames;
    TorrentListViewModel model;
    RelativeLayout emptyResultsView;
    private EndlessRecyclerViewScrollListener scrollListener;



    @Nullable
    private TorrentFragment.ViewLifecycleOwner viewLifecycleOwner;



    private void showEmptyResultsPlaceholder(boolean show) {
        if(show) {
            emptyResultsView.setVisibility(View.VISIBLE);
            emptyResultsView.bringToFront();
        }else {
            emptyResultsView.setVisibility(View.INVISIBLE);
        }
    }

    static class ViewLifecycleOwner implements LifecycleOwner {
        private final LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);

        @Override
        public LifecycleRegistry getLifecycle() {
            return lifecycleRegistry;
        }
    }
    @Nullable
    public LifecycleOwner getViewLifecycleOwner() {
        return viewLifecycleOwner;
    }

    @Override
    public void stopProgress() {
        showProgress(false);
    }

    private void showProgress(boolean show) {
        if(show) {
            progressLayout.setVisibility(View.VISIBLE);
            progressLayout.bringToFront();
        }else {
            progressLayout.setVisibility(View.INVISIBLE);
            progressLayout.setVisibility(View.INVISIBLE);
        }
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.torrent_fragment_layout, container, false);
        viewLifecycleOwner = new TorrentFragment.ViewLifecycleOwner();
        viewLifecycleOwner.getLifecycle().handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
        recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        progressLayout = (RelativeLayout) view.findViewById(R.id.progressViewLayout);
        emptyResultsView = (RelativeLayout) view.findViewById(R.id.empty_results_placeholder);
        showProgress(true);
        mBook = new Book();
        Bundle bundle = getArguments();
        if(bundle.containsKey(TORRENT_TITLE)) {
            mBook.setGoogleid(bundle.getString(TORRENT_GOOGLE_ID));
            mBook.setTitle(bundle.getString(TORRENT_TITLE));
            mBook.setPages(bundle.getInt(TORRENT_PAGES));
            mBook.setLanguage(bundle.getString(TORRENT_LANGUAGE));
            mBook.setCoverURL(bundle.getString(TORRENT_COVERURL));
            mBook.setDescription(bundle.getString(TORRENT_DESCRIPTION));
            mBook.setPublisher(bundle.getString(TORRENT_PUBLISHER));
            mBook.setRatingCount(bundle.getInt(TORRENT_RATING_COUNT));
            mBook.setRating(bundle.getFloat(TORRENT_RATING));
            mBook.setReleaseDate(bundle.getString(TORRENT_RELEASE_DATE));
            mBook.setIsbn10(bundle.getString(TORRENT_ISBN10));
            mBook.setIsbn13(bundle.getString(TORRENT_ISBN13));
            mBook.setAuthors(bundle.getParcelableArrayList(TORRENT_AUTHORS));
            mBook.setCategories(bundle.getParcelableArrayList(TORRENT_CATEGORIES));
            authorsNames = bundle.getStringArrayList(TORRENT_AUTHORS_NAMES);
            categoriesNames = bundle.getStringArrayList(TORRENT_CATEGORIES_NAMES);
        }
        if(bundle.containsKey(TORRENT_TITLE)) {
            String searchQuery = getArguments().getString(TORRENT_TITLE);
            mtorrentsList = new ArrayList<TorrentFile>();
            recyclerViewAdapter = new TorrentAdapter(mtorrentsList,((MainActivity)getActivity()).getTorrentDownloadListener(),mBook);
            VegaLayoutManager vlm = new VegaLayoutManager();
            recyclerView.setLayoutManager(vlm);

            recyclerView.setAdapter(recyclerViewAdapter);
            if(bundle.containsKey(TORRENT_AUTHOR_LASTNAME)) {
                searchQuery += " " + getArguments().getString(TORRENT_AUTHOR_LASTNAME);
            }
            Log.d("SEARCHQUERY",searchQuery);
            TorrentListViewModel.Factory factory = new TorrentListViewModel.Factory(
                    getActivity().getApplication(), searchQuery,1, TorrentFragment.this);

            model = ViewModelProviders.of(this, factory)
                    .get(TorrentListViewModel.class);

            // Observe product data
            model.getObservableTorrents().observe(getViewLifecycleOwner(), new Observer<List<TorrentFile>>() {
                @Override
                public void onChanged(@Nullable List<TorrentFile> torrents) {
                    if(torrents != null) {
                        if(torrents.size() == 0) {
                            showEmptyResultsPlaceholder(true);
                        }else {
                            showEmptyResultsPlaceholder(false);
                        }
                    }else {
                        showEmptyResultsPlaceholder(false);
                    }
                    mtorrentsList = torrents;
                    recyclerViewAdapter.setItems(torrents);
                }
            });
            scrollListener = new EndlessRecyclerViewScrollListener(vlm) {

                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                    model.getTorrents(page);
                }
            };
            recyclerView.addOnScrollListener(scrollListener);
            scrollListener.setStartingPage(1);
        }
        return view;
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
                    return true;
                }
                @Override
                public boolean onQueryTextSubmit(String query) {
                    showProgress(true);
                    //showProgress(true);
                    //DataRepository mRepository = ((BasicApp) getActivity().getApplication()).getRepository();
                    //setupModel(query,true);
                    recyclerView.getRecycledViewPool().clear();
                    recyclerViewAdapter.notifyDataSetChanged();
                    scrollListener.resetState();
                    scrollListener.setStartingPage(1);
                    model.getTorrents(query,1,TorrentFragment.this);
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
