import java.io.File;
import java.util.Scanner;

public class Servidor extends Thread
{

	Buffer buffer;
	Integer numemroAenviar;
	File f;

	public Servidor( Buffer pBuffer , int numeroDeClientesAEnviar)
	{
		buffer = pBuffer;
		numemroAenviar = numeroDeClientesAEnviar;
	}



	public void start()
	{

		System.out.println("******inicia servidor");
		//si lo hay lo trae	
		while(f==null)
		{
			seleccionarArchivo();
		}

		llenarBuffer();


	}
	void seleccionarArchivo()
	{
		Scanner s = new Scanner(System.in);
		System.out.println("ingrese el id de arcvhivo que desea enviar \n[1] texto\n[2] foto\n[3] video");


		int arch = s.nextInt();
		if(arch == 1)
		{
			f =new File("C:\\Users\\jobaj\\eclipse-workspace\\Caso1\\data\\dummy.txt"); 
		}
		else if(arch == 2)
		{
			f= new File("C:\\Users\\jobaj\\eclipse-workspace\\Caso1\\data\\1.jpg");
		}
		else if(arch == 3)
		{
			f =new File("C:\\Users\\jobaj\\eclipse-workspace\\Caso1\\data\\_War Thunder ESP_ Mig 21 + Mi 35 + Tunguska.mp4"); 
		}
	}

	public void llenarBuffer ()
	{	
		if(buffer.isVacio())
		{
			while (!buffer.isLleno())
			{
				buffer.almacenar(f);										
			}

		}
	}



}
