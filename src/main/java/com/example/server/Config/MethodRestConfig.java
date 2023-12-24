package com.example.server.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import com.example.server.User.UserInformation;

import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.Type;

@Configuration
public class MethodRestConfig implements RepositoryRestConfigurer {
    //private String url = "http://localhost:3000";

    @Autowired
    private EntityManager entityManager;


    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {
        config.exposeIdsFor(entityManager.getMetamodel().getEntities().stream().map(Type::getJavaType).toArray(Class[]::new));

        HttpMethod[] disableMethods = {
            HttpMethod.DELETE,
        };
        
        disableHttpMethods(UserInformation.class, config, disableMethods);
    }

    private void disableHttpMethods(Class<?> domainType, RepositoryRestConfiguration config, HttpMethod[] methods) {
        config.getExposureConfiguration().forDomainType(domainType)
            .withItemExposure((metadata, itemMethods) -> itemMethods.disable(methods))
            .withCollectionExposure((metadata, collectionMethods) -> collectionMethods.disable(methods));
    }
    
}
