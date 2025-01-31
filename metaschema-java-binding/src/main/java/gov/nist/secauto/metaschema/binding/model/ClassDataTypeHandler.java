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

package gov.nist.secauto.metaschema.binding.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import gov.nist.secauto.metaschema.binding.io.BindingException;
import gov.nist.secauto.metaschema.binding.io.json.IJsonParsingContext;
import gov.nist.secauto.metaschema.binding.io.json.IJsonWritingContext;
import gov.nist.secauto.metaschema.binding.io.json.JsonUtil;
import gov.nist.secauto.metaschema.binding.io.xml.IXmlParsingContext;
import gov.nist.secauto.metaschema.binding.io.xml.IXmlWritingContext;
import gov.nist.secauto.metaschema.model.common.datatype.IJavaTypeAdapter;
import gov.nist.secauto.metaschema.model.common.util.ObjectUtils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

class ClassDataTypeHandler implements IDataTypeHandler {
  @NotNull
  private final IBoundNamedModelInstance property;
  @NotNull
  private final IClassBinding classBinding;

  public ClassDataTypeHandler(@NotNull IClassBinding classBinding, @NotNull IBoundNamedModelInstance property) {
    this.classBinding = ObjectUtils.requireNonNull(classBinding, "classBinding");
    this.property = ObjectUtils.requireNonNull(property, "property");
  }

  @Override
  public IBoundNamedModelInstance getProperty() {
    return property;
  }

  @Override
  public IJavaTypeAdapter<?> getJavaTypeAdapter() {
    // this is always null
    return null;
  }

  @Override
  public IClassBinding getClassBinding() {
    return classBinding;
  }

  @Override
  public boolean isUnwrappedValueAllowedInXml() {
    // classes are always wrapped
    return false;
  }

  @Override
  public List<Object> get(Object parentInstance, boolean requiresJsonKey, IJsonParsingContext context)
      throws IOException {
    JsonParser parser = context.getReader(); // NOPMD - intentional
    boolean objectWrapper = JsonToken.START_OBJECT.equals(parser.currentToken());
    if (objectWrapper) {
      JsonUtil.assertAndAdvance(parser, JsonToken.START_OBJECT);
    }

    List<Object> retval = classBinding.readItem(parentInstance, requiresJsonKey, context);

    if (objectWrapper) {
      JsonUtil.assertAndAdvance(parser, JsonToken.END_OBJECT);
    }
    return retval;
  }

  @Override
  public Object get(Object parentInstance, StartElement start, IXmlParsingContext context)
      throws IOException, XMLStreamException {
    return classBinding.readItem(parentInstance, start, context);
  }

  @Override
  public void accept(Object item, QName currentParentName, IXmlWritingContext context)
      throws IOException, XMLStreamException {
    classBinding.writeItem(item, currentParentName, context);
  }

  @Override
  public void writeItems(Collection<@NotNull ? extends Object> items, boolean writeObjectWrapper,
      IJsonWritingContext context)
      throws IOException {
    classBinding.writeItems(items, writeObjectWrapper, context);
  }

  @Override
  public Object copyItem(@NotNull Object fromItem, Object parentInstance) throws BindingException {
    return classBinding.copyBoundObject(fromItem, parentInstance);
  }
}
