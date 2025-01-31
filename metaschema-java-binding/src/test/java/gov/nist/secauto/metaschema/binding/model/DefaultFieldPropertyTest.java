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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ctc.wstx.stax.WstxInputFactory;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import gov.nist.secauto.metaschema.binding.IBindingContext;
import gov.nist.secauto.metaschema.binding.io.json.IJsonParsingContext;
import gov.nist.secauto.metaschema.binding.io.xml.IXmlParsingContext;
import gov.nist.secauto.metaschema.binding.model.annotations.BoundField;
import gov.nist.secauto.metaschema.binding.model.annotations.Metaschema;
import gov.nist.secauto.metaschema.binding.model.annotations.MetaschemaAssembly;
import gov.nist.secauto.metaschema.model.common.IMetaschema;
import gov.nist.secauto.metaschema.model.common.JsonGroupAsBehavior;
import gov.nist.secauto.metaschema.model.common.XmlGroupAsBehavior;
import gov.nist.secauto.metaschema.model.common.datatype.adapter.StringAdapter;
import gov.nist.secauto.metaschema.model.common.datatype.markup.MarkupLine;
import gov.nist.secauto.metaschema.model.common.datatype.markup.MarkupMultiline;
import gov.nist.secauto.metaschema.model.common.util.CollectionUtil;

import org.codehaus.stax2.XMLEventReader2;
import org.jetbrains.annotations.NotNull;
import org.jmock.Expectations;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

class DefaultFieldPropertyTest {
  @RegisterExtension
  JUnit5Mockery context = new JUnit5Mockery();

  private final IAssemblyClassBinding classBinding = context.mock(IAssemblyClassBinding.class);
  private final IBindingContext bindingContext = context.mock(IBindingContext.class);
  private final IJsonParsingContext jsonParsingContext = context.mock(IJsonParsingContext.class);
  private final IXmlParsingContext xmlParsingContext = context.mock(IXmlParsingContext.class);

  @Test
  void testJsonRead()
      throws JsonParseException, IOException, NoSuchFieldException, SecurityException {
    String json = "{ \"field1\": \"field1value\", \"fields2\": [ \"field2value\" ] } }";
    JsonFactory factory = new JsonFactory();
    JsonParser jsonParser = factory.createParser(json);
    Class<?> theClass = TestField.class;

    context.checking(new Expectations() {
      { // NOPMD - intentional
        allowing(bindingContext).getJavaTypeAdapterInstance(StringAdapter.class);
        will(returnValue(new StringAdapter()));

        allowing(classBinding).getBoundClass();
        will(returnValue(theClass));
        allowing(classBinding).getBindingContext();
        will(returnValue(bindingContext));

        allowing(jsonParsingContext).getReader();
        will(returnValue(jsonParser));
      }
    });

    java.lang.reflect.Field field1 = theClass.getDeclaredField("field1");
    DefaultFieldProperty field1Property = DefaultFieldProperty.createInstance(classBinding, field1);
    java.lang.reflect.Field field2 = theClass.getDeclaredField("_field2");
    DefaultFieldProperty field2Property = DefaultFieldProperty.createInstance(classBinding, field2);

    TestField obj = new TestField();

    assertEquals(JsonToken.START_OBJECT, jsonParser.nextToken());
    assertEquals("field1", jsonParser.nextFieldName());
    assertTrue(field1Property.read(obj, jsonParsingContext));
    assertEquals("field1value", obj.getField1());

    assertEquals(JsonToken.FIELD_NAME, jsonParser.currentToken());
    assertEquals("fields2", jsonParser.currentName());

    assertTrue(field2Property.read(obj, jsonParsingContext));
    assertTrue(obj.getField2() instanceof LinkedList);
    assertIterableEquals(Collections.singleton("field2value"), obj.getField2());

    // assertEquals(JsonToken.START_OBJECT, jsonParser.nextToken());
    // assertEquals(JsonToken.FIELD_NAME, jsonParser.nextToken());
    // assertEquals("id", jsonParser.currentName());

  }

