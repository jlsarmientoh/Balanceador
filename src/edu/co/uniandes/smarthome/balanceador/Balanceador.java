package edu.co.uniandes.smarthome.balanceador;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.concurrent.BlockingQueue;

public class Balanceador implements Runnable {

	private BlockingQueue<byte[]> queue;
	
	public BlockingQueue<byte[]> getQueue() {
		return queue;
	}

	public void setQueue(BlockingQueue<byte[]> queue) {
		this.queue = queue;
	}

	public Nodo[] getNodos() {
		return nodos;
	}

	public void setNodos(Nodo[] nodos) {
		this.nodos = nodos;
	}

	private Nodo[] nodos; 

	public Balanceador(BlockingQueue<byte[]> q, Nodo[] nodos) {
		this.queue = q;
		this.nodos = nodos;
	}

	@Override
	public void run() {
		try {
			
			byte[] msg;
			// consuming messages until exit message is received
			while ((msg = queue.take()) != null) {
//				Abrir socket
				//procesar(msg);
				int idNodo = (int) (Math.floor(Math.random() * (nodos.length - 1 + 1) + 1));
				
				conectarse (nodos[idNodo-1], msg);
			}
//				
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	private boolean conectarse(Nodo nodoAConectarse, byte[] msg) {
		System.out.println(nodoAConectarse.getIp() + ":"  + nodoAConectarse.getPuerto() + " " + msg);
		Socket clientSocket;
		try {
			clientSocket = new Socket(nodoAConectarse.getIp(), Integer.parseInt(nodoAConectarse.getPuerto()));
			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
			outToServer.write(msg);
			clientSocket.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}   
		 
		
		return true;
	}
	
	
	public boolean procesar(byte[] trama) {
		if(trama != null && trama.length > 0){
			long timeStamp = 0;
			int idCasa = 0;
			try{
				timeStamp = (((long) trama[0]) & 0xFF)
						+ ((((long) trama[1]) & 0xFF) << 8)
						+ ((((long) trama[2]) & 0xFF) << 16)
						+ ((((long) trama[3]) & 0xFF) << 24)
						+ ((((long) trama[4]) & 0xFF) << 32)
						+ ((((long) trama[5]) & 0xFF) << 40)
						+ ((((long) trama[6]) & 0xFF) << 48)
						+ ((((long) trama[7]) & 0xFF) << 56);
				
				idCasa = (((int) trama[8]) & 0xFF)
							+ ((((int) trama[9]) & 0xFF) << 8);
			
				Date date = new Date(timeStamp);
				
				// El sensor cambio de estado. Hay que validar
				System.out.println("Consumed: timeStamp("
						+ date + ") idCasa(" + idCasa
						+ ")" + " --" + new Date(System.currentTimeMillis()));
			}catch(Exception e){
				e.printStackTrace();
				System.out.println("Trama no procesada{casa:" + idCasa + "-timesstamp:" + timeStamp + "}{" + trama + "}");
			}
			
			return true;
		}
		return false;
	}

}
