/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jSudoku;

/**
 *
 * @author nekro
 */
public class Sudoku {

    public enum dificultad {
        MUYDIFICIL,
        DIFICIL,
        MEDIA,
        FACIL,
        MUYFACIL,
        VACIO;
    }

    private final dificultad dificultad;
    private final char[][] Tablero;
    private final int id;
    private final int index;

    public Sudoku(dificultad dif, int id, int index) {
        this.dificultad = dif;
        this.Tablero = new char[9][9];
        this.id = id;
        this.index = index;
    }

    public dificultad getDificultad() {
        return dificultad;
    }

    public int getId() {
        return id;
    }

    public void setValorEnTablero(int fila, int columna, char valor) {
        Tablero[fila][columna] = valor;
    }

    public char getValorEnTablero(int fila, int columna) {
        return Tablero[fila][columna];
    }

    public char[][] getTablero() {
        return Tablero;
    }

    public boolean posicionOcupada(int fila, int columna) {
        return Tablero[fila][columna] == ' ';
    }

}
