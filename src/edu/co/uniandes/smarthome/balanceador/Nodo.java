package edu.co.uniandes.smarthome.balanceador;

public class Nodo {
	
	private String ip;
	
	private String puerto;
	
	

	public Nodo(String ip, String puerto) {
		super();
		this.ip = ip;
		this.puerto = puerto;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPuerto() {
		return puerto;
	}

	public void setPuerto(String puerto) {
		this.puerto = puerto;
	}
	

}
