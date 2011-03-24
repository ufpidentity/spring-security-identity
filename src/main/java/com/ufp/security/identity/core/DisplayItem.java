package com.ufp.security.identity.core;

import java.io.Serializable;

public class DisplayItem implements Serializable {
    private static final long serialVersionUID = -1766676948426740271L;
    private String name;
    private String htmlInput;
    private String nickName;

    public DisplayItem() {
        this(null, null, null);
    }

    public DisplayItem(String name, String htmlInput, String nickName) {
        this.name = name;
        this.htmlInput = htmlInput;
        this.nickName = nickName;
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

    public String getNickName() {
        return this.nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

}
