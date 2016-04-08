package com.varunp.scicalc;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.SyncFailedException;

public class Main
{
    public static void main(String[] args)
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        Parser parser;
        while (true)
        {
            System.out.println();

            try
            {
                String input = br.readLine();
                if(input.equalsIgnoreCase("help") || input.isEmpty())
                {
                    displayHelp();
                    continue;
                }
                else if(input.equalsIgnoreCase("quit"))
                    break;

                String[] comm = input.split(" ");

                parser = new Parser(comm[0]);

                for (int i = 1; i < comm.length; i++)
                    parser.addVariable(comm[i]);

                System.out.println(parser.getSolution());
            }
            catch (Exception e)
            {
                System.out.println("Error:");
                System.out.println(e.toString() + ": " + e.getMessage());
            }
        }
    }

    private static void displayHelp()
    {
        System.out.println("Arguments as follows:");
        System.out.println("<equation> <variable name>=<variable value>...");
        System.out.println("Variable names must be in ALL CAPS.");
        System.out.println();
        System.out.println("Allowed operations;");
        System.out.println("factorial(), sin(), cod(), tan(), ln(), log(), ^, sqrt(), %, /, *, -, +");
        System.out.println();
        System.out.println("Supported constants:");
        System.out.println("pi, e");
    }
}
