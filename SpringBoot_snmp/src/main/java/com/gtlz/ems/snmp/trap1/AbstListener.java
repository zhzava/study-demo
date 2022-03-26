package com.gtlz.ems.snmp.trap1;

import org.snmp4j.CommandResponderEvent;
 
 
/**
 * 处理类的抽象方法
 * 
 * @author 何盼
 *
 */
public abstract class AbstListener implements ListenerInterface ,Runnable {
 
	@Override
	public void putMessage2Queue(CommandResponderEvent respEvnt) {
		try {
			QueueCenter.putRespEvntLogsQueue(respEvnt);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		init();
	}
	
 
}
