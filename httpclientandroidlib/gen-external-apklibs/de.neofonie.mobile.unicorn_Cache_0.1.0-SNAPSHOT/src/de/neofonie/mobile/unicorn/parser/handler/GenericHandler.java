/*
 * (c) Neofonie Mobile GmbH
 * 
 * This computer program is the sole property of Neofonie Mobile GmbH (http://mobile.neofonie.de)
 * and is protected under the German Copyright Act (paragraph 69a UrhG).
 * 
 * All rights are reserved. Making copies, duplicating, modifying, using or distributing
 * this computer program in any form, without prior written consent of Neofonie Mobile GmbH, is prohibited.
 * Violation of copyright is punishable under the German Copyright Act (paragraph 106 UrhG).
 * 
 * Removing this copyright statement is also a violation.
 */

package de.neofonie.mobile.unicorn.parser.handler;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Currency;
import java.util.Locale;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.text.TextUtils;
import de.neofonie.mobile.unicorn.Log;

public abstract class GenericHandler extends DefaultHandler {

  protected final static String TAG          = GenericHandler.class.getSimpleName();

  protected final static String XML_RESPONSE = "response".intern();
  protected final static String XML_ERROR    = "error".intern();

  protected String              actualTag;
  protected boolean             error;

  protected DecimalFormat       currencyFormatter;
  protected DecimalFormat       numberFormatter;

  private StringBuffer          tagCache;

  public GenericHandler() {
    initNumberFormatter();
    initCurrencyFormatter();
    tagCache = new StringBuffer();
  }

  @Override
  public final void startDocument() throws SAXException {
    error = false;
    actualTag = null;
    onStartDocument();
  }

  public static Locale useLocale() {
    return Locale.GERMANY;
  }

  private void initCurrencyFormatter() {
    currencyFormatter = new DecimalFormat("##,###,##0.00");
    currencyFormatter.setGroupingUsed(true);
    currencyFormatter.setGroupingSize(3);
    currencyFormatter.setDecimalFormatSymbols(new DecimalFormatSymbols(useLocale()));
    currencyFormatter.setCurrency(Currency.getInstance(useLocale()));
  }

  private void initNumberFormatter() {
    numberFormatter = new DecimalFormat("##,###,##0.##");
    numberFormatter.setGroupingUsed(true);
    numberFormatter.setGroupingSize(3);
    numberFormatter.setDecimalFormatSymbols(new DecimalFormatSymbols(useLocale()));
  }

  @Override
  public final void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    actualTag = localName;
    tagCache.setLength(0);
    if (localName != null) {
      /*
       * a response tag usually means that an error occured in the data api 
       */
      if (localName.equalsIgnoreCase(XML_RESPONSE) || localName.equalsIgnoreCase(XML_ERROR)) {
        error = true;
      }
      onStartElement(localName, attributes);
    }
  }

  @Override
  public final void characters(char[] ch, int start, int length) throws SAXException {
    tagCache.append(new String(ch, start, length));
  }

  @Override
  public final void endElement(String uri, String localName, String qName) throws SAXException {
    if (tagCache.length() > 0) {
      dispatchContent(tagCache.toString());
    }
    onEndElement(uri, localName, qName);
    tagCache.setLength(0);
  }

  /**
   * overwrite this method if necessary
   * 
   * @param uri
   * @param localName
   * @param qName
   * @throws SAXException
   */
  protected void onEndElement(String uri, String localName, String qName) throws SAXException {};

  private final void dispatchContent(String chars) throws SAXException {
    if (error) {
      Log.w(TAG, "throwing new error: " + chars);
      throw new SAXException(chars);
    }
    onCharacters(actualTag, chars);
  }

  protected String reformatCurrency(final String value) {
    try {
      return TextUtils.concat(currencyFormatter.format(Double.parseDouble(value)),
                              " ",
                              currencyFormatter.getCurrency().getSymbol()).toString();
    } catch (NumberFormatException nfe) {
    }
    return value;
  }

  protected String reformatNumber(final String value) {
    try {
      return numberFormatter.format(Double.parseDouble(value));
    } catch (NumberFormatException nfe) {
    }
    return value;
  }

  /**
   * @throws SAXException
   *           thrown when there is an error while parsing
   */
  protected void onStartDocument() throws SAXException {};

  protected abstract void onCharacters(String currentTag, String content);

  protected abstract void onStartElement(String localName, Attributes attributes) throws SAXException;

  /**
   * dirty hack to override the error handling
   */
  protected void overrideError() {
    error = false;
  }
}
