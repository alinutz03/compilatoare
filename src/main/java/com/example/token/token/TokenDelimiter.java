package com.example.token.token;

public class TokenDelimiter {

    private final String WORD_REGEX = "[a-zA-Z_][a-zA-Z0-9_]*";
    private final String NUMBER_REGEX = "[0-9]+";
    private final String NUMBER_REGEX2 = "[0-9.]+";

    private static int roundBracketCheck = 0;
    public static int squareBracketCheck = 0;
    public static int curlyBracketCheck = 0;
    public int multiLineCommentLineNumber = -1;

    private static boolean multiLineCommentCheck = false;

    public boolean isMultiLineComment() {
        return multiLineCommentCheck;
    }


    public String processLine(String lineString, int lineNumber) throws Exception {

        StringBuilder outputResponse = new StringBuilder();
        boolean isEmpty = false;
        boolean comment = false;
        char[] characters = lineString.toCharArray();


        String currentCharacter = "";
        for (int i = 0; i < characters.length; i++) {
            char nextChr = (i + 1 < characters.length) ? characters[i + 1] : '\0';
            if (characters[i] == ' ' && !isEmpty) {
                continue;
            }
            currentCharacter += characters[i];
            if (isValidCharacter(currentCharacter.charAt(0))) {
                throw new Exception("Illeegal character " + currentCharacter + " at line  '" + lineNumber + "'!");
            }
            if (!comment && !this.multiLineCommentCheck) {
                if ((currentCharacter + nextChr).equals("//")) {
                    comment = true;
                    i++;
                    currentCharacter = "";
                } else if ((currentCharacter + nextChr).equals("/*")) {
                    this.multiLineCommentLineNumber = lineNumber;
                    i++;
                    this.multiLineCommentCheck = true;
                    currentCharacter = "";
                } else if ((currentCharacter + nextChr).equals("*/")) {
                    throw new Exception("Line number " + lineNumber + " contains multiline comment ending without multiline comment context!");
                } else if (TokenHelper.keyWords.contains(currentCharacter) && !TokenHelper.keyWords.contains(currentCharacter + nextChr)) {
                    outputResponse.append(buildResponse(currentCharacter, "KEY_WORD", lineNumber));
                    currentCharacter = "";
                } else if (TokenHelper.keydelimiters.contains(currentCharacter)) {
                    delimitersCorrecteness(currentCharacter, lineNumber);
                    outputResponse.append(buildResponse(currentCharacter, "DELIMITER", lineNumber));
                    currentCharacter = "";
                } else if (currentCharacter.matches(WORD_REGEX)
                        && !Character.isDigit(nextChr)
                        && !Character.isAlphabetic(nextChr)) {
                    outputResponse.append(buildResponse(currentCharacter, "IDENTIFIER", lineNumber));
                    currentCharacter = "";
                } else if (TokenHelper.keyOperators.contains(currentCharacter) &&
                        !TokenHelper.keyOperators.contains(currentCharacter + nextChr)) {
                    if (currentCharacter.equals(".") && nextChr == '.') {
                        throw new Exception("Exception on line " + lineNumber + ": ilegal ..");
                    }

                    outputResponse.append(buildResponse(currentCharacter, "OPERATOR", lineNumber));
                    currentCharacter = "";
                } else if (currentCharacter.matches(NUMBER_REGEX) &&
                        !currentCharacter.contains(".") &&
                        nextChr != '.' &&
                        !Character.isDigit(nextChr)) {
                    outputResponse.append(buildResponse(currentCharacter, "INT_CONSTANT", lineNumber));
                    currentCharacter = "";
                } else if (currentCharacter.matches(NUMBER_REGEX2) &&
                        currentCharacter.contains(".") &&
                        !Character.isDigit(nextChr)) {
                    if (currentCharacter.equals(".")) {
                        outputResponse.append(buildResponse(currentCharacter, "DELIMITER", lineNumber));
                    } else {
                        outputResponse.append(buildResponse(currentCharacter, "FLOAT_CONSTANT", lineNumber));
                    }
                    currentCharacter = "";
                } else if (currentCharacter.equals("\"")) {
                    isEmpty = true;
                } else if (isEmpty && currentCharacter.charAt(currentCharacter.length() - 1) == '\"' && currentCharacter.length() > 1) {
                    outputResponse.append(buildResponse(currentCharacter.substring(1, currentCharacter.length() - 1), "LITERAL", lineNumber));
                    currentCharacter = "";
                    isEmpty = false;
                }
            } else if (comment) {
                outputResponse.append(buildResponse(lineString.substring(i), "COMMENT", lineNumber));
                break;
            } else {
                if (!(lineString.substring(i).contains("*/"))) {
                    outputResponse.append(buildResponse(lineString.substring(i).trim(), "COMMENT", lineNumber));
                    break;
                } else {
                    for (int j = i + 1; j < characters.length; j++) {
                        currentCharacter += characters[j];
                        if (currentCharacter.endsWith("*/")) {
                            this.multiLineCommentCheck = false;
                            outputResponse.append(buildResponse(currentCharacter.substring(0, currentCharacter.length() - 2).trim(), "COMMENT", lineNumber));
                            currentCharacter = "";
                            i = j + 2;
                        }
                    }
                }
            }
        }

        return outputResponse.toString();
    }

    private String buildResponse(String token, String type, int lineNumber) {
        return "'" + token + "', " + type + "; " + token.length() + "; linia " + lineNumber + "\n";
    }


    private boolean isValidCharacter(char c) {
        if (c == '#' || c == '@' || c == ',') {
            return true;
        }
        return false;
    }


    private boolean delimitersCorrecteness(String token, int lineNumber) throws Exception {
        if (token.equals(")")) {

            if (roundBracketCheck == 0) {
                throw new Exception("Exception on line " + lineNumber + ":round bracket is not open to be closed!!");
            } else {
                roundBracketCheck--;
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

        } else if (token.equals("[")) {
            squareBracketCheck++;
        } else if (token.equals("{")) {
            curlyBracketCheck++;
        }
        return true;
    }


}
