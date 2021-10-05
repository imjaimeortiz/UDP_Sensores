package practica2;


public class MultithreadCommunication {
	
	public static void main(String[] args) {
	
		Server s1 = new Server();
		Server s2 = new Server();
		Server s3 = new Server();
		Client c1 = new Client();
		
		s1.start();
		s2.start();
		s3.start();
		c1.start();
		
		/*
		 * USO :
		 * Al ejecutarse se verá el ID del servidor, el puerto por el que recibe e información sobre sus sensores.
		 * Para usarlo, hay que escribir el número de ID del servidor y hay 3 funcionalidades :
		 * 		1 - detener el envío de datos : ID stop
		 * 		2 - asignar nueva frecuencia de envío : ID frecuencia
		 * 		3 - cambiar temperatura grados Cº-K : ID kelvin/centigrades
		 * 		4 - cambiar velocidad del viento km/h-m/s : ID km/h/m/s
		 */

	}

}
