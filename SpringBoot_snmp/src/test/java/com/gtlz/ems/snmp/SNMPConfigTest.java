package com.gtlz.ems.snmp;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.MessageProcessingModel;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.PDUFactory;
import org.snmp4j.util.TableEvent;
import org.snmp4j.util.TableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class SNMPConfigTest {
	@Autowired
	private Target target;
	@Autowired
	private Snmp snmp;

	/**
	 * 整合Spring后，好像一直没有数据返回过来
	 */
	@Test
	public void getSynTest() {
		PDU pdu = new PDU();
		// 设置要获取的对象ID
		// OID oid = new OID(".1.3.6.1.2.1.1.1.0");
		OID oid = new OID(".1.3.6.1.2.1.25.2.2.0");
		// OID oid2 = new OID(".1.3.6.1.2.1.1.6.0");

		pdu.add(new VariableBinding(oid));
		// pdu.add(new VariableBinding(oid1));
		// pdu.add(new VariableBinding(oid2));
		// 设置报文格式
		pdu.setType(PDU.GET);
		System.out.println("执行到了try那里");
		try {
			System.out.println("执行到了try里面");
			log.debug(snmp.toString());
			log.debug(target.toString());
			log.debug(pdu.toString());

			// 这个地方有问题
			// 应该是snmp出现的问题
			// 不是这里的问题
			// transportMapping.listen(); 开启监听
			ResponseEvent response = snmp.send(pdu, target);
			// 处理响应
			System.out.println("Synchronize(同步) message(消息) from(来自) " + response.getPeerAddress() + "\r\n"
					+ "request(发送的请求):" + response.getRequest() + "\r\n" + "response(返回的响应):" + response.getResponse());
		} catch (IOException e) {
			System.out.println("抛出了异常");
			e.printStackTrace();
		}
	}

	@Test
	// 获取cpu使用率
	public void collectCPU() {
		TransportMapping transport = null;
		Snmp snmp = null;
		CommunityTarget target;
		String[] oids = { "1.3.6.1.2.1.25.3.3.1.2" };
		try {
			System.out.println("111111111111111111");
			transport = new DefaultUdpTransportMapping();
			snmp = new Snmp(transport);// 创建snmp
			snmp.listen();// 监听消息
			target = new CommunityTarget();
			target.setCommunity(new OctetString("public"));
			target.setRetries(2);
			target.setAddress(GenericAddress.parse("udp:127.0.0.1/161"));
			target.setTimeout(8000);
			target.setVersion(SnmpConstants.version2c);
			System.out.println("2222222222222222222222");
			TableUtils tableUtils = new TableUtils(snmp, new PDUFactory() {
				@Override
				public PDU createPDU(Target arg0) {
					PDU request = new PDU();
					request.setType(PDU.GET);
					return request;
				}

				@Override
				public PDU createPDU(MessageProcessingModel messageProcessingModel) {
					// TODO Auto-generated method stub
					PDU request = new PDU();
					request.setType(PDU.GET);
					return request;
				}
			});
			OID[] columns = new OID[oids.length];
			for (int i = 0; i < oids.length; i++)
				columns[i] = new OID(oids[i]);
			List<TableEvent> list = tableUtils.getTable(target, columns, null, null);
			if (list.size() == 1 && list.get(0).getColumns() == null) {
				System.out.println(" null");
			} else {
				int percentage = 0;
				for (TableEvent event : list) {
					VariableBinding[] values = event.getColumns();
					if (values != null)
						percentage += Integer.parseInt(values[0].getVariable().toString());
				}
				System.out.println("CPU利用率为：" + percentage / list.size() + "%");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (transport != null)
					transport.close();
				if (snmp != null)
					snmp.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Test
	// 获取内存相关信息
	public void collectMemory() {
		TransportMapping transport = null;
		Snmp snmp = null;
		CommunityTarget target;
		String[] oids = { "1.3.6.1.2.1.25.2.3.1.2", // type 存储单元类型
				"1.3.6.1.2.1.25.2.3.1.3", // descr
				"1.3.6.1.2.1.25.2.3.1.4", // unit 存储单元大小
				"1.3.6.1.2.1.25.2.3.1.5", // size 总存储单元数
				"1.3.6.1.2.1.25.2.3.1.6" }; // used 使用存储单元数;
		String PHYSICAL_MEMORY_OID = "1.3.6.1.2.1.25.2.1.2";// 物理存储
		String VIRTUAL_MEMORY_OID = "1.3.6.1.2.1.25.2.1.3"; // 虚拟存储
		System.out.println("执行到了try外面");
		try {
			transport = new DefaultUdpTransportMapping();
			snmp = new Snmp(transport);// 创建snmp
			snmp.listen();// 监听消息
			target = new CommunityTarget();
			target.setCommunity(new OctetString("public"));
			target.setRetries(2);
			target.setAddress(GenericAddress.parse("udp:127.0.0.1/161"));
			target.setTimeout(8000);
			target.setVersion(SnmpConstants.version2c);
			TableUtils tableUtils = new TableUtils(snmp, new PDUFactory() {
				@Override
				public PDU createPDU(Target arg0) {
					PDU request = new PDU();
					request.setType(PDU.GET);
					return request;
				}

				@Override
				public PDU createPDU(MessageProcessingModel messageProcessingModel) {
					// TODO Auto-generated method stub
					return null;
				}
			});
			OID[] columns = new OID[oids.length];
			for (int i = 0; i < oids.length; i++) {
				columns[i] = new OID(oids[i]);
				System.out.println(columns[i]);
			}
			@SuppressWarnings("unchecked")
			List<TableEvent> list = tableUtils.getTable(target, columns, null, null);
			System.out.println("list----------------"+list);
			if (list.size() == 1 && list.get(0).getColumns() == null) {
				System.out.println(" null");
			} else {
				for (TableEvent event : list) {
					VariableBinding[] values = event.getColumns();
					if (values == null)
						continue;
					int unit = Integer.parseInt(values[2].getVariable().toString());// unit 存储单元大小
					int totalSize = Integer.parseInt(values[3].getVariable().toString());// size 总存储单元数
					int usedSize = Integer.parseInt(values[4].getVariable().toString());// used 使用存储单元数
					String oid = values[0].getVariable().toString();
					if (PHYSICAL_MEMORY_OID.equals(oid)) {
						System.out
								.println("PHYSICAL_MEMORY----->物理内存大小：" + (long) totalSize * unit / (1024 * 1024 * 1024)
										+ "G   内存使用率为：" + (long) usedSize * 100 / totalSize + "%");
					} else if (VIRTUAL_MEMORY_OID.equals(oid)) {
						System.out
								.println("VIRTUAL_MEMORY----->虚拟内存大小：" + (long) totalSize * unit / (1024 * 1024 * 1024)
										+ "G   内存使用率为：" + (long) usedSize * 100 / totalSize + "%");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (transport != null)
					transport.close();
				if (snmp != null)
					snmp.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
