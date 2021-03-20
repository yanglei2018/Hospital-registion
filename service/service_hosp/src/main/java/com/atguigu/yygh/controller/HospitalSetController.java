package com.atguigu.yygh.controller;

import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.common.utils.MD5;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.service.HospitalSetService;
import com.atguigu.yygh.vo.hosp.HospitalSetQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.jdbc.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

@RestController
@Api(tags = "医院管理相关接口")
@RequestMapping("/admin/hosp/hospitalSet")
public class HospitalSetController {
    @Autowired
    private HospitalSetService hospitalSetService;


    @ApiOperation("查询所有医院信息")
    @GetMapping("/findAll")
    public Result listAllHospitalSet(){
        return Result.ok(hospitalSetService.list());
    }

    @ApiOperation("逻辑删除医院信息")
    @DeleteMapping("{id}")
    public Result deleteHospitalById(@PathVariable Long id){
        boolean b = hospitalSetService.removeById(id);
        if (b){
            return Result.ok();
        }else {
            return Result.fail();
        }
    }

    @ApiOperation("分页条件查询")
    @PostMapping("/findPageHospSet/{current}/{limit}")
    public Result findPageHospSet(@PathVariable long current,
                                  @PathVariable long limit,
                                  //@RequestBody(required = false)方便前端使用Json传递数据
                                  @RequestBody(required = false) HospitalSetQueryVo hospitalSetQueryVo){
        //创建page对象
        Page<HospitalSet> page = new Page<>(current,limit);
        //构建查询条件
        QueryWrapper<HospitalSet> queryWrapper = new QueryWrapper<>();
        String hosname = hospitalSetQueryVo.getHosname();
        String hoscode = hospitalSetQueryVo.getHoscode();
        //防止为空
        if (!StringUtils.isNullOrEmpty(hosname)){
            queryWrapper.like("hosname", hospitalSetQueryVo.getHosname());
        }
        if (!StringUtils.isNullOrEmpty(hoscode)){
            queryWrapper.eq("hoscode", hospitalSetQueryVo.getHoscode());
        }
        Page<HospitalSet> hospitalSetPage = hospitalSetService.page(page, queryWrapper);
        return Result.ok(hospitalSetPage);
    }

    @ApiOperation("医院设置添加")
    @PostMapping("/saveHospitalSet")
    public Result saveHospitalSet(@RequestBody HospitalSet hospitalSet){
        //设置状态码 1可用 0不可用
        hospitalSet.setStatus(1);

        //设置签名
        Random random = new Random();
        String encrypt = MD5.encrypt(System.currentTimeMillis() + "" + random.nextInt(1000));
        hospitalSet.setSignKey(encrypt);

        boolean save = hospitalSetService.save(hospitalSet);

        if (save){
            return Result.ok();
        }else {
            return Result.fail();
        }
    }

    @ApiOperation("查找医院设置")
    @GetMapping("/getHospitalSet/{id}")
    public Result getHospitalSetById(@PathVariable("id") long id){
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        return Result.ok(hospitalSet);
    }
    @ApiOperation("修改医院设置")
    @PostMapping("/updateHospitalSet")
    public Result updateHospitalSet(@RequestBody HospitalSet hospitalSet){
        boolean b = hospitalSetService.updateById(hospitalSet);
        if (b){
            return Result.ok();
        }else {
            return Result.fail();
        }
    }

    @ApiOperation("批量删除医院设置")
    @PostMapping("/batchRemoveHospitalSet")
    public Result batchRemoveHospitalSet(@RequestBody List<Long> idList){
        hospitalSetService.removeByIds(idList);
        return Result.ok();
    }
    @ApiOperation("锁定解锁医院状态")
    @PutMapping("HospitalSet/{id}/{status}")
    public Result lockHospitalSet(@PathVariable("id") Integer id,
                                  @PathVariable("status") Integer status){
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        hospitalSet.setStatus(status);
        boolean b = hospitalSetService.updateById(hospitalSet);
        if (b){
            return Result.ok();
        }else {
            return Result.fail();
        }
    }
    @ApiOperation("发送医院密钥")
    @PutMapping("HospitalSet/{id}")
    public Result sendHospitalSet(@PathVariable("id") Integer id){
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        String hosname = hospitalSet.getHosname();
        String hoscode = hospitalSet.getHoscode();
        String signKey = hospitalSet.getSignKey();
        //TODO
        return Result.ok();
    }

}
