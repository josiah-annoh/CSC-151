import java.util.Random;
import java.util.Scanner;

public class Demo {

    public static int roll () {
        // roll for a die, return from 1 to 6
        Random roller = new Random();
        int roll = roller.nextInt (5) + 1; // 0-5 becomes 1-6
        return roll;
        return 1;

    }

    public static void main (String[] args) {
        System.out.println("Hey, what's your name?");
        Scanner input = new Scanner(System.in);
        String name = input.nextLine();

        System.out.println();
        System.out.println("Nice to meetcha, " + name);
        int chips = 100;
        System.out.println("Well, " + name + " you start with " + chips + " chips.");
        System.out.println();
        // Loop
        for (int i=0; i <20; i++) {
            System.out.println( "Rolling the  dice..."); 
            int roll = roll ();
            System.out.println("You rolled a: " + roll ); 
        }
       
        //end of program, close the input
        input.close();
    }

}
