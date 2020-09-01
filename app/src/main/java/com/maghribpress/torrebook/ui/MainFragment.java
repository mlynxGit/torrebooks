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
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;

import com.maghribpress.torrebook.OnImportBooks;
import com.maghribpress.torrebook.ProgressListener;
import com.maghribpress.torrebook.R;
import com.maghribpress.torrebook.classes.ApiTokenObject;
import com.maghribpress.torrebook.classes.ReadTrackingInfoAsyncTask;
import com.maghribpress.torrebook.db.entity.Book;
import com.maghribpress.torrebook.viewmodel.BookListViewModel;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment implements ProgressListener, OnImportBooks {
    private BookListViewModel model;
    private BookShelfView gridView;
    private BooksGridAdapter booksGridAdapter;
    private RelativeLayout progressLayout;
    private SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;

    @Override
    public void stopProgress() {
        showProgress(false);
    }

    @Override
    public void importFinished(List<Book> books) {
        //model.updateOfflineBooks(books);
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
    private void showProgress(boolean show) {
        if(show) {
            progressLayout.setVisibility(View.VISIBLE);
            progressLayout.bringToFront();
        }else {
            progressLayout.setVisibility(View.INVISIBLE);
        }
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
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bookshelf_layout, container, false);
        viewLifecycleOwner = new ViewLifecycleOwner();
        viewLifecycleOwner.getLifecycle().handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
        gridView = (BookShelfView) view.findViewById(R.id.gridView);
        progressLayout =(RelativeLayout) view.findViewById(R.id.progressViewLayout_bookshelf);
        showProgress(true);
        booksGridAdapter = new BooksGridAdapter(new ArrayList<Book>());
        gridView.setAdapter(booksGridAdapter);
        BookListViewModel.Factory factory = new BookListViewModel.Factory(
                getActivity().getApplication(), ApiTokenObject.getInstance().getToken(),1,MainFragment.this);

        model = ViewModelProviders.of(this, factory)
                .get(BookListViewModel.class);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Book book = (Book) parent.getItemAtPosition(position);
                MainActivity mainAct = (MainActivity) getActivity();
                new ReadTrackingInfoAsyncTask(new WeakReference<MainActivity>(mainAct),book,mainAct).execute();

            }
        });
        // Observe product data
        model.getBooksOffline().observe(getViewLifecycleOwner(), new Observer<List<Book>>() {
            @Override
            public void onChanged(@Nullable List<Book> newBooks) {
                booksGridAdapter = new BooksGridAdapter(newBooks);
                gridView.setAdapter(booksGridAdapter);
            }
        });
        if(getActivity()!=null)
            ((MainActivity) getActivity()).setDrawerItemSelected(0);
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
                    Log.i("onQueryText", newText);
                    return true;
                }
                @Override
                public boolean onQueryTextSubmit(String query) {
                    showProgress(true);
                    SearchFragment searchFragment = new SearchFragment();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(SearchFragment.SEARCH_DIRECT,true);
                    bundle.putString(SearchFragment.SEARCH_QUERY,query);
                    searchFragment.setArguments(bundle);
                    ((MainActivity)getActivity()).startFragment(searchFragment,true);
                    return true;
                }
            };
            searchView.setOnQueryTextListener(queryTextListener);
        }
        super.onCreateOptionsMenu(menu, inflater);
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
