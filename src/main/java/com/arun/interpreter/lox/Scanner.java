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
            scanToken();
        }

        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    // have we consumed all characters
    private boolean isAtEnd() {
        return current >= source.length();
    }


    private void scanToken() {
        char c = advance();
        switch (c) {
            // single character lexemes
            case '(': addToken(LEFT_PAREN); break;
            case ')': addToken(RIGHT_PAREN); break;
            case '{': addToken(LEFT_BRACE); break;
            case '}': addToken(RIGHT_BRACE); break;
            case ',': addToken(COMMA); break;
            case '.': addToken(DOT); break;
            case '-': addToken(MINUS); break;
            case '+': addToken(PLUS); break;
            case ';': addToken(SEMICOLON); break;
            case '*': addToken(STAR); break;
            case '!':
                // look at second character to determine if we’re on a != or merely a !
                addToken(match('=') ? BANG_EQUAL : BANG);
                break;
            case '=':
                addToken(match('=') ? EQUAL_EQUAL : EQUAL);
                break;
            case '<':
                addToken(match('=') ? LESS_EQUAL : LESS);
                break;
            case '>':
                addToken(match('=') ? GREATER_EQUAL : GREATER);
                break;
            case '/':
                if (match('/')) {
                    // A comment goes until the end of the line.
                    while (peek() != '\n' && !isAtEnd()) advance();
                } else {
                    addToken(SLASH);
                }
                break;
            case ' ':
            case '\r':
            case '\t':
                // Ignore whitespace.
                break;
            case '\n':
                // Next line
                line++;
                break;
            // String literals start with "
            // eg: "arun"
            case '"': string(); break;
            default:
                // To recognize the beginning of a number lexeme, we look for any digit. It’s kind of tedious to add a case statement for every decimal digit,
                // so use an if statement in the default case instead
                if (isDigit(c)) {
                    number();
                } else {
                    Lox.error(line, "Unexpected character.");
                }
                break;
        }
    }

    // consumes the next character in the source file and returns it.
    private char advance() {
        return source.charAt(current++);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    // grabs the text of the current lexeme and creates a new token for it
    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

    // look at the second character; only consume if it matches expected
    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;

        current++;
        return true;
    }

    // lookahead without advancing
    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    /**
     * Consume string literals
     * We consume characters until we hit the " that ends the string.
     * We also gracefully handle running out of input before the string is closed and report an error for that.
     * Handle multi-line strings
     */
    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++;
            advance();
        }

        if (isAtEnd()) {
            Lox.error(line, "Unterminated string.");
            return;
        }

        // The closing ".
        advance();

        // Trim the surrounding quotes and get the actual string value
        String value = source.substring(start + 1, current - 1);
        addToken(STRING, value);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    // Consume number literals
    // Consume as many digits as we find for the integer part of the literal.
    // Then we look for a fractional part, which is a decimal point (.) followed by at least one digit.
    // If we do have a fractional part, again, we consume as many digits as we can find.
    private void number() {
        while (isDigit(peek())) advance();

        // Look for a fractional part.
        if (peek() == '.' && isDigit(peekNext())) {
            // Consume the "."
            advance();

            while (isDigit(peek())) advance();
        }

        addToken(NUMBER,
                Double.parseDouble(source.substring(start, current)));
    }

    // Looking past the decimal point requires a second character of lookahead since we don’t want to consume the . until we’re sure there is a digit after it.
    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

}