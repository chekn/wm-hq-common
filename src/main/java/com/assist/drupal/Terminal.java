package com.assist.drupal;

/**
 * 
 * @author Pactera-NEN
 * @date 2016年3月10日-下午7:27:56
 */
public enum Terminal {
	
	TVAM("TVAM.ftl","am"),
	TVPM("TVPM.ftl","pm"),
	CLIENT("ClientEnd.ftl","全文"),
	WEB("CBNWeb.ftl","一财网"),
	NEWS("CBNNews.ftl","第一财讯");
	
	private String defFileNameSuf;
	private String terminalName;
	private Terminal(String defFileNameSuf,String terminalName){
		this.defFileNameSuf=defFileNameSuf;
		this.terminalName=terminalName;
	}
	
	
	public String getDefFileNameSuf() {
		return defFileNameSuf;
	}
	public void setDefFileNameSuf(String defFileNameSuf) {
		this.defFileNameSuf = defFileNameSuf;
	}
	
	public String getTerminalName() {
		return terminalName;
	}
	public void setTerminalName(String terminalName) {
		this.terminalName = terminalName;
	}

}
