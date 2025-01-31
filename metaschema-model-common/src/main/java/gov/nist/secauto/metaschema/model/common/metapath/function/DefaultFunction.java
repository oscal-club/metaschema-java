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

package gov.nist.secauto.metaschema.model.common.metapath.function;

import gov.nist.secauto.metaschema.model.common.metapath.DynamicContext;
import gov.nist.secauto.metaschema.model.common.metapath.INodeContext;
import gov.nist.secauto.metaschema.model.common.metapath.MetapathException;
import gov.nist.secauto.metaschema.model.common.metapath.evaluate.ISequence;
import gov.nist.secauto.metaschema.model.common.metapath.function.library.FnData;
import gov.nist.secauto.metaschema.model.common.metapath.item.IAnyAtomicItem;
import gov.nist.secauto.metaschema.model.common.metapath.item.IAnyUriItem;
import gov.nist.secauto.metaschema.model.common.metapath.item.IItem;
import gov.nist.secauto.metaschema.model.common.metapath.item.INodeItem;
import gov.nist.secauto.metaschema.model.common.metapath.item.IStringItem;
import gov.nist.secauto.metaschema.model.common.metapath.item.IUntypedAtomicItem;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class DefaultFunction
    extends AbstractFunction {
  // private static final Logger logger = LogManager.getLogger(AbstractFunction.class);

  @NotNull
  private final Set<FunctionProperty> properties;
  @NotNull
  private final ISequenceType result;
  @NotNull
  private final IFunctionExecutor handler;

  /**
   * Construct a new function signature.
   * 
   * @param name
   *          the name of the function
   * @param properties
   *          the characteristics of the function
   * @param arguments
   *          the argument signatures or an empty list
   * @param result
   *          the type of the result
   * @param handler
   *          the handler to call to execute the function
   */
  @SuppressWarnings("null")
  DefaultFunction(
      @NotNull String name,
      @NotNull EnumSet<FunctionProperty> properties,
      @NotNull List<IArgument> arguments,
      @NotNull ISequenceType result,
      @NotNull IFunctionExecutor handler) {
    super(name, arguments);
    this.properties = Collections.unmodifiableSet(properties);
    this.result = result;
    this.handler = handler;
  }

  @Override
  public Set<FunctionProperty> getProperties() {
    return properties;
  }

  @Override
  public ISequenceType getResult() {
    return result;
  }
  //
  // @Override
  // public boolean isSupported(List<IExpression<?>> expressionArguments) {
  // boolean retval;
  // if (expressionArguments.isEmpty() && getArguments().isEmpty()) {
  // // no arguments
  // retval = true;
  // // } else if (arity() == 1 && expressionArguments.isEmpty()) {
  // // // the context item will be the argument
  // // // TODO: check the context item for type compatibility
  // // retval = true;
  // } else if ((expressionArguments.size() == getArguments().size())
  // || (isArityUnbounded() && expressionArguments.size() > getArguments().size())) {
  // retval = true;
  // // check that argument requirements are satisfied
  // Iterator<IArgument> argumentIterator = getArguments().iterator();
  // Iterator<IExpression<?>> expressionIterator = expressionArguments.iterator();
  //
  // IArgument argument = null;
  // while (argumentIterator.hasNext()) {
  // argument = argumentIterator.next();
  // IExpression<?> expression = expressionIterator.hasNext() ? expressionIterator.next() : null;
  //
  // if (expression != null) {
  // // is the expression supported by the argument?
  // retval = argument.isSupported(expression);
  // if (!retval) {
  // break;
  // }
  // } else {
  // // there are no more expression arguments. Make sure that the remaining arguments are optional
  // if (!argument.getSequenceType().getOccurrence().isOptional()) {
  // retval = false;
  // break;
  // }
  // }
  // }
  //
  // if (retval && expressionIterator.hasNext()) {
  // if (isArityUnbounded()) {
  // // check remaining expressions against the last argument
  // while (expressionIterator.hasNext()) {
  // IExpression<?> expression = expressionIterator.next();
  // @SuppressWarnings("null")
  // boolean result = argument.isSupported(expression);
  // if (!result) {
  // retval = result;
  // break;
  // }
  // }
  // } else {
  // // there are extra expressions, which do not match the arguments
  // retval = false;
  // }
  // }
  // } else {
  // retval = false;
  // }
  // return retval;
  // }

  /**
   * Converts arguments in an attempt to align with the function's signature.
   * 
   * @param function
   *          the function
   * @param parameters
   *          the argument parameters
   * @return the converted argument list
   */
  @NotNull
  public static List<@NotNull ISequence<?>> convertArguments(@NotNull IFunction function,
      @NotNull List<@NotNull ISequence<?>> parameters) {
    @NotNull
    List<@NotNull ISequence<?>> retval = new ArrayList<>(parameters.size());

    Iterator<IArgument> argumentIterator = function.getArguments().iterator();
    Iterator<@NotNull ISequence<?>> parametersIterator = parameters.iterator();

    IArgument argument = null;
    while (parametersIterator.hasNext()) {
      argument = argumentIterator.hasNext() ? argumentIterator.next() : function.isArityUnbounded() ? argument : null;

      if (argument == null) {
        throw new InvalidTypeMetapathException(
            String.format("argument signature doesn't match '%d'", function.toSignature()));
      }

      @SuppressWarnings("null")
      ISequence<?> parameter = parametersIterator.next();

      int size = parameter.size();
      Occurrence occurrence = argument.getSequenceType().getOccurrence();
      switch (occurrence) {
      case ONE: {
        if (size != 1) {
          throw new InvalidTypeMetapathException(
              String.format("a sequence of one expected, but found '%d'", size));
        }

        IItem item = FunctionUtils.getFirstItem(parameter, true);
        parameter = item == null ? ISequence.empty() : ISequence.of(item);
        break;
      }
      case ZERO_OR_ONE: {
        if (size > 1) {
          throw new InvalidTypeMetapathException(
              String.format("a sequence of zero or one expected, but found '%d'", size));
        }

        IItem item = FunctionUtils.getFirstItem(parameter, false);
        parameter = item == null ? ISequence.empty() : ISequence.of(item);
        break;
      }
      case ONE_OR_MORE:
        if (size < 1) {
          throw new InvalidTypeMetapathException(
              String.format("a sequence of zero or more expected, but found '%d'", size));
        }
        break;
      case ZERO:
        if (size != 0) {
          throw new InvalidTypeMetapathException(
              String.format("an empty sequence expected, but found '%d'", size));
        }
        break;
      case ZERO_OR_MORE:
      default:
        // do nothing
      }

      Class<? extends IItem> argumentClass = argument.getSequenceType().getType();

      // apply function conversion and type promotion to the parameter
      parameter = convertSequence(argument, parameter);

      // check resulting values
      for (IItem item : parameter.asList()) {
        Class<? extends IItem> itemClass = item.getClass();
        if (!argumentClass.isAssignableFrom(itemClass)) {
          throw new InvalidTypeMetapathException(
              String.format("The type '%s' is not a subtype of '%s'", itemClass.getName(), argumentClass.getName()));
        }
      }

      retval.add(parameter);
    }
    return retval;
  }

  /**
   * Based on XPath 3.1 <a href="https://www.w3.org/TR/xpath-31/#dt-function-conversion">function
   * conversion</a> rules.
   * 
   * @param argument
   *          the function argument signature details
   * @param sequence
   *          the sequence to convert
   * @return the converted sequence
   */
  @NotNull
  protected static ISequence<?> convertSequence(@NotNull IArgument argument, @NotNull ISequence<?> sequence) {
    @NotNull
    ISequence<?> retval;
    if (sequence.isEmpty()) {
      retval = ISequence.empty();
    } else {
      ISequenceType requiredSequenceType = argument.getSequenceType();
      Class<? extends IItem> requiredSequenceTypeClass = requiredSequenceType.getType();

      List<@NotNull IItem> result = new ArrayList<>(sequence.size());

      boolean atomize = IAnyAtomicItem.class.isAssignableFrom(requiredSequenceTypeClass);

      for (IItem item : sequence.asList()) {
        if (atomize) {
          item = FnData.fnDataItem(item);

          if (IUntypedAtomicItem.class.isInstance(item)) { // NOPMD
            // TODO: apply cast to atomic type
          }

          // promote URIs to strings if a string is required
          if (IStringItem.class.equals(requiredSequenceTypeClass) && IAnyUriItem.class.isInstance(item)) {
            item = IStringItem.cast((IAnyUriItem) item);
          }
        }

        // item = requiredSequenceType.
        if (!requiredSequenceTypeClass.isInstance(item)) {
          throw new InvalidTypeMetapathException(
              String.format("The type '%s' is not a subtype of '%s'", item.getClass().getName(),
                  requiredSequenceTypeClass.getName()));
        }
        result.add(item);
      }
      retval = ISequence.of(result);
    }
    return retval;
  }

  @Override
  public ISequence<?> execute(@NotNull List<@NotNull ISequence<?>> arguments, @NotNull DynamicContext dynamicContext,
      INodeContext focus) {
    try {
      List<@NotNull ISequence<?>> convertedArguments = convertArguments(this, arguments);

      CallingContext callingContext;
      ISequence<?> result;
      if (isDeterministic()) {
        // check cache
        callingContext = newCallingContext(arguments, focus);
        // attempt to get the result from the cache
        result = dynamicContext.getCachedResult(callingContext);
      } else {
        callingContext = null;
        result = null;
      }

      if (result == null) {
        // logger.info(String.format("Executing function '%s' with arguments '%s'.", toSignature(),
        // convertedArguments.toString()));
        result = handler.execute(this, convertedArguments, dynamicContext, focus.getContextNodeItem());

        if (callingContext != null) {
          // add result to cache
          dynamicContext.cacheResult(callingContext, result);
        }
      }

      // logger.info(String.format("Executed function '%s' with arguments '%s' producing result '%s'",
      // toSignature(), convertedArguments.toString(), result.asList().toString()));
      return result;
    } catch (MetapathException ex) {
      throw new MetapathException(String.format("Unable to execute function '%s'", toSignature()), ex);
    }
  }

  @Override
  public int hashCode() {
    return Objects.hash(getArguments(), handler, getName(), properties, result);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    DefaultFunction other = (DefaultFunction) obj;
    return Objects.equals(getArguments(), other.getArguments()) && Objects.equals(handler, other.handler)
        && Objects.equals(getName(), other.getName()) && Objects.equals(properties, other.properties)
        && Objects.equals(result, other.result);
  }

  @Override
  public String toString() {
    return toSignature();
  }

  @Override
  public String toSignature() {
    StringBuilder builder = new StringBuilder()
        .append(getName()) // name
        .append("("); // arguments

    List<IArgument> arguments = getArguments();
    if (arguments.isEmpty()) {
      builder.append("()");
    } else {
      builder.append(arguments.stream().map(argument -> argument.toSignature()).collect(Collectors.joining(",")));

      if (isArityUnbounded()) {
        builder.append(", ...");
      }
    }

    builder.append(") as ")
        .append(getResult().toSignature());// return type

    return builder.toString();
  }

  @NotNull
  public CallingContext newCallingContext(@NotNull List<@NotNull ISequence<?>> arguments, @NotNull INodeContext focus) {
    return new CallingContext(arguments, focus);
  }

  public class CallingContext {
    @Nullable
    private final INodeItem contextNodeItem;
    @NotNull
    private final List<@NotNull ISequence<?>> arguments;

    private CallingContext(@NotNull List<@NotNull ISequence<?>> arguments, @NotNull INodeContext focus) {
      if (isFocusDepenent()) {
        contextNodeItem = focus.getContextNodeItem();
      } else {
        contextNodeItem = null;
      }
      this.arguments = arguments;
    }

    @NotNull
    protected DefaultFunction getFunction() {
      return DefaultFunction.this;
    }

    @Nullable
    public INodeItem getContextNodeItem() {
      return contextNodeItem;
    }

    @NotNull
    public List<@NotNull ISequence<?>> getArguments() {
      return arguments;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + getFunction().hashCode();
      result = prime * result + Objects.hash(contextNodeItem, arguments);
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      CallingContext other = (CallingContext) obj;
      if (!getFunction().equals(other.getFunction())) {
        return false;
      }
      return Objects.equals(arguments, other.arguments) && Objects.equals(contextNodeItem, other.contextNodeItem);
    }
  }
}
