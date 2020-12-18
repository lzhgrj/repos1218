package com.sibd.pojo;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "blog1",type = "article")
public class Article {
    @Id
    @Field(store=true, index = false,type = FieldType.Integer)
    private Integer id;
    @Field(index=true ,analyzer="ik_max_word",store=true,searchAnalyzer="ik_max_word",type = FieldType.text)
    private String title;
    @Field(index=true ,analyzer="ik_max_word",store=true,searchAnalyzer="ik_max_word",type = FieldType.text)
    private String content;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Article{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
