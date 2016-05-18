package com.xyj.dao;

import java.util.List;

import com.xyj.domain.Category;

public interface CategoryDao {

	void save(Category category);

	List<Category> findAll();

}