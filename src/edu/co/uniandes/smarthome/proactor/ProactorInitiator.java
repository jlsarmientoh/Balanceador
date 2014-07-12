package edu.co.uniandes.smarthome.proactor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.Properties;

import edu.co.uniandes.smarthome.balanceador.Nodo;

/**
 * Inicia la operaci�n asincr�nica aceptando conexiones de clientes. 
 * Este suele ser el subproceso principal de la aplicaci�n de servidor. 
 * Registra un Completion Handler junto con un Completion Dispatcher 
 * para manejar la aceptaci�n conexi�n de notificaci�n de eventos as�ncronos.
 * @author Administrador
 */
public class ProactorInitiator {

    static int ASYNC_SERVER_PORT = 4333;

    public void initiateProactiveServer(int port) throws IOException {
    	FileInputStream fis;
    	try{
	    	fis = new FileInputStream( new File( "./config/nodos.properties" ) );
			Properties properties = new Properties( );
	        properties.load( fis );
	        ASYNC_SERVER_PORT = Integer.parseInt(properties.getProperty("balanceador.puerto"));
	        System.out.println("Async server listening on port : " + ASYNC_SERVER_PORT);
    	}catch(Exception e){
    		System.out.println("No se cargaron las propiedades, se usa el puerto por defecto");
    	}
        //Abre un canal de servidor-socket asincr�nico. y
        //Enlaza socket del canal a una direcci�n local y configura el socket para escuchar las conexiones 
        final AsynchronousServerSocketChannel listener =
                AsynchronousServerSocketChannel.open().bind(new InetSocketAddress("192.168.0.100" , port));
        // instancimos el completion handler que va escuchar por socket creado
        AcceptCompletionHandler acceptCompletionHandler = new AcceptCompletionHandler(listener);
        SessionState state = new SessionState();
        
        /*
         * Este m�todo inicia una operaci�n asincr�nica para aceptar una conexi�n 
         * realizada al socket asociado al canal. 
         * El manejador del  par�metro es un completion handler que se invoca 
         * cuando se acepta una conexi�n (o la operaci�n falla). El resultado pasado al
         * completion handler es el AsynchronousSocketChannel.
         */
        listener.accept(state, acceptCompletionHandler);

    }

    public static void main(String[] args) {
    	
        try {
            //System.out.println("Async server listening on port : " + ASYNC_SERVER_PORT);
            
            new ProactorInitiator().initiateProactiveServer(ASYNC_SERVER_PORT);
          
//            Lectura de valores archivo de propiedas
        } catch (IOException e) {
            e.printStackTrace();
        }

        // definimos un ciclo infinito para que la JVM no termine el proceso
        while (true) {
            try {
                Thread.sleep(Long.MAX_VALUE);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
}

