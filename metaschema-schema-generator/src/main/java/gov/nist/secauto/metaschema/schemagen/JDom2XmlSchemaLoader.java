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

package gov.nist.secauto.metaschema.schemagen;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JDom2XmlSchemaLoader {
  @NotNull
  public static final String NS_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";

  @NotNull
  private final Document document;

  @SuppressWarnings("null")
  public JDom2XmlSchemaLoader(@NotNull Path path) throws JDOMException, IOException {
    this(new SAXBuilder().build(path.toFile()));
  }

  @SuppressWarnings("null")
  public JDom2XmlSchemaLoader(@NotNull InputStream is) throws JDOMException, IOException {
    this(new SAXBuilder().build(is));
  }

  public JDom2XmlSchemaLoader(@NotNull Document document) {
    this.document = document;
  }

  protected Document getNode() {
    return document;
  }

  @SuppressWarnings("null")
  @NotNull
  public List<@NotNull Element> getContent(
      @NotNull String path,
      @NotNull Map<@NotNull String, String> prefixToNamespaceMap) {

    Collection<Namespace> namespaces = prefixToNamespaceMap.entrySet().stream()
        .map(entry -> Namespace.getNamespace(entry.getKey(), entry.getValue()))
        .collect(Collectors.toList());
    XPathExpression<Element> xpath = XPathFactory.instance().compile(path, Filters.element(), null, namespaces);
    return xpath.evaluate(getNode());
  }
}
