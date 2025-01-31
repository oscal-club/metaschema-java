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

package gov.nist.secauto.metaschema.model.common.metapath;

import gov.nist.secauto.metaschema.model.common.metapath.item.IFlagNodeItem;
import gov.nist.secauto.metaschema.model.common.metapath.item.IModelNodeItem;
import gov.nist.secauto.metaschema.model.common.metapath.item.INodeItem;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public interface INodeContext {

  /**
   * Get the current node in this context.
   * 
   * @return the context node
   */
  @NotNull
  INodeItem getContextNodeItem();

  /**
   * Get the Metaschema flags associated this node. The resulting collection is expected to be
   * ordered, with the results in document order.
   * 
   * @return a collection of flags
   */
  @NotNull
  Collection<@NotNull ? extends IFlagNodeItem> getFlags();

  /**
   * Lookup a Metaschema flag on this node by it's effective name.
   * 
   * @param name
   *          the effective name of the flag
   * @return the flag with the matching effective name or {@code null} if no match was found
   */
  IFlagNodeItem getFlagByName(@NotNull String name);

  /**
   * Get the Metaschema flags associated with this node as a stream.
   * 
   * @return the stream of flags or an empty stream if none exist
   */
  @SuppressWarnings("null")
  @NotNull
  default Stream<? extends IFlagNodeItem> flags() {
    return getFlags().stream();
  }

  /**
   * Get the Metaschema model items (i.e., fields, assemblies) associated this node. A given model
   * instance can be multi-valued, so the value of each instance will be a list. The resulting
   * collection is expected to be ordered, with the results in document order.
   * 
   * @return a collection of list(s), with each list containing the items for a given model instance
   */
  @NotNull
  Collection<@NotNull ? extends List<@NotNull ? extends IModelNodeItem>> getModelItems();

  @NotNull
  List<@NotNull ? extends IModelNodeItem> getModelItemsByName(String name);

  /**
   * Get the Metaschema model items (i.e., fields, assemblies) associated this node as a stream.
   * 
   * @return the stream of model items or an empty stream if none exist
   */
  @SuppressWarnings("null")
  @NotNull
  default Stream<? extends IModelNodeItem> modelItems() {
    return getModelItems().stream().flatMap(list -> list.stream());
  }

  //
  // /**
  // * Searches the node graph for {@link INodeItem} instances that match the provided
  // * {@link IExpression}. The resulting nodes are returned in document order.
  // *
  // * @param expr
  // * the search expression
  // * @param recurse
  // * if the search should recurse over the child model instances
  // * @return a stream of matching flag node items
  // */
  // @NotNull
  // Stream<? extends INodeItem> getMatchingChildInstances(@NotNull IExpressionEvaluationVisitor
  // visitor,
  // @NotNull IExpression<?> expr,
  // boolean recurse);
  //
  // /**
  // * Searches the child flags for {@link IFlagNodeItem} instances that match the provided {@link
  // Flag}
  // * expression. The resulting nodes are returned in document order.
  // *
  // * @param flag
  // * the search expression
  // * @return a stream of matching flag node items
  // */
  // @NotNull
  // Stream<? extends IFlagNodeItem> getMatchingChildFlags(@NotNull Flag flag);
  //
  // /**
  // * Searches the child model nodes for {@link IModelNodeItem} instances that match the provided
  // * {@link ModelInstance} expression. The resulting nodes are returned in document order.
  // *
  // * @param modelInstance
  // * the search expression
  // * @return a stream of matching model node items
  // */
  // @NotNull
  // Stream<? extends IModelNodeItem> getMatchingChildModelInstances(@NotNull ModelInstance
  // modelInstance);

  // default IMetapathResult evaluateMetapath(MetapathExpression metapath) {
  // MetaschemaPathEvaluationVisitor visitor = new MetaschemaPathEvaluationVisitor();
  // // logger.info(String.format("Evaluating path '%s' as AST '%s'", metapath.getPath(),
  // // metapath.toString()));
  // return visitor.visit(metapath.getASTNode(), this);
  //
  // }
}
