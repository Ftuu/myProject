package com.jk.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jk.reggie.dto.DishDto;
import com.jk.reggie.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {

    void saveWithFlavor(DishDto dishDto);

    DishDto getByIdWithFlavor(Long id);

    void updateWithFlavor(DishDto dishDto);

    void removeWithFlavor(List<Long> ids);
}
