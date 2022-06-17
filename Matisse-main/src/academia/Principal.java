package academia;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.matisse.MtDatabase;
import com.matisse.MtException;
import com.matisse.MtObjectIterator;

public class Principal {
	public static void main(String[] args) {
		creaObjetos("localhost","academia");
		//borrarTodos("localhost","academia");
		modificaObjeto("localhost", "academia", "Maria José", "María José");
		ejecutaOQL("localhost", "academia");
	}

	public static void creaObjetos(String hostname, String dbname) {
		try {
			// Abre la base de datos con el hostname (localhost), y el nombre de la base de datos dbname (academia).
			MtDatabase db = new MtDatabase(hostname, dbname);
			db.open();
			db.startTransaction();
			System.out.println("Conectado a la base de datos " +
					db.getName() + " de Matisse");
			// Crea un objeto Profesor
			profesores a1 = new profesores(db);
			a1.setNombre("Maria José");
			a1.setApellidos("Martínez");
			a1.setTelefono("532323232");
			a1.setDni("12345678P");
			System.out.println("Objeto de tipo profesores creado.");
			// Crea un objeto clases
			clases c1 = new clases(db);
			c1.setAula(4);
			c1.setNombre("Acceso a Datos");
			c1.setDuracion("1 hora");
			c1.setHoraInicio("8 de la mañana");
			// Crea un objeto clases
			clases c2 = new clases(db);
			c2.setAula(4);
			c2.setNombre("Diseño de Interfaces");
			c2.setDuracion("1 hora");
			c2.setHoraInicio("11 de la mañana");

			// Crea un array de clases y hacer las relaciones
			clases lista[]= new clases[2];
			lista[0]=c1;
			lista[1]=c2;
			// Guarda las relaciones del profesor que ha impartido clases.
			a1.setImparten(lista);
			// Ejecuta un commit para materializar las peticiones.
			db.commit();
			// Cierra la base de datos.
			db.close();
			System.out.println("\nHecho.");
		} catch (MtException mte) {
			System.out.println("MtException : " + mte.getMessage());
		}
	}

	// Borrar todos los objetos de una clase
	public static void borrarTodos(String hostname, String dbname) {
		System.out.println("====================== Borrar Todos=====================\n");
		try {
			MtDatabase db = new MtDatabase(hostname, dbname);
			db.open();
			db.startTransaction();
			System.out.println("Conectado a la base de datos " +
					db.getName() + " de Matisse.");
			/* El método getInstanceNumber(db) cuenta el número de objetos del tipo de la clase
			 * con la que lo llamemos que en este caso es Obra */
			System.out.println("\n" + clases.getInstanceNumber(db) + " objetos de tipo clases tenemos en la DB.");
			// Borra todas las instancias de Obra
			clases.getClass(db).removeAllInstances();
			// Materializa los cambios y cierra la BD
			db.commit();
			db.close();
			System.out.println("\nTodos los objetos de tipo clases eliminados correctamente de la base de datos.");
		} catch (MtException mte) {
			System.out.println("MtException : " + mte.getMessage());
		}
	}
	public static void modificaObjeto(String hostname, String dbname, String nombre, String nuevoNombre) {
		System.out.println("=========== Modifica un objeto==========\n");
				int nProfesores = 0;
				try {
					MtDatabase db = new MtDatabase(hostname, dbname);
					db.open();
					db.startTransaction();
					System.out.println("Conectado a la base de datos " +
							db.getName() + " de Matisse.");
					/*
					 * El método getInstanceNumber(db) cuenta el número de objetos del tipo de la
					 * clase con la que lo llamemos que en este caso es profesores.
					 */
					System.out.println("\n" + profesores.getInstanceNumber(db) + "objetos de tipo Autor tenemos en la DB.");
							nProfesores = (int) profesores.getInstanceNumber(db);
					// Crea un Iterador (propio de Java)
					MtObjectIterator<profesores> iter =
							profesores.<profesores>instanceIterator(db);
					System.out.println("\nRecorro el iterador de uno en uno y cambio cuando encuentro 'nombre'");
							while (iter.hasNext()) {
								profesores[] profesores = iter.next(nProfesores);
								for (int i = 0; i < profesores.length; i++) {
									/* Si el nombre del Autor coincide con el
				parámetro nombre pasado al método, le establecemos
									 * el pa´rametro edad que le pasamos al
				método.
									 */
									if (profesores[i].getNombre().compareTo(nombre)
											== 0) {
										profesores[i].setNombre(nuevoNombre);
									} else {
										System.out.println("No se ha encontrado ningún profesor de nombre " + nombre + " en la base de datos " +
												db.getName() + ".");
									}
								}
							}
							iter.close();
							// Materializa los cambios y cierra la BD
							db.commit();
							db.close();
							System.out.println("\nLa modificación del objeto,finalizada correctamrnte.");
				} catch (MtException mte) {
					System.out.println("MtException : " + mte.getMessage());
				}
	}

	public static void ejecutaOQL(String hostname, String dbname) {
		MtDatabase dbcon = new MtDatabase(hostname, dbname);
		// Abre una conexión a la base de datos
		dbcon.open();
		try {
			// Crea una instancia de Statement
			Statement stmt = dbcon.createStatement();
			/*
			 * Asigna una consulta OQL. Esta consulta lo que hace es
			utilizar REF() para
			 * obtener el objeto directamente.
			 * biblioteca2020.Autor es el mapeo a la clase Autor. Es
			decir biblioteca2020 es el paquete
			 * en el que tenemos la clase Autor.
			 */
			String commandText = "SELECT REF(a) from academia.profesores a;";
			/*
			 * Ejecuta la consulta y obtiene un ResultSet que
			contendrá las referencias a
			 * los objetos que en este caso serán de tipo Autor.
			 */
			ResultSet rset = stmt.executeQuery(commandText);
			/*
			 * Creamos una referencia a un objeto de tipo Autor donde
			almacenaremos los
			 * objetos devueltos en el ResultSet.
			 */
			profesores a1;
			// Recorremos el ResultSet.
			while (rset.next()) {
				/*
				 * Con el método getObject() recuperamos cada
			objeto del ResultSet y lo
				 * almacenamos en a1. El casteo es necesario porque
			el método getObject devuelve
				 * un tipo Object.
				 */
				a1 = (profesores) rset.getObject(1);
				/*
				 * Una vez el objeto es referenciado en a1, ya se
			pueden recuperar de él los
				 * valores de sus atributos.
				 */
				System.out.println("Los valores de los atributos del objeto de tipo profesor son: " + a1.getNombre() + " "
						+ a1.getApellidos() + " " +a1.getDni() + 
						a1.getTelefono() + ".");
			}
			/* Cierra las conexiones. Solamente debemos cerrar el
			ResultSet y el Statement, no el MtDatabase
			porque lanza una excepción de que no conoce la fuente.*/
			rset.close();
			stmt.close();
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
		}
	}
}

