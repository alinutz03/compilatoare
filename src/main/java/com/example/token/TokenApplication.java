package com.example.token;

import com.example.token.token.TokenDelimiter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.FileReader;

@SpringBootApplication
public class TokenApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(TokenApplication.class, args);
        lexicalHelper();
    }

    private static void lexicalHelper() throws Exception {
        readFile("input.txt");
    }

    private static void readFile(String fileName) {
        BufferedReader reader;

        try {

            reader = new BufferedReader(new FileReader(fileName));
            String line = reader.readLine();
            TokenDelimiter lexicalHelper = new TokenDelimiter();
            StringBuilder result = new StringBuilder();
            int lineNumber = 1;

            while (line != null) {
                //System.out.println(line);
                // read next line
                result.append(lexicalHelper.processLine(line, lineNumber));
                lineNumber++;
                line = reader.readLine();
            }
            if(lexicalHelper.isMultiLineComment()) {
                throw new Exception("Multi line comment started at line " + lexicalHelper.isMultiLineComment()+ " in input file is unclosed");
            }
            System.out.println(result);

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
