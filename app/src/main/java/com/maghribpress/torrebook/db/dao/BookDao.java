package com.maghribpress.torrebook.db.dao;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;

import com.maghribpress.torrebook.db.converter.DateConverter;
import com.maghribpress.torrebook.db.entity.Author;
import com.maghribpress.torrebook.db.entity.Book;
import com.maghribpress.torrebook.db.entity.BookAuthor;
import com.maghribpress.torrebook.db.entity.BookCategory;
import com.maghribpress.torrebook.db.entity.BookTracking;
import com.maghribpress.torrebook.db.entity.Category;
import com.maghribpress.torrebook.db.entity.Tracking;

import java.util.ArrayList;
import java.util.List;

@Dao
@TypeConverters(DateConverter.class)
public interface BookDao {

    @Query("SELECT * FROM books order by rating desc")
    LiveData<List<Book>> getBooks();

    @Query("SELECT * FROM books where id=:id")
    LiveData<Book> getBookById(long id);

    @Query("SELECT * FROM books where id=:id")
    Book _getBookById(long id);

    @Query("SELECT categories.id, categories.name, categories.created_at FROM categories inner join bookcategories on bookcategories.categoryid = categories.id where bookcategories.bookid=:bookid")
    LiveData<List<Category>> getBookCategories(long bookid);

    @Query("SELECT name, pictureURL, authors.id, authors.created_at FROM authors inner join booksauthors on authors.id = booksauthors.authorid where booksauthors.bookid=:bookid")
    LiveData<List<Author>> getBookAuthors(long bookid);

    @Query("SELECT authors.id, name, pictureURL, authors.created_at FROM authors inner join booksauthors on authors.id = booksauthors.authorid where booksauthors.bookid=:bookid")
    List<Author> getBookAuthorsList(long bookid);

    @Query("SELECT categories.id, name, categories.created_at FROM categories inner join bookcategories on bookcategories.categoryid = categories.id where bookcategories.bookid=:bookid")
    public List<Category> getBookCategoriesList(long bookid);

    @Query("SELECT currentPage FROM tracking where bookid=:bookid")
    public int getBookLastPage(long bookid);

    @Query("SELECT id, name, pictureURL, created_at FROM authors where name=:name")
    public Author getAuthorByName(String name);

    @Query("SELECT id, name, created_at FROM categories where name=:name")
    public Category getCategoryByName(String name);

    @Query("SELECT * FROM tracking where bookid=:bookid")
    public Tracking getBookTracking(long bookid);

    @Query("SELECT * FROM tracking where bookimportid=:bookimportid")
    public Tracking getBookTrackingByImportID(String bookimportid);

    @Query("SELECT books.id, books.epubPath, tracking.bookid as trck_bookid, tracking.created_at as trck_created_at, tracking.chapterHref as trck_chapterHref, tracking.folioBookid as trck_folioBookid, tracking.usingId as trck_usingId, tracking.value as trck_value FROM tracking inner join books on tracking.bookimportid=books.importedid ORDER BY tracking.created_at DESC LIMIT 1")
    public BookTracking getLastReadBookTracking();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public long insertBook(Book book);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public long insertAuthor(Author author);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public long[] insertAuthors(ArrayList<Author> authors);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public long insertCategory(Category category);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public long[] insertCategories(ArrayList<Category> categories);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public long[] insertBookCategories(List<BookCategory> bookcategories);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public long[] insertBookAuthors(List<BookAuthor> bookauthors);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public long insertTracking(Tracking tracking);
}
