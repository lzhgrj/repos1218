package com.sibd.service;

import com.sibd.pojo.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ArticleService {
    void save(Article article);
    void delete(Article article);
    Iterable<Article> findAll();
    Page<Article> findAll(Pageable pageable);
    List<Article> findByTitle(String condition);
}
