package com.jk.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jk.reggie.common.CustomException;
import com.jk.reggie.entity.Category;
import com.jk.reggie.entity.Dish;
import com.jk.reggie.entity.Setmeal;
import com.jk.reggie.mapper.CategoryMapper;
import com.jk.reggie.service.CategoryService;
import com.jk.reggie.service.DishService;
import com.jk.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;

    @Override
    public void deleteById(Long id) {
        //判断分类是否关联菜品
        LambdaQueryWrapper<Dish> dishQueryWrapper = new LambdaQueryWrapper<>();
        dishQueryWrapper.eq(Dish::getCategoryId, id);
        int dishCount = dishService.count(dishQueryWrapper);
        if (dishCount > 0){
            //抛出异常
            throw new CustomException("此分类已关联菜品，无法删除");
        }

        //判断分类是否关联套餐
        LambdaQueryWrapper<Setmeal> SetmealQueryWrapper = new LambdaQueryWrapper<>();
        SetmealQueryWrapper.eq(Setmeal::getCategoryId, id);
        int SetmealCount = setmealService.count(SetmealQueryWrapper);
        if (SetmealCount > 0){
            //抛出异常
            throw new CustomException("此分类已关联套餐，无法删除");
        }

        //删除分类
        super.removeById(id);
    }
}
