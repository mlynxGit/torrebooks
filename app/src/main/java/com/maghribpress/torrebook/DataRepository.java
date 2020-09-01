package com.maghribpress.torrebook;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.os.AsyncTask;

import com.maghribpress.torrebook.classes.ApiTokenObject;
import com.maghribpress.torrebook.classes.InsertBookOffline;
import com.maghribpress.torrebook.classes.TorrentFile;
import com.maghribpress.torrebook.db.AppDatabase;
import com.maghribpress.torrebook.db.entity.Author;
import com.maghribpress.torrebook.db.entity.Book;
import com.maghribpress.torrebook.db.entity.BookAuthor;
import com.maghribpress.torrebook.db.entity.BookCategory;
import com.maghribpress.torrebook.db.entity.BookTracking;
import com.maghribpress.torrebook.db.entity.Category;
import com.maghribpress.torrebook.db.entity.Tracking;
import com.maghribpress.torrebook.network.GetBooksDataService;
import com.maghribpress.torrebook.network.RetrofitInstance;


import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.arch.core.util.Function;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DataRepository {
    private static DataRepository sInstance;

    private final AppDatabase mDatabase;
    private MediatorLiveData<List<Book>> mObservableProducts;

    private DataRepository(final AppDatabase database) {
        mDatabase = database;
        mObservableProducts = new MediatorLiveData<>();

        /*mObservableProducts.addSource(mDatabase.BookDao().getBooks(),
                productEntities -> {
                    if (mDatabase.getDatabaseCreated().getValue() != null) {
                        mObservableProducts.postValue(productEntities);
                    }
                });*/
    }

    public static DataRepository getInstance(final AppDatabase database) {
        if (sInstance == null) {
            synchronized (DataRepository.class) {
                if (sInstance == null) {
                    sInstance = new DataRepository(database);
                }
            }
        }
        return sInstance;
    }
    /*public LiveData<Book> getFullBook(int bookid) {
        LiveData<Book> bookLiveData = mDatabase.BookDao().getBookById(bookid);
        bookLiveData = Transformations.switchMap(bookLiveData, new Function<Book, LiveData<Book>>() {

            @Override
            public LiveData<Book> apply(final Book inputBook) {
                LiveData<List<Author>> authorsLiveData = mDatabase.BookDao().getBookAuthors(inputBook.getId());
               // LiveData<List<Category>> categoriesLiveDate = mDatabase.BookDao().getBookCategories(inputBook.getId());
                LiveData<Book> outputLiveData = Transformations.map(authorsLiveData, new Function<List<Author>, Book>() {

                    @Override
                    public Book apply(List<Author> inputAuthors) {
                        inputBook.setAuthors(inputAuthors);
                        return inputBook;
                    }
                });
                return outputLiveData;
            }
        });
        return bookLiveData;
    }*/
    public LiveData<Book> getBookOffline(long bookid) {
        LiveData<Book> bookLiveData = mDatabase.BookDao().getBookById(bookid);
        bookLiveData = Transformations.map(bookLiveData, new Function<Book, Book>() {

            @Override
            public Book apply(final Book inputBook) {
                inputBook.setAuthors(new ArrayList<Author>(mDatabase.BookDao().getBookAuthorsList(inputBook.getId())));
                inputBook.setCategories(new ArrayList<Category>(mDatabase.BookDao().getBookCategoriesList(inputBook.getId())));
                return inputBook;
            }
        });
        return bookLiveData;
    }

    public Tracking getTrackingForBook(long bookid) {
        return mDatabase.BookDao().getBookTracking(bookid);
    }
    public Tracking getTrackingForImportedBook(String bookimportid) {
        return mDatabase.BookDao().getBookTrackingByImportID(bookimportid);
    }
    public BookTracking getLastReadBookTrackings() {
        return mDatabase.BookDao().getLastReadBookTracking();
    }
    public long insertLocal(Book book) {
        long bookid = mDatabase.BookDao().insertBook(book);
        Log.d("TORRENTTAGS","book inserted");
        if(book.getAuthors() != null) {
            if(book.getAuthors().size() > 0) {
                long authorsIds[] = mDatabase.BookDao().insertAuthors(book.getAuthors());
                List<BookAuthor> _bookAuthors = new ArrayList<BookAuthor>();
                for(int i=0;i<authorsIds.length;i++) {
                    BookAuthor _bookAuthor = new BookAuthor();
                    _bookAuthor.setBookid(book.getId());
                    _bookAuthor.setAuthorid(authorsIds[i]);
                    _bookAuthors.add(_bookAuthor);
                }
                mDatabase.BookDao().insertBookAuthors(_bookAuthors);
            }
        }

        Log.d("TORRENTTAGS","authors inserted");
        if(book.getCategories()!=null) {
            if(book.getCategories().size()>0){
                long categoreisIds[] = mDatabase.BookDao().insertCategories(book.getCategories());
                List<BookCategory> _bookCategories = new ArrayList<BookCategory>();
                for(int i=0;i<categoreisIds.length;i++) {
                    BookCategory _bookCategory = new BookCategory();
                    _bookCategory.setBookid(book.getId());
                    _bookCategory.setCategoryid(categoreisIds[i]);
                    _bookCategories.add(_bookCategory);
                }
                mDatabase.BookDao().insertBookCategories(_bookCategories);
            }
        }

        Log.d("TORRENTTAGS","categories inserted");

        Log.d("TORRENTTAGS","_bookAuthors inserted");

        Log.d("TORRENTTAGS","_bookCategories inserted");
        return bookid;
    }
    public long insertTrackingInfo(Tracking tracking) {
       return mDatabase.BookDao().insertTracking(tracking);
    }
    public void insertBookOnlineOffline(Book book, OnBookInsertCompleted onBookInsertCompleted) {
        GetBooksDataService service = RetrofitInstance.getRetrofitInstance().create(GetBooksDataService.class);
        Call<ResponseBody> bookscall = service.insertbook(book.getGoogleid(),book.getTitle(),book.getPages(),book.getLanguage(),book.getCoverURL(),book.getIsbn10(),book.getIsbn13(),book.getReleaseDate(),book.getRating(),book.getRatingCount(),book.getPublisher(),book.getDescription(),book.getFieldListFromAuthors(),new ArrayList<>(),book.getFieldListFromCategories(),ApiTokenObject.getInstance().getToken());
        bookscall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                //SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                if (response.isSuccessful()) {
                    Log.d("TORRENTTAGS","insert is successfull");
                    try {
                        JSONObject jsonRESULTS = new JSONObject(response.body().string());
                        if (jsonRESULTS.has("book")) {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            JSONObject bookJsonObject = jsonRESULTS.getJSONObject("book");
                                ArrayList<Author> authors = new ArrayList<Author>();
                                ArrayList<Category> categories = new ArrayList<Category>();
                                JSONObject jsonbook = bookJsonObject.getJSONObject("details");
                                Book tmpBook = new Book();
                                tmpBook.setImportedid(UUID.randomUUID().toString());
                            if(jsonbook.has("id"))
                                    tmpBook.setId(jsonbook.getInt("id"));
                            if(jsonbook.has("googleid"))
                                if(!jsonbook.isNull("googleid"))
                                    tmpBook.setGoogleid(jsonbook.getString("googleid"));
                            if(jsonbook.has("title"))
                                if(!jsonbook.isNull("title"))
                                    tmpBook.setTitle(jsonbook.getString("title"));
                            if(jsonbook.has("pages"))
                                if(!jsonbook.isNull("pages"))
                                    tmpBook.setPages(jsonbook.getInt("pages"));
                            if(jsonbook.has("language"))
                                if(!jsonbook.isNull("language"))
                                    tmpBook.setLanguage(jsonbook.getString("language"));
                            if(jsonbook.has("coverURL"))
                                if(!jsonbook.isNull("coverURL"))
                                    tmpBook.setCoverURL(jsonbook.getString("coverURL"));
                            if(jsonbook.has("isbn10"))
                                if(!jsonbook.isNull("isbn10"))
                                    tmpBook.setIsbn10(jsonbook.getString("isbn10"));
                            if(jsonbook.has("isbn13"))
                                if(!jsonbook.isNull("isbn13"))
                                    tmpBook.setIsbn13(jsonbook.getString("isbn13"));
                            if(jsonbook.has("releaseDate"))
                                if(!jsonbook.isNull("releaseDate"))
                                    tmpBook.setReleaseDate(jsonbook.getString("releaseDate"));
                            if(jsonbook.has("rating")) {
                                if(!jsonbook.isNull("rating"))
                                    tmpBook.setRating((float)jsonbook.getDouble("rating"));
                            }
                            if(jsonbook.has("ratingcount")) {
                                if(!jsonbook.isNull("ratingcount"))
                                    tmpBook.setRatingCount(jsonbook.getInt("ratingcount"));
                            }
                            if(jsonbook.has("publisher"))
                                if(!jsonbook.isNull("publisher"))
                                    tmpBook.setPublisher(jsonbook.getString("publisher"));
                            if(jsonbook.has("description"))
                                if(!jsonbook.isNull("description"))
                                    tmpBook.setDescription(jsonbook.getString("description"));
                                try {
                                    tmpBook.setCreated_at(sdf.parse(jsonbook.getString("created_at")).getTime() );
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            if(bookJsonObject.has("authors")) {
                                if(!bookJsonObject.isNull("authors")) {
                                    JSONArray authorsArray = bookJsonObject.getJSONArray("authors");
                                    for (int a = 0; a < authorsArray.length(); a++) {
                                        JSONObject _authorJson = authorsArray.getJSONObject(a);
                                        Author mauthor = new Author();
                                        mauthor.setName(_authorJson.getString("name"));
                                        mauthor.setPictureURL(_authorJson.getString("pictureUrl"));
                                        mauthor.setCreated_at(System.currentTimeMillis());
                                        authors.add(mauthor);
                                    }
                                }

                            }
                            if(bookJsonObject.has("categories")) {
                                if(!bookJsonObject.isNull("categories")) {
                                    JSONArray categoriesArray = bookJsonObject.getJSONArray("categories");
                                    for (int c = 0; c < categoriesArray.length(); c++) {
                                        JSONObject _categoryJson = categoriesArray.getJSONObject(c);
                                        Category mcategory = new Category();
                                        mcategory.setName(_categoryJson.getString("name"));
                                        mcategory.setCreated_at(System.currentTimeMillis());
                                        categories.add(mcategory);
                                    }
                                }

                            }

                                tmpBook.setAuthors(authors);
                                tmpBook.setCategories(categories);
                                tmpBook.setEpubPath(book.getEpubPath());
                                onBookInsertCompleted.onInsertComplete(tmpBook);
                            Log.d("TORRENTTAGS","launch asynctask to insert book in room");
                                new InsertBookOffline(new WeakReference<Book>(tmpBook),new WeakReference<>(mDatabase)).execute();
                                //insertLocal(tmpBook);

                        } else {
                            Log.d("TORRENTTAGS","in successfull book not found");
                            String error_message = "نعتدر حدث خطأ ما !";
                            //Toast.makeText(getContext(), error_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                // generateNewsList(response.body().getNewslist());

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("TORRENTTAGS","request failed!!");
            }
        });
    }
    public static boolean isNumeric(String strNum) {
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException | NullPointerException nfe) {
            return false;
        }
        return true;
    }
    public LiveData<List<Book>> getBooksOffline(ProgressListener pl) {
        LiveData<List<Book>> booksLiveData = mDatabase.BookDao().getBooks();
        booksLiveData = Transformations.map(booksLiveData, new Function<List<Book>, List<Book>>() {

            @Override
            public List<Book> apply(final List<Book> inputBooks) {
                for (Book book : inputBooks) {
                    new getRoomAuthorsTask(mDatabase,book).execute();
                    new getRoomCategoriesTask(mDatabase,book).execute();
                }
                return inputBooks;
            }
        });
        pl.stopProgress();
        return booksLiveData;
    }
    public MutableLiveData<List<Book>> getBooksOnline(String token,int page, ProgressListener pl) {
        final MutableLiveData<List<Book>> data = new MutableLiveData<>();
        GetBooksDataService service = RetrofitInstance.getRetrofitInstance().create(GetBooksDataService.class);
        Call<ResponseBody> bookscall = service.books(page,token);
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
                            data.setValue(books);
                            pl.stopProgress();
                        } else {
                            pl.stopProgress();
                            String error_message = "نعتدر حدث خطأ ما !";
                            //Toast.makeText(getContext(), error_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        pl.stopProgress();
                        e.printStackTrace();

                    } catch (IOException e) {
                        pl.stopProgress();
                        e.printStackTrace();
                    }

                }else {
                    pl.stopProgress();
                }
                // generateNewsList(response.body().getNewslist());

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pl.stopProgress();
            }
        });
        return data;
    }
    public MutableLiveData<List<TorrentFile>> getTorrents(String query, int page, ProgressListener pl) {
        Log.d("TORRENTTAG","getTorrents Called");
        final MutableLiveData<List<TorrentFile>> data = new MutableLiveData<>();
        GetBooksDataService service = RetrofitInstance.getRetrofitInstance().create(GetBooksDataService.class);
        Call<ResponseBody> torrentscall = service.getbook(query,ApiTokenObject.getInstance().getToken(),page);
        torrentscall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("TORRENTTAG","Before Response Succesfull");
                List<TorrentFile> torrents = new ArrayList<TorrentFile>();
                if (response.isSuccessful()) {
                    Log.d("TORRENTTAG","Response Succesfull");
                    try {
                        JSONObject jsonRESULTS = new JSONObject(response.body().string());
                        if (jsonRESULTS.has("data")) {
                            JSONArray torrentsArray = jsonRESULTS.getJSONArray("data");
                            for (int i = 0; i < torrentsArray.length(); i++) {
                                JSONObject torrent = torrentsArray.getJSONObject(i);
                                TorrentFile tmpTorrent = new TorrentFile();
                                tmpTorrent.setTitle(torrent.getString("title"));
                                tmpTorrent.setMagnet(torrent.getString("magnet"));
                                tmpTorrent.setTorrentsize(torrent.getString("filesize"));
                                tmpTorrent.setAdded(torrent.getString("added"));
                                tmpTorrent.setSeeders(Integer.valueOf(torrent.getString("seeders")));
                                tmpTorrent.setPopularity(torrent.getInt("popularity"));
                                tmpTorrent.setSource(torrent.getString("source"));
                                tmpTorrent.setType(torrent.getString("type"));
                                Log.d("TORRENTTAG", torrent.getString("title"));
                                torrents.add(tmpTorrent);
                            }
                            data.setValue(torrents);
                            pl.stopProgress();
                        } else {
                            String error_message = "نعتدر حدث خطأ ما !";
                            //Toast.makeText(getContext(), error_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                // generateNewsList(response.body().getNewslist());

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pl.stopProgress();
            }
        });
        return data;
    }
    public MutableLiveData<List<Book>> searchBooksOnline(String query, String lang,String token,ProgressListener pl) {
        final MutableLiveData<List<Book>> data = new MutableLiveData<>();
        GetBooksDataService service = RetrofitInstance.getRetrofitInstance().create(GetBooksDataService.class);
        Call<ResponseBody> bookscall = service.findbook(query,token,lang);
        bookscall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                List<Book> books = new ArrayList<Book>();
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
                                Log.d("JSONLOG", tmpBook.getTitle());
                                Log.d("JSONLOG", String.valueOf(books.size()) );
                                books.add(tmpBook);
                            }
                            data.setValue(books);
                            pl.stopProgress();
                        } else {
                            pl.stopProgress();
                            String error_message = "نعتدر حدث خطأ ما !";
                            //Toast.makeText(getContext(), error_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        pl.stopProgress();
                        e.printStackTrace();

                    } catch (IOException e) {
                        pl.stopProgress();
                        e.printStackTrace();
                    }

                }else {
                    pl.stopProgress();
                }
                // generateNewsList(response.body().getNewslist());

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pl.stopProgress();
            }
        });
        return data;
    }
    public List<Book> _searchBooksOnline(String query, String lang, String token, ProgressListener pl) {
        List<Book> books = new ArrayList<Book>();
        GetBooksDataService service = RetrofitInstance.getRetrofitInstance().create(GetBooksDataService.class);
        Call<ResponseBody> bookscall = service.findbook(query,token,lang);
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
                                Log.d("JSONLOG", tmpBook.getTitle());
                                Log.d("JSONLOG", String.valueOf(books.size()) );
                                books.add(tmpBook);
                            }
                            pl.stopProgress();
                        } else {
                            String error_message = "نعتدر حدث خطأ ما !";
                            //Toast.makeText(getContext(), error_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                // generateNewsList(response.body().getNewslist());

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                String error = "";
            }
        });
        return books;
    }

    private static class getRoomAuthorsTask extends AsyncTask<Void, Void, ArrayList<Author>> {
        Book mbook;
        AppDatabase mAppDB;
        public getRoomAuthorsTask(AppDatabase appdb, Book book) {
        this.mbook =book;
        this.mAppDB = appdb;
        }
        @Override
        protected ArrayList<Author> doInBackground(Void... voids) {
            return new ArrayList<Author>(mAppDB.BookDao().getBookAuthorsList(mbook.getId())) ;
        }

        @Override
        protected void onPostExecute(ArrayList<Author> authors) {
            mbook.setAuthors(authors);
        }
    }
    private static class getRoomCategoriesTask extends AsyncTask<Void, Void, ArrayList<Category>> {
        Book mbook;
        AppDatabase mAppDB;
        public getRoomCategoriesTask(AppDatabase appdb, Book book) {
            this.mbook =book;
            this.mAppDB = appdb;
        }
        @Override
        protected ArrayList<Category> doInBackground(Void... voids) {
            return new ArrayList<Category>(mAppDB.BookDao().getBookCategoriesList(mbook.getId()));
        }

        @Override
        protected void onPostExecute(ArrayList<Category> categories) {
            mbook.setCategories(categories);
        }
    }

}
