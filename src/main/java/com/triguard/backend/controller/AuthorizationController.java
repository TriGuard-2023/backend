package com.triguard.backend.controller;

import com.triguard.backend.entity.RestBean;
import com.triguard.backend.entity.vo.request.Authorization.EmailConfirmResetVO;
import com.triguard.backend.entity.vo.request.Authorization.EmailRegisterVO;
import com.triguard.backend.entity.vo.request.Authorization.EmailResetVO;
import com.triguard.backend.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.function.Supplier;

/**
 * 用于验证相关Controller包含用户的注册、重置密码等操作
 */
@Validated
@RestController
@RequestMapping("/api/auth")
@Tag(name = "登录校验相关", description = "包括用户登录、注册、验证码请求等操作。")
public class AuthorizationController {

    @Resource
    AccountService accountService;

    /**
     * 请求邮件验证码
     * @param email 请求邮件
     * @param type 类型
     * @param request 请求
     * @return 是否请求成功
     */
    @GetMapping("/email-code")
    @Operation(summary = "请求邮件验证码")
    public RestBean<Void> askVerifyCode(@RequestParam @NotNull @Email String email,
                                        @RequestParam @NotNull @Pattern(regexp = "(register|reset)")  String type,
                                        HttpServletRequest request) {
        if (email == null || email.isEmpty())
            return RestBean.failure(400, "邮箱不能为空");
        return this.messageHandle(() ->
                accountService.sendEmailVerificationCode(type, email, request.getRemoteAddr()));
    }

    /**
     * 请求短信验证码
     * @param phone 请求手机号
     * @param type 类型
     * @param request 请求
     * @return 是否请求成功
     */
    @GetMapping("/phone-code")
    @Operation(summary = "请求短信验证码")
    public RestBean<Void> askPhoneCode(@RequestParam @NotNull @Pattern(regexp = "^1[3456789]\\d{9}$") String phone,
                                       @RequestParam @NotNull @Pattern(regexp = "(register|reset)") String type,
                                       HttpServletRequest request){
        return this.messageHandle(() ->
                accountService.sendPhoneVerificationCode(type, String.valueOf(phone), request.getRemoteAddr()));
    }

    /**
     * 进行用户注册操作，需要先请求邮件验证码
     * @param vo 注册信息
     * @return 是否注册成功
     */
    @PostMapping("/email-register")
    @Operation(summary = "用户邮箱注册操作")
    public RestBean<Void> register(@RequestBody @Valid EmailRegisterVO vo){
        return this.messageHandle(() ->
                accountService.registerEmailAccount(vo));
    }

    /**
     * 执行密码重置确认，检查验证码是否正确
     * @param vo 密码重置信息
     * @return 是否操作成功
     */
    @PostMapping("/reset-confirm")
    @Operation(summary = "密码重置确认")
    public RestBean<Void> resetConfirm(@RequestBody @Valid EmailConfirmResetVO vo){
        return this.messageHandle(() -> accountService.emailConfirmReset(vo));
    }

    /**
     * 执行密码重置操作
     * @param vo 密码重置信息
     * @return 是否操作成功
     */
    @PostMapping("/reset-password")
    @Operation(summary = "密码重置操作")
    public RestBean<Void> resetPassword(@RequestBody @Valid EmailResetVO vo){
        return this.messageHandle(() ->
                accountService.resetEmailAccountPassword(vo));
    }

    /**
     * 针对于返回值为String作为错误信息的方法进行统一处理
     * @param action 具体操作
     * @return 响应结果
     * @param <T> 响应结果类型
     */
    private <T> RestBean<T> messageHandle(Supplier<String> action){
        String message = action.get();
        if(message == null)
            return RestBean.success();
        else
            return RestBean.failure(400, message);
    }
}
