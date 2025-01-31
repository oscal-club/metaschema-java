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

package gov.nist.secauto.metaschema.binding.metapath.item;

import gov.nist.secauto.metaschema.binding.model.IBoundFlagInstance;
import gov.nist.secauto.metaschema.binding.model.IBoundNamedModelDefinition;
import gov.nist.secauto.metaschema.model.common.metapath.item.IFlagNodeItem;
import gov.nist.secauto.metaschema.model.common.metapath.item.IModelNodeItem;
import gov.nist.secauto.metaschema.model.common.util.CollectionUtil;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

abstract class AbstractBoundXdmModelNodeItem
    extends AbstractBoundXdmValuedNodeItem
    implements IModelNodeItem {

  private final int position;
  private Map<String, IFlagNodeItem> flags;

  public AbstractBoundXdmModelNodeItem(@NotNull Object value, int position) {
    super(value);
    this.position = position;
  }

  @Override
  public abstract IBoundNamedModelDefinition getDefinition();

  @Override
  public int getPosition() {
    return position;
  }

  @SuppressWarnings("null")
  @Override
  public Collection<@NotNull ? extends IFlagNodeItem> getFlags() {
    return initFlags().values();
  }

  @Override
  public IFlagNodeItem getFlagByName(@NotNull String name) {
    return initFlags().get(name);
  }

  protected Map<String, IFlagNodeItem> initFlags() {
    synchronized (this) {
      if (this.flags == null) {
        Map<String, IFlagNodeItem> flags = new LinkedHashMap<>();
        Object parentValue = getValue();
        for (IBoundFlagInstance instance : getDefinition().getFlagInstances()) {
          Object instanceValue = instance.getValue(parentValue);
          if (instanceValue != null) {
            IFlagNodeItem item = IXdmFactory.INSTANCE.newFlagNodeItem(instance, instanceValue, this);
            flags.put(instance.getEffectiveName(), item);
          }
        }
        this.flags = CollectionUtil.unmodifiableMap(flags);
      }
    }
    return this.flags;
  }
}
