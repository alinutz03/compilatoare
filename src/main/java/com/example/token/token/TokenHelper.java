package com.example.token.token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface TokenHelper {

    List<String> keyWords= Arrays.asList(
            "abstract",
            "ArrayList",
            "args",
            "boolean",
            "Boolean",
            "byte",
            "break",
            "class",
            "case",
            "catch",
            "char",
            "continue",
            "default",
            "do",
            "double",
            "else",
            "extends",
            "final",
            "finally",
            "float",
            "for",
            "forEach",
            "if",
            "implements",
            "import",
            "instanceof",
            "int",
            "Integer",
            "interface",
            "long",
            "Long",
            "List",
            "main",
            "native",
            "new",
            "package",
            "private",
            "protected",
            "public",
            "return",
            "short",
            "Short",
            "static",
            "sealed",
            "super",
            "switch",
            "String",
            "synchronized",
            "this",
            "thro",
            "throws",
            "transient",
            "try",
            "void",
            "volatile",
            "while",
            "assert",
            "const",
            "enum",
            "goto",
            "strictfp"
    );

    List<String> keyOperators = Arrays.asList(
            "+",
            "-",
            "/",
            "*",
            "%",
            "++",
            "--",
            "!",
            "=",
            "+=",
            "-=",
            "*=",
            "/=",
            "%=",
            "^=",
            "==",
            "!=",
            "<",
            ">",
            "<=",
            ">=",
            "&&",
            "||",
            "?",
            ":",
            "<<",
            ">>",
            "&=",
            "->",
            "()",
            "[]",
            ".",
            "? :",
            ">>>",
            "~",
            "^",
            "|",
            "&"

            );

    List<String> keydelimiters = Arrays.asList(
            "(",
            ")",
            "[",
            "]",
            "{",
            "}",
            ";"
    );

}
