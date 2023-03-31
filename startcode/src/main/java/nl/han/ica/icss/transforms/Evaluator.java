package nl.han.ica.icss.transforms;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;

import java.util.ArrayList;
import java.util.HashMap;

public class Evaluator implements Transform {

    private IHANLinkedList<HashMap<String, Literal>> variableValues;

    public Evaluator() {
        variableValues = new HANLinkedList<>();
    }

    @Override
    public void apply(AST ast) {
        traverseStylesheet(ast.root);
    }
    
    private void traverseStylesheet(Stylesheet node) {
        this.variableValues.addFirst(new HashMap<String, Literal>());

        for (var child : node.getChildren()) {
            if (child instanceof VariableAssignment) {
                handleVariableAssignment((VariableAssignment) child);
            } else if (child instanceof Stylerule) {
                traverseRuleBody((Stylerule) child, node);
            }
        }

        this.variableValues.removeFirst();
    }

    private void traverseRuleBody(ASTNode ruleBody, ASTNode parent) {
        var newBody = new ArrayList<ASTNode>();

        this.variableValues.addFirst(new HashMap<String, Literal>());

        for (var child : ruleBody.getChildren()) {
            if (child instanceof VariableAssignment) {
                handleVariableAssignment((VariableAssignment) child);
            } else if (child instanceof Declaration) {
                handleDeclaration((Declaration) child);
                newBody.add(child);
            // TODO: Fix for else
            } else if (child instanceof IfClause) {
                var name = ((VariableReference) ((IfClause) child).conditionalExpression).name;
                var value = ((BoolLiteral) getVariableValue(name)).value;

                if (value) {
                    traverseRuleBody(child, ruleBody);
                    newBody.addAll(((IfClause) child).body);
                } else if (((IfClause) child).elseClause != null) {
                    traverseRuleBody(child, ruleBody);
                    newBody.addAll(((IfClause) child).elseClause.body);
                }
            } else if (child instanceof ElseClause) {
                traverseRuleBody(child, ruleBody);
                newBody.addAll(((ElseClause) child).body);
            }
        }

        this.variableValues.removeFirst();

        if (ruleBody instanceof Stylerule) {
            ((Stylerule) ruleBody).body = newBody;
        } else if (ruleBody instanceof IfClause) {
            ((IfClause) ruleBody).body = newBody;
        } else if (ruleBody instanceof ElseClause) {
            ((ElseClause) ruleBody).body = newBody;
        }
    }

    private void handleDeclaration(Declaration declaration) {
        var expression = declaration.expression;

        if (expression instanceof VariableReference) {
            var name = ((VariableReference) expression).name;
            declaration.expression = getVariableValue(name);
        } else if (expression instanceof Operation) {
            declaration.expression = handleOperation((Operation) expression);
        }
    }

    private Literal handleOperation(Operation operation) {
        var lhs = operation.lhs;
        var rhs = operation.rhs;

        if (lhs instanceof VariableReference) {
            var name = ((VariableReference) lhs).name;
            lhs = getVariableValue(name);
        } else if (lhs instanceof Operation) {
            lhs = handleOperation((Operation) lhs);
        }

//        System.out.println(rhs.getClass());
        if (rhs instanceof VariableReference) {
            var name = ((VariableReference) rhs).name;
//            System.out.println("variablerefrence");
            rhs = getVariableValue(name);
        } else if (rhs instanceof Operation) {
//            System.out.println("operation");
            rhs = handleOperation((Operation) rhs);
        }

//        System.out.println("lhs: " + lhs);
        var lhsValue = getLiteralValue((Literal) lhs);
//        System.out.println("rhs: " + rhs);
        var rhsValue = getLiteralValue((Literal) rhs);

        if (operation instanceof AddOperation) {
            return newLiteral(lhs, lhsValue + rhsValue);
        } else if (operation instanceof SubtractOperation) {
            return newLiteral(lhs, lhsValue - rhsValue);
        } else {
            return newLiteral(lhs, lhsValue * rhsValue);
        }
    }

    private static Literal newLiteral(Expression lhs, int value) {
        if (lhs instanceof PixelLiteral) {
            return new PixelLiteral(value);
        } else if (lhs instanceof PercentageLiteral) {
            return new PercentageLiteral(value);
        } else {
            return new ScalarLiteral(value);
        }
    }

    private int getLiteralValue(Literal literal) {
        if (literal instanceof PixelLiteral) {
            return ((PixelLiteral) literal).value;
        } else if (literal instanceof ScalarLiteral) {
            return ((ScalarLiteral) literal).value;
        } else {
            return ((PercentageLiteral) literal).value;
        }
    }

    private void handleVariableAssignment(VariableAssignment variableAssignment) {
        var name = variableAssignment.name.name;
        Literal literal = (Literal) variableAssignment.getChildren().get(1);

        // Check if variable is already declared in scope
        int index = -1;
        for (int i = 0; i < variableValues.getSize(); i++) {
            if (this.variableValues.get(i).containsKey(name)) {
                index = i;
            }
        }
        // If variable is already declared in scope, replace it
        if (index != -1) {
            this.variableValues.get(index).put(name, literal);
        } else {
            this.variableValues.getFirst().put(name, literal);
        }
    }

    private Literal getVariableValue(String name) {
        for (int i = 0; i < this.variableValues.getSize(); i++) {
            if (this.variableValues.get(i).containsKey(name)) {
                return this.variableValues.get(i).get(name);
            }
        }
        throw new NullPointerException();
    }
}
