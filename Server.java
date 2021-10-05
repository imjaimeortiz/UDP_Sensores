package practica2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class Server extends Thread {

	static public final String	IP_DEFAULT = "192.168.100.1";
	static public final String	BCAST_DEFAULT = "192.168.100.255";
	static public final int PORT_DEFAULT = 6666;
	private DatagramSocket socket;
	private boolean running;
	private int delay;
	private int id;
	private int nSensores;
	private int port;
	private InetAddress broadcastAddress;
	private boolean temp, wind;
	
	public Server() {
		this.temp = true;
		this.wind = true;
		this.id = (int)(Math.random()*90) + 10;
		this.delay = 1000;
		this.nSensores = (int)(Math.random()*6) + 1;
		this.port = (int)(Math.random()*60000) + 1024;
		try {
			this.broadcastAddress = InetAddress.getByName(findBroadcastAddress());
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			this.socket = new DatagramSocket(port);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		running = true;
		byte[] buf = new byte[256];
		// Se delega la funcionalidad del mensaje de BC a un hilo auxiliar para que el servidor pueda atender las peticiones unicast del cliente
		new ServerSender(this).start();
		while (running) {
			sendBroadcast();
			// Recepci�n paquete unicast
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			try {
				socket.receive(packet);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// Procesamiento en funci�n de qu� hemos recibido
			processReceived(new String(packet.getData(), 0, packet.getLength()));
		}
		socket.close();
	} 
	
	// M�todo de env�o broadcast
	public void sendBroadcast() {
		byte [] buf = getSensors(nSensores);
		DatagramPacket packet = new DatagramPacket(buf, buf.length, broadcastAddress, PORT_DEFAULT);
		try {
			socket.send(packet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// Procesamiento en funci�n de qu� operaci�n se va a realizar
	private void processReceived (String received) {
		if (received.equals("stop")) {
			running = false;
			socket.close();
		}	
		else if (received.equals("centigrades"))
			temp = true;
		else if (received.equals("kelvin"))
			temp = false;
		else if (received.equals("km/h"))
			wind = true;
		else if (received.equals("m/s"))
			wind = false;
		else {
			try { // Esto lo hacemos porque introducimos la frecuencia en Hz
				delay = 1000000/Integer.parseInt(received);
			} catch (Exception e) {
				System.out.println("<Servidor>	<" + received + ">	Operaci�n no soportada");
			}
		}
	}
	
	static protected String findBroadcastAddress () {
		int	count = 0;
		String ipBcastAddress = null;
		do {
			try {
				Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces ();
		        while (interfaces.hasMoreElements ()) {
		            NetworkInterface iface = interfaces.nextElement ();
		            // filters out 127.0.0.1 and inactive interfaces
		            if (iface.isLoopback () || !iface.isUp ())
		                continue;
	                for (InterfaceAddress address : iface.getInterfaceAddresses ()) {
	                	InetAddress bcast = address.getBroadcast ();
	                	if (bcast != null) {
	                		ipBcastAddress = bcast.getHostAddress ();
	                	}
	                }
		        }
		    } catch (SocketException e) { e.printStackTrace(); ipBcastAddress = IP_DEFAULT; }
		    
		    if (ipBcastAddress == null) {
		    	System.out.println ("--[UdpManager] No network interface ready yet. Waiting ... ");
		        try { Thread.sleep (5000); } catch (Exception e) { }
		        count ++;
		    }
		} while ((ipBcastAddress == null) && (count < 5));
	    if (ipBcastAddress == null)
	    	ipBcastAddress = BCAST_DEFAULT;
	    
	    return ipBcastAddress;
	}
	
	// Generaci�n de los nSensores de cada servidor de forma aleatoria
	private byte [] getSensors(int nSensores) {
		String sensores = "<ServidorId=" + this.id + "><PORT=" + port + ">";
		if (nSensores >= 1 && temp)
			sensores = sensores.concat("<Temperatura(C�)=" + (int)(Math.random()*50) + ">");
		else if (!temp)
			sensores = sensores.concat("<Temperatura(K)=" + (int)((Math.random()*50) + 273) + ">");
		if (nSensores >= 2 && wind)
			sensores = sensores.concat("<Viento(km/h)=" + (int)((Math.random()*120) + 5) + ">");
		else if (nSensores >= 2 && !wind) {
			double value = (Math.random()*120.0 + 5.0)/3.6;
			sensores = sensores.concat("<Viento(m/s)=" + String.format("%.2f", value) + ">");
		}
		else return sensores.getBytes();
		if (nSensores >= 3)
			sensores = sensores.concat("<Humedad(%)=" + (int)((Math.random()*70) + 20) + ">");
		else return sensores.getBytes();
		if (nSensores >= 4)
			sensores = sensores.concat("<PPM=" + (int)(Math.random()*10000) + ">");
		else return sensores.getBytes();
		if (nSensores >= 5)
			sensores = sensores.concat("<SO2(%)=" + (int)(Math.random()*100) + ">");
		else return sensores.getBytes();
		sensores = sensores.concat("<O3(%)=" + (int)(Math.random()*100) + ">");
		return sensores.getBytes();
	}
	
	public boolean isRunning() {
		return this.running;
	}
}