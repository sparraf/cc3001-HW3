import java.util.Scanner;

public class calculadorDerivadas {
	
	//Clase lista enlazada utilizada para construir pilas
	public class nodoLista {
		
		private nodoArbol val;
		private nodoLista sig;
		
		//Constructor
		public nodoLista(nodoArbol valor, nodoLista siguiente) {
			val = valor;
			sig = siguiente;
		}
	}
	
	//Clase pila para construir un arbol a partir del string de entrada
	public class Pila {
		
		private nodoLista lista;
		
		//Constructor
		public Pila() {
			lista = null;
		}
		
		//Metodo de instancia, agrega un elemento a la pila (no retorna nada)
		public void apilar(nodoArbol elemento) {
			lista = new nodoLista(elemento, lista);
		}
		
		//Metodo de instancia, remueve el elemento en el tope de la lista y
		//lo devuelve (retorna un char)
		public nodoArbol desapilar() {
			nodoArbol elemento = null;
			if(lista != null) {
				elemento = lista.val;
				lista = lista.sig;
				return elemento;
			}
			return elemento;
		}
	}
	
	//Clase arbol para guardar expresion
	public class nodoArbol {
		private char val;
		private nodoArbol izq;
		private nodoArbol der;
		
		//Constructor
		public nodoArbol(char valor, nodoArbol izquierdo, nodoArbol derecho) {
			val = valor;
			izq = izquierdo;
			der = derecho;
		}
		
		//Metodo de instancia, construye un arbol que contiene la expresion de la 
		//derivada del arbol referenciado con respecto a la variable especificada
		//(retorna un nodoArbol)
		public nodoArbol construirDerivada(char variable) {
			nodoArbol derivada = null;
			//Caso 1: Raiz contiene la variable con respecto a la cual se deriva
			if (val == variable) {
				derivada = new nodoArbol('1', null, null);
			}
			//Caso 2: Raiz contiene operacion
			else if (esOperacion(val)) {
				nodoArbol derivadaIzq = izq.construirDerivada(variable);
				nodoArbol derivadaDer = der.construirDerivada(variable);
				//Caso 2a: Raiz contiene operacion suma
				if (val == '+') {
					derivada = new nodoArbol('+', derivadaIzq, derivadaDer);
				}
				//Caso 2b: Raiz contiene operacion resta
				else if (val == '-') {
					derivada = new nodoArbol('-', derivadaIzq, derivadaDer);
				}
				//Caso 2c: Raiz contiene operacion producto
				else if (val == '*') {
					//Primer termino: Derivada de hijo izquierdo * hijo derecho
					nodoArbol terminoIzq = new nodoArbol('*', derivadaIzq, der);
					//Segundo termino: Hijo izquierdo * derivada hijo derecho
					nodoArbol terminoDer = new nodoArbol('*', izq, derivadaDer);
					//Sumar ambos terminos
					derivada = new nodoArbol('+', terminoIzq, terminoDer);
				}
				//Caso 2d: Raiz contiene operacion division
				else if (val == '/') {
					//Primer termino: Derivada de hijo izquierdo * hijo derecho
					nodoArbol terminoIzq = new nodoArbol('*', derivadaIzq, der);
					//Segundo termino: Hijo izquierdo * derivada hijo derecho
					nodoArbol terminoDer = new nodoArbol('*', izq, derivadaDer);
					//Restar ambos terminos
					nodoArbol resta = new nodoArbol('-', terminoIzq, terminoDer);
					//Denominador fraccion: hijo derecho * hijo derecho
					nodoArbol denominador = new nodoArbol('*', der, der);
					
					//Retornar fraccion
					derivada = new nodoArbol('/', resta, denominador);
				}
			}
			//Caso 3: Raiz contiene variable distinta a la cual con respecto se desea derivar
			else {
				derivada = new nodoArbol('0', null, null);
			}
		return derivada;
		}
		
