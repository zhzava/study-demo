package com.gtlz.ems.snmp.trap1;

import java.util.concurrent.LinkedBlockingQueue;
 
import org.snmp4j.CommandResponderEvent;
 
/**
 * 队列中心存放原始trap记录即接收到的CommandResponderEvent对象
 * @author 何盼
 *
 */
public class QueueCenter {
	private static LinkedBlockingQueue<CommandResponderEvent> trapQueue = new LinkedBlockingQueue<CommandResponderEvent>();
 
	
	/**
	 * 获得trap队列
	 * @return CommandResponderEvent
	 */
	public static LinkedBlockingQueue<CommandResponderEvent> getRespEvntMsg (){
		if(trapQueue==null){
			return new LinkedBlockingQueue<CommandResponderEvent>();
		}else{
			return trapQueue;
		}
	}
 
	/**
	 * 存入trap队列
	 * @param message
	 * @throws InterruptedException
	 * 
	 * 
	 */
	public static void putRespEvntLogsQueue(CommandResponderEvent message) throws InterruptedException{
		trapQueue.put(message);
	}
}
