package com.meaf.apeps.view;

import com.meaf.apeps.model.User;

import java.io.Serializable;

public class PersonModifiedEvent implements Serializable {

    private final User person;

    public PersonModifiedEvent(User p) {
        this.person = p;
    }

    public User getPerson() {
        return person;
    }
    
}
