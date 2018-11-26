package com.pinyougou.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Result;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RequestMapping("/brand")
@RestController
public class BrandController {

    @Reference
    private BrandService brandService;

    @PostMapping("/search")
    public PageResult search(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer rows, @RequestBody TbBrand brand) {
        return brandService.search(brand, page,rows);
    }

    @GetMapping("/delete")
    public Result delete(Long[] ids){
        try {
            brandService.deleteByIds(ids);
            return Result.ok("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("删除失败");
    }

    @PostMapping("/update")
    public Result update(@RequestBody TbBrand brand) {
        try {
            brandService.update(brand);
            return Result.ok("更新品牌成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("更新品牌失败");
    }

    /**
     * 根据品牌主键查询品牌
     * @param id 品牌主键
     * @return 品牌
     */
    @GetMapping("/findOne")
    public TbBrand findOne(Long id) {
        return brandService.findOne(id);
    }

    /**
     * 保存品牌数据到数据库中
     * @param brand 品牌
     * @return 操作结果
     */
    @PostMapping("/add")
    public Result add(@RequestBody TbBrand brand) {
        try {
            brandService.add(brand);
            return Result.ok("新增成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  Result.fail("新增失败");
    }

    /**
     * 根据页号和页大小查询品牌列表
     * @param page 页号
     * @param rows 页大小
     * @return 分页结果
     */
    @GetMapping("/findPage")
    public PageResult findPage(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer rows) {
        return brandService.findPage(page, rows);
    }

    /**
     * 根据页号和页大小查询品牌列表
     * @param page 页号
     * @param rows 页大小
     * @return 品牌列表
     */
    @GetMapping("/testPage")
    public List<TbBrand> testPage(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer rows) {
        //return brandService.testPage(page,rows);
        return (List<TbBrand>) brandService.findPage(page, rows);
    }

    /**
     * 查询所有品牌数据
     * @return 品牌列表json格式字符串
     */
    @GetMapping("/findAll")
    public List<TbBrand> findAll() {
        //return brandService.queryAll();
        return brandService.findAll();
    }
}
