/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package numbersquare;

import java.util.Scanner;

public class NumberSquare {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.print("Input Your Number : ");
        int number = input.nextInt();
        for (int i = 0; i < number; i++) {
            for (int j = 0; j < number; j++) {
                if (i == 0 || i == (number - 1) || j == 0 || j == (number - 1)) {
                    System.out.print(number);
                } else {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
    }
}
