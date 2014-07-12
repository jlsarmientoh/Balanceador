package edu.co.uniandes.smarthome.proactor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;

import edu.co.uniandes.smarthome.balanceador.Balanceador;
import edu.co.uniandes.smarthome.balanceador.Nodo;

/**
 * completion handler de aceptacion 
 * clase usada por la aplicación para procesar la aceptacion de la coneccion y
 * eventos de finalización ambos asincronos.
 * @author Administrador
 */

public class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, SessionState> {

    private AsynchronousServerSocketChannel listener;
    private Balanceador balanceador;

    public AcceptCompletionHandler(AsynchronousServerSocketChannel listener) {
        this.listener = listener;
        FileInputStream fis;
		try {
			fis = new FileInputStream( new File( "./config/nodos.properties" ) );
			Properties properties = new Properties( );
	        properties.load( fis );
	        String sizePropertie = "total.nodos";
	        String sizeValue = properties.getProperty(sizePropertie);
	        int size = Integer.parseInt(sizeValue);
	        Nodo[] nodos = new Nodo[size];
	        for (int i=1; i<= size; ++i) {
	        	String ipPropertie = "nodo" + i + ".ip";
	        	String ip = properties.getProperty(ipPropertie);
	        	String puertoPropertie = "nodo" + i + ".puerto";
	        	String puerto = properties.getProperty(puertoPropertie);
	        	nodos[i-1] = new Nodo(ip, puerto);
	        }
	        fis.close( );
	        this.balanceador = new Balanceador(new ArrayBlockingQueue<byte[]>(110) , nodos);
			new Thread(balanceador).start();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
    }

    //Se invoca cuando una operación se ha completado
    @Override
    public void completed(AsynchronousSocketChannel result, SessionState attachment) {
        // aceptar la conexión siguiente
        SessionState newSessionState = new SessionState();
        listener.accept(newSessionState, this);
        // Maneja la conexion
        ByteBuffer inputBuffer = ByteBuffer.allocate(2048);
        ReadCompletionHandler readCompletionHandler = new ReadCompletionHandler(result, inputBuffer, balanceador);
        result.read(inputBuffer, attachment, readCompletionHandler);
        
    }

    //Se invoca cuando falla una operación.
    @Override
    public void failed(Throwable exc, SessionState attachment) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

