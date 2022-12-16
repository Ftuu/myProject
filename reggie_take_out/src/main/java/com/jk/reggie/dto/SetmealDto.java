package com.jk.reggie.dto;


import com.jk.reggie.entity.Setmeal;
import com.jk.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
