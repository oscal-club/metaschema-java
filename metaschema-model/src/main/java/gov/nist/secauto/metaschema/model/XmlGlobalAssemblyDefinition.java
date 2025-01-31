/*
 * Portions of this software was developed by employees of the National Institute
 * of Standards and Technology (NIST), an agency of the Federal Government and is
 * being made available as a public service. Pursuant to title 17 United States
 * Code Section 105, works of NIST employees are not subject to copyright
 * protection in the United States. This software may be subject to foreign
 * copyright. Permission in the United States and in foreign countries, to the
 * extent that NIST may hold copyright, to use, copy, modify, create derivative
 * works, and distribute this software and its documentation without fee is hereby
 * granted on a non-exclusive basis, provided that this notice and disclaimer
 * of warranty appears in all copies.
 *
 * THE SOFTWARE IS PROVIDED 'AS IS' WITHOUT ANY WARRANTY OF ANY KIND, EITHER
 * EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT LIMITED TO, ANY WARRANTY
 * THAT THE SOFTWARE WILL CONFORM TO SPECIFICATIONS, ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, AND FREEDOM FROM
 * INFRINGEMENT, AND ANY WARRANTY THAT THE DOCUMENTATION WILL CONFORM TO THE
 * SOFTWARE, OR ANY WARRANTY THAT THE SOFTWARE WILL BE ERROR FREE.  IN NO EVENT
 * SHALL NIST BE LIABLE FOR ANY DAMAGES, INCLUDING, BUT NOT LIMITED TO, DIRECT,
 * INDIRECT, SPECIAL OR CONSEQUENTIAL DAMAGES, ARISING OUT OF, RESULTING FROM,
 * OR IN ANY WAY CONNECTED WITH THIS SOFTWARE, WHETHER OR NOT BASED UPON WARRANTY,
 * CONTRACT, TORT, OR OTHERWISE, WHETHER OR NOT INJURY WAS SUSTAINED BY PERSONS OR
 * PROPERTY OR OTHERWISE, AND WHETHER OR NOT LOSS WAS SUSTAINED FROM, OR AROSE OUT
 * OF THE RESULTS OF, OR USE OF, THE SOFTWARE OR SERVICES PROVIDED HEREUNDER.
 */

package gov.nist.secauto.metaschema.model;

import gov.nist.secauto.metaschema.model.common.IAssemblyDefinition;
import gov.nist.secauto.metaschema.model.common.IAssemblyInstance;
import gov.nist.secauto.metaschema.model.common.IChoiceInstance;
import gov.nist.secauto.metaschema.model.common.IDefinition;
import gov.nist.secauto.metaschema.model.common.IFieldInstance;
import gov.nist.secauto.metaschema.model.common.IFlagInstance;
import gov.nist.secauto.metaschema.model.common.IMetaschema;
import gov.nist.secauto.metaschema.model.common.IModelInstance;
import gov.nist.secauto.metaschema.model.common.INamedInstance;
import gov.nist.secauto.metaschema.model.common.INamedModelInstance;
import gov.nist.secauto.metaschema.model.common.ModuleScopeEnum;
import gov.nist.secauto.metaschema.model.common.constraint.IAllowedValuesConstraint;
import gov.nist.secauto.metaschema.model.common.constraint.IAssemblyConstraintSupport;
import gov.nist.secauto.metaschema.model.common.constraint.ICardinalityConstraint;
import gov.nist.secauto.metaschema.model.common.constraint.IConstraint;
import gov.nist.secauto.metaschema.model.common.constraint.IExpectConstraint;
import gov.nist.secauto.metaschema.model.common.constraint.IIndexConstraint;
import gov.nist.secauto.metaschema.model.common.constraint.IIndexHasKeyConstraint;
import gov.nist.secauto.metaschema.model.common.constraint.IMatchesConstraint;
import gov.nist.secauto.metaschema.model.common.constraint.IUniqueConstraint;
import gov.nist.secauto.metaschema.model.common.datatype.markup.MarkupLine;
import gov.nist.secauto.metaschema.model.common.datatype.markup.MarkupMultiline;
import gov.nist.secauto.metaschema.model.xmlbeans.GlobalAssemblyDefinitionType;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

