/*
 * GWTEventService
 * Copyright (c) 2009, GWTEventService Committers
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package de.novanic.eventservice.service.registry.user;

import de.novanic.eventservice.client.event.domain.Domain;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * DomainUserMapping manages the allocation of users ({@link de.novanic.eventservice.service.registry.user.UserInfo}) to domains
 * ({@link de.novanic.eventservice.client.event.domain.Domain}) and provides several methods for access and modifications.
 *
 * @author sstrohschein
 *         <br>Date: 09.09.2009
 *         <br>Time: 15:04:36
 */
public class DomainUserMapping
{
    private final ConcurrentMap<Domain, Set<UserInfo>> myDomainUserInfoMap;

    /**
     * Creates a new, empty DomainUserMapping.
     */
    public DomainUserMapping() {
        myDomainUserInfoMap = new ConcurrentHashMap<Domain, Set<UserInfo>>();
    }

    /**
     * Adds a new user to a domain and creates a new domain entry when the domain is new to the DomainUserMapping.
     * @param aDomain domain to add the user to
     * @param aUserInfo user
     */
    public void addUser(Domain aDomain, UserInfo aUserInfo) {
        myDomainUserInfoMap.putIfAbsent(aDomain, new ConcurrentSkipListSet<UserInfo>());
        Set<UserInfo> theUsers = myDomainUserInfoMap.get(aDomain);
        theUsers.add(aUserInfo);
    }

    /**
     * Removes a user from all domains.
     * @param aUserInfo user
     */
    public void removeUser(UserInfo aUserInfo) {
        for(Map.Entry<Domain, Set<UserInfo>> theDomainUsersEntry: myDomainUserInfoMap.entrySet()) {
            Domain theDomain = theDomainUsersEntry.getKey();
            Set<UserInfo> theDomainUsers = theDomainUsersEntry.getValue();
            removeUser(theDomain, theDomainUsers, aUserInfo);
        }
    }

    /**
     * Removes a user from a specified domain.
     * @param aDomain domain
     * @param aUserInfo user
     * @return true when the user is removed from the domain, otherwise false
     */
    public boolean removeUser(Domain aDomain, UserInfo aUserInfo) {
        boolean isUserRemoved = false;

        Set<UserInfo> theDomainUsers = getUsers(aDomain);
        if(theDomainUsers != null) {
            isUserRemoved = removeUser(aDomain, theDomainUsers, aUserInfo);
        }
        return isUserRemoved;
    }

    /**
     * Removes a user from a specified domain and removes the domain when no other users are added to the domain.
     * @param aDomain domain
     * @param aDomainUsers users of the domain
     * @param aUser user
     * @return true when the user is removed from the domain, otherwise false
     */
    private boolean removeUser(Domain aDomain, Set<UserInfo> aDomainUsers, UserInfo aUser) {
        boolean isUserRemoved = aDomainUsers.remove(aUser);
        if(isUserRemoved) {
            if(aDomainUsers.isEmpty()) {
                //Atomic operation to remove only when the collection is empty. Otherwise another thread could add a user between the check of is empty and remove.
                //isEmpty is checked before for more performance for the most cases.
                myDomainUserInfoMap.remove(aDomain, new ConcurrentSkipListSet<UserInfo>());
            }
        }
        return isUserRemoved;
    }

    /**
     * Returns all domains which have added users
     * @return all domains which have added users
     */
    public Set<Domain> getDomains() {
        return myDomainUserInfoMap.keySet();
    }

    /**
     * Returns all domains to a user.
     * @param aUserInfo user
     * @return all domains where the user is added
     */
    public Set<Domain> getDomains(UserInfo aUserInfo) {
        if(aUserInfo != null) {
            Set<Domain> theDomains = new HashSet<Domain>(myDomainUserInfoMap.size());

            for(Map.Entry<Domain, Set<UserInfo>> theDomainUserEntry : myDomainUserInfoMap.entrySet()) {
                Set<UserInfo> theDomainUsers = theDomainUserEntry.getValue();
                if(theDomainUsers.contains(aUserInfo)) {
                    theDomains.add(theDomainUserEntry.getKey());
                }
            }
            return theDomains;
        }
        return new HashSet<Domain>(0);
    }

    /**
     * Returns all users of a domain.
     * @param aDomain domain
     * @return all users of the domain
     */
    public Set<UserInfo> getUsers(Domain aDomain) {
        return myDomainUserInfoMap.get(aDomain);
    }

    /**
     * Checks if a user is added to a domain.
     * @param aUserInfo user
     * @return true when the user is added to a domain, otherwise false
     */
    public boolean isUserContained(UserInfo aUserInfo) {
        for(Set<UserInfo> theDomainUsers: myDomainUserInfoMap.values()) {
            if(theDomainUsers.contains(aUserInfo)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a user is added to the domain.
     * @param aDomain domain
     * @param aUserInfo user
     * @return true when the user is added to the domain, otherwise false
     */
    public boolean isUserContained(Domain aDomain, UserInfo aUserInfo) {
        Set<UserInfo> theDomainUsers = myDomainUserInfoMap.get(aDomain);
        return theDomainUsers != null && theDomainUsers.contains(aUserInfo);
    }
}