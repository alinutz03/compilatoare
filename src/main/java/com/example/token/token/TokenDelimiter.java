package com.example.token.token;

public class TokenDelimiter {

    private final String WORD_REGEX = "[a-zA-Z_][a-zA-Z0-9_]*";
    private final String NUMBER_REGEX = "[0-9]+";

    private static boolean multiLineComment = false;
    private int multiLineCommentLineNumber = -1;
    private static int roundBracketCheck = 0;
    private static int squareBracketCheck = 0;
    private static int curlyBracketCheck = 0;

    public boolean isMultiLineComment() {
        return multiLineComment;
    }

    public String processLine(String line, int lineNumber) throws Exception {

        // Check the line
        if (containsNonASCII(line)) {
            throw new Exception("Exception on line " + lineNumber);
        }

        StringBuilder outputResonse = new StringBuilder();
        char[] lineCharacters = line.toCharArray();
        boolean comment = false;
        String token = "";
        boolean isEmpty = true;

        for (int i = 0; i < lineCharacters.length; i++) {
            char nextChar;

            // Verify the end of line
            if (i + 1 < lineCharacters.length) {
                nextChar = lineCharacters[i + 1];
            } else {
                nextChar = '\0';
            }

            if (lineCharacters[i] == ' ' && isEmpty) {
                continue;
            }
            token += lineCharacters[i];
            if (!this.multiLineComment && !comment) {
                if ((token + nextChar).equals("//")) {
                    comment = true;
                    i++;
                    token = "";
                } else if ((token + nextChar).equals("/*")) {
                    this.multiLineComment = true;
                    comment = true;
                    i++;
                    token = "";
                } else if ((token + nextChar).equals("*/")) {
                    throw new Exception("Exception on line " + lineNumber + ": there shoud be a multiline comment begin before a multiline comment end!");
                } else if ((TokenHelper.keyWords.contains(token)) && !TokenHelper.keydelimiters.contains(token + nextChar)) {
                    outputResonse.append(buildResponse(token, "KEY WORD", lineNumber));
                    token = "";
                } else if (TokenHelper.keydelimiters.contains(token)) {
                    delimitersCorrecteness(token, lineNumber);
                    outputResonse.append(buildResponse(token, "DELIMITER", lineNumber));
                    token = "";

                } else if (token.matches(WORD_REGEX) && !Character.isAlphabetic(nextChar) && !Character.isDigit(nextChar)) {
                    outputResonse.append(buildResponse(token, "IDENTIFIER", lineNumber));
                    token = "";
                } else if (TokenHelper.keyOperators.contains(token) && !TokenHelper.keyOperators.contains(token + nextChar)) {
                    outputResonse.append(buildResponse(token, "OPERATOR", lineNumber));
                    token = "";
                } else if (token.matches(NUMBER_REGEX) && token.contains(".") && !Character.isDigit(nextChar)) {
                    if (token.equals(".")) {
                        outputResonse.append(buildResponse(token, "INT_CONSTANT", lineNumber));
                    } else {
                        outputResonse.append(buildResponse(token, "FLOAT_CONSTANT", lineNumber));
                    }
                    token = "";

                } else if (token.equals("\"")) {
                    isEmpty = false;
                } else if (!isEmpty && token.charAt(token.length() - 1) == '\"' && token.length() > 1) {
                    outputResonse.append(buildResponse(token.substring(1, token.length() - 1), "LITERAL", lineNumber));
                    isEmpty = true;
                    token = "";
                }
            } else if (comment) {
                outputResonse.append(buildResponse(line.substring(i), "COMMENT", lineNumber));
                break;
            } else {
                for (int j = i + 1; j < lineCharacters.length; j++) {
                    token += lineCharacters[j];
                    if (token.endsWith("*/")) {
                        this.multiLineComment = false;
                        outputResonse.append(buildResponse(token.substring(0, token.length() - 2).trim(), "COMMENT", lineNumber));
//                        outputResonse.append(buildResponse(token, "COMMENT", lineNumber));//                        outputResonse.append(buildResponse(token, "COMMENT", lineNumber));
                        token = "";
                        i = j + 2;
                    }
                }
            }
        }
        return outputResonse.toString();
    }


    public String buildResponse(String token, String tokenType, int lineNumber) {
        System.out.println("'" + token + "', " + tokenType + "; " + token.length() + "; linia " + lineNumber);
        return "'" + token + "', " + tokenType + "; " + token.length() + "; linia " + lineNumber + "\n";
    }

    public static boolean containsNonASCII(String str) {
        return str.matches("[^\\x00-\\x7F]+");
    }

    private boolean delimitersCorrecteness(String token, int lineNumber) throws Exception {
        if (token.equals(")")) {

            if (roundBracketCheck == 0) {
                throw new Exception("Exception on line " + lineNumber + ":round bracket is not open to be closed!!");
            } else {
                roundBracketCheck--;
//                System.out.println("Linia: " + lineNumber + " count " + roundBracketCheck);
            }
        } else if (token.equals("]")) {
            if (squareBracketCheck == 0) {
                throw new Exception("Exception on line " + lineNumber + ":square bracket is not open to be closed!!");
            } else {
                squareBracketCheck--;
            }
        } else if (token.equals("}")) {
            if (curlyBracketCheck == 0) {
                throw new Exception("Exception on line " + lineNumber + ":curly bracket is not open to be closed!!");
            } else {
                curlyBracketCheck--;
            }

        } else if (token.equals("(")) {
            roundBracketCheck++;
//            System.out.println("Linia: " + lineNumber + " count " + roundBracketCheck);

        } else if (token.equals("[")) {
            squareBracketCheck++;
        } else if (token.equals("{")) {
            curlyBracketCheck++;
        }
        return true;
    }

}
