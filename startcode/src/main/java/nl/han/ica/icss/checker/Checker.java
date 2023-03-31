package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.ArrayList;
import java.util.HashMap;


public class Checker {

    private IHANLinkedList<HashMap<String, ExpressionType>> variableTypes;

    public void check(AST ast) {
        variableTypes = new HANLinkedList<>();
        traverse(ast.root);
    }

    private void traverse(ASTNode node) {
        var scopeVariables = new ArrayList<String>();

        variableTypes.addFirst(new HashMap<String, ExpressionType>());

        // deny operations with colors
        if (node instanceof Operation) {
            if (node.getChildren().get(0) instanceof ColorLiteral || node.getChildren().get(1) instanceof ColorLiteral) {
                node.setError("Invalid ColorLiteral operation");
            }
        }

        for (var child : node.getChildren()) {
            // capture any new variable assignment
            if (child instanceof VariableAssignment) {
                var type = getExpressionType(child);
                var variableName = ((VariableReference) child.getChildren().get(0)).name;
//                if (!containsVariable(variableName)) {
//                    scopeVariables.add(variableName);
//                }
                this.variableTypes.getFirst().put(variableName, type);
            }

            if (child instanceof IfClause) {
                var ifClause = (IfClause) child;

                if (ifClause.conditionalExpression instanceof VariableReference) {
                    var name = ((VariableReference) ifClause.conditionalExpression).name;
                    if (this.getVariableValue(name) != ExpressionType.BOOL) {
                        child.setError("Invalid expression type: " + name);
                    }
                }
            }

            // clear scope variables when entering else clause
            if (child instanceof ElseClause) {
//                scopeVariables.forEach(this.variableTypes.getFirst()::remove);
                this.variableTypes.getFirst().clear();
                System.out.println("after clear:" + this.variableTypes);
            }

            System.out.println(containsVariable("Poop"));
            // deny any undefined variable references
//            System.out.println(variableTypes);
            if (child instanceof VariableReference
                    && !(node instanceof VariableAssignment)
                    && !containsVariable(((VariableReference) child).name)) {
                System.out.println("    error");
                child.setError("Undefined in scope: " + ((VariableReference) child).name);
            }

            if (child.getChildren() != null) {
                traverse(child);
            }
        }

        // remove the variables when leaving a scope
        this.variableTypes.removeFirst();
    }

    private static ExpressionType getExpressionType(ASTNode child) {
        return switch (child.getChildren().get(1).getClass().getSimpleName()) {
            case "PixelLiteral" -> ExpressionType.PIXEL;
            case "PercentageLiteral" -> ExpressionType.PERCENTAGE;
            case "ColorLiteral" -> ExpressionType.COLOR;
            case "BoolLiteral" -> ExpressionType.BOOL;
            case "ScalarLiteral" -> ExpressionType.SCALAR;
            default -> ExpressionType.UNDEFINED;
        };
    }

    private ExpressionType getVariableValue(String name) {
        for (int i = 0; i < this.variableTypes.getSize(); i++) {
            if (this.variableTypes.get(i).containsKey(name)) {
                return this.variableTypes.get(i).get(name);
            }
        }
        throw new RuntimeException("Variable " + name + " is not declared");
    }

    private boolean containsVariable(String name) {
        for (int i = 0; i < this.variableTypes.getSize(); i++) {
            if (this.variableTypes.get(i).containsKey(name)) {
                return true;
            }
        }
        return false;
    }
}
