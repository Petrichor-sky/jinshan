package com.haizhi.empower.rest;

import com.haizhi.empower.base.BaseController;
import com.haizhi.empower.base.resp.BaseResponse;
import com.haizhi.empower.base.resp.ObjectRestResponse;
import com.haizhi.empower.entity.po.Attachment;
import com.haizhi.empower.service.AttachmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("attachment")
@Api(tags = "附件")
public class AttachmentController extends BaseController<AttachmentService,Attachment> {

    /**
     * 附件上传接口
     *
     * @param file
     * @param type
     * @return
     */
    @PostMapping("upload")
    @ApiOperation(value = "附件上传接口",notes = "附件上传接口")
    public BaseResponse attachUpload(@RequestParam("file") MultipartFile file,
                                     @RequestParam(required = false, defaultValue = "0") Integer type) {
        Map<String, Object> result = baseService.attachUpload(file, type, false);

        return new ObjectRestResponse<>().data(result);
    }

    /**
     * 表单数据上传接口
     *
     * @param file
     *
     * @param type
     * @return
     */
    @PostMapping("formDataUpload")
    @ApiOperation(value = "表单数据上传接口",notes = "表单数据上传接口")
    public BaseResponse formDataUpload(@RequestParam("file") MultipartFile file,
                                     @RequestParam(required = false, defaultValue = "0") Integer type) {
        Map<String, Object> result = baseService.formDataUpload(file, type, false);

        return new ObjectRestResponse<>().data(result);
    }

    /**
     * 删除附件接口
     *
     * @param
     * @param
     * @return
     */
    @GetMapping("delete")
    @ApiOperation(value = "删除附件接口",notes = "删除附件接口")
    public BaseResponse deleteAttachment(@RequestParam Long id) {
        return baseService.deleteAttachment(id);
    }

    @GetMapping("getByteByFileName")
    @ApiOperation(value = "根据文件名获得数据的输入流")
    public void getByteByFileName(@RequestParam("fileSuffixPath") String fileSuffixPath, HttpServletResponse response) throws IOException {
        response.getOutputStream().write(baseService.getByteByFileName(fileSuffixPath));
    }
}