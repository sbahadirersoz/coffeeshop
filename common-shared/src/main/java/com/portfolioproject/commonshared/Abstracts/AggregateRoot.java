package com.portfolioproject.commonshared.Abstracts;

import com.portfolioproject.commonshared.Events.IDomainEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AggregateRoot<TId extends AggregateID> implements IAggregateRoot
{
    private TId id;

    protected AggregateRoot() {
    }

    public TId getId() {
        return id;
    }

    protected AggregateRoot(TId id) {
        this.id = id;
    }

    private final List<IDomainEvent> domainEvents = new ArrayList<>();

    public List<IDomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    protected void addEvent(IDomainEvent event) {
        this.domainEvents.add(event);

    }
    protected void clearEvents() {
        this.domainEvents.clear();
    }
}
