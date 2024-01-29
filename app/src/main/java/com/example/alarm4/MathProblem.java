package com.example.alarm4;

import java.util.Random;

public class MathProblem {
    public String expression;
    public int answer;

    private MathProblem(String expression, int answer) {
        this.expression = expression;
        this.answer = answer;
    }

    public static MathProblem generate(int size) {
        Random random = new Random();
        MathProblem problem = new MathProblem("", 0);
        int index;
        for (index = 0; index < size; index++) {
            int num = random.nextInt(10) + 1;
            int operator = random.nextInt(2);
            problem = problem.applyOperator(operator, num);
        }
        problem.expression = problem.expression + " = ";
        if (problem.expression.charAt(1) == '+') {
            problem.expression = problem.expression.substring(2);
        }
        return problem;
    }

    private MathProblem applyOperator(int op, int num) {
        MathProblem problem;
        switch (op) {
            case 0:
                problem = new MathProblem(this.expression + " + " + num, this.answer + num);
                break;
            case 1:
                problem = new MathProblem(this.expression + " - " + num, this.answer - num);
                break;
            default:
                problem = this;
        }
        return problem;
    }
}
