package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.HANStack;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.datastructures.IHANStack;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.BoolLiteral;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


public class Checker {

    private HashMap<String, ExpressionType> variableTypes;

    public void check(AST ast) {
        variableTypes = new HashMap<>();
        traverse(ast.root);
    }

    private void traverse(ASTNode node) {
        var scopeVariables = new ArrayList<String>();

        // deny operations with colors
        if (node instanceof Operation) {
            if (node.getChildren().get(0) instanceof ColorLiteral || node.getChildren().get(1) instanceof ColorLiteral) {
                node.setError("Invalid operation");
            }
        }

        for (var child : node.getChildren()) {
            // capture any new variable assignment
            if (child instanceof VariableAssignment) {
                var type = getExpressionType(child);
                var variableName = ((VariableReference) child.getChildren().get(0)).name;
                this.variableTypes.put(variableName, type);
                if (!variableTypes.containsKey(variableName)) {
                    scopeVariables.add(variableName);
                }
            }

            // TODO: Fix for bool
            if (child instanceof IfClause) {
                var name = ((VariableReference) child.getChildren().get(0)).name;
                if (this.variableTypes.get(name) != ExpressionType.BOOL) {
                    child.setError("Invalid expression type: " + name);
                }
            }

            // clear scope variables when entering else clause
            if (child instanceof ElseClause) {
                scopeVariables.forEach(this.variableTypes::remove);
            }

            // deny any undefined variable references
            if (child instanceof VariableReference && !(node instanceof VariableAssignment) && !this.variableTypes.containsKey(((VariableReference) child).name)) {
                child.setError("Undefined in scope: " + ((VariableReference) child).name);
            }

            if (child.getChildren() != null) {
                traverse(child);
            }
        }

        // remove the variables when leaving a scope
        scopeVariables.forEach(this.variableTypes::remove);
    }

    private static ExpressionType getExpressionType(ASTNode child) {
        ExpressionType type;
        switch (child.getChildren().get(1).getClass().getSimpleName()) {
            case "PixelLiteral":
                type = ExpressionType.PIXEL;
                break;
            case "PercentageLiteral":
                type = ExpressionType.PERCENTAGE;
                break;
            case "ColorLiteral":
                type = ExpressionType.COLOR;
                break;
            case "BoolLiteral":
                type = ExpressionType.BOOL;
                break;
            case "ScalarLiteral":
                type = ExpressionType.SCALAR;
                break;
            default:
                type = ExpressionType.UNDEFINED;
        }
        return type;
    }
}
