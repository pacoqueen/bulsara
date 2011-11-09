package bulsara;

import java.awt.AWTException;
import java.io.IOException;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Bulsara prueba = new Bulsara();
		Process p = null;
		
		if (! Bulsara.DEBUG){
			try {
				p = Runtime.getRuntime().exec("/usr/bin/gvim");
			} catch (IOException e1) {
				// Intento lanzar el notepad asumiendo que estoy en MS-Windows.
				try {
					p = Runtime.getRuntime().exec(System.getenv("windir") + "\notepad.exe");
				} catch (IOException e2){
					e2.printStackTrace();
					System.exit(1);
				}
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		try {
			prueba.teclear('i');
			prueba.enviar_tecla_especial("Enter");
			prueba.teclear('Q');
			prueba.enviar_tecla_especial("espacio");
			prueba.teclear('_');
			prueba.teclear("Pete Thownshed\nKeith Moon\nRoger Daltrey\nJohn Entwistle");
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (p != null)
			p.destroy();
	}

}
