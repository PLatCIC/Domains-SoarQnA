package edu.umich.soar.qna.math;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.umich.soar.qna.ComputationalQueryState;

public class FloatQueryState extends ComputationalQueryState {
	Double convertedValue;
	
	FloatQueryState() {
		convertedValue = null;
	}

	public boolean initialize(String querySource, Map<Object, List<Object>> queryParameters) {
		boolean returnVal = false;
		
		if ((queryParameters.size() == 1) && (queryParameters.containsKey("operand1"))) {
			List<Object> tempList;
			
			tempList = queryParameters.get("operand1");
			if ((tempList.size() == 1)) {
				Object tempObject = tempList.iterator().next();
				
				if (tempObject instanceof Number) {
					convertedValue = Double.valueOf(((Number) tempObject).doubleValue());
				} else if (tempObject instanceof String) {
					try {
						convertedValue = Double.valueOf(Double.parseDouble(((String) tempObject)));
					} catch (NumberFormatException e) {
					}
				}
				
				if (convertedValue != null) {
					returnVal = true;
					hasComputed = false;
				}
			}
		}
		
		return returnVal;
	}

	public Map<String, List<Object>> next() {
		if (!hasComputed) {
			hasComputed = true;
			HashMap<String, List<Object>> returnVal = new HashMap<String, List<Object>>();
			List<Object> newList = new LinkedList<Object>();
			
			newList.add(convertedValue);
			returnVal.put("result", newList);
			
			return returnVal;
		}
		
		return null;
	}
}
