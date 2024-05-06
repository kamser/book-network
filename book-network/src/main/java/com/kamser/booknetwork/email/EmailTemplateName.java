package com.kamser.booknetwork.email;

import lombok.Getter;

@Getter
public enum EmailTemplateName {
    ACTIVATE_ACCOUNT("activate_account");  //the name here is for the activate_account.html that I have to create in the "templates" folder, so spring search for the template file with this name.

    private final String name;

    EmailTemplateName(String name){
        this.name = name;
    }
}
