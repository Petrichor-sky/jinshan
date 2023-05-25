package com.haizhi.empower.rest;

import com.haizhi.empower.base.BaseController;
import com.haizhi.empower.base.resp.BaseResponse;
import com.haizhi.empower.entity.bo.DicTypeBo;
import com.haizhi.empower.entity.po.DicItem;
import com.haizhi.empower.service.DicItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("dicItem")
@Api(tags = "字典子项")
public class DicItemController extends BaseController<DicItemService, DicItem> {

    @GetMapping("items")
    @ApiOperation(value ="根据主项编码获取子项数据",notes="根据主项编码获取子项数据")
    public BaseResponse getDicItemByMainType(@RequestParam String dicTypeName) {
        return baseService.getDicItemByMainType(dicTypeName);
    }

    /**
     * 字典子项列表
     *
     * @param parentCode
     * @param keyword
     * @return
     */
    @GetMapping("list")
    @ApiOperation(value ="字典子项列表",notes="字典子项列表")
    public BaseResponse getDicItemList(@RequestParam String parentCode,
                                       @RequestParam(required = false, name = "keyword", defaultValue = "") String keyword,
                                       @RequestParam(required = false, name = "pageNum", defaultValue = "1") Integer pageNum,
                                       @RequestParam(required = false, name = "pageSize", defaultValue = "10") Integer pageSize) {
        return baseService.getDicItemList(parentCode, keyword, pageNum, pageSize);
    }

    /**
     * 根据字典编码获取下拉字典
     * @return
     */
    @GetMapping("getDictItemByCode")
    @ApiOperation(value ="根据字典编码获取下拉字典",notes="根据字典编码获取下拉字典")
    public BaseResponse getDictItemByCode(String keyword){
        return baseService.getDictItemByCode(keyword);
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @GetMapping("delete")
    @ApiOperation(value ="删除",notes="删除")
    public BaseResponse delete(@RequestParam Integer id) {
        return baseService.deleteItem(id);
    }

    /**
     * 批量删除字典子项
     *
     * @param batchDeleteBo
     * @return
     */
    @PostMapping("batchDelete")
    @ApiOperation(value ="批量删除字典子项",notes="批量删除字典子项")
    public BaseResponse batchDelete(@RequestBody @Valid DicTypeBo.BatchDeleteBo batchDeleteBo) {
        return baseService.batchDelete(batchDeleteBo);
    }

    /**
     * 添加字典子项
     *
     * @param dicItemBo
     * @return
     */
    @PostMapping("add")
    @ApiOperation(value ="添加字典子项",notes="添加字典子项")
    public BaseResponse addDicItem(@RequestBody @Valid DicTypeBo.DicItemBo dicItemBo) {
        return baseService.addDicItem(dicItemBo);
    }


    /**
     * 字典子项更新
     *
     * @param dicItemModifiedBo
     * @return
     */
    @PostMapping("modified")
    @ApiOperation(value ="字典子项更新",notes="字典子项更新")
    public BaseResponse modified(@RequestBody DicTypeBo.DicItemModifiedBo dicItemModifiedBo) {
        return baseService.modified(dicItemModifiedBo);

    }
}