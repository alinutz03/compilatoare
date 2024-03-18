package com.example.token.token;

public class TokenDelimiter {

    private final String WORD_REGEX = "[a-zA-Z_][a-zA-Z0-9_]*";
    private final String NUMBER_REGEX = "[0-9]+";
    private final String NUMBER_REGEX2 = "[0-9.]+";

    private static boolean multiLineCommentCheck = false;
    private int multiLineCommentLineNumber = -1;
    private static int roundBracketCheck = 0;
    private static int squareBracketCheck = 0;
    private static int curlyBracketCheck = 0;

    public boolean isMultiLineComment() {
        return multiLineCommentCheck;
    }
    

    public String processLine(String line, int lineNumber) throws Exception {
        
        StringBuilder outputResponse = new StringBuilder();

        char[] characters = line.toCharArray();

        boolean comment = false;


        boolean isEmpty = false;

        String token = "";
        for (int i = 0 ; i < characters.length ; i++) {
            char nextCharacter = (i + 1 < characters.length) ? characters[i + 1] : '\0';
            if(characters[i] == ' ' && !isEmpty) {
                continue;
            }
            token += characters[i];
            if (isValidCharacter(token.charAt(0))) {
                throw new Exception("Line number " + lineNumber + " contains illegal character '" + token + "'!");
            }
            if(!comment && !this.multiLineCommentCheck) {
                if ((token + nextCharacter).equals("//")) {
                    comment = true;
                    i++;
                    token = "";
                } else if ((token + nextCharacter).equals("/*")) {
                    this.multiLineCommentCheck = true;
                    this.multiLineCommentLineNumber = lineNumber;
                    i++;
                    token = "";
                } else if ((token + nextCharacter).equals("*/")) {
                    throw new Exception("Line number " + lineNumber + " contains multiline comment ending without multiline comment context!");
                } else if (TokenHelper.keyWords.contains(token) && !TokenHelper.keyWords.contains(token + nextCharacter)) {
                    outputResponse.append(buildResponse(token, "KEY_WORD", lineNumber));
                    token = "";
                } else if (TokenHelper.keydelimiters.contains(token)) {
                    delimitersCorrecteness(token, lineNumber);
                    outputResponse.append(buildResponse(token, "DELIMITER", lineNumber));
                    token = "";
                } else if (token.matches(WORD_REGEX)
                        && !Character.isAlphabetic(nextCharacter)
                        && !Character.isDigit(nextCharacter)) {
                    outputResponse.append(buildResponse(token, "IDENTIFIER", lineNumber));
                    token = "";
                } else if (TokenHelper.keyOperators.contains(token) &&
                        !TokenHelper.keyOperators.contains(token + nextCharacter)) {
                    outputResponse.append(buildResponse(token, "OPERATOR", lineNumber));
                    token = "";
                } else if (token.matches(NUMBER_REGEX) &&
                        !token.contains(".") &&
                        nextCharacter != '.' &&
                        !Character.isDigit(nextCharacter)) {
                    outputResponse.append(buildResponse(token, "INT_CONSTANT", lineNumber));
                    token = "";
                } else if (token.matches(NUMBER_REGEX2) &&
                        token.contains(".") &&
                        !Character.isDigit(nextCharacter)) {
                    if (token.equals(".")) {
                        outputResponse.append(buildResponse(token, "DELIMITER", lineNumber));
                    } else {
                        outputResponse.append(buildResponse(token, "FLOAT_CONSTANT", lineNumber));
                    }
                    token = "";
                } else if (token.equals("\"")) {
                    isEmpty = true;
                } else if (isEmpty && token.charAt(token.length()-1) == '\"' && token.length() > 1) {
                    outputResponse.append(buildResponse(token.substring(1, token.length()-1), "LITERAL", lineNumber));
                    isEmpty = false;
                    token = "";
                }
            } else if (comment) {
                outputResponse.append(buildResponse(line.substring(i), "COMMENT", lineNumber));
                break;
            } else {
                if (!(line.substring(i).contains("*/"))) {
                    outputResponse.append(buildResponse(line.substring(i).trim(), "COMMENT", lineNumber));
                    break;
                } else {
                    for (int j = i + 1 ; j < characters.length ; j++) {
                        token += characters[j];
                        if (token.endsWith("*/")) {
                            this.multiLineCommentCheck = false;
                            outputResponse.append(buildResponse(token.substring(0, token.length()-2).trim(), "COMMENT", lineNumber));
                            token = "";
                            i = j + 2;
                        }
                    }
                }
            }
        }

        return outputResponse.toString();
    }

    public String buildResponse(String token, String tokenType, int lineNumber) {
        return "'" + token + "', " + tokenType + "; " + token.length() + "; linia " + lineNumber + "\n";
    }

