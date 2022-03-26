package com.gtlz.ems.snmp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.List;

import org.snmp4j.PDU;
import org.snmp4j.ScopedPDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.UserTarget;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.AuthMD5;
import org.snmp4j.security.PrivDES;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

public class Snmpv3Test {
	public static void main(String[] args) throws IOException {
		try {
			//初始化snmp
			TransportMapping<?> transport = new DefaultUdpTransportMapping();// 设定传输协议为UDP       
			transport.listen();
			//设置安全模式
			USM usm=new USM(SecurityProtocols.getInstance(), new OctetString(MPv3.createLocalEngineID()), 0);
			SecurityModels.getInstance().addSecurityModel(usm);
			Snmp snmp = new Snmp(transport);
			//注册加密协议 md5 认证协议des  snmp4j 3.x版本以上需要
			SecurityProtocols securityProtocols=SecurityProtocols.getInstance();
			securityProtocols.addAuthenticationProtocol(new AuthMD5());
			securityProtocols.addPrivacyProtocol(new PrivDES());
			//添加用户
			UsmUser user=new UsmUser(new OctetString("zhairiyun"), 
					AuthMD5.ID, new OctetString("zhairiyun1"), PrivDES.ID, new OctetString("zhairiyun2"));
			snmp.getUSM().addUser(new OctetString("zhairiyun"), user);
			//设置ip
			Address localAdd = GenericAddress.parse("udp:192.168.0.122/161");
			//v3是UserTaget  v1v2c是CommunityTarget
			UserTarget target=new UserTarget();
			target.setVersion(SnmpConstants.version3);//协议版本v3
			target.setAddress(localAdd);
			target.setSecurityLevel(SecurityLevel.AUTH_PRIV);//设置安全模式  加密认证
			/*因为不高加密和认证所以不需要设置*/
			target.setSecurityName(new OctetString("zhairiyun"));//v3的用户名
			target.setTimeout(3000);
			target.setRetries(2);
			//创建ScopePDU
			ScopedPDU pdu=new ScopedPDU();
			//get
			//pdu.add(new VariableBinding(new OID(".1.3.6.1.2.1.1.5.0")));//OID代表设备名字
			//pdu.setType(PDU.GET);
			//set
			pdu.add(new VariableBinding(new OID(".1.3.6.1.2.1.1.5.0"),new OctetString("windows10")));
			pdu.setType(PDU.SET);
			
			//发送报文
			ResponseEvent responseEvent=snmp.send(pdu, target);
			//显示信息
			System.out.println("Synchronize(同步) message(消息) from(来自) "
                    + responseEvent.getPeerAddress() + "\r\n"+"request(发送的请求):"
                    + responseEvent.getRequest() + "\r\n"+"response(返回的响应):"
                    + responseEvent.getResponse());
		} catch (SocketException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		
	}
	
}