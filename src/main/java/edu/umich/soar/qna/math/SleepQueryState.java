package edu.umich.soar.qna.math;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SleepQueryState extends UnaryMathQueryState {

	public Map<String, List<Object>> next() {
		if (!hasComputed) {
			hasComputed = true;
			HashMap<String, List<Object>> returnVal = new HashMap<String, List<Object>>();
			List<Object> newList = new LinkedList<Object>();
			
			Long sleepTime = null;
			
			if ((operand1 instanceof Long)) {
				newList.add(Long.valueOf((((Long) operand1).longValue())));
				
				sleepTime = (Long.valueOf((((Long) operand1).longValue()))).longValue();
			} else {
				newList.add(Double.valueOf((((Number) operand1).doubleValue())));
				
				sleepTime = (Double.valueOf((((Number) operand1).doubleValue()))).longValue();
			}
			returnVal.put("result", newList);
			
			try {
				Thread.sleep(Math.abs(sleepTime));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			return returnVal;
		}
		
		return null;
	}

}
