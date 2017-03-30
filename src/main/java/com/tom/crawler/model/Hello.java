package com.tom.crawler.model;

/**
 * Created by Mr Tom on 2017/3/25.
 */
public class Hello {
    private Integer id;
    private String test;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    @Override
    public String toString() {
        return "Hello{" +
                "id=" + id +
                ", test='" + test + '\'' +
                '}';
    }
}
