package edu.co.uniandes.smarthome.proactor;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import edu.co.uniandes.smarthome.balanceador.Balanceador;

/**
 * completion handler de lectura clase usada por la aplicación para procesar
 * eventos asíncronos de lectura y la finalización.
 * 
 * @author Administrador
 */
public class ReadCompletionHandler implements
		CompletionHandler<Integer, SessionState> {

	private AsynchronousSocketChannel socketChannel;
	private ByteBuffer inputBuffer;
	private Balanceador balanceador;

	public ReadCompletionHandler(AsynchronousSocketChannel socketChannel,
			ByteBuffer inputBuffer, Balanceador balanceador) {
		this.socketChannel = socketChannel;
		this.inputBuffer = inputBuffer;
		this.balanceador = balanceador;

	}

	@Override
	public void completed(Integer bytesRead, SessionState sessionState) {
		if (bytesRead <= 0) {
			return;
		}

		byte[] buffer = new byte[bytesRead];

		inputBuffer.rewind();

		// rebobinamos la memoria de entrada para leer desde el principio

		inputBuffer.get(buffer);
		// String message = new String(buffer);
		// System.out.println("Recibido el mensaje desde el cliente : " +
		// message);

		try {
			balanceador.getQueue().put(buffer);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// the message back to client

		// WriteCompletionHandler writeCompletionHandler = new
		// WriteCompletionHandler(socketChannel);
		//
		// ByteBuffer outputBuffer = ByteBuffer.wrap(buffer);
		//
		// socketChannel.write(outputBuffer, sessionState,
		// writeCompletionHandler);

	}

	@Override
	public void failed(Throwable exc, SessionState attachment) {
		// Handle read failure.....
	}
}
