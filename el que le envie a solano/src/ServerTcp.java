import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

import org.json.simple.JSONValue;

public class ServerTcp {

	protected static int numConectados = 0;
	protected static int numParaEnviar =0;
	protected static Buffer buffer;
	protected static Object espera;
	protected static ArrayList<Thread> bufferThreads;



	public static void main(String[] args) 
	{
		ServerTcp main = new ServerTcp();
		espera = new Object();
		Scanner s = new Scanner(System.in);
		bufferThreads = new ArrayList<>();

		System.out.println("ingrese el numero clientes");


		numParaEnviar = s.nextInt();
		buffer = new Buffer(numParaEnviar);
		int servidores = 1;

		System.out.println("numero de clientes conectados para transmitir "+numParaEnviar);

		System.out.println("numero de servidores "+servidores);



		Servidor servidoreI = new Servidor( buffer, numParaEnviar );
		servidoreI.start();	
		System.out.println("El server fue desplegado");
		// Abre un server socket para servir a los clientes por el puerto 4444	
		try (ServerSocket server = new ServerSocket(4446)) {
			// En un loop infinito se mantiene aceptando clientes
			while(true){

				Socket socket = server.accept();
				socket.setSoLinger(true, 10);
				numConectados++;			

				// Comienza una nueva thread para el cliente
				Thread t = new Thread(() -> {
					try {
						main.serveClient(socket);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
				bufferThreads.add(t);
				if(numConectados==numParaEnviar)
				{
					while (!bufferThreads.isEmpty())
					{				

						Thread thread = bufferThreads.get(0);
						thread.start();
						bufferThreads.remove(thread);
					}
					servidoreI.llenarBuffer();
					numConectados=0;
				}

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	/**
	 * Serves a single client - identified by the socket.
	 * @param socket
	 * @return 
	 * @throws InterruptedException 
	 */
	private  void serveClient(Socket socket) throws InterruptedException {
		try (Socket clientSocket = socket) {
			// Get the in/out streams of the sockets
			DataInputStream in = new DataInputStream(clientSocket.getInputStream());
			DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream()); 
			String message = in.readUTF();
			if(message.equals("R"))
			{
				File file = buffer.retirar();
				byte[] sendData = new byte[(int)file.length()];
				FileInputStream fileReader = new FileInputStream(file);					
				fileReader.read(sendData);
				//Creo un mensaje Json para enviar atributos del archivo el nombre y el tamano, esto me servira 
				//del lado del servidor
				Map<String, String> jsonObj = new LinkedHashMap<String,String >();
				jsonObj.put("fileLength", String.valueOf(file.length()));
				jsonObj.put("fileName", file.getName()); 
				int a= (int) (file.length()/13);
				jsonObj.put("hashCode", ""+a);

				out.writeUTF(JSONValue.toJSONString(jsonObj));    

				// Envio el arreglo de bytes al cliente
				out.write(sendData, 0, sendData.length);	
				BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
				Long inicio = System.currentTimeMillis();
				BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
				Long fin = System.currentTimeMillis();
				System.out.println("se demoro en milesimas :"+(fin-inicio));

				message = in.readUTF();
				if(message.equals("R"))
				{
					System.out.println("El cliente confirma el correcto envio del archivo.");
				}
				else {
					System.out.println("Me muero. Me desmayo. CALLESE TCP lesbiano.");
				}

				bis.close();
				bos.close();		
			}
			out.flush();
			in.close();

		}		
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
