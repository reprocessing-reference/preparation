/*
 * Auxip Entity Data Model Provider, 
 * The EDM model basically defines the available EntityTypes and the relation between the entities. 
 * An EntityType consists of primitive, complex or navigation properties. 
 * The model can be invoked with the Metadata Document request.
 */
package com.csgroup.auxip.odata;

import java.util.ArrayList;
import java.util.List;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlAbstractEdmProvider;
import org.apache.olingo.commons.api.edm.provider.CsdlAction;
import org.apache.olingo.commons.api.edm.provider.CsdlComplexType;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainer;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainerInfo;
import org.apache.olingo.commons.api.edm.provider.CsdlEntitySet;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityType;
import org.apache.olingo.commons.api.edm.provider.CsdlEnumType;
import org.apache.olingo.commons.api.edm.provider.CsdlParameter;
import org.apache.olingo.commons.api.edm.provider.CsdlSchema;
import org.apache.olingo.commons.api.ex.ODataException;

import com.csgroup.auxip.model.jpa.*;

public class AuxipEdmProvider extends CsdlAbstractEdmProvider {

  // Service Namespace
  public static final String NAMESPACE = Globals.NAMESPACE;

  // EDM Actions
  // Pause
  public static final String A_PAUSE_NAME = "Pause";
  public static final FullQualifiedName A_PAUSE_FQN = new FullQualifiedName(NAMESPACE, A_PAUSE_NAME);

  // Resume
  public static final String A_RESUME_NAME = "Resume";
  public static final FullQualifiedName A_RESUME_FQN = new FullQualifiedName(NAMESPACE, A_RESUME_NAME);

  // Cancel
  public static final String A_CANCEL_NAME = "Cancel";
  public static final FullQualifiedName A_CANCEL_FQN = new FullQualifiedName(NAMESPACE, A_CANCEL_NAME);

  // EDM Container
  public static final String CONTAINER_NAME = "Container";
  public static final FullQualifiedName CONTAINER = new FullQualifiedName(NAMESPACE, CONTAINER_NAME);

  @Override
  public CsdlEntityType getEntityType(FullQualifiedName entityTypeName) {

    // this method is called for each EntityType that are configured in the Schema
    CsdlEntityType entityType = null;

    // Product ET
    if (entityTypeName.equals(Product.FQN)) {
      entityType = Product.getEntityType();

    } else if (entityTypeName.equals(Attribute.FQN)) {
      entityType = Attribute.getEntityType();

    } else if (entityTypeName.equals(StringAttribute.FQN)) {
      entityType = StringAttribute.getEntityType();

    } else if (entityTypeName.equals(IntegerAttribute.FQN)) {
      entityType = IntegerAttribute.getEntityType();

    } else if (entityTypeName.equals(DoubleAttribute.FQN)) {
      entityType = DoubleAttribute.getEntityType();

    } else if (entityTypeName.equals(DateTimeOffsetAttribute.FQN)) {
      entityType = DateTimeOffsetAttribute.getEntityType();

    }else if (entityTypeName.equals(Subscription.FQN)) {
      entityType = Subscription.getEntityType();
      
    }
  
    return entityType;

  }

  @Override
  public CsdlComplexType getComplexType(final FullQualifiedName complexTypeName) {
    CsdlComplexType complexType = null;

    if (complexTypeName.equals(Checksum.FQN)) {
      complexType = Checksum.getComplexType();

    } else if (complexTypeName.equals(TimeRange.FQN)) {
      complexType = TimeRange.getComplexType();

    } else if (complexTypeName.equals(Property.FQN)) {
      complexType = Property.getComplexType();
    } 

    return complexType;
  }

