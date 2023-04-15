/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clases;

import jSudoku.GestorJuegos;
import jSudoku.Sudoku;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 *
 * @author nekro
 */
public class CalculadoraServidor implements GestorJuegos, Serializable {

    private ArrayList<Sudoku> arraySudokus;
    private int numeroSudokus;
    private Sudoku.dificultad dificultad;

    private static final int LONGITUD_CODIGO = 6;
    private static final String CARACTERES_VALIDOS = "0123456789";

    public static int generarCodigo() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < LONGITUD_CODIGO; i++) {
            int indiceAleatorio = random.nextInt(CARACTERES_VALIDOS.length());
            char caracterAleatorio = CARACTERES_VALIDOS.charAt(indiceAleatorio);
            sb.append(caracterAleatorio);
        }
        String codigoStr = sb.toString();
        int codigoInt = Integer.parseInt(codigoStr);
        return codigoInt;
    }

    private void Inicializar(Sudoku.dificultad pDifi, int idSudoku) throws RemoteException {

        this.dificultad = pDifi;
        if (arraySudokus.size() <= idSudoku) {
            for (int i = arraySudokus.size(); i <= idSudoku; i++) {
                arraySudokus.add(new Sudoku(this.dificultad, idSudoku, i));
            }

        }
        for (int f = 0; f < 9; f++) {
            for (int c = 0; c < 9; c++) {
                arraySudokus.get(idSudoku).setValorEnTablero(f, c, ' ');
            }
        }
    }

    public ArrayList<Sudoku> getArraySudoku() {
        System.out.println("Longitud array = " + arraySudokus.size());
        return arraySudokus;
    }

    private int SearchPorFila(int idSudokus, int pFil, char pVal) throws RemoteException {
        int Cuantos = 0;
        char aux;
        for (int c = 0; c < 9 && Cuantos < 2; c++) {
            aux = arraySudokus.get(idSudokus).getValorEnTablero(pFil, c);
            if (aux == pVal) {
                Cuantos++;
            }
        }
        return Cuantos;
    }

    private int BuscarPorColumna(int idSudokus, int pCol, char pVal) throws RemoteException {
        int Cuantos = 0;
        char aux;
        for (int c = 0; c < 9 && Cuantos < 2; c++) {
            aux = arraySudokus.get(idSudokus).getValorEnTablero(c, pCol);
            if (aux == pVal) {
                Cuantos++;
            }
        }
        return Cuantos;
    }

    private int BuscarPorBloque(int idSudoku, int pFil, int pCol, char pVal) throws RemoteException {
        int minF = (pFil / 3) * 3;
        int minC = (pCol / 3) * 3;
        int maxF = minF + 3;
        int maxC = minC + 3;
        char aux;
        int Cuantos = 0;
        for (int f = minF; f < maxF && Cuantos < 2; f++) {
            for (int c = minC; c < maxC && Cuantos < 2; c++) {
                aux = arraySudokus.get(idSudoku).getValorEnTablero(f, c);

                if (aux == pVal) {
                    Cuantos++;
                }
            }
        }
        return Cuantos;
    }

    @Override
    public int NuevoJuego(Sudoku.dificultad pDifi) throws RemoteException {

        int f, c;
        char Valor;

        if (arraySudokus == null) {
            arraySudokus = new ArrayList<>();
        }

        numeroSudokus++;
        int codigoSudokuCreado = generarCodigo();// El codigo del sudoku será el creado por la funcion.

        Inicializar(pDifi, codigoSudokuCreado);
        char[] ValoresActuales = new char[81];
        Random random = new Random(System.currentTimeMillis());

        for (int i = 0; i < 81; i++) // 81--> 9x9 del tablero
        {
            ValoresActuales[i] = ' '; // Inicializa a 0 cada posicion del tablero
        }

        int Pos;
        for (char v = '1'; v <= '9'; v++) {

            Pos = (random.nextInt() % 9 + 9) % 9;
            while (ValoresActuales[Pos] != ' ') {
                Pos++;
                if (Pos == 9) {
                    Pos = 0;
                }
            }
            ValoresActuales[Pos] = v;
            PonerValor(codigoSudokuCreado, 0, Pos, v);
        }

        Pos = 9;
        while (Pos < 81) {
            f = Pos / 9;
            c = Pos % 9;
            Valor = ValoresActuales[Pos] != ' ' ? ValoresActuales[Pos] : '0';

            boolean EsCorrecto = false;
            while (EsCorrecto == false && Valor < '9') {
                Valor++;
                PonerValor(codigoSudokuCreado, f, c, Valor);
                EsCorrecto = ComprobarValor(codigoSudokuCreado, f, c, Valor);
            }

            if (EsCorrecto == true) {
                ValoresActuales[Pos] = Valor;
                Pos++;
            } else {
                ValoresActuales[Pos] = ' ';
                PonerValor(codigoSudokuCreado, f, c, ' ');
                Pos--;
            }
        }

        int NHuecos;
        switch (pDifi) {
            case MUYFACIL:
                NHuecos = 10;
                break;
            case FACIL:
                NHuecos = 30;
                break;
            case DIFICIL:
                NHuecos = 60;
                break;
            case MUYDIFICIL:
                NHuecos = 70;
                break;
            default:
                NHuecos = 40;
        }

        for (int i = 0; i < NHuecos; i++) {
            do {
                f = (random.nextInt() % 9 + 9) % 9;
                c = (random.nextInt() % 9 + 9) % 9;
            } while (ObtenerValor(codigoSudokuCreado, f, c) == ' ');
            PonerValor(codigoSudokuCreado, f, c, ' ');
        }

        return codigoSudokuCreado;

    }

    @Override
    public boolean BorrarJuego(int idSudoku) throws RemoteException {
        Inicializar(Sudoku.dificultad.VACIO, idSudoku);
        return true;
    }

    @Override
    public boolean PonerValor(int pCodJuego, int pFila, int pColumna, char pValor) throws RemoteException {
        arraySudokus.get(pCodJuego).setValorEnTablero(pFila, pColumna, pValor);
        return ComprobarValor(pCodJuego, pFila, pColumna, pValor);
    }

    @Override
    public char ObtenerValor(int pCodJuego, int pFila, int pColumna) throws RemoteException {
        return arraySudokus.get(pCodJuego).getValorEnTablero(pFila, pColumna);
    }

    @Override
    public boolean ComprobarValor(int pCodJuego, int pFila, int pColumna, char pValor) throws RemoteException {
        boolean Salida = true;
        if (SearchPorFila(pCodJuego, pFila, pValor) != 1
                || BuscarPorColumna(pCodJuego, pColumna, pValor) != 1
                || BuscarPorBloque(pCodJuego, pFila, pColumna, pValor) != 1) {
            Salida = false;
        }
        return Salida;
    }

    @Override
    public int NumeroHuecos(int pCodJuego) throws RemoteException {
        int Cuantos = 0;
        for (int f = 0; f < 9; f++) {
            for (int c = 0; c < 9; c++) {
                if (arraySudokus.get(pCodJuego).getValorEnTablero(f, c) == ' ') {
                    Cuantos++;
                }
            }
        }
        return Cuantos;
    }

    @Override
    public String Ayuda(int pCodJuego, int fila, int columna) throws RemoteException {
        // Primero comprobamos si la posición ya está ocupada
        char[][] tablero = arraySudokus.get(pCodJuego).getTablero();
        if (tablero[fila][columna] != ' ') {
            return "La posición [" + fila + "][" + columna + "] ya está ocupada con el número " + tablero[fila][columna];
        }

        // Creamos un conjunto para almacenar los números que ya están en la misma fila, columna y subcuadrícula
        Set<Character> numerosEnFila = new HashSet<>();
        Set<Character> numerosEnColumna = new HashSet<>();
        Set<Character> numerosEnSubcuadricula = new HashSet<>();

        int tamSubcuadricula = (int) Math.sqrt(tablero.length);
        int filaSubcuadricula = (fila / tamSubcuadricula) * tamSubcuadricula;
        int columnaSubcuadricula = (columna / tamSubcuadricula) * tamSubcuadricula;

        // Recorremos la fila, la columna y la subcuadrícula correspondientes para añadir los números al conjunto
        for (int i = 0; i < tablero.length; i++) {
            numerosEnFila.add(tablero[fila][i]);
            numerosEnColumna.add(tablero[i][columna]);
            numerosEnSubcuadricula.add(tablero[filaSubcuadricula + (i % tamSubcuadricula)][columnaSubcuadricula + (i / tamSubcuadricula)]);
        }

        // Comprobamos cuál es el número que falta en el conjunto {1, 2, ..., 9}
        for (int i = 1; i <= 9; i++) {
            if (!numerosEnFila.contains((char)i) && !numerosEnColumna.contains((char)i)) {
                if (!numerosEnSubcuadricula.contains((char)i)) {
                    return String.valueOf(i);
                }
            }
        }

        // Si hemos llegado aquí, significa que la posición está bloqueada y no se puede poner ningún número
        return "La posición [" + fila + "][" + columna + "] está bloqueada y no se puede poner ningún número";
    }

    @Override
    public boolean Correcto(int pCodJuego) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String GetSudoku(int pCodJuego) throws RemoteException {
        String cadena = "Dificultad: ";
        switch (arraySudokus.get(pCodJuego).getDificultad()) {
            case MUYFACIL:
                cadena += "Muy Fácil";
                break;
            case FACIL:
                cadena += "Fácil";
                break;
            case MEDIA:
                cadena += "Media";
                break;
            case DIFICIL:
                cadena += "Difícil";
                break;
            case MUYDIFICIL:
                cadena += "Muy Difícil";
                break;
            default:
                cadena += "** Sudoku vacío **";
        }
        String numero = Integer.toString(NumeroHuecos(pCodJuego));
        cadena += "\tHuecos: " + numero + "\n  123 456 789\n";
        for (int f = 0; f < 9; f++) {
            if (f % 3 == 0) {
                cadena += " +---+---+---+\n";
            }

            numero = Integer.toString(f + 1);
            cadena += numero;
            for (int c = 0; c < 9; c++) {
                if (c % 3 == 0) {
                    cadena += "|";
                }
                numero = Character.toString(ObtenerValor(pCodJuego, f, c));
                cadena += numero;
            }
            cadena += "|\n";
        }
        cadena += " +---+---+---+\n";

        return cadena;
    }

}
