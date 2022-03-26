package com.gtlz.ems.snmp.trap1;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
 

 
/**
 * 启动类
 * @author 何盼
 *
 */
public class Start {
	//public static ApplicationContext fsxac;
	public static void main(String[] args) {
		//fsxac = new ClassPathXmlApplicationContext("applicationContext.xml");
		
		//启动处理线程
		new Thread(new SnmpTrapHandler()).start();
		//启动监听线程
		new Thread(new SnmpTrapListener()).start();
		
	}
}
