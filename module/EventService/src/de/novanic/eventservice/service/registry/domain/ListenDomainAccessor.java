package de.novanic.eventservice.service.registry.domain;

import de.novanic.eventservice.client.event.domain.Domain;

import java.util.Set;

/**
 * @author sstrohschein
 *         <br>Date: 29.10.2009
 *         <br>Time: 22:00:24
 */
public interface ListenDomainAccessor
{
    /**
     * Returns all domains where the user is registered to.
     * @param aUserId user
     * @return domains where the user is registered to
     */
    Set<Domain> getListenDomains(String aUserId);

    /**
     * Returns all registered/activated domains.
     * @return all registered/activated domains
     */
    Set<Domain> getListenDomains();
}