class XmlGlobalAssemblyDefinition implements IAssemblyDefinition { // NOPMD - intentional

  @NotNull
  private final GlobalAssemblyDefinitionType xmlAssembly;
  @NotNull
  private final IMetaschema metaschema;
  private XmlFlagContainerSupport flagContainer;
  private XmlModelContainerSupport modelContainer;
  private IAssemblyConstraintSupport constraints;

  /**
   * Constructs a new Metaschema Assembly definition from an XML representation bound to Java objects.
   * 
   * @param xmlAssembly
   *          the XML representation bound to Java objects
   * @param metaschema
   *          the containing Metaschema
   */
  public XmlGlobalAssemblyDefinition(
      @NotNull GlobalAssemblyDefinitionType xmlAssembly,
      @NotNull XmlMetaschema metaschema) {
    this.xmlAssembly = xmlAssembly;
    this.metaschema = metaschema;
  }

  /**
   * Get the underlying XML data.
   * 
   * @return the underlying XML data
   */
  @NotNull
  protected GlobalAssemblyDefinitionType getXmlAssembly() {
    return xmlAssembly;
  }

  @Override
  public IMetaschema getContainingMetaschema() {
    return metaschema;
  }

  /**
   * Lazy initialize the flag instances associated with this definition.
   * 
   * @return the flag container
   */
  protected XmlFlagContainerSupport initFlagContainer() {
    synchronized (this) {
      if (flagContainer == null) {
        flagContainer = new XmlFlagContainerSupport(getXmlAssembly(), this);
      }
      return flagContainer;
    }
  }

  @NotNull
  private Map<@NotNull String, ? extends IFlagInstance> getFlagInstanceMap() {
    return initFlagContainer().getFlagInstanceMap();
  }

  @Override
  public IFlagInstance getFlagInstanceByName(String name) {
    return getFlagInstanceMap().get(name);
  }

  @SuppressWarnings("null")
  @Override
  public Collection<@NotNull ? extends IFlagInstance> getFlagInstances() {
    return getFlagInstanceMap().values();
  }

  /**
   * Lazy initialize the model instances associated with this definition.
   */
  protected void initModelContainer() {
    synchronized (this) {
      if (modelContainer == null) {
        modelContainer = new XmlModelContainerSupport(getXmlAssembly(), this);
      }
    }
  }

  private Map<@NotNull String, ? extends INamedModelInstance> getNamedModelInstanceMap() {
    initModelContainer();
    return modelContainer.getNamedModelInstanceMap();
  }

  @Override
  public @Nullable INamedModelInstance getModelInstanceByName(String name) {
    return getNamedModelInstanceMap().get(name);
  }

  @SuppressWarnings("null")
  @Override
  public @NotNull Collection<@NotNull ? extends INamedModelInstance> getNamedModelInstances() {
    return getNamedModelInstanceMap().values();
  }

  private Map<@NotNull String, ? extends IFieldInstance> getFieldInstanceMap() {
    initModelContainer();
    return modelContainer.getFieldInstanceMap();
  }

  @Override
  public IFieldInstance getFieldInstanceByName(String name) {
    return getFieldInstanceMap().get(name);
  }

  @SuppressWarnings("null")
  @Override
  public Collection<@NotNull ? extends IFieldInstance> getFieldInstances() {
    return getFieldInstanceMap().values();
  }

  private Map<@NotNull String, ? extends IAssemblyInstance> getAssemblyInstanceMap() {
    initModelContainer();
    return modelContainer.getAssemblyInstanceMap();
  }

  @Override
  public IAssemblyInstance getAssemblyInstanceByName(String name) {
    return getAssemblyInstanceMap().get(name);
  }

  @SuppressWarnings("null")
  @Override
  public Collection<@NotNull ? extends IAssemblyInstance> getAssemblyInstances() {
    return getAssemblyInstanceMap().values();
  }

