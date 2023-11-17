package com.triguard.backend.controller;

import com.triguard.backend.entity.RestBean;
import com.triguard.backend.entity.dto.BloodPressure;
import com.triguard.backend.entity.vo.request.BloodPressure.BloodPressureCreateVO;
import com.triguard.backend.entity.vo.request.BloodPressure.BloodPressureUpdateVO;
import com.triguard.backend.service.BloodPressureService;
import com.triguard.backend.utils.ConstUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用于血压相关Controller包含用户的血压信息的增删改查
 */
@Validated
@RestController
@RequestMapping("/api/blood-pressure")
@Tag(name = "血压相关", description = "包括用户血压信息的增删改查。")
public class BloodPressureController {
    @Resource
    BloodPressureService bloodPressureService;

    /**
     * 添加血压记录
     * @param vo 血压记录表单
     * @return 响应结果
     */
    @PostMapping("/create")
    @Operation(summary = "添加血压记录")
    public RestBean<BloodPressure> recordBloodPressure(@RequestBody @Valid BloodPressureCreateVO vo,
                                              HttpServletRequest request){
        Integer accountId = (Integer) request.getAttribute(ConstUtils.ATTR_USER_ID);
        BloodPressure bloodPressure = bloodPressureService.createBloodPressure(accountId, vo);
        return bloodPressure == null ? RestBean.failure(400, "添加血压记录失败") : RestBean.success(bloodPressure);
    }

    /**
     * 删除血压记录
     * @param id 血压记录id
     * @return 响应结果
     */
    @GetMapping("/delete")
    @Operation(summary = "删除血压记录")
    public RestBean<Void> deleteBloodPressure(@RequestParam Integer id){
        String message = bloodPressureService.deleteBloodPressure(id);
        return message == null ? RestBean.success() : RestBean.failure(400, message);
    }

    /**
     * 修改血压记录
     * @param vo 血压记录表单
     * @return 响应结果
     */
    @PostMapping("/update")
    @Operation(summary = "修改血压记录")
    public RestBean<Void> updateBloodPressure(@RequestBody @Valid BloodPressureUpdateVO vo){
        String message = bloodPressureService.updateBloodPressure(vo);
        return message == null ? RestBean.success() : RestBean.failure(400, message);
    }

    /**
     * 获取血压记录
     * @param date 日期
     * @return 响应结果
     */
    @GetMapping("/get")
    @Operation(summary = "获取血压记录")
    public RestBean<List<BloodPressure>> getBloodPressure(@RequestParam String date,
                                           HttpServletRequest request){
        Integer accountId = (Integer) request.getAttribute(ConstUtils.ATTR_USER_ID);
        List<BloodPressure> bloodPressureList = bloodPressureService.getBloodPressure(accountId, date);
        return RestBean.success(bloodPressureList);
    }
}
