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

package gov.nist.secauto.metaschema.model.common.metapath.function.library;

import gov.nist.secauto.metaschema.model.common.metapath.DynamicContext;
import gov.nist.secauto.metaschema.model.common.metapath.evaluate.ISequence;
import gov.nist.secauto.metaschema.model.common.metapath.function.FunctionUtils;
import gov.nist.secauto.metaschema.model.common.metapath.function.IArgument;
import gov.nist.secauto.metaschema.model.common.metapath.function.IFunction;
import gov.nist.secauto.metaschema.model.common.metapath.function.InvalidTypeFunctionMetapathException;
import gov.nist.secauto.metaschema.model.common.metapath.item.IAnyAtomicItem;
import gov.nist.secauto.metaschema.model.common.metapath.item.IAtomicValuedItem;
import gov.nist.secauto.metaschema.model.common.metapath.item.IItem;
import gov.nist.secauto.metaschema.model.common.metapath.item.INodeItem;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Stream;

/**
 * Since a node doesn't have a base URI in Metaschema, this is an alias for the document-uri
 * function.
 */
public final class FnData {

  @NotNull
  static final IFunction SIGNATURE_NO_ARG = IFunction.builder()
      .name("data")
      .deterministic()
      .contextDependent()
      .focusDependent()
      .returnType(IAnyAtomicItem.class)
      .returnOne()
      .functionHandler(FnData::executeNoArg)
      .build();

  @NotNull
  static final IFunction SIGNATURE_ONE_ARG = IFunction.builder()
      .name("data")
      .deterministic()
      .contextIndependent()
      .focusIndependent()
      .argument(IArgument.newBuilder()
          .name("arg1")
          .type(IItem.class)
          .zeroOrMore()
          .build())
      .returnType(IAnyAtomicItem.class)
      .returnOne()
      .functionHandler(FnData::executeOneArg)
      .build();

  private FnData() {
    // disable construction
  }

  @SuppressWarnings("unused")
  @NotNull
  private static ISequence<IAnyAtomicItem> executeNoArg(@NotNull IFunction function,
      @NotNull List<@NotNull ISequence<?>> arguments,
      @NotNull DynamicContext dynamicContext,
      INodeItem focus) {

    INodeItem item = focus;

    ISequence<IAnyAtomicItem> retval;
    if (item == null) {
      retval = ISequence.empty();
    } else {
      IAnyAtomicItem data = fnDataItem(item);
      retval = ISequence.of(data);
    }
    return retval;
  }

  @SuppressWarnings("unused")
  @NotNull
  private static ISequence<IAnyAtomicItem> executeOneArg(@NotNull IFunction function,
      @NotNull List<@NotNull ISequence<?>> arguments,
      @NotNull DynamicContext dynamicContext,
      INodeItem focus) {

    ISequence<?> sequence = FunctionUtils.asType(arguments.get(0));
    return fnData(sequence);
  }

  /**
   * An implementation of XPath 3.1
   * <a href="https://www.w3.org/TR/xpath-functions-31/#func-data">fn:data</a> supporting
   * <a href="https://www.w3.org/TR/xpath-31/#id-atomization">item atomization</a>.
   * 
   * @param sequence
   *          the sequence of items to atomize
   * @return the atomized result
   */
  @SuppressWarnings("null")
  @NotNull
  public static ISequence<IAnyAtomicItem> fnData(@NotNull ISequence<?> sequence) {
    @NotNull
    Stream<? extends IItem> stream = sequence.asStream();
    return ISequence.of(stream.flatMap(x -> {
      return Stream.of(fnDataItem(x));
    }));
  }

  /**
   * An implementation of <a href="https://www.w3.org/TR/xpath-31/#id-atomization">item
   * atomization</a>.
   * 
   * @param item
   *          the item to atomize
   * @return the atomized result
   */
  @NotNull
  public static IAnyAtomicItem fnDataItem(@NotNull IItem item) {
    IAnyAtomicItem retval;
    if (item instanceof IAnyAtomicItem) {
      retval = (IAnyAtomicItem) item;
    } else if (item instanceof IAtomicValuedItem) {
      retval = ((IAtomicValuedItem) item).toAtomicItem();
    } else {
      throw new InvalidTypeFunctionMetapathException(InvalidTypeFunctionMetapathException.NODE_HAS_NO_TYPED_VALUE,
          String.format("Item '%s' has no typed value", item.getClass().getName()));
    }
    return retval;
  }
}
