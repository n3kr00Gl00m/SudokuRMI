/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clases;

import jSudoku.Sudoku;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author nekro
 */
public class CalculadoraCliente {

    /*
    FALTA POR HACERLE:
        - Cambiar el arraylist de integer por uno de sudokus y averiguar como poner 
            la dificultad etc.
        - Hacer que se pueda elegir el sudoku por numero y no por id
    
        - Hacer que no se salga cada vez que creemos un sudoku
     */
    public static int menuJuego() {
        int valor;
        Scanner scanf = new Scanner(System.in);
        System.out.println("\nMENU JUEGO--------\n");
        System.out.println("1.- Poner Valor\n");
        System.out.println("2.- Borrar Valor\n");
        System.out.println("3.- Ayuda\n");
        System.out.println("4.- Salir\n");
        System.out.println("Elige Opción: ");
        valor = Integer.parseInt(scanf.nextLine());

        return valor;
    }

    public static Sudoku.dificultad MenuDificultad() {
        int valor;
        Sudoku.dificultad dif;
        Scanner scanf = new Scanner(System.in);
        System.out.println("\n--------MENU DIFICULTAD------\n");
        System.out.println("Muy Fácil(1) Fácil(2) Medio(3) Difícil(4) Muy Difícil(5)\n");
        System.out.println("Elige Opción: ");

        valor = Integer.parseInt(scanf.nextLine());

        switch (valor) {
            case 1:
                dif = Sudoku.dificultad.MUYFACIL;

                break;
            case 2:
                dif = Sudoku.dificultad.FACIL;

                break;
            case 3:
                dif = Sudoku.dificultad.MEDIA;

                break;
            case 4:
                dif = Sudoku.dificultad.DIFICIL;

                break;
            case 5:
                dif = Sudoku.dificultad.MUYDIFICIL;

                break;
            default:
                throw new AssertionError();
        }

        return dif;
    }

    public static int menuServidorJuegos() {

        System.out.println("\n--------SERVIDOR JUEGOS--------\n");
        System.out.println("\n1. Gestionar Juegos");
        System.out.println("\n2. Jugar");
        System.out.println("\n0. Salir");
        System.out.println("\nOpcion:\n");

        Scanner scanf = new Scanner(System.in);

        return Integer.parseInt(scanf.nextLine());
    }

    public static int menuGestionJuegos() {
        System.out.println("\n-----MENU JUEGOS-----\n");
        System.out.println("\n1. Crear Juego\n");
        System.out.println("2. Borrar Juego\n");
        System.out.println("3. Seleccionar Juego\n");
        System.out.println("4. Salir\n");
        System.out.println("\nOpcion:\n");

        Scanner scanf = new Scanner(System.in);

        return Integer.parseInt(scanf.nextLine());
    }

    public static int menuBorrarJuegos(ArrayList<Integer> juegos, ArrayList<Sudoku> auxListaSudokus) {
        System.out.println("\n-----BORRAR JUEGO-----\n");
        boolean esta = false;
        System.out.println("\n");
        int j = 0;

        for (int i : juegos) {
            System.out.println(String.format("%d\t\t%d\t\t\n", j, i));
        }
        System.out.println("\n Introduce el codigo del juego que quiere borrar: ");

        Scanner scanf = new Scanner(System.in);
        j = Integer.parseInt(scanf.nextLine());
        j = buscarJuego(j, juegos);
        System.out.println(auxListaSudokus.contains(j));
        if (juegos.contains(j)) {
            esta = true;
        } else {
            System.err.println("ERROR: No se encuentra ningun sudoku con el codigo seleccionado, intentelo de nuevo");
        }
        return esta == true ? j : -1;
    }

    public static int buscarJuego(int index, ArrayList<Integer> juego) {

        System.out.println("\n\n" + juego.get(index));

        return juego.get(index);
    }

    public static int menuSeleccionarJuegos(ArrayList<Integer> juegos, ArrayList<Sudoku> auxListaSudokus) {
        System.out.println("\n-----JUEGOS DISPONIBLES-----\n");
        boolean esta = false;
        System.out.println("\n");
        int j = 0;
        for (int i = 0; i < juegos.size(); i++) {
            System.out.println(String.format("%s\t\t%s\t\t%s", j, juegos.get(i),
                    auxListaSudokus.get(juegos.get(i)).getDificultad()));
            j++;
        }

        System.out.println("\n Introduce el indice del juego: ");

        Scanner scanf = new Scanner(System.in);
        j = Integer.parseInt(scanf.nextLine());
        j = buscarJuego(j, juegos);

        if (juegos.contains(j)) {
            esta = true;
        } else {
            System.err.println("ERROR: No se encuentra ningun sudoku con el codigo seleccionado, intentelo de nuevo");
        }
        return esta == true ? j : -1;
    }

