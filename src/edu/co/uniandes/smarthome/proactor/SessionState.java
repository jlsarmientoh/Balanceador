package edu.co.uniandes.smarthome.proactor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * El objeto que se adhieren a la operación de E / S
 * @author Administrador
 */
public class SessionState {
    private Map<String, String> sessionProps =new ConcurrentHashMap<String, String>();
    
    public String getProperty(String key) {
        return sessionProps.get(key);
    }
    
    public void setProperty(String key, String value) {
        sessionProps.put(key, value);

    }
}
