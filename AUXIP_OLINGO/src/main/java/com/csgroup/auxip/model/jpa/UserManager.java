package com.csgroup.auxip.model.jpa;


import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.keycloak.TokenVerifier;
import org.keycloak.representations.AccessToken;

import ch.qos.logback.core.joran.conditional.ElseAction;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.Transient;

import com.csgroup.auxip.controller.AuxipBeanUtil;


/**
 * @author Naceur MESKINI
 */
public class UserManager {

    // This entity manager will be serves to get and update users
    private EntityManager entityManager;

    public UserManager() {
        entityManager = AuxipBeanUtil.getBean(EntityManagerFactory.class).createEntityManager();
    }
    /**
     * 
     * save all resources related the current entityManager
     *
     */
    public void saveUser()
    {
        EntityTransaction transac = entityManager.getTransaction();
        try {
            
            transac.begin(); 
            // this code looks strange and a bit counter-intuitive! 
            // but it's working because the life cycle of a managed entities are related the entity manager
            transac.commit();

        } catch (Exception e) {
            transac.rollback();
        }
    }

    /**
     * This method is responsable of getting the user from
     * @param request
     * @return User
     */
    public User getUser(ODataRequest request) 
    {
        User user;
        AccessToken token;
        try
        {
            String bearerToken = request.getHeader("Authorization").replace("Bearer ", "") ;
            token = TokenVerifier.create(bearerToken,AccessToken.class).getToken();
            
        }catch (Exception e)
        {
           return null;
        } 

        String userName = token.getPreferredUsername();

        String queryString = "SELECT user FROM " + User.class.getName() + " user WHERE user.name =  'userName'".replace("userName", userName) ;
        try {
            Query query = this.entityManager.createQuery(queryString);
            user = (User)query.getSingleResult();
            // add Keycloak roles 
            user.setRoles(token.getRealmAccess().getRoles());

        } catch (NoResultException e) 
        {
            // create a user if not exits
            EntityTransaction transac = this.entityManager.getTransaction();

            transac.begin();
            user = new User(token);            
            entityManager.persist(user);
            transac.commit();
             
		}

        return user;
    }

    public  void close() {
        try {
             this.entityManager.close();
        } catch (Exception e) {
            //TODO: handle exception
        }
       
    }
}
