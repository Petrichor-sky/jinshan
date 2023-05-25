package com.haizhi.empower.rest;

import com.haizhi.empower.base.BaseController;
import com.haizhi.empower.base.resp.BaseResponse;
import com.haizhi.empower.entity.bo.DicTypeBo;
import com.haizhi.empower.entity.po.DicType;
import com.haizhi.empower.service.DicTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("dicType")
@Api(tags = "字典主项")
public class DicTypeController extends BaseController<DicTypeService, DicType> {

    /**
     * 主字典列表
     *
     * @param keyword
     * @return
     */
    @GetMapping("list")
    @ApiOperation(value ="主字典列表",notes = "主字典列表")
    public BaseResponse getDicTypes(@RequestParam(required = false, name = "keyword", defaultValue = "") String keyword) {
        return baseService.getDicTypes(keyword);
    }

    /**
     * 主字典新增
     *
     * @param dicTypeBo
     * @return
     */
    @PostMapping("add")
    @ApiOperation(value ="主字典新增",notes = "主字典新增")
    public BaseResponse addDicType(@RequestBody @Valid DicTypeBo dicTypeBo) {
        return baseService.addDicType(dicTypeBo);
    }

    /**
     * 主字典删除
     *
     * @param id
     * @return
     */
    @GetMapping("delete")
    @ApiOperation(value ="主字典删除",notes = "主字典删除")
    public BaseResponse deleteDicType(@RequestParam Integer id) {
        return baseService.deleteDicType(id);
    }

    @GetMapping("detail")
    @ApiOperation(value ="主字典详情",notes = "主字典详情")
    public BaseResponse detail(@RequestParam Long id) {
        return baseService.detail(id);
    }

    /**
     * 字典主项更新
     *
     * @param dicItemModifiedBo
     * @return
     */
    @PostMapping("modified")
    @ApiOperation(value ="字典主项更新",notes = "字典主项更新")
    public BaseResponse modified(@RequestBody DicTypeBo.DicItemModifiedBo dicItemModifiedBo) {
        return baseService.modified(dicItemModifiedBo);

    }
}