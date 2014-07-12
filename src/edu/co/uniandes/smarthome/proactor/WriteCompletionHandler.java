package edu.co.uniandes.smarthome.proactor;


import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;


/**
 * completion handler de escritura
 * clase usada por la aplicación para procesar eventos asíncronos de escritura y 
 * el evento finalización.
 * @author Administrador
 */
public class WriteCompletionHandler implements CompletionHandler<Integer, SessionState> {
    private AsynchronousSocketChannel socketChannel;
    
    public WriteCompletionHandler(AsynchronousSocketChannel socketChannel){
        this.socketChannel = socketChannel;
    }
    
    @Override
    public void completed(Integer bytesWritten, SessionState attachment){
         try {
             socketChannel.close();

         }catch (IOException e) {
             e.printStackTrace();

         }
    }
    
    @Override
    public void failed(Throwable exc, SessionState attachment) {
        // Handle write failure.....
    }

}
