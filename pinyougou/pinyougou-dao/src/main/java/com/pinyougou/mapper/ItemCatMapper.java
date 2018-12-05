package com.pinyougou.mapper;

import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemCat;

import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface ItemCatMapper extends Mapper<TbItemCat> {

    List<Map> findTypeTemplateList();
}
