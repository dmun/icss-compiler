package nl.han.ica.icss.parser;

import java.util.Stack;


import nl.han.ica.datastructures.HANStack;
import nl.han.ica.datastructures.IHANStack;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;

/**
 * This class extracts the ICSS Abstract Syntax Tree from the Antlr Parse tree.
 */
public class ASTListener extends ICSSBaseListener {
	
	//Accumulator attributes:
	private AST ast;

	//Use this to keep track of the parent nodes when recursively traversing the ast
	private IHANStack<ASTNode> currentContainer;

	public ASTListener() {
		ast = new AST();
		currentContainer = new HANStack<>();
	}
    public AST getAST() {
        return ast;
    }

	@Override
	public void enterStylesheet(ICSSParser.StylesheetContext ctx) {
		var stylesheet = new Stylesheet();
		currentContainer.push(stylesheet);
	}

	@Override
	public void exitStylesheet(ICSSParser.StylesheetContext ctx) {
		var stylesheet = currentContainer.pop();
		ast.setRoot((Stylesheet) stylesheet);
	}

	@Override
	public void enterStylerule(ICSSParser.StyleruleContext ctx) {
		currentContainer.push(new Stylerule());
	}

	@Override
	public void exitStylerule(ICSSParser.StyleruleContext ctx) {
		var stylerule = currentContainer.pop();
		currentContainer.peek().addChild(stylerule);
	}

	@Override
	public void enterTagSelector(ICSSParser.TagSelectorContext ctx) {
		currentContainer.push(new TagSelector(ctx.getText()));
	}

	@Override
	public void exitTagSelector(ICSSParser.TagSelectorContext ctx) {
		var selector = currentContainer.pop();
		currentContainer.peek().addChild(selector);
	}

	@Override
	public void enterIdSelector(ICSSParser.IdSelectorContext ctx) {
		currentContainer.push(new IdSelector(ctx.getText()));
	}

	@Override
	public void exitIdSelector(ICSSParser.IdSelectorContext ctx) {
		var selector = currentContainer.pop();
		currentContainer.peek().addChild(selector);
	}

	@Override
	public void enterClassSelector(ICSSParser.ClassSelectorContext ctx) {
		currentContainer.push(new ClassSelector(ctx.getText()));
	}

	@Override
	public void exitClassSelector(ICSSParser.ClassSelectorContext ctx) {
		var selector = currentContainer.pop();
		currentContainer.peek().addChild(selector);
	}

	@Override
	public void enterDeclaration(ICSSParser.DeclarationContext ctx) {
		currentContainer.push(new Declaration());
	}

	@Override
	public void exitDeclaration(ICSSParser.DeclarationContext ctx) {
		var declaration = currentContainer.pop();
		currentContainer.peek().addChild(declaration);
	}

	@Override
	public void enterPropertyName(ICSSParser.PropertyNameContext ctx) {
		currentContainer.push(new PropertyName(ctx.getText()));
	}

	@Override
	public void exitPropertyName(ICSSParser.PropertyNameContext ctx) {
		var property = currentContainer.pop();
		currentContainer.peek().addChild(property);
	}

	@Override
	public void enterBoolLiteral(ICSSParser.BoolLiteralContext ctx) {
		var bool = new BoolLiteral(ctx.getText());
		currentContainer.peek().addChild(bool);
	}

	@Override
	public void enterColorLiteral(ICSSParser.ColorLiteralContext ctx) {
		var color = new ColorLiteral(ctx.getText());
		currentContainer.peek().addChild(color);
	}

	@Override
	public void enterPercentageLiteral(ICSSParser.PercentageLiteralContext ctx) {
		var percentage = new PercentageLiteral(ctx.getText());
		currentContainer.peek().addChild(percentage);
	}

	@Override
	public void enterPixelLiteral(ICSSParser.PixelLiteralContext ctx) {
		var pixel = new PixelLiteral(ctx.getText());
		currentContainer.peek().addChild(pixel);
	}

	@Override
	public void enterScalarLiteral(ICSSParser.ScalarLiteralContext ctx) {
		var scalar = new ScalarLiteral(ctx.getText());
		currentContainer.peek().addChild(scalar);
	}

	@Override
	public void enterVariableAssignment(ICSSParser.VariableAssignmentContext ctx) {
		currentContainer.push(new VariableAssignment());
	}

	@Override
	public void exitVariableAssignment(ICSSParser.VariableAssignmentContext ctx) {
		var variableAssignment = currentContainer.pop();
		currentContainer.peek().addChild(variableAssignment);
	}

	@Override
	public void enterVariableReference(ICSSParser.VariableReferenceContext ctx) {
		currentContainer.peek().addChild(new VariableReference(ctx.getText()));
	}

	@Override
	public void enterOperation(ICSSParser.OperationContext ctx) {
		if (ctx.getChildCount() == 3) {
			Operation operation;
			switch (ctx.getChild(1).getText()) {
				case "*":
					operation = new MultiplyOperation();
					break;
				case "+":
					operation = new AddOperation();
					break;
				default:
					operation = new SubtractOperation();
					break;
			}
			currentContainer.push(operation);
		}
	}

	@Override
	public void exitOperation(ICSSParser.OperationContext ctx) {
		// only exit when the operation is not just a literal
		if (ctx.getChildCount() != 1) {
			var operation = currentContainer.pop();
			currentContainer.peek().addChild(operation);
		}
	}

	@Override
	public void enterIfClause(ICSSParser.IfClauseContext ctx) {
		currentContainer.push(new IfClause());
	}

	@Override
	public void exitIfClause(ICSSParser.IfClauseContext ctx) {
		var ifClause = currentContainer.pop();
		currentContainer.peek().addChild(ifClause);
	}

	@Override
	public void enterElseClause(ICSSParser.ElseClauseContext ctx) {
		currentContainer.push(new ElseClause());
	}

	@Override
	public void exitElseClause(ICSSParser.ElseClauseContext ctx) {
		var elseClause = currentContainer.pop();
		currentContainer.peek().addChild(elseClause);
	}
}