package com.pinyougou.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.sellergoods.service.SepecficationOptionService;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/sepecficationOption")
@RestController
public class SepecficationOptionController {

    @Reference
    private SepecficationOptionService sepecficationOptionService;

    @RequestMapping("/findAll")
    public List<TbSpecificationOption> findAll() {
        return sepecficationOptionService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult findPage(@RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "10")Integer rows) {
        return sepecficationOptionService.findPage(page, rows);
    }

    @PostMapping("/add")
    public Result add(@RequestBody TbSpecificationOption sepecficationOption) {
        try {
            sepecficationOptionService.add(sepecficationOption);
            return Result.ok("增加成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("增加失败");
    }

    @GetMapping("/findOne")
    public TbSpecificationOption findOne(Long id) {
        return sepecficationOptionService.findOne(id);
    }

    @PostMapping("/update")
    public Result update(@RequestBody TbSpecificationOption sepecficationOption) {
        try {
            sepecficationOptionService.update(sepecficationOption);
            return Result.ok("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("修改失败");
    }

    @GetMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            sepecficationOptionService.deleteByIds(ids);
            return Result.ok("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("删除失败");
    }

    /**
     * 分页查询列表
     * @param sepecficationOption 查询条件
     * @param page 页号
     * @param rows 每页大小
     * @return
     */
    @PostMapping("/search")
    public PageResult search(@RequestBody  TbSpecificationOption sepecficationOption, @RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "10")Integer rows) {
        return sepecficationOptionService.search(page, rows, sepecficationOption);
    }

}