  @Test
  void testXmlRead() throws JsonParseException, IOException, NoSuchFieldException, SecurityException,
      XMLStreamException {
    String xml = "<test xmlns='http://example.com/ns'>" + "  <field1>field1value</field1>" + "</test>";
    XMLInputFactory factory = WstxInputFactory.newInstance();
    XMLEventReader2 eventReader = (XMLEventReader2) factory.createXMLEventReader(new StringReader(xml));
    Class<?> theClass = TestField.class;

    IMetaschema metaschema = new TestMetaschema(CollectionUtil.emptyList(), bindingContext);

    context.checking(new Expectations() {
      { // NOPMD - intentional
        allowing(bindingContext).getJavaTypeAdapterInstance(StringAdapter.class);
        will(returnValue(new StringAdapter()));

        allowing(classBinding).getBoundClass();
        will(returnValue(theClass));
        allowing(classBinding).getBindingContext();
        will(returnValue(bindingContext));
        allowing(classBinding).getContainingMetaschema();
        will(returnValue(metaschema));

        allowing(xmlParsingContext).getReader();
        will(returnValue(eventReader));
      }
    });

    java.lang.reflect.Field field1 = theClass.getDeclaredField("field1");
    DefaultFieldProperty field1Property = DefaultFieldProperty.createInstance(classBinding, field1);
    java.lang.reflect.Field field2 = theClass.getDeclaredField("_field2");
    DefaultFieldProperty field2Property = DefaultFieldProperty.createInstance(classBinding, field2);

    TestField obj = new TestField();

    assertEquals(XMLEvent.START_DOCUMENT, eventReader.nextEvent().getEventType());
    XMLEvent event = eventReader.nextEvent();
    assertEquals(XMLEvent.START_ELEMENT, event.getEventType());
    StartElement start = event.asStartElement();
    // assertEquals("test", jsonParser.nextFieldName());
    // assertEquals(JsonToken.START_OBJECT, jsonParser.nextToken());
    // assertEquals(JsonToken.FIELD_NAME, jsonParser.nextToken());

    assertTrue(field1Property.read(obj, start, xmlParsingContext));
    assertFalse(field2Property.read(obj, start, xmlParsingContext));

    assertEquals("field1value", obj.getField1());
    assertEquals(null, obj.getField2());
  }

  @Test
  void testXmlReadNoFieldValue() throws JsonParseException, IOException, NoSuchFieldException, SecurityException,
      XMLStreamException {
    String xml = "<test xmlns='http://example.com/ns'>\n" + "  <fields2>\n" + "    <field2>field2value</field2>\n"
        + "  </fields2>\n" + "</test>";
    XMLInputFactory factory = WstxInputFactory.newInstance();
    XMLEventReader2 eventReader = (XMLEventReader2) factory.createXMLEventReader(new StringReader(xml));
    Class<?> theClass = TestField.class;

    IMetaschema metaschema = new TestMetaschema(CollectionUtil.emptyList(), bindingContext);

    context.checking(new Expectations() {
      { // NOPMD - intentional
        allowing(bindingContext).getJavaTypeAdapterInstance(StringAdapter.class);
        will(returnValue(new StringAdapter()));

        allowing(classBinding).getBoundClass();
        will(returnValue(theClass));
        allowing(classBinding).getBindingContext();
        will(returnValue(bindingContext));
        allowing(classBinding).getContainingMetaschema();
        will(returnValue(metaschema));

        allowing(xmlParsingContext).getReader();
        will(returnValue(eventReader));
      }
    });

    java.lang.reflect.Field field1 = theClass.getDeclaredField("field1");
    DefaultFieldProperty field1Property = DefaultFieldProperty.createInstance(classBinding, field1);
    java.lang.reflect.Field field2 = theClass.getDeclaredField("_field2");
    DefaultFieldProperty field2Property = DefaultFieldProperty.createInstance(classBinding, field2);

    TestField obj = new TestField();

    assertEquals(XMLEvent.START_DOCUMENT, eventReader.nextEvent().getEventType());
    XMLEvent event = eventReader.nextEvent();
    assertEquals(XMLEvent.START_ELEMENT, event.getEventType());
    StartElement start = event.asStartElement();
    // assertEquals("test", jsonParser.nextFieldName());
    // assertEquals(JsonToken.START_OBJECT, jsonParser.nextToken());
    // assertEquals(JsonToken.FIELD_NAME, jsonParser.nextToken());

    assertFalse(field1Property.read(obj, start, xmlParsingContext));
    assertTrue(field2Property.read(obj, start, xmlParsingContext));

    assertEquals(null, obj.getField1());
    assertIterableEquals(Collections.singleton("field2value"), obj.getField2());
  }

