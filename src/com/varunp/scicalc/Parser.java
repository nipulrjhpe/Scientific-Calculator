package com.varunp.scicalc;

import javafx.util.Pair;
import javafx.util.converter.CharacterStringConverter;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Parses the equation
 */
public class Parser
{
    private static final String OPERATORS = "^ /* -+ %";
    private static final String EXPONENT = "^",
                                DIVIDE = "/",
                                MULTIPLY = "*",
                                SUBTRACT = "-",
                                ADD = "+",
                                MODULO = "%";

    private String equation;
    private Map<String, Double> variables;

    public Parser(String eq)
    {
        this.equation = eq;

        variables = new HashMap<String, Double>();
        variables.put("pi", Math.PI);
        variables.put("e", Math.E);
    }

    public void addVariable(String declaration) throws Exception
    {
        String[] pair = declaration.split("=");
        if(pair.length != 2)
            throw new Exception("Improper variable argument.");

        if(!pair[0].toUpperCase().equals(pair[0]))
            throw new Exception("Invalid variable name " + pair[0]);

        variables.put(pair[0], Double.parseDouble(pair[1]));
    }

    public double getSolution() throws Exception
    {
        equation = replaceVariables(equation);
        return solveEquation(equation);
    }

    private double simplifyFunction(String eq) throws Exception
    {
        Double d = Double.parseDouble(eq.substring(eq.indexOf('(') + 1, eq.lastIndexOf(')')));
            //solveEquation(eq.substring(eq.indexOf('(') + 1, eq.lastIndexOf(')')));
        Method method = Functions.class.getDeclaredMethod(eq.substring(0, eq.indexOf('(')), Double.class);
        return(double)method.invoke(Functions.class, d);
    }

    private double solveEquation(String eq) throws Exception
    {
        if(!eq.contains("("))
            return evaluatePostfix(generatePostfix(eq));

        Stack<String> sections = new Stack<String>();

        for(int i = 0; i < eq.length(); i++)
        {
            char c = eq.charAt(i);

            if(c == '(')
            {
                sections.push("");
            }
            else if(Character.isLetter(c) && (sections.isEmpty() ||
                !Character.isLetter(eq.charAt(i - 1))))
            {
                sections.push(c + "");
            }
            else if(c == ')')
            {
                String s = sections.pop();
                if(Character.isLetter(s.charAt(0)))
                    s = simplifyFunction(generateFunction(s)) + "";
                else
                    s = evaluatePostfix(generatePostfix(s)) + "";

                if(!sections.isEmpty())
                {
                    String temp = sections.pop();
                    temp = temp + s;
                    sections.push(temp);
                }
                else
                    sections.push(s);
            }
            else
            {
                String s = sections.pop();
                s = s + c;
                sections.push(s);
            }
        }

        while(sections.size() > 1)
        {
            String s = sections.pop();
            s = (Character.isLetter(s.charAt(0)) ? simplifyFunction(generateFunction(s)) : evaluatePostfix(generatePostfix(s))) + "";
            s = sections.pop() + s;
            sections.push(s);
        }

        return evaluatePostfix(generatePostfix(sections.pop()));
    }

    private String generateFunction(String s)
    {
        String ret = "";
        boolean flag = false;
        for(char c : s.toCharArray())
        {
            if(!flag && (Character.isDigit(c) || c == '.')) {
                ret += '(';
                flag = true;
            }
            ret += c;
        }
        return ret + ')';
    }

    private String replaceVariables(String eq)
    {
        String ret = new String(eq);
        for(Map.Entry<String, Double> entry : variables.entrySet())
        {
            ret = ret.replaceAll(entry.getKey(), entry.getValue() + "");
        }
        return ret;
    }

    private String getNextNumber(String eq, int index)
    {
        String ret = "";

        for(int i = index; i < eq.length(); i++)
        {
            if(!Character.isDigit(eq.charAt(i)) && eq.charAt(i) != '.')
                break;
            else
                ret += eq.charAt(i);
        }

        return ret;
    }

    private boolean isInteger(double d)
    {
        return (d == Math.floor(d)) && !Double.isInfinite(d);
    }

    private String getPreviousNumber(String eq, int index) throws Exception
    {
        String ret = "";
        for(int i = index - 1; i >= 0; i--)
        {
            if(!Character.isDigit(eq.charAt(i)) && eq.charAt(i) != '.')
                break;
            else
                ret = eq.charAt(i) + ret;
        }

        return ret;
    }

    private double evaluatePostfix(Queue<String> postfix) throws Exception
    {
        Stack<Double> operands = new Stack<Double>();

        while (!postfix.isEmpty())
        {
            if(OPERATORS.contains(postfix.peek()))
            {
                double temp = operands.pop();
                operands.push(processOperation(operands.pop(), temp, postfix.remove()));
            }
            else
                operands.push(Double.parseDouble(postfix.remove()));
        }

        if(operands.size() > 1)
            throw new Exception("Postfix processing error");

        return operands.pop();
    }

    private double processOperation(double first, double second, String operation) throws Exception
    {
        if(operation.equals(EXPONENT))
            return Functions.pow(first, second);
        if(operation.equals(DIVIDE))
            return first / second;
        if(operation.equals(MULTIPLY))
            return first * second;
        if(operation.equals(SUBTRACT))
            return first - second;
        if(operation.equals(ADD))
            return first + second;
        if(operation.equals(MODULO))
            return first % second;

        else
            throw new Exception("Invalid operation");
    }


    private Queue<String> generatePostfix(String eq) throws Exception
    {
        Queue<String> postfix = new ArrayDeque<String>();

        Stack<Character> opStack = new Stack<Character>();

        String temp = "";
        for(char c : eq.toCharArray())
        {
            if(OPERATORS.contains(c + ""))
            {
                if(temp.equals(""))
                    throw new Exception("Invalid operation");

                postfix.add(temp);
                temp = "";

                if(opStack.isEmpty())
                    opStack.push(c);
                else
                {
                    while(!opStack.isEmpty() && hasHigherPrecedence(opStack.peek(), c))
                    {
                        postfix.add(opStack.pop() + "");
                    }

                    opStack.push(c);
                }
            }
            else
                temp += c;
        }

        if(!temp.isEmpty())
            postfix.add(temp);

        while(!opStack.isEmpty())
            postfix.add(opStack.pop() + "");

        return postfix;
    }

    private boolean hasHigherPrecedence(char a, char b)
    {
        int diff = OPERATORS.indexOf(a) - OPERATORS.indexOf(b);
        return diff < -1;
    }
}
