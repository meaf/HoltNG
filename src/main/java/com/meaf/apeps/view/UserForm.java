package com.meaf.apeps.view;

import com.meaf.apeps.model.User;
import com.meaf.apeps.model.UserRepository;
import com.vaadin.data.converter.LocalDateToDateConverter;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.TextField;
import org.vaadin.spring.events.EventBus;
import org.vaadin.teemu.switchui.Switch;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.form.AbstractForm;
import org.vaadin.viritin.layouts.MFormLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

@UIScope
@SpringComponent
public class UserForm extends AbstractForm<User> {

    private static final long serialVersionUID = 1L;

    EventBus.UIEventBus eventBus;
    UserRepository repo;

    TextField name = new MTextField("Name");

    UserForm(UserRepository r, EventBus.UIEventBus b) {
        super(User.class);
        this.repo = r;
        this.eventBus = b;

        // On save & cancel, publish events that other parts of the UI can listen
        setSavedHandler(person -> {
            // persist changes
            repo.save(person);
            // send the event for other parts of the application
            eventBus.publish(this, new PersonModifiedEvent(person));
        });
        setResetHandler(p -> eventBus.publish(this, new PersonModifiedEvent(p)));

        setSizeUndefined();
    }

    @Override
    protected Component createContent() {
        return new MVerticalLayout(
                new MFormLayout(
                        name
                ).withWidth(""),
                getToolbar()
        ).withWidth("");
    }

}
