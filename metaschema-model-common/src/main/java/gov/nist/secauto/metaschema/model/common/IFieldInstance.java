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

package gov.nist.secauto.metaschema.model.common;

import gov.nist.secauto.metaschema.model.common.metapath.MetapathExpression;
import gov.nist.secauto.metaschema.model.common.metapath.evaluate.instance.DefaultMetaschemaContext;
import gov.nist.secauto.metaschema.model.common.metapath.evaluate.instance.IInstanceSet;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Objects;

import javax.xml.namespace.QName;

public interface IFieldInstance extends INamedModelInstance, IField {
  @Override
  default String getXmlNamespace() {
    return isInXmlWrapped() ? INamedModelInstance.super.getXmlNamespace() : null;
  }

  @Override
  default QName getXmlQName() {
    return isInXmlWrapped() ? INamedModelInstance.super.getXmlQName() : null;
  }

  @Override
  default String getJsonName() {
    @NotNull
    String retval;
    if (getMaxOccurs() == -1 || getMaxOccurs() > 1) {
      @SuppressWarnings("null")
      @NotNull
      String groupAsName = Objects.requireNonNull(getGroupAsName(), "null group-as name");
      retval = groupAsName;
    } else {
      retval = getEffectiveName();
    }
    return retval;
  }

  @Override
  default String getGroupAsXmlNamespace() {
    return isInXmlWrapped() ? getContainingMetaschema().getXmlNamespace().toASCIIString() : null;
  }

  @Override
  IFieldDefinition getDefinition();

  /**
   * Determines if the field is configured to have a wrapper in XML.
   * 
   * @return {@code true} if an XML wrapper is required, or {@code false} otherwise
   */
  boolean isInXmlWrapped();

  /**
   * Determines if the instance is a simple field value without flags, or if it has a complex
   * structure (i.e, flags, model).
   * 
   * @return {@code true} if the instance contains only a value, or {@code false} otherwise
   */
  default boolean isSimple() {
    return getDefinition().isSimple();
  }

  @Override
  default IInstanceSet evaluateMetapathInstances(MetapathExpression metapath) {
    return metapath.evaluateMetaschemaInstance(
        new DefaultMetaschemaContext(IInstanceSet.newInstanceSet(Collections.singleton(this))));
  }
}
