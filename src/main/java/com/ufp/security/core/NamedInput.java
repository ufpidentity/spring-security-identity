package com.ufp.security.core;

public class NamedInput {
    private String name;
    private String htmlInput;

    public NamedInput(String name, String htmlInput) {
        this.name = name;
        this.htmlInput = htmlInput;
    }
        
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHtmlInput() {
        return this.htmlInput;
    }

    public void setHtmlInput(String htmlInput) {
        this.htmlInput = htmlInput;
    }
}
