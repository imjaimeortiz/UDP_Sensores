package practica2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;

public class Client extends Thread {

	private static final int PORT_DEFAULT = 6666;
	private DatagramSocket socket;
	private InetAddress serverAddress;
	private boolean running;
	private HashMap<Integer, Integer> servidores;
		
	public Client() {
		try {
			this.socket = new DatagramSocket(PORT_DEFAULT);
			new ReadCommands(this).start();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.servidores = new HashMap<Integer, Integer>();
	}
	
	// Se recibe lo que el servidor envía y se guardan los datos de los servidores para el posterior envío
	@Override
	public void run() {
		running = true;
		while (running) {
			byte[] buf = new byte[256];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			try {
				socket.receive(packet);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String received = new String(packet.getData(), 0, packet.getLength());
			System.out.println(received);
			serverAddress = packet.getAddress();
			this.saveServer(received);
		}
		socket.close();
	}
	
	// Almacena en un mapa la tupla id,puerto de cada servidor
	private void saveServer(String received) {
		String id = received.substring(12, 14);
		String [] lines = received.split("><PORT=");
		String [] lines2 = lines[1].split("><");
		this.servidores.put(Integer.parseInt(id), Integer.parseInt(lines2[0]));
	}
	
	// Procesamiento anterior al envío de la trama unicast en función de los distintos mensajes que puede enviar el emisor
	public void processUnicast(String line) {
		String [] lines = line.split(" ");
		int id = 0;
		try {
			id = Integer.parseInt(lines[0]);
		} catch (Exception e) {
			System.out.println("<Cliente> Operación no soportada");
			System.exit(0);
		}
		String op = lines[1];
		// Esto no tiene gran funcionalidad, es para dar información al usuario sobre la aplicación
		if (servidores.keySet().contains(id)) {
			if (op.equals("stop"))
				System.out.println("<Cliente><" + "Deteniendo el servidor " + id + ">");
			else if (op.equals("centigrades"))
				System.out.println("<Cliente><" + "Estableciendo la temperatura del servidor " + id + " a grados centígrados>");
			else if (op.equals("kelvin"))
				System.out.println("<Cliente><" + "Estableciendo la temperatura del servidor " + id + " a grados kelvin>");
			else if (op.equals("km/h"))
				System.out.println("<Cliente><" + "Estableciendo la velocidad del viento del servidor " + id + " a km/h>");
			else if (op.equals("m/s"))
				System.out.println("<Cliente><" + "Estableciendo la velocidade del viento del servidor " + id + " a m/s>");
			else if (isNumeric(op))
				System.out.println("<Cliente><" + "Cambiando a "+ op +"Hz la frecuencia de envío del servidor " + id + ">");
			
			this.send(id, op.getBytes());
		}
			else
			System.out.println("<Cliente>No se pudo conectar con el ServidorId=" + id);
	}
	
	// Envío de la trama unicast
	private void send(int id, byte[] buf) {
		DatagramPacket packet = new DatagramPacket(buf, buf.length, serverAddress, servidores.get(id));
		try {
			socket.send(packet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private boolean isNumeric(String cadena) {
        boolean resultado;
        try {
            Integer.parseInt(cadena);
            resultado = true;
        } catch (NumberFormatException excepcion) {
            resultado = false;
        }
        return resultado;
    }
}