  @Override
  public CsdlEntitySet getEntitySet(FullQualifiedName entityContainer, String entitySetName) {

    CsdlEntitySet entitySet = null;

    if (entityContainer.equals(CONTAINER)) {

      switch (entitySetName) {
        case Product.ES_NAME:
          entitySet = Product.getEntitySet();
          break;

        case Attribute.ES_NAME:
          entitySet = Attribute.getEntitySet();
          break;

        case StringAttribute.ES_NAME:
          entitySet = StringAttribute.getEntitySet();
          break;

        case IntegerAttribute.ES_NAME:
          entitySet = IntegerAttribute.getEntitySet();
          break;

        case DoubleAttribute.ES_NAME:
          entitySet = DoubleAttribute.getEntitySet();
          break;

        case DateTimeOffsetAttribute.ES_NAME:
          entitySet = DateTimeOffsetAttribute.getEntitySet();
          break;

        // case Metric.ES_NAME:
        //   entitySet = Metric.getEntitySet();
        //   break;

        case Subscription.ES_NAME:
          entitySet = Subscription.getEntitySet();
          break;

        default:
          break;
      }

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

    // // add EntityTypes
    List<CsdlEntityType> entityTypes = new ArrayList<CsdlEntityType>();

    FullQualifiedName[] fullQualifiedNames = new FullQualifiedName[] { Product.FQN, Attribute.FQN, StringAttribute.FQN,
    IntegerAttribute.FQN, DoubleAttribute.FQN, DateTimeOffsetAttribute.FQN  ,Subscription.FQN /*Metric.FQN,*/};

    for (FullQualifiedName fullQualifiedName : fullQualifiedNames) {
      entityTypes.add(getEntityType(fullQualifiedName));
    }

    schema.setEntityTypes(entityTypes);

    // // add Complex Types
    List<CsdlComplexType> complexTypes = new ArrayList<>();
    complexTypes.add(getComplexType(Checksum.FQN));
    complexTypes.add(getComplexType(TimeRange.FQN));
    complexTypes.add(getComplexType(Property.FQN));
    schema.setComplexTypes(complexTypes);


    //add EnumTypes
    List<CsdlEnumType> enumTypes = new ArrayList<>();

    enumTypes.add( SubscriptionStatus.getEnumType() );
    schema.setEnumTypes(enumTypes);

    // // add EntityContainer
    schema.setEntityContainer(getEntityContainer());

    // add subscription actions running pause and resume
    // add actions
    List<CsdlAction> actions = new ArrayList<>();
    actions.addAll(getActions(A_RESUME_FQN));
    actions.addAll(getActions(A_PAUSE_FQN));
    actions.addAll(getActions(A_CANCEL_FQN));

    schema.setActions(actions);

    
    // // finally
    List<CsdlSchema> schemas = new ArrayList<>();
    schemas.add(schema);

    return schemas;
  }


  @Override
  public List<CsdlAction> getActions(FullQualifiedName actionName) throws ODataException {
    
    List<CsdlAction> actions = new ArrayList<>();
    final List<CsdlParameter> parameters = new ArrayList<>();
    final CsdlParameter parameter = new CsdlParameter();
    parameter.setName(Subscription.ET_NAME);
    parameter.setType(Subscription.FQN);
    parameter.setNullable(false);
    parameters.add(parameter);

    if( actionName.equals( A_PAUSE_FQN ))
    {
      //Pause action
      CsdlAction pauseAction = new CsdlAction();
      pauseAction.setName(A_PAUSE_NAME);
      pauseAction.setParameters(parameters);
      pauseAction.setBound(true);
      // pauseAction.setReturnType(new CsdlReturnType().setCollection(false).setType(Subscription.FQN));

      actions.add(pauseAction);
      return actions;
    }else if( actionName.equals( A_RESUME_FQN ) )
    {
      //Resume action
      CsdlAction resumeAction = new CsdlAction();
      resumeAction.setName(A_RESUME_NAME);
      resumeAction.setParameters(parameters);
      resumeAction.setBound(true);
      // resumeAction.setReturnType(new CsdlReturnType().setCollection(false).setType(Subscription.FQN));

      actions.add(resumeAction);
      return actions;
    }else if( actionName.equals( A_CANCEL_FQN ) )
    {
      //Cancel action
      CsdlAction cancelAction = new CsdlAction();
      cancelAction.setName(A_CANCEL_NAME);
      cancelAction.setParameters(parameters);
      cancelAction.setBound(true);
      // cancelAction.setReturnType(new CsdlReturnType().setCollection(false).setType(Subscription.FQN));
      actions.add(cancelAction);
      return actions;
    }

    return null;
  }

  @Override
  public CsdlEntityContainer getEntityContainer() {

    // create EntitySets
    List<CsdlEntitySet> entitySets = new ArrayList<CsdlEntitySet>();

    String[] entitySetNames = new String[] { Product.ES_NAME, Attribute.ES_NAME, StringAttribute.ES_NAME,
        IntegerAttribute.ES_NAME, DoubleAttribute.ES_NAME, DateTimeOffsetAttribute.ES_NAME /*, Metric.ES_NAME */,
    Subscription.ES_NAME};

    for (String esName : entitySetNames) {
      entitySets.add(getEntitySet(CONTAINER, esName));
    }

    // create EntityContainer
    CsdlEntityContainer entityContainer = new CsdlEntityContainer();
    entityContainer.setName(CONTAINER_NAME);
    entityContainer.setEntitySets(entitySets);

    return entityContainer;
  }



}
