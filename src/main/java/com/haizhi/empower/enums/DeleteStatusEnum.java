package com.haizhi.empower.enums;

public enum DeleteStatusEnum {
	DEL_NO(0, "未删除"),
	DEL_YES(1, "已删除"),
	;

	private Integer code;
	private String status;

	DeleteStatusEnum(Integer code, String status) {
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
