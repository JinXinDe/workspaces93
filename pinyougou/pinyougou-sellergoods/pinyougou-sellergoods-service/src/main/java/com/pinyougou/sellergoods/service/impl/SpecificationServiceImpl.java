package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.SepecficationOptionMapper;
import com.pinyougou.mapper.SpecificationMapper;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.sellergoods.service.SpecificationService;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import javax.persistence.Id;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = SpecificationService.class)
public class SpecificationServiceImpl extends BaseServiceImpl<TbSpecification> implements SpecificationService {

    @Autowired
    private SpecificationMapper specificationMapper;

    @Autowired
    private SepecficationOptionMapper sepecficationOptionMapper;

    @Override
    public PageResult search(Integer page, Integer rows, TbSpecification specification) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbSpecification.class);
        Example.Criteria criteria = example.createCriteria();
        if(!StringUtils.isEmpty(specification.getSpecName())){
            criteria.andLike("specName", "%" + specification.getSpecName() + "%");
        }

        List<TbSpecification> list = specificationMapper.selectByExample(example);
        PageInfo<TbSpecification> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public void add(Specification specification) {
        //保存规格；通用mapper可以在执行新增之后回填主键
        specificationMapper.insertSelective(specification.getSpecification());

        //保存规格选项列表
        //判断规格选项列表不为空
        if (specification.getSpecificationOptionList() != null && specification.getSpecificationOptionList().size() > 0) {
            //不为空就遍历循环选项列表
            for (TbSpecificationOption specificationOption : specification.getSpecificationOptionList()) {
                //设置规格id
                //从规格及其规格选项列表获取规格列表再获取规格id
                specificationOption.setSpecId(specification.getSpecification().getId());
                //保存规格选项
                sepecficationOptionMapper.insertSelective(specificationOption);
            }
        }
    }

    @Override
    public Specification findOne(Long id) {
        //创建一个规格及其规格列表对象
        Specification specification = new Specification();

        //1、规格
        //查询规格信息
        TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(id);
        //将查询出来的规格对象信息保存到规格及其规格列表中
        specification.setSpecification(tbSpecification);

        //2、选项列表：根据规格id查询其所有的规格选项列表
        //select * from tb_specification_option where spec_id = ?
        //创建一个规格列表对象
        TbSpecificationOption param = new TbSpecificationOption();
        //获取规格列表对象的id
        param.setSpecId(id);

        //根据规格列表对象id查询规格规格列表
        List<TbSpecificationOption> specificationOptionList = sepecficationOptionMapper.select(param);
        //将规格列表保存到规格及其规格列表中
        specification.setSpecificationOptionList(specificationOptionList);

        //返回规格及其规格列表
        return specification;
    }

    @Override
    public void update(Specification specification) {
        //1、更新规格
        update(specification.getSpecification());

        //2、删除规格对应的所有规格选项
        //创建选项列表对象
        TbSpecificationOption param = new TbSpecificationOption();
        //将选项id传送给选项列表
        param.setSpecId(specification.getSpecification().getId());
        //通过选项列表id删除选项列表
        sepecficationOptionMapper.delete(param);

        //3、保存规格选项列表
        //判断选项列表不为空
        if (specification.getSpecificationOptionList() != null && specification.getSpecificationOptionList().size() > 0) {
            //遍历选项列表
            for (TbSpecificationOption specificationOption : specification.getSpecificationOptionList()) {
                //设置规格id
                //通过获取选项id将获取到的id传给选项列表
                specificationOption.setSpecId(specification.getSpecification().getId());
                //保存规格选项
                sepecficationOptionMapper.insertSelective(specificationOption);
            }
        }
    }

    @Override
    public void deleteSpecificationByIds(Long[] ids) {
        //根据规格id集合删除规格及其每一个规格对应的所有的选项
        //1、根据规格id集合删除规格
        deleteByIds(ids);

        //2、删除每一个规格对应的所有的选项
        //sql：DELETE  FROM tb_specification_option WHERE spec_id  in (x, xx, xxx)
        Example example = new Example(TbSpecificationOption.class);
        example.createCriteria().andIn("specId", Arrays.asList(ids));

        sepecficationOptionMapper.deleteByExample(example);
    }

    @Override
    public List<Map> selectOptionList() {
        return specificationMapper.selectOptionList();
    }
}
