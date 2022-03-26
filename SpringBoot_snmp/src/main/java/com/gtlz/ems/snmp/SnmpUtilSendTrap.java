package com.gtlz.ems.snmp;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.util.Vector;
/**
 * @description:
 * @author: qb
 * @time: 2021/7/28 10:51
 */
@Configuration
public class SnmpUtilSendTrap {

    private Snmp snmp = null;

    private Address targetAddress = null;

    public static void main(String[] args) {
    	try {
            SnmpUtilSendTrap util = new SnmpUtilSendTrap();
            util.initComm();
            util.sendPDU();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

    //@Scheduled(cron="0/5 * *  * * ? ")
    public static void test(){
        try {
            SnmpUtilSendTrap util = new SnmpUtilSendTrap();
            util.initComm();
            util.sendPDU();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initComm() throws IOException {

        // 设置管理进程的IP和端口
        targetAddress = GenericAddress.parse("udp:192.168.0.121/161");
        TransportMapping transport = new DefaultUdpTransportMapping();
        snmp = new Snmp(transport);
        transport.listen();

    }

    /**
     * 向管理进程发送Trap报文
     *
     * @throws IOException
     */
    public void sendPDU() throws IOException {

        // 设置 target
        CommunityTarget target = new CommunityTarget();
        target.setAddress(targetAddress);

        // 通信不成功时的重试次数
        target.setRetries(2);
        // 超时时间
        target.setTimeout(1500);
        // snmp版本
        target.setVersion(SnmpConstants.version2c);

        // 创建 PDU
        PDU pdu = new PDU();
        pdu.add(new VariableBinding(new OID("1.3.6.1.2.1.1.5.0"),
                new OctetString("代理端发送消息")));
        pdu.add(new VariableBinding(new OID("1.3.6.1.2.1.1.6.0"),
                new OctetString("发生告警信息，推送到管理端")));
        pdu.setType(PDU.TRAP);

        // 向Agent发送PDU，并接收Response
        ResponseEvent respEvnt = snmp.send(pdu, target);
        System.out.println("respEvnt.getRequest---------"+respEvnt.getRequest());
        // 解析Response
        if (respEvnt != null && respEvnt.getResponse() != null) {
            Vector<? extends VariableBinding> recVBs = (Vector<? extends VariableBinding>) respEvnt.getResponse()
                    .getVariableBindings();
            for (int i = 0; i < recVBs.size(); i++) {
                VariableBinding recVB = recVBs.elementAt(i);
                System.out.println(recVB.getOid() + " : " + recVB.getVariable());
            }
        }
    }
}
