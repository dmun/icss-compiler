package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.HANStack;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.datastructures.IHANStack;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Checker {

    private IHANLinkedList<HashMap<String, ExpressionType>> variableTypes;
    private List<ASTNode> variablesInScope;
    private List<ASTNode> variablesInIf;

    public void check(AST ast) {
        variableTypes = new HANLinkedList<>();
        variablesInScope = new ArrayList<>();
        variablesInIf = new ArrayList<>();
        traverse(ast.root, variablesInScope);
    }

    private void traverse(ASTNode node, List<ASTNode> variables) {
        var variablesInScope = new ArrayList<>(variables);
        if (node instanceof ElseClause) {
            variablesInIf.clear();
        }
        if (node instanceof Operation) {
            if (node.getChildren().get(0) instanceof ColorLiteral || node.getChildren().get(1) instanceof ColorLiteral) {
                node.setError("Invalid operation");
            }
        }
        for (var child : node.getChildren()) {
            if (child instanceof VariableAssignment) {
                if (node instanceof IfClause) {
                    variablesInIf.add(child.getChildren().get(0));
                } else {
                    variablesInScope.add(child.getChildren().get(0));
                }
            }
            if (child instanceof VariableReference && !(node instanceof VariableAssignment) && !(variablesInScope.contains(child) || variablesInIf.contains(child))) {
                child.setError("Undefined in scope: " + ((VariableReference) child).name);
            }
            if (child.getChildren() != null) {
                traverse(child, variablesInScope);
            }
        }
    }
}
