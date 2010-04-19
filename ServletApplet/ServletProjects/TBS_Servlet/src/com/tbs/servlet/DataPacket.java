package com.tbs.servlet;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class DataPacket implements Serializable {

	private static final long serialVersionUID = 2017239314838485953L;
	
	private String command;
	private List<String[]> data;
	
	public DataPacket(String command){
		this(command, new LinkedList<String[]>());
	}
	public DataPacket(String command, List<String[]> data){
		this.command = command;
		this.data = data;
	}
	
	public String getCommand() {
		return command;
	}
	public List<String[]> getData() {
		return data;
	}
}
