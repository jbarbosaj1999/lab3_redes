import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class Buffer 
{
	private int numAdmitidos;

	private static ArrayList<File> buff;

	private Object lleno;
	private Object vacio;


	public Buffer (int pAdmitidos)
	{
		numAdmitidos = pAdmitidos;
		buff= new ArrayList<File>();
		//		File f = new File("C:\\Users\\jobaj\\eclipse-workspace\\Caso1\\data\\dummy.txt"); 
		//		buff.add(f);

		lleno = new Object();
		vacio = new Object();
	}


	public  void almacenar ( File i )
	{		
		synchronized( lleno )
		{
			while ( buff.size( ) == numAdmitidos)
			{ 
				try { lleno.wait( ); 

				}
				catch( InterruptedException e )
				{
					e.printStackTrace();
				}
			}
		}
		synchronized( this )
		{
			buff.add( i );
			i.getAbsoluteFile();
			//			System.out.println("el mensaje almacenado fue: "+i);
		}
		synchronized( vacio )
		{ 
			vacio.notify(); 
		}
	}

	public boolean isVacio()
	{
		return buff.size( ) ==0;
	}

	public boolean isLleno()
	{
		return buff.size( ) == numAdmitidos;
	}

	public  File retirar ()
	{
		synchronized( vacio )
		{
			while ( buff.size( ) == 0 ){ 
				try { vacio.wait( ); }
				catch( InterruptedException e ){}
			}
		}
		File i;
		synchronized( this ){

			i = (File)buff.remove(0); 
		}
		synchronized( lleno ){ lleno.notify( ); }
		return i;
	}
}
