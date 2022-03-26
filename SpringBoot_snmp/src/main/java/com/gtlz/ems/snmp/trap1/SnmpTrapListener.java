package com.gtlz.ems.snmp.trap1;

import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.CommunityTarget;
import org.snmp4j.MessageDispatcherImpl;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.MPv1;
import org.snmp4j.mp.MPv2c;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.TcpAddress;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.MultiThreadedMessageDispatcher;
import org.snmp4j.util.ThreadPool;
 
/**
 * 监听类
 * @author 何盼
 *
 */
public class SnmpTrapListener extends AbstListener implements CommandResponder{
	
	private String ip="192.168.0.115"; //本地IP
	private String port="161"; //监听端口
	private Address listenAddress; //地址信息
	private ThreadPool threadPool;
	private MultiThreadedMessageDispatcher dispatcher;
	@Override
	public void init() {
		try{
		/*threadPool=ThreadPool.create("Trap", 2); 
		dispatcher=new MultiThreadedMessageDispatcher(threadPool, new MessageDispatcherImpl());
		listenAddress = GenericAddress.parse(System.getProperty(
				"snmp4j.listenAddress", "udp:" + ip + "/" + port));
		System.out.println("listenAddress:------------------"+listenAddress);
		TransportMapping transport = new DefaultUdpTransportMapping((UdpAddress) listenAddress);
		// 对TCP与UDP协议进行处理
		/**if (listenAddress instanceof UdpAddress) {
			transport = new DefaultUdpTransportMapping((UdpAddress) listenAddress);
		} else {
			transport = new DefaultTcpTransportMapping(
					(TcpAddress) listenAddress);
		}*/
		/**Snmp snmp = new Snmp(dispatcher, transport);
		//snmp.getMessageDispatcher().addMessageProcessingModel(new MPv1());
		snmp.getMessageDispatcher().addMessageProcessingModel(new MPv2c());
		//snmp.getMessageDispatcher().addMessageProcessingModel(new MPv3());
		//USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(MPv3.createLocalEngineID()), 0);
		//SecurityModels.getInstance().addSecurityModel(usm);
		snmp.listen();**/
			//设定CommunityTarget
            CommunityTarget myTarget = new CommunityTarget();
            //定义远程主机的地址
            //Address deviceAdd = GenericAddress.parse("udp:192.168.1.233/161");
            //定义本机的地址
            Address localAdd = GenericAddress.parse("udp:192.168.0.121/161");
            //设定远程主机的地址
            //myTarget.setAddress(deviceAdd);
            //设定本地主机的地址
            myTarget.setAddress(localAdd);
            //设置snmp共同体
            myTarget.setCommunity(new OctetString("public"));
            //设置超时重试次数
            myTarget.setRetries(2);
            //设置超时的时间
            myTarget.setTimeout(5*60);
            //设置使用的snmp版本
            myTarget.setVersion(SnmpConstants.version2c);

            //设定采取的协议
            TransportMapping transport = new DefaultUdpTransportMapping();//设定传输协议为UDP
            //调用TransportMapping中的listen()方法，启动监听进程，接收消息，由于该监听进程是守护进程，最后应调用close()方法来释放该进程
            transport.listen();
            //创建SNMP对象，用于发送请求PDU
            Snmp snmp = new Snmp(transport);
		snmp.addCommandResponder(this);
		
		System.out.println("启动监听成功");
		}catch (Exception e){
			System.out.println("snmp 初始化失败");
			e.printStackTrace();
		}
	}
	
	
	//此方法为CommandResponder 接口实现方法用于监听后的处理方法,将接收到的trap信息入队
	@Override
	public void processPdu(CommandResponderEvent CREvent) {
		System.out.println("in processPdu");
		this.putMessage2Queue(CREvent);
	}
 
 
	public String getIp() {
		return ip;
	}
 
 
	public void setIp(String ip) {
		this.ip = ip;
	}
 
 
	public String getPort() {
		return port;
	}
 
 
	public void setPort(String port) {
		this.port = port;
	}
 
 
	public Address getListenAddress() {
		return listenAddress;
	}
 
 
	public void setListenAddress(Address listenAddress) {
		this.listenAddress = listenAddress;
	}
 
 
 
}
