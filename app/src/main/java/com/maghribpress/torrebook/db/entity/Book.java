package com.maghribpress.torrebook.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import java.util.ArrayList;


@Entity(tableName = "books",primaryKeys = {"id", "importedid"})
public class Book {
    private long id;
    @NonNull
    private String importedid;
    private String googleid;
    private String title;
    private int pages;
    private String language;
    private String coverURL;
    private String isbn13;
    private String isbn10;
    private String releaseDate;
    private float rating;
    private String epubPath;
    private int ratingCount;
    private String publisher;
    private String description;
    private long created_at;
    @Ignore
    private ArrayList<Author> authors;
    @Ignore
    private ArrayList<Category> categories;

    private Bitmap cover;

    public Book() {
    }

    public String getImportedid() {
        return importedid;
    }

    public void setImportedid(String importedid) {
        this.importedid = importedid;
    }

    public Bitmap getCover() {
        return cover;
    }

    public void setCover(Bitmap cover) {
        this.cover = cover;
    }
    public String getEpubPath() {
        return epubPath;
    }

    public void setEpubPath(String epubPath) {
        this.epubPath = epubPath;
    }

    public ArrayList<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(ArrayList<Author> authors) {
        this.authors = authors;
    }

    public ArrayList<Category> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<Category> categories) {
        this.categories = categories;
    }

    public String getGoogleid() {
        return googleid;
    }

    public void setGoogleid(String googleid) {
        this.googleid = googleid;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCoverURL() {
        return coverURL;
    }

    public void setCoverURL(String coverURL) {
        this.coverURL = coverURL;
    }

    public String getIsbn13() {
        return isbn13;
    }

    public void setIsbn13(String isbn13) {
        this.isbn13 = isbn13;
    }

    public String getIsbn10() {
        return isbn10;
    }

    public void setIsbn10(String isbn10) {
        this.isbn10 = isbn10;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getCreated_at() {
        return created_at;
    }

    public void setCreated_at(long created_at) {
        this.created_at = created_at;
    }

    public String getAuthorsString() {
        StringBuilder _authors = new StringBuilder();
        _authors.append("By ");
        for (int i = 0; i<getAuthors().size();i++) {
            if(i==getAuthors().size()-1) {
                _authors.append(getAuthors().get(i).getName());
            }else {
                _authors.append(getAuthors().get(i).getName());
                _authors.append(", ");
            }
        }
        return  _authors.toString();
    }
    public ArrayList<String> categoriesListtoString() {
        ArrayList<String> _categories = new ArrayList<String>();
        for (Category category: getCategories()) {
            _categories.add(category.getName());
        }
        return _categories;
    }
    public ArrayList<String> getFieldListFromAuthors() {
        ArrayList<String> authorsname = new ArrayList<String>();
        for (Author author: authors) {
            authorsname.add(author.getName());
        }
        return  authorsname;
    }
    public ArrayList<String> getFieldListFromCategories() {
        ArrayList<String> categoriesname = new ArrayList<String>();
        for (Category category: categories) {
            categoriesname.add(category.getName());
        }
        return  categoriesname;
    }
}
