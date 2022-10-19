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

    private List<HashMap<String, ExpressionType>> variableTypes;

    public void check(AST ast) {
        variableTypes = new LinkedList<>();
        traverse(ast.root);
    }

    private void traverse(ASTNode node) {
        var variableCount = 0;

        // deny operations with colors
        if (node instanceof Operation) {
            if (node.getChildren().get(0) instanceof ColorLiteral || node.getChildren().get(1) instanceof ColorLiteral) {
                node.setError("Invalid operation");
            }
        }

        for (var child : node.getChildren()) {
            // capture any new variable assignment
            if (child instanceof VariableAssignment) {
                var newVariable = new HashMap<String, ExpressionType>();
                ExpressionType type;
                type = getExpressionType(child);
                newVariable.put(((VariableReference) child.getChildren().get(0)).name, type);
                this.variableTypes.add(newVariable);
                variableCount++;
            }

            // clear scope variables when entering else clause
            if (child instanceof ElseClause) {
                clearScopeVariables(variableCount);
                variableCount = 0;
            }

            // deny any undefined variable references
            if (child instanceof VariableReference && !(node instanceof VariableAssignment) && !inScope(((VariableReference) child).name)) {
                child.setError("Undefined in scope: " + ((VariableReference) child).name);
            }

            if (child.getChildren() != null) {
                traverse(child);
            }
        }

        // remove the variables when leaving a scope
        clearScopeVariables(variableCount);
    }

    private void clearScopeVariables(int variableCount) {
        for (int i = 0; i < variableCount; i++) {
            this.variableTypes.remove(this.variableTypes.size() - 1);
        }
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

    private boolean inScope(String variableName) {
        return this.variableTypes.stream().anyMatch(e -> e.containsKey(variableName));
    }
}
