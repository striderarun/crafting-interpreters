package com.arun.interpreter.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.arun.interpreter.lox.TokenType.*;

class Scanner {
    // the raw source code
    private final String source;

    /** keep track of where scanner is in the source code */
    // points to the first character in the lexeme being scanned
    private int start = 0;

    // points at the character currently being considered
    private int current = 0;

    // tracks what source line current is on so we can produce tokens that know their location.
    private int line = 1;

    private final List<Token> tokens = new ArrayList<>();

    Scanner(String source) {
        this.source = source;
    }

    List<Token> scanTokens() {
        while (!isAtEnd()) {
            // We are at the beginning of the next lexeme.
            start = current;

        }

        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    // have we consumed all characters
    private boolean isAtEnd() {
        return current >= source.length();
    }

}