    public static void main(String[] args) throws RemoteException, MalformedURLException {

        CalculadoraServidor cServ = new CalculadoraServidor();
        ArrayList<Integer> idSudokus = new ArrayList<>();
        int sudokuActual, juegoAJugar = -1;

        ArrayList<Sudoku> auxListaSudokus;

        java.rmi.registry.LocateRegistry.createRegistry(1099);
        java.rmi.Naming.rebind("rmi://localhost/ServidorRemoto", cServ);

        int opcion;

        Sudoku.dificultad opcDificultad;

        do {
            opcion = menuServidorJuegos();

            if (opcion == 1) { //Si Desea Gestionar los Juegos:

                opcion = menuGestionJuegos();

                switch (opcion) {
                    case 1: {
                        opcDificultad = MenuDificultad(); //Escogemos la dificultad
                        sudokuActual = cServ.NuevoJuego(opcDificultad);
                        idSudokus.add(sudokuActual); //Crceamos y metemos el nuevo juego en el array de juegos creados
                    }
                    break;

                    case 2: {
                        auxListaSudokus = cServ.getArraySudoku();
                        int juegoABorrar = menuBorrarJuegos(idSudokus, auxListaSudokus);
                        if (juegoABorrar != -1) {
                            cServ.BorrarJuego(juegoABorrar);
                            System.err.println("\nSe ha borrado el juego correctamente\n");
                        } else {
                            System.err.println("No se ha podido borrar ningun juego\n");
                        }
                    }
                    break;
                    case 3: {
                        auxListaSudokus = cServ.getArraySudoku();
                        juegoAJugar = menuSeleccionarJuegos(idSudokus, auxListaSudokus);
                        System.out.println("Codigo del juego seleccionado: "+ juegoAJugar);

                    }
                    break;
                    default:
                        System.err.println("Introduce un valor valido");

                }
            }else if(opcion == 2) {
                        auxListaSudokus = cServ.getArraySudoku();
                        System.out.println("Codigo del juego a jugar: " + juegoAJugar);
                        if (juegoAJugar != -1) {

                            do {
                                String cadenita = cServ.GetSudoku(juegoAJugar);
                                System.out.println(cadenita);
                                opcion = menuJuego();
                                switch (opcion) {
                                    case 1: {
                                        Scanner scanner = new Scanner(System.in);
                                        int fila, columna;
                                        char valor;
                                        boolean todoOk = false;

                                        System.out.println("\nFILA: ");
                                        fila = scanner.nextInt();
                                        System.out.println("\nCOLUMNA: ");
                                        columna = scanner.nextInt();
                                        System.out.println("\nVALOR: ");
                                        valor = scanner.next().charAt(0);

                                        if (auxListaSudokus.get(juegoAJugar).posicionOcupada(fila, columna) == false) {
                                            System.out.println("La posicion [" + fila + "][" + columna + "] está ocupada.\n");
                                        } else {
                                            todoOk = cServ.PonerValor(juegoAJugar, fila - 1, columna - 1, valor);
                                        }

                                        System.out.println(todoOk == false ? "\t" : "No se ha podido colocar el valor");
                                    }
                                    break;
                                    case 2: {
                                        Scanner scanner = new Scanner(System.in);
                                        int fila, columna;

                                        System.out.println("\nFILA: ");
                                        fila = scanner.nextInt();
                                        System.out.println("\nCOLUMNA: ");
                                        columna = scanner.nextInt();

                                        cServ.PonerValor(juegoAJugar, fila - 1, columna - 1, ' ');

                                    }
                                    break;
                                    case 3: {
                                        Scanner scanner = new Scanner(System.in);
                                        int fila, columna;

                                        System.out.println("\nFILA: ");
                                        fila = scanner.nextInt();
                                        System.out.println("\nCOLUMNA: ");
                                        columna = scanner.nextInt();
                                        System.out.println(cServ.Ayuda(juegoAJugar, fila - 1, columna - 1));
                                    }
                                    break;
                                    default:
                                        System.err.println("Introduce un valor valido");
                                }
                            } while (opcion != 4);

                        } else {
                            System.err.println("Primero debe seleccionar un juego\n");
                        }
                
            }

        } while (opcion != 0);
        System.out.println("\n\nSALIENDO DEL PROGRAMA...\n");
    }

}
