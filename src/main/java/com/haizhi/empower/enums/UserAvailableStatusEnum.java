package com.haizhi.empower.enums;

public enum UserAvailableStatusEnum {
	FREEZE_NO(0, "正常"),
	FREEZE_YES(1, "已禁用"),
	;

	private Integer code;
	private String status;

	UserAvailableStatusEnum(Integer code, String status) {
		this.code = code;
		this.status = status;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
