package com.gtlz.ems.snmp.trap1;

import org.snmp4j.CommandResponderEvent;
 
/**
 * 接收trap信息类接口定义
 * @author 何盼
 *
 */
public interface ListenerInterface {
	/**
	 * 存入队列方法
	 * @param respEvnt
	 */
	public void putMessage2Queue(CommandResponderEvent respEvnt );
	
	/**
	 * 初始化方法初始化监听端口、ip等信息，这些信息需要从SPRING配置文件中读取
	 */
	public void init();
}
