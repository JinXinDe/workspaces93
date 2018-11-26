package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.SepecficationOptionMapper;

import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.sellergoods.service.SepecficationOptionService;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service(interfaceClass = SepecficationOptionService.class)
public class SepecficationOptionServiceImpl extends BaseServiceImpl<TbSpecificationOption> implements SepecficationOptionService {

    @Autowired
    private SepecficationOptionMapper sepecficationOptionMapper;

    @Override
    public PageResult search(Integer page, Integer rows, TbSpecificationOption sepecficationOption) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbSpecificationOption.class);
        Example.Criteria criteria = example.createCriteria();
         /*if(!StringUtils.isEmpty(specificationOption.get***())){
            criteria.andLike("***", "%" + specificationOption.get***() + "%");
        }*/

        List<TbSpecificationOption> list = sepecficationOptionMapper.selectByExample(example);
        PageInfo<TbSpecificationOption> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }
}
