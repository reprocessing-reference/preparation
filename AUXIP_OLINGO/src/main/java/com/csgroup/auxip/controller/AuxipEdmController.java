package com.csgroup.auxip.controller;

import java.util.ArrayList;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataHttpHandler;
import org.apache.olingo.server.api.ServiceMetadata;
import org.keycloak.TokenVerifier;
import org.keycloak.common.VerificationException;
import org.keycloak.representations.AccessToken;
import org.apache.olingo.commons.api.edmx.EdmxReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.csgroup.auxip.odata.AuxipActionProcessor;
import com.csgroup.auxip.odata.AuxipComplexProcessor;
import com.csgroup.auxip.odata.AuxipEdmProvider;
import com.csgroup.auxip.odata.AuxipEntityCollectionProcessor;
import com.csgroup.auxip.odata.AuxipPrimitiveProcessor;
import com.csgroup.auxip.odata.AuxipEntityProcessor;
import com.csgroup.auxip.model.repository.Storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// TODO: Auto-generated Javadoc
/**
 * The Class AuxipEdmController.
 *
 * @author rohitghatol
 */
@RestController
@RequestMapping(AuxipEdmController.URI)
public class AuxipEdmController {

    private static final Logger LOG = LoggerFactory.getLogger(AuxipEdmController.class);

	public static final String URI = "/auxipv2";

    	/** The ProductRepository to get data with */

    private Storage storage;
    @Autowired
    private EntityManagerFactory  entityManagerFactory;

	/**
	 * Process.
	 *
	 * @param request the req
	 * @param response the Http response
	 */
	@RequestMapping("/**")
	public void process(HttpServletRequest request, HttpServletResponse response) {


        // Get a bearer token 
        AccessToken token;
        try
        {
            String bearerToken = request.getHeader("Authorization").replace("Bearer ", "") ;
            token = TokenVerifier.create(bearerToken,AccessToken.class).getToken();
            
        }catch (Exception e)
        {
            token = null;
            LOG.error("Server Error occurred while trying to get AccessToken in Auxip request", e);
        }

        try {

            if( token == null )
            {
                throw new RuntimeException("Server Error occurred : AccessToken not found in Auxip request");
            }

            storage = new Storage();
            storage.setEntityManagerFactory(entityManagerFactory);
            storage.setAccessToken(token) ;
            // create odata handler and configure it with EdmProvider and Processor
            OData odata = OData.newInstance();
            ServiceMetadata edm = odata.createServiceMetadata(new AuxipEdmProvider(), new ArrayList<EdmxReference>());
            ODataHttpHandler handler = odata.createHandler(edm);

            handler.register(new AuxipEntityCollectionProcessor(storage));
            handler.register(new AuxipEntityProcessor(storage));
            handler.register(new AuxipPrimitiveProcessor(storage));
            handler.register(new AuxipComplexProcessor(storage));
            handler.register(new AuxipActionProcessor(storage));
            
            // let the handler do the work
            handler.process(new HttpServletRequestWrapper(request) {
                // Spring MVC matches the whole path as the servlet path
                // Olingo wants just the prefix, ie upto /odata, so that it
                // can parse the rest of it as an OData path. So we need to override
                // getServletPath()
                @Override
                public String getServletPath() {
                    return AuxipEdmController.URI;
                }
            }, response);
          } catch (RuntimeException e) {
            LOG.error("Server Error occurred in Auxip service", e);
          }
        }

}
