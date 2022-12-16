package com.jk.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jk.reggie.common.CustomException;
import com.jk.reggie.dto.SetmealDto;
import com.jk.reggie.entity.Setmeal;
import com.jk.reggie.entity.SetmealDish;
import com.jk.reggie.mapper.SetmealMapper;
import com.jk.reggie.service.SetmealDishService;
import com.jk.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;

    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map(item -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);
    }

    @Transactional
    @Override
    public void removeWithDish(List<Long> ids) {

        if (ids == null){
            throw new CustomException("批量操作，请先勾选操作菜品！");
        }

        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.in(Setmeal::getId, ids);
        setmealLambdaQueryWrapper.eq(Setmeal::getStatus, 1);
        int count = this.count(setmealLambdaQueryWrapper);
        if (count > 0){
            throw new CustomException("套餐正在售卖，无法删除");
        }

        this.removeByIds(ids);

        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.in(SetmealDish::getSetmealId, ids);
        setmealDishService.remove(setmealDishLambdaQueryWrapper);

    }

    @Transactional
    @Override
    public SetmealDto getByIdWithDish(Long id) {
        Setmeal setmeal = this.getById(id);

        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal, setmealDto);

        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SetmealDish::getSetmealId, id);
        List<SetmealDish> setmealDishList = setmealDishService.list(queryWrapper);

        setmealDto.setSetmealDishes(setmealDishList);

        return setmealDto;
    }

    @Transactional
    @Override
    public void updateByIdWithDish(SetmealDto setmealDto) {
        this.updateById(setmealDto);

        Long setmealId = setmealDto.getId();

        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SetmealDish::getSetmealId, setmealId);
        setmealDishService.remove(queryWrapper);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        setmealDishes = setmealDishes.stream().map(item -> {
            item.setSetmealId(setmealId);
            return item;
        }).toList();

        setmealDishService.saveBatch(setmealDishes);
    }


}
