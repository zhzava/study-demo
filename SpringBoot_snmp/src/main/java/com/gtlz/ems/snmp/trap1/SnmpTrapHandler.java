package com.gtlz.ems.snmp.trap1;

import org.snmp4j.CommandResponderEvent;
 
 
/**
 * 处理类
 * @author 何盼
 *
 */
public class SnmpTrapHandler implements Runnable{
 
	@Override
	public void run() {
		while(true){
			try {
				CommandResponderEvent resEvent=QueueCenter.getRespEvntMsg().take();
				
				System.out.println("event:"+resEvent);
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
 
}
