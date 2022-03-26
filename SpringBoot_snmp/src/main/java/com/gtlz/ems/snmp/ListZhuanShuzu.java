package com.gtlz.ems.snmp;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.IpAddress;
import org.snmp4j.smi.Null;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.TimeTicks;
import org.snmp4j.smi.UnsignedInteger32;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;

public class ListZhuanShuzu {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		List<String> scalarOids = new ArrayList<String>();
		scalarOids.add("1.3.6.1.2.1.1.1.0");
		scalarOids.add("1.3.6.1.2.1.1.5.0");
		scalarOids.add("1.3.6.1.2.1.1.6.0");
        String[] varBinding = new String[3];
        for (int i = 0; i < 3; i++) {
			String str = (String) scalarOids.get(i);
			varBinding[i] = str;
		}
        Vector requestScaVectorRead = getVariableBindings(varBinding);
        System.out.println("Vector requestScaVectorRead ="+requestScaVectorRead.toString());
	}

	/** �������ַ��������л�ȡvector-vector��̬���� **/
	private static Vector getVariableBindings(String[] varBindings) {
		// init a vector collection to contain the request data
		// the varBindings's form is as follow:
		// get: 1.3.6.9.1.2.1.5.0
		// get: 1.3.6.9.1.2.1.2-10
		// set: 1.3.6.9.1.2.1.5.0={s}snmp4j
		Vector v = new Vector(varBindings.length + 1);

		/*����*/
		for (int i = 0; i < varBindings.length; i++) {
			// init scalar oid
			String oid = varBindings[i];

			// init scalar type
			char type = 'i';

			// init saclar value
			String value = null;

			// judge the type of option: set,get
			//���ء�={����������
			int equal = oid.indexOf("={");

			// if it contains the "={", it is a set option
			if (equal > 0) {
				oid = varBindings[i].substring(0, equal);
				type = varBindings[i].charAt(equal + 2);
				value = varBindings[i]
						.substring(varBindings[i].indexOf('}') + 1);
			}

			// if it doesn't has it, it is a get option
			// get option has to model: scalar,table
			// if it has "-",it is a table get
			else if (oid.indexOf('-') > 0) {
				StringTokenizer st = new StringTokenizer(oid, "-");
				if (st.countTokens() != 2) {
					throw new IllegalArgumentException(
							"Illegal OID range specified: '" + oid);
				}
				oid = st.nextToken();
				VariableBinding vbLower = new VariableBinding(new OID(oid));

				// put each item of the table into vector
				v.add(vbLower);
				long last = Long.parseLong(st.nextToken());
				long first = vbLower.getOid().lastUnsigned();
				for (long k = first + 1; k <= last; k++) {
					OID nextOID = new OID(vbLower.getOid().getValue(), 0,
							vbLower.getOid().size() - 1);
					nextOID.appendUnsigned(k);
					VariableBinding next = new VariableBinding(nextOID);
					v.add(next);
				}
				continue;
			}
			

			// if it doesn't has "-", it is a scalar get
			// create a vb on behalf of scalar
			// vb contain two parameter,one is oid,the other is value
			VariableBinding vb = new VariableBinding(new OID(oid));
			if (value != null) {
				Variable variable;
				switch (type) {
				case 'i':
					variable = new Integer32(Integer.parseInt(value));
					break;
				case 'u':
					variable = new UnsignedInteger32(Long.parseLong(value));
					break;
				case 's':
					variable = new OctetString(value);
					break;
				case 'x':
					variable = OctetString.fromString(value, ':', 16);
					break;
				case 'd':
					variable = OctetString.fromString(value, '.', 10);
					break;
				case 'b':
					variable = OctetString.fromString(value, ' ', 2);
					break;
				case 'n':
					variable = new Null();
					break;
				case 'o':
					variable = new OID(value);
					break;
				case 't':
					variable = new TimeTicks(Long.parseLong(value));
					break;
				case 'a':
					variable = new IpAddress(value);
					break;
				default:
					throw new IllegalArgumentException("Variable type " + type
							+ " not supported");
				}

				// set value of vb
				vb.setVariable(variable);
			}

			// put scalar into vector
			v.add(vb);
		}
		return v;
	}
}
