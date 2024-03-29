grammar ICSS;

//--- LEXER: ---

// IF support:
IF: 'if';
ELSE: 'else';
BOX_BRACKET_OPEN: '[';
BOX_BRACKET_CLOSE: ']';


//Literals
TRUE: 'TRUE';
FALSE: 'FALSE';
PIXELSIZE: [0-9]+ 'px';
PERCENTAGE: [0-9]+ '%';
SCALAR: [0-9]+;


//Color value takes precedence over id idents
COLOR: '#' [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f];

//Specific identifiers for id's and css classes
ID_IDENT: '#' [a-z0-9\-]+;
CLASS_IDENT: '.' [a-z0-9\-]+;

//General identifiers
LOWER_IDENT: [a-z] [a-z0-9\-]*;
CAPITAL_IDENT: [A-Z] [A-Za-z0-9_]*;

//All whitespace is skipped
WS: [ \t\r\n]+ -> skip;

//
OPEN_BRACE: '{';
CLOSE_BRACE: '}';
SEMICOLON: ';';
COLON: ':';
PLUS: '+';
MIN: '-';
MUL: '*';
ASSIGNMENT_OPERATOR: ':=';




//--- PARSER: ---
stylesheet: variableAssignment* stylerule*;
variableAssignment: variableReference ASSIGNMENT_OPERATOR literal SEMICOLON;
variableReference: CAPITAL_IDENT;
stylerule: selector OPEN_BRACE ruleElement* CLOSE_BRACE;

selector: tagSelector | idSelector | classSelector;
tagSelector: LOWER_IDENT;
idSelector: ID_IDENT;
classSelector: CLASS_IDENT;

ruleElement: ifClause | declaration | variableAssignment;

declaration: propertyName COLON operation SEMICOLON;
propertyName: LOWER_IDENT;
literal: boolLiteral | colorLiteral | percentageLiteral | pixelLiteral | scalarLiteral | variableReference;
boolLiteral: TRUE | FALSE;
colorLiteral: COLOR;
percentageLiteral: PERCENTAGE;
pixelLiteral: PIXELSIZE;
scalarLiteral: SCALAR;

operation: literal |
    operation MUL operation |
    operation PLUS operation |
    operation MIN operation;

ifClause: IF BOX_BRACKET_OPEN (variableReference|boolLiteral) BOX_BRACKET_CLOSE OPEN_BRACE ruleElement* CLOSE_BRACE elseClause?;
elseClause: ELSE OPEN_BRACE ruleElement* CLOSE_BRACE;