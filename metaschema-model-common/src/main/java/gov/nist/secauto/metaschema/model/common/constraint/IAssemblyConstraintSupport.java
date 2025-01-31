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

package gov.nist.secauto.metaschema.model.common.constraint;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public interface IAssemblyConstraintSupport extends IValueConstraintSupport {
  static final IAssemblyConstraintSupport NULL_CONSTRAINT = new IAssemblyConstraintSupport() {

    @SuppressWarnings("null")
    @Override
    public List<@NotNull ? extends IConstraint> getConstraints() {
      return Collections.emptyList();
    }

    @SuppressWarnings("null")
    @Override
    public List<@NotNull ? extends IAllowedValuesConstraint> getAllowedValuesContraints() {
      return Collections.emptyList();
    }

    @SuppressWarnings("null")
    @Override
    public List<@NotNull ? extends IMatchesConstraint> getMatchesConstraints() {
      return Collections.emptyList();
    }

    @SuppressWarnings("null")
    @Override
    public List<@NotNull ? extends IIndexHasKeyConstraint> getIndexHasKeyConstraints() {
      return Collections.emptyList();
    }

    @SuppressWarnings("null")
    @Override
    public List<@NotNull ? extends IExpectConstraint> getExpectConstraints() {
      return Collections.emptyList();
    }

    @SuppressWarnings("null")
    @Override
    public List<@NotNull ? extends IIndexConstraint> getIndexContraints() {
      return Collections.emptyList();
    }

    @SuppressWarnings("null")
    @Override
    public List<@NotNull ? extends IUniqueConstraint> getUniqueConstraints() {
      return Collections.emptyList();
    }

    @SuppressWarnings("null")
    @Override
    public List<@NotNull ? extends ICardinalityConstraint> getHasCardinalityConstraints() {
      return Collections.emptyList();
    }
  };

  /**
   * Get the collection of index constraints, if any.
   * 
   * @return the constraints or an empty list
   */
  @NotNull
  List<@NotNull ? extends IIndexConstraint> getIndexContraints();

  /**
   * Get the collection of unique constraints, if any.
   * 
   * @return the constraints or an empty list
   */
  @NotNull
  List<@NotNull ? extends IUniqueConstraint> getUniqueConstraints();

  /**
   * Get the collection of cardinality constraints, if any.
   * 
   * @return the constraints or an empty list
   */
  @NotNull
  List<@NotNull ? extends ICardinalityConstraint> getHasCardinalityConstraints();
}