		//Metodo de instancia, simplifica el arbol removiendo terminos redundantes
		//(no retorna nada)
		public void simplificar() {
			//Caso base: Si no es una operacion (variable o null), no hacer nada
			if (!esOperacion(val)) {
				return;
			}
			else {
				//Si no se cumple caso base, partir simplificando hijos 
				//recursivamente
				izq.simplificar();
				der.simplificar();
				
				//Asumiendo que los hijos fueron simplificados, simplificar raiz
				//Caso 1: Multiplicacion
				if (val == '*') {
					//Caso 1a: Multiplicacion por cero
					if (izq.val == '0' || der.val == '0') {
						val = '0';
						izq = null;
						der = null;
					}
					//Caso 1b: Multiplicacion por uno
					else if (izq.val == '1') {
						nodoArbol nuevoIzq = der.izq;
						nodoArbol nuevoDer = der.der;
						val = der.val;
						izq = nuevoIzq;
						der = nuevoDer;
					}
					else if (der.val == '1') {
						nodoArbol nuevoIzq = izq.izq;
						nodoArbol nuevoDer = izq.der;
						val = izq.val;
						izq = nuevoIzq;
						der = nuevoDer;
					}
				}
				//Caso 2: Suma por cero
				else if (val == '+') {
					if (izq.val == '0') {
						nodoArbol nuevoIzq = der.izq;
						nodoArbol nuevoDer = der.der;
						val = der.val;
						izq = nuevoIzq;
						der = nuevoDer;
					}
					else if (der.val == '0') {
						nodoArbol nuevoIzq = izq.izq;
						nodoArbol nuevoDer = izq.der;
						val = izq.val;
						izq = nuevoIzq;
						der = nuevoDer;
					}
				}
				//Caso 3: Resta por cero
				else if (val == '-' && der.val == '0') {
					nodoArbol nuevoIzq = izq.izq;
					nodoArbol nuevoDer = izq.der;
					val = izq.val;
					izq = nuevoIzq;
					der = nuevoDer;
				}
				//Caso 4: Division por uno
				else if (val == '/' && der.val == '1') {
					nodoArbol nuevoIzq = izq.izq;
					nodoArbol nuevoDer = izq.der;
					val = izq.val;
					izq = nuevoIzq;
					der = nuevoDer;
					
				}
			}
		}
		
		//Metodo de instancia, obtiene el string de la expresion guardada en el
		//arbol a partir de un objeto StringBuilder, agregando parentesis cuando
		//sea necesario (devuelve un String)
		public String stringExpresion(StringBuilder constructor) {
			//Primero se recorre el hijo izquierdo, si es que existe
			if (izq != null) {
				//Caso especial 1: Suma/resta como hijo izquierdo de una
				//multiplicacion o division
				if ((val == '*' || val == '/') && (izq.val == '+' || izq.val == '-')) {
					//Se abre parentesis, se obtiene el string del hijo izquierdo
					//y se cierra parentesis
					constructor.append('(');
					izq.stringExpresion(constructor);
					constructor.append(')');
				}
				else
					izq.stringExpresion(constructor);
			}
			//Luego se recorre la raiz
			constructor.append(val);
			//Finalmente se recorre el hijo derecho, si es que existe
			if (der != null) {
				//Caso especial 2: La raiz es una division y el hijo derecho
				//es una operacion cualquiera
				if (val == '/' && esOperacion(der.val)) {
					//Se abre parentesis, se obtiene el string del hijo derecho, 
					//y se cierra parentesis
					constructor.append('(');
					der.stringExpresion(constructor);
					constructor.append(')');
				}
				//Caso especial 3: La raiz es un producto y el hijo derecho es una 
				//suma o resta
				else if(val == '*' &&(der.val == '+' || der.val == '-')) {
					//Mismo procedimiento que caso especial 2
					constructor.append('(');
					der.stringExpresion(constructor);
					constructor.append(')');
				}
				else
					der.stringExpresion(constructor);
			}
			//Se retorna el string de todos los caracteres concatenados
			return constructor.toString();
		}
		
	}
	
