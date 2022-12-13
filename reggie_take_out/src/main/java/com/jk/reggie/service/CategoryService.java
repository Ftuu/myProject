package com.jk.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jk.reggie.entity.Category;

public interface CategoryService extends IService<Category> {

    void deleteById(Long id);
}
