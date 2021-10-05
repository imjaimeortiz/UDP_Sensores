package practica2;

import java.util.Scanner;

// Este hilo se utiliza para recibir lo que el usuario introduce por la consola
public class ReadCommands extends Thread {
	
	private Client client;
	
	public ReadCommands(Client client) {
		this.client = client;
	}
	
	@Override
	public void run() {
		Scanner sc = new Scanner(System.in);
		while (sc.hasNext()) {
			String line = sc.nextLine();
			client.processUnicast(line);
		}
	}
}
