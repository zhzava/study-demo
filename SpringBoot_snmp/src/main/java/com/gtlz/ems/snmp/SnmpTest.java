package com.gtlz.ems.snmp;

import java.util.ArrayList;

import java.util.List;

import org.snmp4j.log.ConsoleLogFactory;

import org.snmp4j.log.LogAdapter;

import org.snmp4j.log.LogFactory;

import com.gtlz.ems.snmp.SnmpData;

public class SnmpTest {

	/**
	 * 
	 * @param args
	 * @throws Exception 
	 * 
	 */

	public static void main(String[] args) throws Exception {

// TODO Auto-generated method stub

		SnmpTest test = new SnmpTest();
		
		//test.testWalk();
		//test.testGet();
        test.testSetPDU();
		test.testGet();
	}

	public void testGet()

	{

		String ip = "192.168.0.121";

		String community = "public";

		String oidval = "1.3.6.1.2.1.1.5.0";

		SnmpData.snmpGet(ip, community, oidval);

	}

	public void testGetList() {

		String ip = "127.0.0.1";

		String community = "public";

		List<String> oidList = new ArrayList<String>();

		oidList.add("1.3.6.1.2.1.1.1.0");

		oidList.add("1.3.6.1.2.1.1.5.0");

		SnmpData.snmpGetList(ip, community, oidList);

	}

	//异步获取
	public void testGetAsyList()

	{

		String ip = "127.0.0.1";

		String community = "public";

		List<String> oidList = new ArrayList<String>();

		oidList.add("1.3.6.1.2.1.1.1.0");

		oidList.add("1.3.6.1.2.1.1.5.0");

		SnmpData.snmpAsynGetList(ip, community, oidList);

		System.out.println("i am first!");

	}

	public void testWalk()

	{

		String ip = "192.168.0.121";

		String community = "public";

		String targetOid = "1.3.6.1.2.1.25.4.2.1.2";

		SnmpData.snmpWalk(ip, community, targetOid);

	}

	public void testAsyWalk()

	{

		String ip = "127.0.0.1";

		String community = "public";

// 异步采集数据

		SnmpData.snmpAsynWalk(ip, community, "1.3.6.1.2.1.25.4.2.1.2");

	}

	public void testSetPDU() throws Exception

	{

		String ip = "192.168.0.121";

		String community = "public";

		SnmpData.setPDU(ip, community, "1.3.6.1.2.1.1.5.0", "nike");

	}

	public void testVersion()

	{

		System.out.println(org.snmp4j.version.VersionInfo.getVersion());

	}

}
