package edu.umich.soar.qna.math;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DivisionQueryState extends BinaryMathQueryState {
	
	public Map<String, List<Object>> next() {
		if (!hasComputed) {
			hasComputed = true;
			HashMap<String, List<Object>> returnVal = new HashMap<String, List<Object>>();
			List<Object> newList = new LinkedList<Object>();
			
			if ((operand1 instanceof Long) && (operand2 instanceof Long) && (((Long) operand1).longValue() % ((Long) operand2).longValue() == 0)) {
				newList.add(Long.valueOf(((Long) operand1).longValue()/((Long) operand2).longValue()));
			} else {
				newList.add(Double.valueOf(((Number) operand1).doubleValue()/((Number) operand2).doubleValue()));
			}
			returnVal.put("result", newList);
			
			return returnVal;
		}
		
		return null;
	}
}
