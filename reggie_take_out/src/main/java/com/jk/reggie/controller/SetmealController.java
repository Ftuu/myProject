package com.jk.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jk.reggie.common.R;
import com.jk.reggie.dto.SetmealDto;
import com.jk.reggie.entity.Category;
import com.jk.reggie.entity.Setmeal;
import com.jk.reggie.entity.SetmealDish;
import com.jk.reggie.service.CategoryService;
import com.jk.reggie.service.SetmealDishService;
import com.jk.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private CategoryService categoryService;

    @CacheEvict(value = "setmealCache", allEntries = true)
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){

        setmealService.saveWithDish(setmealDto);

        return R.success("保存成功");
    }


    @GetMapping("/page")
    public R<Page<SetmealDto>> page(int page, int pageSize, String name){

        Page<Setmeal> setmealPage = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null, Setmeal::getName, name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(setmealPage, queryWrapper);

        BeanUtils.copyProperties(setmealPage, setmealDtoPage, "records");

        List<Setmeal> records = setmealPage.getRecords();

        List<SetmealDto> dtoRecords = records.stream().map(item -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);

            Long categoryId = item.getCategoryId();
            String categoryName = categoryService.getById(categoryId).getName();
            setmealDto.setCategoryName(categoryName);
            return setmealDto;
        }).toList();

        setmealDtoPage.setRecords(dtoRecords);
        return R.success(setmealDtoPage);
    }

    @CacheEvict(value = "setmealCache", allEntries = true)
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){

        setmealService.removeWithDish(ids);
        return R.success("删除成功");
    }

    @CacheEvict(value = "setmealCache", allEntries = true)
    @PostMapping("/status/{status}")
    public R<String> setStatus(@RequestParam List<Long> ids, @PathVariable int status){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId, ids);
        List<Setmeal> list = setmealService.list(queryWrapper);

        list = list.stream().map(item -> {
            item.setStatus(status == 0 ? 0 : 1);
            return item;
        }).toList();

        setmealService.updateBatchById(list);

        return R.success("修改成功");
    }

    @GetMapping("/{id}")
    public R<SetmealDto> get(@PathVariable Long id){
        SetmealDto setmealDto = setmealService.getByIdWithDish(id);

        return R.success(setmealDto);
    }

    @CacheEvict(value = "setmealCache", allEntries = true)
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){

        setmealService.updateByIdWithDish(setmealDto);

        return R.success("修改成功");
    }

    @Cacheable(value = "setmealCache", key = "#setmeal.CategoryId + '_' + #setmeal.Status")
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Setmeal::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(Setmeal::getStatus, 1);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setmealService.list(queryWrapper);

        return R.success(list);
    }
}
