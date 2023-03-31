package nl.han.ica.icss.generator;


import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;

public class Generator {
	private static final int TABSTOP = 2;

	public String generate(AST ast) {
		StringBuilder stringBuilder = new StringBuilder();
//		for (ASTNode node : ast.root.getChildren()) {
		for (int i = 0; i < ast.root.getChildren().size(); i++) {
			var node = ast.root.getChildren().get(i);

			if (node instanceof Stylerule) {
				stringBuilder.append(((Stylerule) node).selectors.get(0))
						.append(" {")
						.append(System.lineSeparator());

				for (ASTNode child : node.getChildren()) {
					if (child instanceof Declaration) {
						var declaration = (Declaration) child;
						stringBuilder.append(" ".repeat(TABSTOP))
								.append(declaration.property.name)
								.append(": ")
								.append(getLiteralValue((Literal) declaration.expression))
								.append(";")
								.append(System.lineSeparator());
					}
				}

				stringBuilder.append("}")
						.append(System.lineSeparator());

				if (i < ast.root.getChildren().size() - 1) {
					stringBuilder.append(System.lineSeparator());
				}
			}
		}
        return stringBuilder.toString();
	}

	private String getLiteralValue(Literal literal) {
		return switch (literal.getClass().getSimpleName()) {
			case "ColorLiteral" -> ((ColorLiteral) literal).value;
			case "PercentageLiteral" -> ((PercentageLiteral) literal).value + "%";
			case "PixelLiteral" -> ((PixelLiteral) literal).value + "px";
			case "ScalarLiteral", "BoolLiteral" -> literal.toString();
			default -> "";
		};
	}
	
}
