package com.airteltv.utility;

import static java.lang.System.out;

import java.util.HashMap;
import java.util.Map;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.exceptions.InvalidQueryException;

public class CassandraManager {

    Cluster                              clusterCassandra     = null;
    Session                              sessionCassandra     = null;
    String                               host                 = null;
    int                                  port                 = 0;
    private String 					   	cassandraKeySpace	  = null;
    static Map<String, CassandraManager> cassandraInstanceMap = new HashMap<String, CassandraManager>();

    private void connectCassandra(String _host, int _port, String _cassandraKeySpace) {
        this.host = _host;
        this.port = _port;
        this.cassandraKeySpace = _cassandraKeySpace;
    }

    private void connectCassandra() {
        try {
            if(clusterCassandra == null || clusterCassandra.isClosed()) {
                clusterCassandra = Cluster.builder().addContactPoint(host).withPort(port).build();
            }
            final Metadata metadata = clusterCassandra.getMetadata();
            out.printf("Connected to cluster: %s\n", metadata.getClusterName());
            for(final Host localhost : metadata.getAllHosts()) {
                out.printf("Datacenter: %s; Host: %s; Rack: %s\n", localhost.getDatacenter(), localhost.getAddress(), localhost.getRack());
            }
            if(sessionCassandra == null || sessionCassandra.isClosed()) {
                sessionCassandra = clusterCassandra.connect(cassandraKeySpace);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            CassandraManager.closeAll();
        }
    }

    public void close() {
        if(sessionCassandra != null && !sessionCassandra.isClosed()) {
            sessionCassandra.closeAsync();
        }

        if(clusterCassandra != null && !clusterCassandra.isClosed()) {
            clusterCassandra.closeAsync();
        }
        sessionCassandra = null;
        clusterCassandra = null;
        cassandraInstanceMap.remove(host + port);
    }

    public static void closeAll() {
        for(String key : cassandraInstanceMap.keySet()) {
            cassandraInstanceMap.get(key).close();
        }
    }

    private Session getSession() {
        if(sessionCassandra == null || sessionCassandra.isClosed())
            connectCassandra();
        return sessionCassandra;
    }

    private boolean isClosed() {
        if(sessionCassandra != null)
            return sessionCassandra.isClosed();
        else
            return true;
    }

    public ResultSet executeQuery(String query) throws InvalidQueryException, InvalidQueryException {
        ResultSet rs = this.getSession().execute(query);
        return rs;
    }

    public static CassandraManager getCassandraInstance(String host, int port, String cassandraKeySpace) {
        if(cassandraInstanceMap.containsKey((host + port)) && !cassandraInstanceMap.get(host + port).isClosed())
            return cassandraInstanceMap.get(host + port);
        else {
            CassandraManager cm = new CassandraManager();
            cm.connectCassandra(host, port, cassandraKeySpace);
            cassandraInstanceMap.put(host + port, cm);
            return cm;
        }
    }
    
	public static CassandraManager getCassandraSessionWCFStaging() {
		return CassandraManager.getCassandraInstance("10.1.2.77", 9042, "userdata");
	}
    
    public static void main(String[] args) {
    	final String selectcql = "select * from operator_info where msisdn = '+912323455601';";
    		Row row = getCassandraSessionWCFStaging().executeQuery(selectcql).one();
    		System.out.println(row.toString());
    	
	}
}
