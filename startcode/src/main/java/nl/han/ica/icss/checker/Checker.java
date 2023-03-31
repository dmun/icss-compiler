package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;


public class Checker {

    private IHANLinkedList<HashMap<String, ExpressionType>> variableTypes;

    public void check(AST ast) {
        variableTypes = new HANLinkedList<>();
        traverse(ast.root);
    }

    private void traverse(ASTNode node) {
        variableTypes.addFirst(new HashMap<String, ExpressionType>());

        checkColorOperations(node);

        for (var child : node.getChildren()) {
            handleVariableAssignment(child);
            checkIfClause(child);
            handleElseClause(child);
            checkUndefinedVariables(node, child);

            if (child.getChildren() != null) {
                traverse(child);
            }
        }

        // remove the variables when leaving a scope
        this.variableTypes.removeFirst();
    }

    private static void checkColorOperations(ASTNode node) {
        if (node instanceof Operation) {
            if (node.getChildren().get(0) instanceof ColorLiteral || node.getChildren().get(1) instanceof ColorLiteral) {
                node.setError("Invalid ColorLiteral operation");
            }
        }
    }

    private void checkUndefinedVariables(ASTNode node, ASTNode child) {
        if (child instanceof VariableReference
                && !(node instanceof VariableAssignment)
                && !containsVariable(((VariableReference) child).name)) {
            child.setError("Undefined in scope: " + ((VariableReference) child).name);
        }
    }

    private void handleElseClause(ASTNode child) {
        if (child instanceof ElseClause) {
            this.variableTypes.getFirst().clear();
        }
    }

    private void checkIfClause(ASTNode child) {
        if (child instanceof IfClause) {
            var ifClause = (IfClause) child;

            if (ifClause.conditionalExpression instanceof VariableReference) {
                var name = ((VariableReference) ifClause.conditionalExpression).name;
                if (this.getVariableValue(name) != ExpressionType.BOOL) {
                    child.setError("Invalid expression type: " + name);
                }
            }
        }
    }

    private void handleVariableAssignment(ASTNode child) {
        if (child instanceof VariableAssignment) {
            var type = getExpressionType(child);
            var variableName = ((VariableReference) child.getChildren().get(0)).name;
            this.variableTypes.getFirst().put(variableName, type);
        }
    }

    private static ExpressionType getExpressionType(ASTNode child) {
        switch (child.getChildren().get(1).getClass().getSimpleName()) {
            case "PixelLiteral":
                return ExpressionType.PIXEL;
            case "PercentageLiteral":
                return ExpressionType.PERCENTAGE;
            case "ColorLiteral":
                return ExpressionType.COLOR;
            case "BoolLiteral":
                return ExpressionType.BOOL;
            case "ScalarLiteral":
                return ExpressionType.SCALAR;
            default:
                return ExpressionType.UNDEFINED;
        }
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
