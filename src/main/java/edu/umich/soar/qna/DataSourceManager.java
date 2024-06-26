package edu.umich.soar.qna;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.ini4j.InvalidFileFormatException;
import org.ini4j.Wini;
import org.ini4j.spi.EscapeTool;

public class DataSourceManager {
	
	private class ConnectionInstance {
		final public DataSourceConnection conn;
		final public Map<String, String> queries;
		
		ConnectionInstance(DataSourceConnection conn) {
			this.conn = conn;
			this.queries = new HashMap<String, String>();
		}
	}
	
	Map<String, ConnectionInstance> connections;
	
	public DataSourceManager() {
		connections = new HashMap<String, ConnectionInstance>();
	}
	
	public boolean addDataSource(String uid, String driverName, Map<String, String> connectionParameters) throws InvalidDataSourceUIDException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		boolean returnVal = false;
		
		if (connections.containsKey(uid)) {
			throw new InvalidDataSourceUIDException();
		}
		
		Class<?> driverClass = Class.forName(driverName);
		DataSourceDriver driver = null;
		try {
			driver = (DataSourceDriver) driverClass.getDeclaredConstructor().newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DataSourceConnection conn = driver.connect(connectionParameters);
		
		if (conn != null) {
			connections.put(uid, new ConnectionInstance(conn));
			returnVal = true;
		}
		
		return returnVal;
	}
	
	public boolean addDataSource(String uid, String fileName) throws InvalidFileFormatException, IOException, InvalidDataSourceUIDException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvalidQueryUIDException {
		boolean returnVal = false;
		
		Wini ini = new Wini(new File(fileName));
		EscapeTool esc = EscapeTool.getInstance();
		
		if ((ini.get("connection")!=null) && (ini.get("parameters")!=null) && (ini.get("queries")!=null)) {
			String driver = ini.get("connection", "driver");
			
			if ((driver != null)) {
				driver = esc.unquote(driver);
				
				Map<String, String> params = new HashMap<String, String>();
				for (Entry<String, String> e : ini.get("parameters").entrySet()) {
					params.put(e.getKey(), esc.unquote(e.getValue()));
				}
				
				if (addDataSource(uid, driver, params)) {
					for (Entry<String, String> e : ini.get("queries").entrySet()) {
						registerQuery(uid, e.getKey(), esc.unquote(e.getValue()));
					}
					
					returnVal = true;
				}
			}
		}
		
		return returnVal;
	}
	
	public void registerQuery(String connectionUID, String queryName, String querySource) throws InvalidDataSourceUIDException, InvalidQueryUIDException {
		if (!connections.containsKey(connectionUID)) {
			throw new InvalidDataSourceUIDException();
		}
		ConnectionInstance inst = connections.get(connectionUID);
		
		if (inst.queries.containsKey(queryName)) {
			throw new InvalidQueryUIDException();
		}
		
		inst.queries.put(queryName, querySource);
	}
	
	private boolean validateQueryUID(String connectionUID) {
		return connections.containsKey(connectionUID);
	}
	
	private boolean validateQueryName(String connectionUID, String queryName, boolean skipUID) {
		if (!skipUID) {
			if (!validateQueryUID(connectionUID)) {
				return false;
			}
		}
		
		return (connections.get(connectionUID).queries.containsKey(queryName));
	}
	
	public boolean validateQuery(String connectionUID, String queryName) {
		return (validateQueryUID(connectionUID) && validateQueryName(connectionUID, queryName, true));
	}
	
	public QueryState executeQuery(String connectionUID, String queryName, Map<Object, List<Object>> queryParameters) throws InvalidDataSourceUIDException, InvalidQueryUIDException {
		if (!validateQueryUID(connectionUID)) {
			throw new InvalidDataSourceUIDException();
		}
		
		if (!validateQueryName(connectionUID, queryName, true)) {
			throw new InvalidQueryUIDException();
		}
		
		ConnectionInstance inst = connections.get(connectionUID);
		return inst.conn.executeQuery(inst.queries.get(queryName), queryParameters);
	}
	
	public void close() {
		for (Entry<String, ConnectionInstance> inst : connections.entrySet()) {
			inst.getValue().conn.disconnect();
		}
		
		connections.clear();
	}
	
	public Map<String, Map<String, String>> getRegistry() {
		Map<String, Map<String, String>> returnVal = new HashMap<String, Map<String, String>>();
		
		for (Entry<String, ConnectionInstance> s : connections.entrySet()) {
			returnVal.put(s.getKey(), new HashMap<String, String>(s.getValue().queries));
		}
		
		return returnVal;
	}
}
