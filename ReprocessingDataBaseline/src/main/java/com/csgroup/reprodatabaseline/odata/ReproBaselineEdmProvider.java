/*
 * Auxip Entity Data Model Provider, 
 * The EDM model basically defines the available EntityTypes and the relation between the entities. 
 * An EntityType consists of primitive, complex or navigation properties. 
 * The model can be invoked with the Metadata Document request.
 */
package com.csgroup.reprodatabaseline.odata;

import java.util.ArrayList;
import java.util.List;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlAbstractEdmProvider;
import org.apache.olingo.commons.api.edm.provider.CsdlFunction;
import org.apache.olingo.commons.api.edm.provider.CsdlFunctionImport;
import org.apache.olingo.commons.api.edm.provider.CsdlComplexType;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainer;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainerInfo;
import org.apache.olingo.commons.api.edm.provider.CsdlEntitySet;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityType;
import org.apache.olingo.commons.api.edm.provider.CsdlParameter;
import org.apache.olingo.commons.api.edm.provider.CsdlSchema;
import org.apache.olingo.commons.api.edm.provider.CsdlReturnType;
import org.apache.olingo.commons.api.ex.ODataException;
import org.apache.olingo.commons.api.edm.provider.CsdlProperty;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import java.util.Arrays;

public class ReproBaselineEdmProvider extends CsdlAbstractEdmProvider {

  // Service Namespace
  public static final String NAMESPACE = "OData.CSC";

  // EDM Function
  public static final String FUNCTION_NAME = "getReprocessingDataBaseline";
  public static final FullQualifiedName FUNCTION_FQN = new FullQualifiedName(NAMESPACE, FUNCTION_NAME);

  // EDM Container
  public static final String CONTAINER_NAME = "Container";
  public static final FullQualifiedName CONTAINER = new FullQualifiedName(NAMESPACE, CONTAINER_NAME);


  public static final String PRODUCT_ET_NAME = "Product";
	public static final FullQualifiedName PRODUCT_FQN = new FullQualifiedName(NAMESPACE, PRODUCT_ET_NAME);
  public static final String PRODUCT_ES_NAME = "Products";


  @Override
  public CsdlEntityType getEntityType(FullQualifiedName entityTypeName) {


    // this method is called for each EntityType that are configured in the Schema
    CsdlEntityType entityType = null;

    if (entityTypeName.equals(PRODUCT_FQN)) 
    {
      entityType = new CsdlEntityType();
      CsdlProperty id = new CsdlProperty().setName("ID").setType(EdmPrimitiveTypeKind.Guid.getFullQualifiedName());
      CsdlProperty name = new CsdlProperty().setName("Name").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
      CsdlProperty auxipLink = new CsdlProperty().setName("AuxipLink").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
      CsdlProperty cloudLink = new CsdlProperty().setName("CloudLink").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
      
      entityType.setName(PRODUCT_ET_NAME);
      entityType.setProperties(Arrays.asList(id, name,auxipLink,cloudLink));
    }

    return entityType;

  }

  // @Override
  // public CsdlComplexType getComplexType(final FullQualifiedName complexTypeName) {
  //   CsdlComplexType complexType = null;
  //   return complexType;
  // }

  @Override
  public CsdlEntitySet getEntitySet(FullQualifiedName entityContainer, String entitySetName) {

    CsdlEntitySet entitySet = null;

    if (entityContainer.equals(CONTAINER) && entitySetName.equals(PRODUCT_ES_NAME) ) {
      entitySet = new CsdlEntitySet();
      entitySet.setName(PRODUCT_ES_NAME);
      entitySet.setType(PRODUCT_FQN);
      entitySet.setIncludeInServiceDocument(false);
    }
    return entitySet;
  }

  @Override
  public CsdlEntityContainerInfo getEntityContainerInfo(FullQualifiedName entityContainerName) {

    if (entityContainerName == null || entityContainerName.equals(CONTAINER)) {
      CsdlEntityContainerInfo entityContainerInfo = new CsdlEntityContainerInfo();
      entityContainerInfo.setContainerName(CONTAINER);
      return entityContainerInfo;
    }

    return null;
  }

  @Override
  public List<CsdlSchema> getSchemas() throws ODataException {
    // create Schema
    CsdlSchema schema = new CsdlSchema();
    schema.setNamespace(NAMESPACE);

    //add EntityTypes
    List<CsdlEntityType> entityTypes = new ArrayList<CsdlEntityType>();
    entityTypes.add(getEntityType(PRODUCT_FQN));
    schema.setEntityTypes(entityTypes);
    
    // // add EntityContainer
    schema.setEntityContainer(getEntityContainer());

    // add the main function
    List<CsdlFunction> functions = new ArrayList<>();
    functions.addAll(getFunctions(FUNCTION_FQN));
    schema.setFunctions(functions);

    // finally
    List<CsdlSchema> schemas = new ArrayList<>();
    schemas.add(schema);

    return schemas;
  }


  @Override
  public List<CsdlFunction> getFunctions(FullQualifiedName functionName) throws ODataException {
    
    List<CsdlFunction> functions = null;

    if( functionName.equals(FUNCTION_FQN))
    {
      functions = new ArrayList<>();

      final List<CsdlParameter> parameters = new ArrayList<>();
      final CsdlParameter satelliteUnit = new CsdlParameter();
      satelliteUnit.setName("satellite_unit");
      satelliteUnit.setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
      satelliteUnit.setNullable(false);
      parameters.add(satelliteUnit);

      final CsdlParameter productType = new CsdlParameter();
      productType.setName("product_type");
      productType.setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
      productType.setNullable(false);
      parameters.add(productType);

      final CsdlParameter dataTakeId = new CsdlParameter();
      dataTakeId.setName("data_take_id");
      dataTakeId.setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
      dataTakeId.setNullable(false);
      parameters.add(dataTakeId);


      CsdlFunction getReprocessingDataBaseline = new CsdlFunction();
      getReprocessingDataBaseline.setName(FUNCTION_NAME);
      getReprocessingDataBaseline.setParameters(parameters);
      getReprocessingDataBaseline.setBound(false);
      getReprocessingDataBaseline.setReturnType(new CsdlReturnType().setCollection(true).setType(PRODUCT_FQN));

      functions.add(getReprocessingDataBaseline);
    }

    return functions;

  }

  @Override
  public CsdlFunctionImport getFunctionImport(FullQualifiedName entityContainer, String functionImportName) {
    if(entityContainer.equals(CONTAINER) && functionImportName.equals(FUNCTION_NAME)) {
      
        return new CsdlFunctionImport()
                  .setName(functionImportName)
                  .setFunction(FUNCTION_FQN)
                  .setIncludeInServiceDocument(true);
      
    }

    return null;
  }

  @Override
  public CsdlEntityContainer getEntityContainer() {

    // create EntitySets
    List<CsdlEntitySet> entitySets = new ArrayList<CsdlEntitySet>();
    entitySets.add(getEntitySet(CONTAINER, PRODUCT_ES_NAME));
    // create EntityContainer
    CsdlEntityContainer entityContainer = new CsdlEntityContainer();
    entityContainer.setName(CONTAINER_NAME);
    entityContainer.setEntitySets(entitySets);


    // Create function imports
    List<CsdlFunctionImport> functionImports = new ArrayList<CsdlFunctionImport>();
    functionImports.add(getFunctionImport(CONTAINER, FUNCTION_NAME));
    entityContainer.setFunctionImports(functionImports);

    return entityContainer;
  }



}
