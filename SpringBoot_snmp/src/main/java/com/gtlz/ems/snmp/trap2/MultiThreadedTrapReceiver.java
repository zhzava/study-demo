package com.gtlz.ems.snmp.trap2;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.Vector;

import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.MessageDispatcherImpl;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.MPv1;
import org.snmp4j.mp.MPv2c;
import org.snmp4j.mp.MPv3;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.TcpAddress;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.MultiThreadedMessageDispatcher;
import org.snmp4j.util.ThreadPool;

/**
 * 本类用于监听代理进程的Trap信息
 *
 * @author zhanjia
 *
 */
public class MultiThreadedTrapReceiver implements CommandResponder {

    private MultiThreadedMessageDispatcher dispatcher;
    private Snmp snmp = null;
    private Address listenAddress;
    private ThreadPool threadPool;

    public MultiThreadedTrapReceiver() {
        // BasicConfigurator.configure();
    }

    private void init() throws UnknownHostException, IOException {
        threadPool = ThreadPool.create("Trap", 2);
        dispatcher = new MultiThreadedMessageDispatcher(threadPool,
                new MessageDispatcherImpl());
        listenAddress = GenericAddress.parse("udp:192.168.0.109/162"); // 本地IP与监听端口
        TransportMapping transport;
        // 对TCP与UDP协议进行处理
        if (listenAddress instanceof UdpAddress) {
            transport = new DefaultUdpTransportMapping((UdpAddress) listenAddress);
        } else {
            transport = new DefaultTcpTransportMapping((TcpAddress) listenAddress);
        }
        snmp = new Snmp(dispatcher, transport);
        snmp.getMessageDispatcher().addMessageProcessingModel(new MPv1());
        snmp.getMessageDispatcher().addMessageProcessingModel(new MPv2c());
        snmp.getMessageDispatcher().addMessageProcessingModel(new MPv3());
        USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(MPv3.createLocalEngineID()), 0);
        SecurityModels.getInstance().addSecurityModel(usm);
        snmp.listen();
    }

    
    public void run() {
        try {
            init();
            snmp.addCommandResponder(this);
            System.out.println("开始监听Trap信息!");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 实现CommandResponder的processPdu方法, 用于处理传入的请求、PDU等信息
     * 当接收到trap时，会自动进入这个方法
     *
     * @param respEvnt
     */
    public void processPdu(CommandResponderEvent respEvnt) {
        System.out.println("有新的trap消息");
        // 解析Response
        if (respEvnt != null && respEvnt.getPDU() != null) {
            Vector<? extends VariableBinding> recVBs = respEvnt.getPDU().getVariableBindings();
            for (int i = 0; i < recVBs.size(); i++) {
                VariableBinding recVB = recVBs.elementAt(i);
                //获取要转换的16进制字节
                String os = recVB.getVariable().toString();
                if(!(os.indexOf(":") == -1)) {
                    String[] temps = os.split(":");
                    byte[] bs = new byte[temps.length];
                    for (int j = 0; j < temps.length; j++) {
                        //转换byte[]
                        bs[j] = (byte) Integer.parseInt(temps[j], 16);
                    }
                    try {
                        //转换String,编码集根据发Snmp的编码集来
                        os = new String(bs, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
               /* int ver2c = SnmpConstants.version2c;
                Snmp4jFirstDemo snm=new Snmp4jFirstDemo(ver2c);
                PDU pdu = new PDU();
                //PDU pdu = new ScopedPDU();
                // 设置要获取的对象ID，这个OID代表远程计算机的名称
                OID oids = recVB.getOid();
                pdu.add(new VariableBinding(oids));
                // 设置报文类型
                pdu.setType(PDU.GET);
                try {
                    snm.sendMessage(false, true, pdu, "udp:192.168.0.126/161");
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                System.out.println(recVB.getOid() + " : " + os);
            }
        }
    }

    public static void main(String[] args) {
        MultiThreadedTrapReceiver multithreadedtrapreceiver = new MultiThreadedTrapReceiver();
        multithreadedtrapreceiver.run();
    }
}

