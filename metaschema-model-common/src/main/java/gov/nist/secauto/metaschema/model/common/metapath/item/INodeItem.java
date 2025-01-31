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

package gov.nist.secauto.metaschema.model.common.metapath.item;

import gov.nist.secauto.metaschema.model.common.constraint.IConstraintValidator;
import gov.nist.secauto.metaschema.model.common.metapath.DynamicContext;
import gov.nist.secauto.metaschema.model.common.metapath.INodeContext;
import gov.nist.secauto.metaschema.model.common.metapath.MetapathException;
import gov.nist.secauto.metaschema.model.common.metapath.MetapathExpression;
import gov.nist.secauto.metaschema.model.common.metapath.StaticContext;
import gov.nist.secauto.metaschema.model.common.metapath.evaluate.ISequence;
import gov.nist.secauto.metaschema.model.common.metapath.evaluate.MetaschemaPathEvaluationVisitor;
import gov.nist.secauto.metaschema.model.common.metapath.format.IPathFormatter;
import gov.nist.secauto.metaschema.model.common.metapath.format.IPathSegment;
import gov.nist.secauto.metaschema.model.common.validation.ValidatingNodeItemVisitor;

import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.util.stream.Stream;

public interface INodeItem extends IPathItem, INodeContext, IPathSegment {

  /**
   * Get the type of node item this is.
   * 
   * @return the node item's type
   */
  @NotNull
  NodeItemType getNodeItemType();

  /**
   * Retrieve the base URI of this node.
   * <p>
   * The base URI of a node will be in order of preference:
   * <ol>
   * <li>the base URI defined on the node</li>
   * <li>the base URI defined on the nearest ancestor node</li>
   * <li>the base URI defined on the document node</li>
   * <li>{@code null} if the document node is unknown</li>
   * </ol>
   * 
   * @return the base URI or {@code null} if it is unknown
   */
  URI getBaseUri();

  /**
   * Get the path for this node item as a Metapath.
   * 
   * @return the Metapath
   */
  @NotNull
  default String getMetapath() {
    return toPath(IPathFormatter.METAPATH_PATH_FORMATER);
  }

  /**
   * Evaluate the provided Metapath, producing a sequence of result items.
   * 
   * @param <ITEM_TYPE>
   *          the type of items in the sequence
   * @param metapath
   *          the compiled Metapath expression
   * @return the result items
   */
  @SuppressWarnings("unchecked")
  @NotNull
  default <ITEM_TYPE extends IItem> ISequence<? extends ITEM_TYPE> evaluateMetapath(MetapathExpression metapath) {
    return (@NotNull ISequence<? extends ITEM_TYPE>) evaluateMetapath(metapath,
        new StaticContext().newDynamicContext());
  }

  /**
   * Evaluate the provided Metapath, producing a sequence of result items.
   * 
   * @param metapath
   *          the compiled Metapath expression
   * @param context
   *          the dynamic Metapath context
   * @return the result items
   * @throws MetapathException
   *           if an error occured while evaluating the expression
   */
  @NotNull
  default ISequence<?> evaluateMetapath(@NotNull MetapathExpression metapath, @NotNull DynamicContext context)
      throws MetapathException {
    return new MetaschemaPathEvaluationVisitor(context).visit(metapath.getASTNode(), this);
  }

  /**
   * A visitor callback.
   * 
   * @param <RESULT>
   *          the type of the visitor result
   * @param <CONTEXT>
   *          the type of the context parameter
   * @param visitor
   *          the calling visitor
   * @param context
   *          a parameter used to pass contextual information between visitors
   * @return the visitor result
   */
  <RESULT, CONTEXT> RESULT accept(@NotNull INodeItemVisitor<RESULT, CONTEXT> visitor, CONTEXT context);

  /**
   * Perform constraint validation on this node (and its children) using the provided validator.
   * 
   * @param validator
   *          the validator instance to use for performing validation
   */
  default void validate(IConstraintValidator validator) {
    new ValidatingNodeItemVisitor().visit(this, validator);
  }

  /**
   * Retrieve the parent node item if it exists.
   * 
   * @return the parent node item, or {@code null} if this node item has no known parent
   */
  // TODO: Should this be IModelNodeItem?
  INodeItem getParentNodeItem();

  @Override
  Stream<@NotNull ? extends INodeItem> getPathStream();

  /**
   * Retrieve the parent content node item if it exists. A content node is a non-document node.
   * 
   * @return the parent content node item, or {@code null} if this node item has no known parent
   *         content node item
   */
  IModelNodeItem getParentContentNodeItem();

  @NotNull
  <CLASS> CLASS toBoundObject();
}
