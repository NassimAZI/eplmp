/*******************************************************************************
  * Copyright (c) 2017-2019 DocDoku.
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v1.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributors:
  *    DocDoku - initial API and implementation
  *******************************************************************************/

package org.polarsys.eplmp.core.security;

import org.polarsys.eplmp.core.common.User;
import org.polarsys.eplmp.core.common.UserGroup;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * This class can be attached to any entity so that an access control
 * list will be applied.
 * In that way, the default access rights defined at the workspace level will be
 * overridden.
 *
 * @author Florent Garin
 * @version 1.1, 17/07/09
 * @since   V1.1
 */
@Table(name="ACL")
@Entity
@NamedQueries ({
    @NamedQuery(name="ACL.removeUserEntries", query = "DELETE FROM ACLUserEntry a WHERE a.acl.id = :aclId"),
    @NamedQuery(name="ACL.removeUserGroupEntries", query = "DELETE FROM ACLUserGroupEntry a WHERE a.acl.id = :aclId")
})
public class ACL implements Serializable, Cloneable{

    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Id
    private int id;

    @OneToMany(cascade=CascadeType.ALL, orphanRemoval = true, mappedBy="acl", fetch=FetchType.EAGER)
    @MapKey(name="principal")
    private Map<User,ACLUserEntry> userEntries=new HashMap<User,ACLUserEntry>();

    @OneToMany(cascade=CascadeType.ALL, orphanRemoval = true, mappedBy="acl", fetch=FetchType.EAGER)
    @MapKey(name="principal")
    private Map<UserGroup,ACLUserGroupEntry> groupEntries=new HashMap<UserGroup,ACLUserGroupEntry>();

    private boolean enabled=true;

    public ACL(){
        
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean hasReadAccess(User user){
        ACLUserEntry userAccess=userEntries.get(user);
        if(userAccess!=null)
            return !userAccess.getPermission().equals(ACLPermission.FORBIDDEN);
        else{
            for(Map.Entry<UserGroup, ACLUserGroupEntry> entry:groupEntries.entrySet()){
                if(entry.getKey().isMember(user) && !entry.getValue().getPermission().equals(ACLPermission.FORBIDDEN)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasWriteAccess(User user){
        ACLUserEntry userAccess=userEntries.get(user);
        if(userAccess!=null)
            return userAccess.getPermission().equals(ACLPermission.FULL_ACCESS);
        else{
            for(Map.Entry<UserGroup, ACLUserGroupEntry> entry:groupEntries.entrySet()){
                if(entry.getKey().isMember(user) && entry.getValue().getPermission().equals(ACLPermission.FULL_ACCESS)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void addEntry(User user, ACLPermission perm){
        userEntries.put(user, new ACLUserEntry(this,user,perm));
    }

    public void addEntry(UserGroup group, ACLPermission perm){
        groupEntries.put(group, new ACLUserGroupEntry(this,group,perm));
    }

    public void removeEntry(User user){
        userEntries.remove(user);
    }

    public void removeEntry(UserGroup group){
        groupEntries.remove(group);
    }

    public Map<User, ACLUserEntry> getUserEntries() {
        return userEntries;
    }

    public void setUserEntries(Map<User, ACLUserEntry> userEntries) {
        this.userEntries = userEntries;
    }

    public Map<UserGroup, ACLUserGroupEntry> getGroupEntries() {
        return groupEntries;
    }

    public void setGroupEntries(Map<UserGroup, ACLUserGroupEntry> groupEntries) {
        this.groupEntries = groupEntries;
    }

    /**
     * perform a deep clone operation
     */
    @Override
    public ACL clone() {
        ACL clone;
        try {
            clone = (ACL) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
        //perform a deep copy
        Map<User,ACLUserEntry> clonedUserEntries = new HashMap<>();
        for (Map.Entry<User,ACLUserEntry> entry : userEntries.entrySet()) {
            ACLUserEntry aclEntry = entry.getValue().clone();
            aclEntry.setACL(clone);
            clonedUserEntries.put(entry.getKey(),aclEntry);
        }
        clone.userEntries = clonedUserEntries;

        //perform a deep copy
        Map<UserGroup,ACLUserGroupEntry> clonedGroupEntries = new HashMap<>();
        for (Map.Entry<UserGroup,ACLUserGroupEntry> entry : groupEntries.entrySet()) {
            ACLUserGroupEntry aclEntry = entry.getValue().clone();
            aclEntry.setACL(clone);
            clonedGroupEntries.put(entry.getKey(),aclEntry);
        }
        clone.groupEntries = clonedGroupEntries;
        return clone;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ACL)) {
            return false;
        }
        ACL acl = (ACL) obj;
        return acl.id == id;
    }

    @Override
    public int hashCode() {
        return id;
    }

}
