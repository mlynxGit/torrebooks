package com.maghribpress.torrebook;

import com.maghribpress.torrebook.db.entity.Book;

import java.util.List;

public interface OnImportBooks {
     void importFinished(List<Book> books);
}
