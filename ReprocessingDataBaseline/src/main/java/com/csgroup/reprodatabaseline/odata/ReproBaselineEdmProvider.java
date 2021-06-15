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


  // public static final String PRODUCT_ET_NAME = "Product";
  public static final String PRODUCT_CT_NAME = "Product";
	public static final FullQualifiedName PRODUCT_FQN = new FullQualifiedName(NAMESPACE, PRODUCT_CT_NAME);

  public static final String DATA_BASELINE_ET_NAME = "DataBaseline";
	public static final FullQualifiedName DATA_BASELINE_FQN = new FullQualifiedName(NAMESPACE, DATA_BASELINE_ET_NAME);
  public static final String DATA_BASELINE_ES_NAME = "DataBaselines";



  @Override
  public CsdlComplexType getComplexType(final FullQualifiedName complexTypeName) {
    CsdlComplexType complexType = null;

    if (complexTypeName.equals(PRODUCT_FQN)) {
      {
        complexType = new CsdlComplexType().setName(PRODUCT_CT_NAME).setProperties(Arrays.asList(
        new CsdlProperty().setName("Name").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName()).setNullable(false),
        new CsdlProperty().setName("AuxipLink").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName()).setNullable(false)  ));
      }
    }

    return complexType;
  }

  @Override
  public CsdlEntityType getEntityType(FullQualifiedName entityTypeName) {


    // this method is called for each EntityType that are configured in the Schema
    CsdlEntityType entityType = null;

    // if(entityTypeName.equals(PRODUCT_FQN)) 
    // {
    //   entityType = new CsdlEntityType();
    //   CsdlProperty name = new CsdlProperty().setName("Name").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName()).setNullable(false);
    //   CsdlProperty auxipLink = new CsdlProperty().setName("AuxipLink").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName()).setNullable(false);

    //   entityType.setName(PRODUCT_ET_NAME);
    //   entityType.setProperties(Arrays.asList(name,auxipLink));
    // }
    if(entityTypeName.equals(DATA_BASELINE_FQN)) 
    {
      entityType = new CsdlEntityType();
      CsdlProperty level0Name = new CsdlProperty().setName("Level0").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
      CsdlProperty auxiDataFiles = new CsdlProperty().setName("AuxDataFiles").setCollection(true).setType(PRODUCT_FQN);
      
      entityType.setName(DATA_BASELINE_ET_NAME);
      entityType.setProperties(Arrays.asList(level0Name,auxiDataFiles));
    }

    return entityType;

  }

  @Override
  public CsdlEntitySet getEntitySet(FullQualifiedName entityContainer, String entitySetName) {

    CsdlEntitySet entitySet = null;

    if( entityContainer.equals(CONTAINER) && entitySetName.equals(DATA_BASELINE_ES_NAME) )
    {
      entitySet = new CsdlEntitySet();
      entitySet.setName(DATA_BASELINE_ES_NAME);
      entitySet.setType(DATA_BASELINE_FQN);
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
    // entityTypes.add(getEntityType(PRODUCT_FQN));
    entityTypes.add(getEntityType(DATA_BASELINE_FQN));
    schema.setEntityTypes(entityTypes);
    
    // add Complex Types
    List<CsdlComplexType> complexTypes = new ArrayList<>();
    complexTypes.add(getComplexType(PRODUCT_FQN));
    schema.setComplexTypes(complexTypes);

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

      List<String> paramNames = List.of("l0_names","mission","unit","product_type");

      for(String name : paramNames)
      {
        final CsdlParameter parameter = new CsdlParameter();
        parameter.setName(name);
        parameter.setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
        parameter.setNullable(false);
        parameters.add(parameter);    
      }

      CsdlFunction getReprocessingDataBaseline = new CsdlFunction();
      getReprocessingDataBaseline.setName(FUNCTION_NAME);
      getReprocessingDataBaseline.setParameters(parameters);
      getReprocessingDataBaseline.setBound(false);
      getReprocessingDataBaseline.setReturnType(new CsdlReturnType().setCollection(true).setType(DATA_BASELINE_FQN));
      functions.add(getReprocessingDataBaseline);

      // Overloading of getReprocessingDataBaseline function 
      final List<CsdlParameter> parameters2 = new ArrayList<>();
      paramNames = List.of("start","stop","mission","unit","product_type");

      for(String name : paramNames)
      {
        final CsdlParameter parameter = new CsdlParameter();
        parameter.setName(name);

        if(name.equals("start") || name.equals("stop"))
        {
          parameter.setType(EdmPrimitiveTypeKind.DateTimeOffset.getFullQualifiedName());
        }else
        {
          parameter.setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
        }
        parameter.setNullable(false);
        parameters2.add(parameter);    
      }

      CsdlFunction getReprocessingDataBaseline2 = new CsdlFunction();
      getReprocessingDataBaseline2.setName(FUNCTION_NAME);
      getReprocessingDataBaseline2.setParameters(parameters2);
      getReprocessingDataBaseline2.setBound(false);
      getReprocessingDataBaseline2.setReturnType(new CsdlReturnType().setCollection(true).setType(DATA_BASELINE_FQN));
      functions.add(getReprocessingDataBaseline2);

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
    // entitySets.add(getEntitySet(CONTAINER, PRODUCT_ES_NAME));
    // entitySets.add(getEntitySet(CONTAINER, DATA_BASELINE_ES_NAME));
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