  @Test
  void testJsonReadMissingFieldValue()
      throws JsonParseException, IOException, NoSuchFieldException, SecurityException {
    String json = "{ \"test\":\n" + "  { \"fields2\": [\n" + "    \"field2value\"\n" + "    ]\n" + "  }\n" + "}\n";
    JsonFactory factory = new JsonFactory();
    JsonParser jsonParser = factory.createParser(json);
    Class<?> theClass = TestField.class;

    context.checking(new Expectations() {
      { // NOPMD - intentional
        allowing(bindingContext).getJavaTypeAdapterInstance(StringAdapter.class);
        will(returnValue(new StringAdapter()));

        allowing(classBinding).getBoundClass();
        will(returnValue(theClass));
        allowing(classBinding).getBindingContext();
        will(returnValue(bindingContext));

        allowing(jsonParsingContext).getReader();
        will(returnValue(jsonParser));
      }
    });

    java.lang.reflect.Field field1 = theClass.getDeclaredField("field1");
    DefaultFieldProperty field1Property = DefaultFieldProperty.createInstance(classBinding, field1);
    java.lang.reflect.Field field2 = theClass.getDeclaredField("_field2");
    DefaultFieldProperty field2Property = DefaultFieldProperty.createInstance(classBinding, field2);

    TestField obj = new TestField();

    // Advance to first property to parse
    assertEquals(JsonToken.START_OBJECT, jsonParser.nextToken());
    assertEquals("test", jsonParser.nextFieldName());
    assertEquals(JsonToken.START_OBJECT, jsonParser.nextToken());
    assertEquals("fields2", jsonParser.nextFieldName());

    // attempt to parse the missing property
    assertFalse(field1Property.read(obj, jsonParsingContext));
    assertEquals(null, obj.getField1());

    // attempt to parse the existing property
    assertEquals(JsonToken.FIELD_NAME, jsonParser.currentToken());
    assertEquals("fields2", jsonParser.currentName());

    assertTrue(field2Property.read(obj, jsonParsingContext));
    assertTrue(obj.getField2() instanceof LinkedList);
    assertIterableEquals(Collections.singleton("field2value"), obj.getField2());
  }

  @Metaschema
  public static class TestMetaschema
      extends AbstractBoundMetaschema {

    public TestMetaschema(@NotNull List<@NotNull ? extends IMetaschema> importedMetaschema,
        @NotNull IBindingContext bindingContext) {
      super(importedMetaschema, bindingContext);
    }

    @Override
    public MarkupLine getName() {
      return MarkupLine.fromMarkdown("Test Metaschema");
    }

    @Override
    public String getVersion() {
      return "1.0";
    }

    @Override
    public MarkupMultiline getRemarks() {
      return null;
    }

    @Override
    public @NotNull String getShortName() {
      return "test-metaschema";
    }

    @Override
    public @NotNull URI getXmlNamespace() {
      return URI.create("http://example.com/ns");
    }

    @Override
    public @NotNull URI getJsonBaseUri() {
      return URI.create("https://csrc.nist.gov/ns/test/json");
    }

  }

  @SuppressWarnings("PMD")
  @MetaschemaAssembly(metaschema = TestMetaschema.class)
  public static class TestField {
    @BoundField(typeAdapter = StringAdapter.class, inJson = JsonGroupAsBehavior.NONE,
        inXml = XmlGroupAsBehavior.UNGROUPED)
    private String field1;

    @BoundField(useName = "field2", groupName = "fields2", maxOccurs = -1, inXml = XmlGroupAsBehavior.GROUPED,
        inJson = JsonGroupAsBehavior.LIST, typeAdapter = StringAdapter.class)
    private List<String> _field2;

    public TestField() {
    }

    public String getField1() {
      return field1;
    }

    public List<String> getField2() {
      return _field2;
    }
  }

}
