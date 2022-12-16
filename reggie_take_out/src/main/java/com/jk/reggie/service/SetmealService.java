package com.jk.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jk.reggie.dto.SetmealDto;
import com.jk.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    void saveWithDish(SetmealDto setmealDto);

    void removeWithDish(List<Long> ids);

    SetmealDto getByIdWithDish(Long id);

    void updateByIdWithDish(SetmealDto setmealDto);
}