  @Override
  public List<@NotNull ? extends IChoiceInstance> getChoiceInstances() {
    initModelContainer();
    return modelContainer.getChoiceInstances();
  }

  @Override
  public List<@NotNull ? extends IModelInstance> getModelInstances() {
    initModelContainer();
    return modelContainer.getModelInstances();
  }

  /**
   * Used to generate the instances for the constraints in a lazy fashion when the constraints are
   * first accessed.
   */
  protected void checkModelConstraints() {
    synchronized (this) {
      if (constraints == null) {
        if (getXmlAssembly().isSetConstraint()) {
          constraints = new AssemblyConstraintSupport(getXmlAssembly().getConstraint());
        } else {
          constraints = IAssemblyConstraintSupport.NULL_CONSTRAINT;
        }
      }
    }
  }

  @Override
  public List<? extends IConstraint> getConstraints() {
    checkModelConstraints();
    return constraints.getConstraints();
  }

  @Override
  public List<? extends IAllowedValuesConstraint> getAllowedValuesContraints() {
    checkModelConstraints();
    return constraints.getAllowedValuesContraints();
  }

  @Override
  public List<? extends IMatchesConstraint> getMatchesConstraints() {
    checkModelConstraints();
    return constraints.getMatchesConstraints();
  }

  @Override
  public List<? extends IIndexHasKeyConstraint> getIndexHasKeyConstraints() {
    checkModelConstraints();
    return constraints.getIndexHasKeyConstraints();
  }

  @Override
  public List<? extends IExpectConstraint> getExpectConstraints() {
    checkModelConstraints();
    return constraints.getExpectConstraints();
  }

  @Override
  public List<? extends IIndexConstraint> getIndexConstraints() {
    checkModelConstraints();
    return constraints.getIndexContraints();
  }

  @Override
  public List<? extends IUniqueConstraint> getUniqueConstraints() {
    checkModelConstraints();
    return constraints.getUniqueConstraints();
  }

  @Override
  public List<? extends ICardinalityConstraint> getHasCardinalityConstraints() {
    checkModelConstraints();
    return constraints.getHasCardinalityConstraints();
  }

  @Override
  public boolean isInline() {
    return false;
  }

  @Override
  public INamedInstance getInlineInstance() {
    return null;
  }

  @SuppressWarnings("null")
  @Override
  public String getName() {
    return getXmlAssembly().getName();
  }

  @Override
  public String getUseName() {
    return getXmlAssembly().isSetUseName() ? getXmlAssembly().getUseName() : getName();
  }

  @Override
  public String getFormalName() {
    return getXmlAssembly().isSetFormalName() ? getXmlAssembly().getFormalName() : null;
  }

  @SuppressWarnings("null")
  @Override
  public MarkupLine getDescription() {
    return getXmlAssembly().isSetDescription() ? MarkupStringConverter.toMarkupString(getXmlAssembly().getDescription())
        : null;
  }

  @Override
  public boolean hasJsonKey() {
    return getXmlAssembly().isSetJsonKey();
  }

  @Override
  public IFlagInstance getJsonKeyFlagInstance() {
    IFlagInstance retval = null;
    if (hasJsonKey()) {
      retval = getFlagInstanceByName(getXmlAssembly().getJsonKey().getFlagRef());
    }
    return retval;
  }

  @Override
  public boolean isRoot() {
    return getXmlAssembly().isSetRootName();
  }

  @Override
  public String getRootName() {
    return getXmlAssembly().isSetRootName() ? getXmlAssembly().getRootName() : null;
  }

  @SuppressWarnings("null")
  @Override
  public ModuleScopeEnum getModuleScope() {
    return getXmlAssembly().isSetScope() ? getXmlAssembly().getScope() : IDefinition.DEFAULT_DEFINITION_MODEL_SCOPE;
  }

  @SuppressWarnings("null")
  @Override
  public MarkupMultiline getRemarks() {
    return getXmlAssembly().isSetRemarks() ? MarkupStringConverter.toMarkupString(getXmlAssembly().getRemarks()) : null;
  }
}