    public boolean validateLine(String line) {
        String[] lineValidation = line.split("[^\\x00-\\x7F]");
        return lineValidation.length < 2;
    }

//    public String processLine(String line, int lineNumber) throws Exception {
//
//        if (!validateLine(line)) {
//            throw new Exception("Line number " + lineNumber + " contains illegal characters!");
//        }
//
//        StringBuilder outputResponse = new StringBuilder();
//
//        char[] lineCharArray = line.toCharArray();
//
//        boolean lineComment = false;
//
//        // if there is "something" in line
//        boolean string = false;
//
//        for (int i = 0; i < lineCharArray.length; i++) {
//            char currentChar = lineCharArray[i];
//            char nextCharacter = (i + 1 < lineCharArray.length) ? lineCharArray[i + 1] : '\0';
//
//            if (!lineComment && !this.multiLineCommentCheck) {
//                if (currentChar == ' ' && !string) {
//                    continue;
//                } else if ((currentChar == '/' && nextCharacter == '/')) {
//                    lineComment = true;
//                    i++;
//                } else if ((currentChar == '/' && nextCharacter == '*')) {
//                    this.multiLineCommentCheck = true;
//                    this.multiLineCommentLineNumber = lineNumber;
//                    i++;
//                } else if ((currentChar == '*' && nextCharacter == '/')) {
//                    throw new Exception("Line number " + lineNumber + " contains multiline comment ending without multiline comment context!");
//                } else if (TokenHelper.keyWords.contains(Character.toString(currentChar)) && !TokenHelper.keyWords.contains(Character.toString(currentChar) + nextCharacter)) {
//                    outputResponse.append(buildResponse(Character.toString(currentChar), "KEY_WORD", lineNumber));
//                } else if (TokenHelper.keydelimiters.contains(Character.toString(currentChar))) {
//                    delimitersCorrecteness(Character.toString(currentChar), lineNumber);
//                    outputResponse.append(buildResponse(Character.toString(currentChar), "DELIMITER", lineNumber));
//                } else if (Character.isLetter(currentChar) || currentChar == '_') {
//                    StringBuilder identifier = new StringBuilder();
//                    identifier.append(currentChar);
//                    while (i + 1 < lineCharArray.length && (Character.isLetterOrDigit(lineCharArray[i + 1]) || lineCharArray[i + 1] == '_')) {
//                        i++;
//                        identifier.append(lineCharArray[i]);
//                    }
//                    outputResponse.append(buildResponse(identifier.toString(), "IDENTIFIER", lineNumber));
//                } else if (TokenHelper.keyOperators.contains(Character.toString(currentChar)) && !TokenHelper.keyOperators.contains(Character.toString(currentChar) + nextCharacter)) {
//                    outputResponse.append(buildResponse(Character.toString(currentChar), "OPERATOR", lineNumber));
//                } else if (Character.isDigit(currentChar)) {
//                    StringBuilder number = new StringBuilder();
//                    number.append(currentChar);
//                    while (i + 1 < lineCharArray.length && (Character.isDigit(lineCharArray[i + 1]) || lineCharArray[i + 1] == '.')) {
//                        i++;
//                        number.append(lineCharArray[i]);
//                    }
//                    if (!number.toString().matches(NUMBER_REGEX) && !number.toString().matches(NUMBER_REGEX2)) {
//                        throw new Exception("Line number " + lineNumber + " contains illegal number format: " + number.toString());
//                    }
//                    if (number.toString().contains(".") && number.toString().matches(NUMBER_REGEX2) && nextCharacter == '.') {
//                        throw new Exception("Line number " + lineNumber + " contains illegal number format: " + number.toString());
//                    }
//                    outputResponse.append(buildResponse(number.toString(), number.toString().contains(".") ? "FLOAT_CONSTANT" : "INT_CONSTANT", lineNumber));
//                } else if (currentChar == '\"') {
//                    string = !string;
//                    if (!string) {
//                        // String ends
//                        outputResponse.append(buildResponse("\"", "DELIMITER", lineNumber));
//                    }
//                } else if (!isValidCharacter(currentChar)) {
//                    throw new Exception("Line number " + lineNumber + " contains illegal character '" + currentChar + "'!");
//                }
//            } else if (lineComment) {
//                outputResponse.append(buildResponse(line.substring(i), "COMMENT", lineNumber));
//                break;
//            } else {
//                if (!(line.substring(i).contains("*/"))) {
//                    outputResponse.append(buildResponse(line.substring(i).trim(), "COMMENT", lineNumber));
//                    break;
//                } else {
//                    for (int j = i + 1; j < lineCharArray.length; j++) {
//                        if (lineCharArray[j] == '*') {
//                            if (j + 1 < lineCharArray.length && lineCharArray[j + 1] == '/') {
//                                // Found closing of multi-line comment
//                                this.multiLineCommentCheck = false;
//                                outputResponse.append(buildResponse("*/", "DELIMITER", lineNumber));
//                                i = j + 2;
//                                break;
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//        return outputResponse.toString();
//    }

    private boolean isValidCharacter(char c)  {
        if (c == '#' || c =='@') {
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
