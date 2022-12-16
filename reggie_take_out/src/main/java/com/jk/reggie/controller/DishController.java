package com.jk.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jk.reggie.common.CustomException;
import com.jk.reggie.common.R;
import com.jk.reggie.dto.DishDto;
import com.jk.reggie.entity.Category;
import com.jk.reggie.entity.Dish;
import com.jk.reggie.entity.Setmeal;
import com.jk.reggie.service.CategoryService;
import com.jk.reggie.service.DishFlavorService;
import com.jk.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;


    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {

        dishService.saveWithFlavor(dishDto);

        return R.success("保存成功");
    }

    @GetMapping("/page")
    public R<Page<DishDto>> page(int page, int pageSize, String name) {
        Page<Dish> pageInfo = new Page<>(page, pageSize);

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.like(name != null, Dish::getName, name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        dishService.page(pageInfo, queryWrapper);

        Page<DishDto> dishDtoPage = new Page<>();
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");

        List<Dish> records = pageInfo.getRecords();

        List<DishDto> dishDtoList = records.stream().map(item -> {

            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);

            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);

            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(dishDtoList);
        return R.success(dishDtoPage);
    }

    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id) {

        DishDto dishDto = dishService.getByIdWithFlavor(id);

        return R.success(dishDto);
    }

    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        dishService.updateWithFlavor(dishDto);

        return R.success("修改成功");
    }

    @GetMapping("/list")
    public R<List<Dish>> list(Dish dish) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus, 1);
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> dishList = dishService.list(queryWrapper);

        return R.success(dishList);

    }

    @PostMapping("/status/{status}")
    public R<String> setStatus(@RequestParam List<Long> ids, @PathVariable int status) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId, ids);
        List<Dish> list = dishService.list(queryWrapper);

        list = list.stream().map(item -> {
            item.setStatus(status == 0 ? 0 : 1);
            return item;
        }).toList();

        dishService.updateBatchById(list);

        return R.success("修改成功");
    }

    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {

        dishService.removeWithFlavor(ids);



        return R.success("删除成功");

    }
}
