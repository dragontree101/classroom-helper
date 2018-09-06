package com.loc.classroom.controller;

import static com.loc.classroom.common.ExceptionCode.MINIAPP_CODE_IS_EMPTY;
import static com.loc.classroom.common.ExceptionCode.MINIAPP_USER_CHECK_ERROR;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import com.loc.framework.autoconfigure.LocServiceException;
import com.loc.framework.autoconfigure.utils.ProblemUtil;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;

/**
 * Created on 2018/9/4.
 */
@Slf4j
@RestController
@RequestMapping("/wechat/miniapp/user")
public class MiniAppUserController {

  @Autowired
  private WxMaService wxService;

  /**
   * 登陆接口
   */
  @GetMapping("/login")
  public Problem login(String code) {
    if (StringUtils.isBlank(code)) {
      throw new LocServiceException(MINIAPP_CODE_IS_EMPTY.getCode(),
          MINIAPP_CODE_IS_EMPTY.getMessage());
    }

    try {
      WxMaJscode2SessionResult session = this.wxService.getUserService().getSessionInfo(code);
      log.info(session.getSessionKey());
      log.info(session.getOpenid());
      //TODO 可以增加自己的逻辑，关联业务相关数据
      return ProblemUtil.createProblem(session);
    } catch (WxErrorException e) {
      log.error(e.getMessage(), e);
      throw new LocServiceException(e.getError().getErrorCode(), e.getError().getErrorMsg());
    }
  }

  /**
   * 获取用户信息接口
   */
  @GetMapping("/info")
  public Problem info(String sessionKey, String signature, String rawData, String encryptedData,
      String iv) {
    // 用户信息校验
    if (!this.wxService.getUserService().checkUserInfo(sessionKey, rawData, signature)) {
      throw new LocServiceException(MINIAPP_USER_CHECK_ERROR.getCode(),
          MINIAPP_USER_CHECK_ERROR.getMessage());
    }

    // 解密用户信息
    WxMaUserInfo userInfo = this.wxService.getUserService()
        .getUserInfo(sessionKey, encryptedData, iv);

    return ProblemUtil.createProblem(userInfo);
  }

  /**
   * 获取用户绑定手机号信息
   */
  @GetMapping("/phone")
  public Problem phone(String sessionKey, String signature, String rawData, String encryptedData,
      String iv) {
    // 用户信息校验
    if (!this.wxService.getUserService().checkUserInfo(sessionKey, rawData, signature)) {
      throw new LocServiceException(MINIAPP_USER_CHECK_ERROR.getCode(),
          MINIAPP_USER_CHECK_ERROR.getMessage());
    }

    // 解密
    WxMaPhoneNumberInfo phoneNoInfo = this.wxService.getUserService()
        .getPhoneNoInfo(sessionKey, encryptedData, iv);

    return ProblemUtil.createProblem(phoneNoInfo);
  }
}
