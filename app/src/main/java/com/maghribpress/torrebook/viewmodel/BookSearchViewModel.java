package com.maghribpress.torrebook.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.maghribpress.torrebook.BasicApp;
import com.maghribpress.torrebook.DataRepository;
import com.maghribpress.torrebook.ProgressListener;
import com.maghribpress.torrebook.classes.ApiTokenObject;
import com.maghribpress.torrebook.db.entity.Author;
import com.maghribpress.torrebook.db.entity.Book;
import com.maghribpress.torrebook.db.entity.Category;
import com.maghribpress.torrebook.network.GetBooksDataService;
import com.maghribpress.torrebook.network.RetrofitInstance;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookSearchViewModel extends AndroidViewModel {
    private final MutableLiveData<List<Book>> mObservableBooks;
    private final DataRepository mRepository;
    private final String mQuery;
    private final String mLang;
    private final String mToken;
    private final int mPage;
    private final ProgressListener mPl;
    private final boolean isSearch;
    public BookSearchViewModel(@NonNull Application application, String query,String lang,String token,int page, boolean issearch, ProgressListener pl) {
        super(application);
        mPage=page;
        mPl= pl;
        mQuery=query;
        mLang=lang;
        mToken=token;
        isSearch=issearch;
        mRepository = ((BasicApp) application).getRepository();
        if(isSearch){
            mObservableBooks = mRepository.searchBooksOnline(mQuery,mLang,mToken,mPl);
        }else {
            mObservableBooks = mRepository.getBooksOnline(mToken,mPage,mPl);
        }
    }
    public MutableLiveData<List<Book>> getSearchedBook() {
        return mObservableBooks;
    }
    public void getSearchedBooksOnline(String query, String lang, ProgressListener pl) {
        List<Book> books = new ArrayList<Book>();
        GetBooksDataService service = RetrofitInstance.getRetrofitInstance().create(GetBooksDataService.class);
        Call<ResponseBody> bookscall = service.findbook(query,ApiTokenObject.getInstance().getToken(),lang);
        bookscall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonRESULTS = new JSONObject(response.body().string());
                        if (jsonRESULTS.has("items")) {
                            JSONArray booksArray = jsonRESULTS.getJSONArray("items");
                            for (int i = 0; i < booksArray.length(); i++) {
                                ArrayList<Author> authors = new ArrayList<Author>();
                                ArrayList<Category> categories = new ArrayList<Category>();
                                JSONObject book = booksArray.getJSONObject(i).getJSONObject("volumeInfo");
                                Book tmpBook = new Book();
                                tmpBook.setGoogleid(booksArray.getJSONObject(i).getString("id"));
                                tmpBook.setTitle(book.getString("title"));
                                if(book.has("pageCount")){
                                    tmpBook.setPages(book.getInt("pageCount"));
                                }
                                if(book.has("language")){
                                    tmpBook.setLanguage(book.getString("language"));
                                }
                                if(book.has("imageLinks")){
                                    tmpBook.setCoverURL(book.getJSONObject("imageLinks").getString("thumbnail"));
                                }
                                //tmpBook.setIsbn10(book.getString("isbn10"));
                                //tmpBook.setIsbn13(book.getString("isbn13"));
                                if(book.has("publishedDate")){
                                    tmpBook.setReleaseDate(book.getString("publishedDate"));
                                }
                                if(book.has("averageRating")) {
                                    tmpBook.setRating(book.getInt("averageRating"));
                                    tmpBook.setRatingCount(book.getInt("ratingsCount"));
                                }
                                if(book.has("publisher")){
                                    tmpBook.setPublisher(book.getString("publisher"));
                                }
                                if(book.has("description")){
                                    tmpBook.setDescription(book.getString("description"));
                                }
                                if(book.has("authors")){
                                    JSONArray authorsArray = book.getJSONArray("authors");
                                    for (int a = 0; a < authorsArray.length(); a++) {
                                        Author mauthor = new Author();
                                        mauthor.setName(authorsArray.getString(a));
                                        authors.add(mauthor);
                                    }
                                }
                                if(book.has("industryIdentifiers")){
                                    JSONArray identifiers = book.getJSONArray("industryIdentifiers");
                                    for (int a = 0; a < identifiers.length(); a++) {
                                        JSONObject identifier = identifiers.getJSONObject(a);
                                        if(identifier.getString("type").equals("ISBN_13")) {
                                            tmpBook.setIsbn13(identifier.getString("identifier"));
                                        }else if (identifier.getString("type").equals("ISBN_10")) {
                                            tmpBook.setIsbn10(identifier.getString("identifier"));
                                        }
                                    }
                                }
                                if(book.has("categories")){
                                    JSONArray categoriesArray = book.getJSONArray("categories");
                                    for (int c = 0; c < categoriesArray.length(); c++) {
                                        Category mcategory = new Category();
                                        mcategory.setName(categoriesArray.getString(c));
                                        categories.add(mcategory);
                                    }
                                }
                                tmpBook.setAuthors(authors);
                                tmpBook.setCategories(categories);
                                books.add(tmpBook);
                            }
                            mObservableBooks.setValue(books);
                            pl.stopProgress();
                        } else {
                            //Toast.makeText(getContext(), error_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pl.stopProgress();
            }
        });
    }
    public void getOnlineBooksByPage(int page) {
        GetBooksDataService service = RetrofitInstance.getRetrofitInstance().create(GetBooksDataService.class);
        Call<ResponseBody> bookscall = service.books(page,mToken);
        bookscall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                List<Book> books = new ArrayList<Book>();
                //SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonRESULTS = new JSONObject(response.body().string());
                        if (jsonRESULTS.has("books")) {
                            JSONArray booksArray = jsonRESULTS.getJSONArray("books");
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            for (int i = 0; i < booksArray.length(); i++) {
                                ArrayList<Author> authors = new ArrayList<Author>();
                                ArrayList<Category> categories = new ArrayList<Category>();
                                JSONObject book = booksArray.getJSONObject(i).getJSONObject("details");
                                Book tmpBook = new Book();
                                tmpBook.setId(book.getInt("id"));
                                tmpBook.setGoogleid(book.getString("googleid"));
                                tmpBook.setTitle(book.getString("title"));
                                tmpBook.setPages(book.getInt("pages"));
                                tmpBook.setLanguage(book.getString("language"));
                                tmpBook.setCoverURL(book.getString("coverURL"));
                                tmpBook.setIsbn10(book.getString("isbn10"));
                                tmpBook.setIsbn13(book.getString("isbn13"));
                                tmpBook.setReleaseDate(book.getString("releaseDate"));
                                tmpBook.setRating(book.getInt("rating"));
                                tmpBook.setRatingCount(book.getInt("ratingcount"));
                                tmpBook.setPublisher(book.getString("publisher"));
                                tmpBook.setDescription(book.getString("description"));
                                try {
                                    tmpBook.setCreated_at(sdf.parse(book.getString("created_at")).getTime() );
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                JSONArray authorsArray = booksArray.getJSONObject(i).getJSONArray("authors");
                                for (int a = 0; a < authorsArray.length(); a++) {
                                    JSONObject _authorJson = authorsArray.getJSONObject(a);
                                    Author mauthor = new Author();
                                    mauthor.setName(_authorJson.getString("name"));
                                    mauthor.setPictureURL(_authorJson.getString("pictureUrl"));
                                    authors.add(mauthor);
                                }
                                JSONArray categoriesArray = booksArray.getJSONObject(i).getJSONArray("categories");
                                for (int c = 0; c < categoriesArray.length(); c++) {
                                    JSONObject _categoryJson = categoriesArray.getJSONObject(c);
                                    Category mcategory = new Category();
                                    mcategory.setName(_categoryJson.getString("name"));
                                    categories.add(mcategory);
                                }
                                tmpBook.setAuthors(authors);
                                tmpBook.setCategories(categories);
                                books.add(tmpBook);
                            }
                            List<Book> previousBooks = mObservableBooks.getValue();
                            previousBooks.addAll(books);
                            mObservableBooks.setValue(previousBooks);
                        } else {
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }else {
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });
    }
    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        @NonNull
        private final Application mApplication;
        private final ProgressListener mPl;
        private final String mQuery;
        private final String mLang;
        private final String mToken;
        private final int mPage;
        private final boolean isSearch;
        public Factory(@NonNull Application application, String query,String lang,String token,int page, boolean issearch, ProgressListener pl) {
            mApplication = application;
            mPl=pl;
            mQuery=query;
            mLang=lang;
            mToken=token;
            mPage=page;
            isSearch=issearch;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            //noinspection unchecked
            return (T) new BookSearchViewModel(mApplication,mQuery,mLang,mToken,mPage,isSearch,mPl);
        }
    }
}
