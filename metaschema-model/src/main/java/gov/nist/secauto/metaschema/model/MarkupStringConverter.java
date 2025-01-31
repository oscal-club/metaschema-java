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

import gov.nist.secauto.metaschema.model.common.datatype.markup.MarkupLine;
import gov.nist.secauto.metaschema.model.common.datatype.markup.MarkupMultiline;
import gov.nist.secauto.metaschema.model.common.util.ObjectUtils;
import gov.nist.secauto.metaschema.model.xmlbeans.MarkupLineDatatype;
import gov.nist.secauto.metaschema.model.xmlbeans.MarkupMultilineDatatype;

import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlTokenSource;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.StringWriter;

// TODO: is this needed, or can we use methods on the markup implementations?
final class MarkupStringConverter {
  private MarkupStringConverter() {
    // disable construction
  }

  /**
   * Converts HTML-like markup into a MarkupLine.
   * 
   * @param content
   *          the content to convert
   * @return the equivalent formatted text as a MarkupLine
   * @throws IllegalArgumentException
   *           if the {@code content} argument contains malformed markup
   */
  @NotNull
  public static MarkupLine toMarkupString(@NotNull MarkupLineDatatype content) {
    String html = processHTML(content);
    return MarkupLine.fromHtml(html);
  }

  /**
   * Converts multiple lines of HTML-like markup into a MarkupMultiline.
   * 
   * @param content
   *          the content to convert
   * @return the equivalent formatted text as a MarkupLine
   * @throws IllegalArgumentException
   *           if the {@code content} argument contains malformed markup
   */
  @NotNull
  public static MarkupMultiline toMarkupString(@NotNull MarkupMultilineDatatype content) {
    String html = processHTML(content);
    return MarkupMultiline.fromHtml(html);
  }

  /**
   * Converts a set of XML tokens, which represent HTML content, into an HTML string.
   * 
   * @param content
   *          the content to convert
   * @return an HTML string
   * @throws IllegalArgumentException
   *           if the {@code content} argument contains malformed markup
   */
  @NotNull
  private static String processHTML(XmlTokenSource content) {
    XmlOptions options = new XmlOptions();
    options.setSaveInner();
    options.setSaveUseOpenFrag();
    StringWriter writer = new StringWriter();
    try {
      content.save(writer, options);
    } catch (IOException e) {
      throw new IllegalArgumentException(e);
    }
    return ObjectUtils.notNull(
        writer.toString().replaceFirst("^<frag\\:fragment[^>]+>", "").replaceFirst("</frag\\:fragment>$", ""));
  }
}