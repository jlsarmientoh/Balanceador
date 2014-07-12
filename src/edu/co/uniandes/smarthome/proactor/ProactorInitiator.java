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
 * Inicia la operación asincrónica aceptando conexiones de clientes. 
 * Este suele ser el subproceso principal de la aplicación de servidor. 
 * Registra un Completion Handler junto con un Completion Dispatcher 
 * para manejar la aceptación conexión de notificación de eventos asíncronos.
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
        //Abre un canal de servidor-socket asincrónico. y
        //Enlaza socket del canal a una dirección local y configura el socket para escuchar las conexiones 
        final AsynchronousServerSocketChannel listener =
                AsynchronousServerSocketChannel.open().bind(new InetSocketAddress("192.168.0.100" , port));
        // instancimos el completion handler que va escuchar por socket creado
        AcceptCompletionHandler acceptCompletionHandler = new AcceptCompletionHandler(listener);
        SessionState state = new SessionState();
        
        /*
         * Este método inicia una operación asincrónica para aceptar una conexión 
         * realizada al socket asociado al canal. 
         * El manejador del  parámetro es un completion handler que se invoca 
         * cuando se acepta una conexión (o la operación falla). El resultado pasado al
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

