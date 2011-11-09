/******************************************************************************
 * (c) 2010 Francisco José Rodríguez Bogado <frbogado@novaweb.es>
 * 
 * 
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 ******************************************************************************/

package bulsara;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

public class Bulsara {
	static boolean DEBUG = false;

	private class Entero{
		public int valor;
		public Entero(int valor){
			this.valor = valor;
		}
	} 
	
	private String getCadenaTecla(final char letra, Entero code_modifier) {
		/*
		 * Devuelve la cadena "released X" correspondiente a la letra recibida 
         * de la forma adecuada en que getKeyStroke la espera para 
         * convertirla en un KeyCode válido. Reserva y crea el objeto cadena.
		 * Solo devuelve KeyCodes para minúsculas.
		 */
		String cad = String.valueOf(letra);
		String res;
		cad = cad.toUpperCase();
		if (Character.isUpperCase(letra))
			code_modifier.valor = KeyEvent.VK_SHIFT;
		res = "pressed " + cad;
		if (DEBUG) System.out.println("(getCadenaTecla) res: " + res);
		if (DEBUG && code_modifier.valor != 0) 
			System.out.println("(getCadenaTecla) code_modifier: " + code_modifier.valor);
		return res;
	}

	void teclear(char letra) throws AWTException {
		Robot teclado = new Robot();
		KeyStroke teclazo;
		int code_letra;
		Entero code_modifier = new Entero(0);

		if (DEBUG)
			System.out.println("(teclear) letra: " + letra);
		teclazo = KeyStroke.getKeyStroke(getCadenaTecla(letra, code_modifier));
		if (DEBUG && code_modifier.valor != 0)
			System.out.println("(teclear) code_modifier: " + code_modifier.valor);
		if (DEBUG)
			System.out.println("(teclear) teclazo: " + teclazo);
		if (teclazo == null)	// Tecla no válida. Lleva modificadores o vete tú a saber.
			switch (letra){
				case ':': code_letra = KeyEvent.VK_COLON;
						  break;
				case ';': code_letra = KeyEvent.VK_SEMICOLON;
						  break;
				case ',': code_letra = KeyEvent.VK_COMMA;
				  		  break;
				case '.': code_letra = KeyEvent.VK_PERIOD;
						  break;
				case '_': // code_letra = KeyEvent.VK_UNDERSCORE;
						  code_letra = KeyEvent.VK_MINUS;	// Win32 no tiene UNDERSCORE.
						  code_modifier = new Entero(KeyEvent.VK_SHIFT);
				  		  break;
				default: code_letra = 0;	// Ya petará.
			}
		else
			code_letra = teclazo.getKeyCode();
		if (DEBUG)
			System.out.println("(teclear) code_letra: " + code_letra);
		else {
			if (code_modifier.valor != 0){
				teclado.keyPress(code_modifier.valor);}
			teclado.keyPress(code_letra);
			teclado.keyRelease(code_letra);
			if (code_modifier.valor != 0)
				teclado.keyRelease(code_modifier.valor);
		}
	}

	void teclear(String cadena) throws AWTException {
		/*
		 * Envía tecla a tecla la cadena recibida a la ventana que tenga el
		 * foco. Acepta \n como ENTER y \t como TAB.
		 */
		for (int i = 0; i < cadena.length(); i++) {
			boolean flag_especial = false; // true cuando sea un carácter escapado.
			char letra = cadena.charAt(i);
			if (letra == '\\') {
				// Me preparo para un carácter especial:
				try {
					char lookahead = cadena.charAt(i + 1);
					if (lookahead == 'n'){
						flag_especial = true;
						enviar_tecla_especial("ENTER");
						i += 1;	// Y consumo.
					}else if (lookahead == 't'){
						flag_especial = true;
						enviar_tecla_especial("TAB");
						i += 1;	// Y consumo.
					} else
						// No era carácter de escape. Envío la barra.
						flag_especial = false;
				} catch (IndexOutOfBoundsException e) {
					// EOS
					flag_especial = false;
				}
			}else if (letra == '\n'){
				flag_especial = true;
				enviar_tecla_especial("Enter");
			}else if (letra == '\t'){
				flag_especial = true;
				enviar_tecla_especial("TAB");
			}
			if (! flag_especial)
				try {
					if (letra == ' ')
						enviar_tecla_especial("espacio");
					else
						teclear(letra);
				} catch (AWTException e) {
					// La letra no la reconoce por lo que sea. La ignoro y saco el
					// warning.
					if (DEBUG) {
						System.err.println("teclear -> " + letra + "no reconocida.");
						e.printStackTrace();
					}
				}
		}
	}

	void enviar_tecla_especial(String nombre_tecla) throws AWTException {
		/*
		 * Recibe "ENTER", "ESPACIO" o "TAB" y lo envía como si fuera un evento
		 * de teclado.
		 */
		Robot teclado = new Robot();

		if (DEBUG) {
			System.out.println("(enviar_tecla_especial) nombre_tecla: " + nombre_tecla);
		}
		if (nombre_tecla.compareToIgnoreCase("ENTER")
				* nombre_tecla.compareToIgnoreCase("TAB") == 0) {
			KeyStroke tecla = KeyStroke.getKeyStroke(nombre_tecla.toUpperCase());
			if (!DEBUG){
				teclado.keyPress(tecla.getKeyCode());
				teclado.keyRelease(tecla.getKeyCode());
			}
		} else if (nombre_tecla.compareToIgnoreCase("ESPACIO") == 0) {
			if (!DEBUG){
				teclado.keyPress(KeyEvent.VK_SPACE);
				teclado.keyRelease(KeyEvent.VK_SPACE);
			}
		} else
			throw new AWTException(nombre_tecla + " no es una tecla válida.\n");
	}
}
