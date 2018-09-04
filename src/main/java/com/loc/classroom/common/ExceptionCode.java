package com.loc.classroom.common;

import lombok.Getter;
import lombok.Setter;

/**
 * Created on 2018/8/30.
 */
public enum ExceptionCode {

  MINIAPP_CODE_IS_EMPTY(10000, "微信小程序code为空"),
  MINIAPP_USER_CHECK_ERROR(10001, "微信小程序用户检查错误"),


  ;


  @Getter
  @Setter
  private Integer code;
  @Getter
  @Setter
  private String message;

  ExceptionCode(Integer code, String message) {
    this.code = code;
    this.message = message;
  }
}
