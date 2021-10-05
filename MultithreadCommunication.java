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
		 * Al ejecutarse se ver� el ID del servidor, el puerto por el que recibe e informaci�n sobre sus sensores.
		 * Para usarlo, hay que escribir el n�mero de ID del servidor y hay 3 funcionalidades :
		 * 		1 - detener el env�o de datos : ID stop
		 * 		2 - asignar nueva frecuencia de env�o : ID frecuencia
		 * 		3 - cambiar temperatura grados C�-K : ID kelvin/centigrades
		 * 		4 - cambiar velocidad del viento km/h-m/s : ID km/h/m/s
		 */

	}

}
