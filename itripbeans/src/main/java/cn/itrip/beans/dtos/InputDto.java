package cn.itrip.beans.dtos;

import io.swagger.annotations.ApiModelProperty;

public class InputDto {

	@ApiModelProperty(value="单一参数传入")
	private String paramString;
	@ApiModelProperty(value="多个参数传入")
	private String[] paramStrings;
	public String getParamString() {
		return paramString;
	}
	public void setParamString(String paramString) {
		this.paramString = paramString;
	}
	public String[] getParamStrings() {
		return paramStrings;
	}
	public void setParamStrings(String[] paramStrings) {
		this.paramStrings = paramStrings;
	}
	
	
	
}