	private Pila pilaExpresion;
	private nodoArbol arbolExpresion;
	private nodoArbol arbolDerivada;
	
	//Constructor
	public calculadorDerivadas() {
		pilaExpresion = new Pila();
		arbolExpresion = null;
		arbolDerivada = null;
	}
	
	//Metodo estático, determina si el char consultado es una operacion ('+', '-',
	//'*', o '/'), retornando True si lo es y False si no. (retorna un booleano)
	public static boolean esOperacion(char unCaracter) {
		if (unCaracter == '+' || unCaracter == '-' || unCaracter == '*' || unCaracter == '/')
			return true;
		else
			return false;
	}
	
	//Metodo de instancia, crea un arbol binario a partir de una expresion en
	//notacion polaca inversa guardada en un arreglo de Strings (no retorna nada)
	public void obtenerArbol(String[] arreglo) {
		//Recorrer el arreglo de strings
		int n = arreglo.length;
		for (int i=0; i < n; i++) {
			char actual = arreglo[i].charAt(0);
			//Caso 1: El caracter es una operacion +, -, *, /
			if (esOperacion(actual)) {
				//Se crea un arbol con la operacion en la raiz donde los hijos
				//son los dos elementos de mas arriba en la pila, los cuales son
				//retirados de esta
				nodoArbol nodoDerecho = pilaExpresion.desapilar();
				nodoArbol nodoIzquierdo = pilaExpresion.desapilar();
				arbolExpresion = new nodoArbol(actual, nodoIzquierdo, nodoDerecho);
				//Se agrega el nuevo arbol a la pila
				pilaExpresion.apilar(arbolExpresion);
			}
			//Caso 2: El caracter es una variable o un numero
			else {
				//Se crea un arbol con la variable en la raiz y sin hijos
				arbolExpresion = new nodoArbol(actual, null, null);
				//Se agrega el arbol a la pila
				pilaExpresion.apilar(arbolExpresion);
			}
		}
		//Al final del algoritmo, solo debiese quedar el arbol armado en la pila
		arbolExpresion = pilaExpresion.desapilar();
	}
	
	//Metodo de instancia, obtiene el arbol de la derivada de arbolExpresion con
	//respecto a la variable especificada y lo guarda en arbolDerivada
	public void obtenerDerivada(char variable) {
		arbolDerivada = arbolExpresion.construirDerivada(variable);
	}
	
	//Metodo de instancia, simplifica el arbol de la derivada eliminando
	//expresiones redundantes (no retorna nada)
	public void simplificarDerivada() {
		arbolDerivada.simplificar();
	}
	
	//Metodo de instancia, imprime la derivada guardada en el arbol binario
	//introduciendo parentesis donde sea necesario (no devuelve nada)
	public void imprimirDerivada() {
		StringBuilder constructorExpresion = new StringBuilder();
		String derivada = arbolDerivada.stringExpresion(constructorExpresion);
		System.out.println(derivada);
	}

	public static void main(String[] args) {
		//Se pide al usuario ingresar una entrada
		System.out.println("Ingrese una expresion en polaca inversa");
		Scanner entrada = new Scanner(System.in);
		
		//Se guarda la expresion en polaca inversa
		String expresion = entrada.nextLine();
		
		//Se pide al usuario la variable con respecto a la cual derivar y se guarda
		System.out.println("Ingrese la variable con respecto a la cual derivar");
		String variable = entrada.nextLine();
		
		//Obtener un arreglo con cada caracter
		String[] caracteres = expresion.split(" ");
		
		//Crear calculador de derivadas
		calculadorDerivadas calculador = new calculadorDerivadas();
		
		//Obtener arbol binario de la expresion
		calculador.obtenerArbol(caracteres);
		
		//Obtener arbol de la derivada con respecto a la variable especificada
		calculador.obtenerDerivada(variable.charAt(0));
		
		//Simplificar la derivada, removiendo expresiones redundantes
		calculador.simplificarDerivada();
		
		//Imprimir salida
		calculador.imprimirDerivada();
	}

}
