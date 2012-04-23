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

package de.neofonie.mobile.unicorn.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import de.neofonie.mobile.unicorn.Log;

public abstract class DefaultParser<O extends Object> {

  private static final String TAG = DefaultParser.class.getSimpleName();

  private SAXParserFactory    spf;
  private SAXParser           sp;
  private XMLReader           xr;

  private InputStreamReader   isr;
  private InputSource         is;

  protected abstract DefaultHandler getHandler();

  public abstract O getResult();

  public DefaultParser() {
    try {
      spf = SAXParserFactory.newInstance();
      sp = spf.newSAXParser();
      xr = sp.getXMLReader();

      xr.setContentHandler(getHandler());
    } catch (ParserConfigurationException e) {
      Log.e(TAG, "ParserConfigurationException in ResultSaxParser()", e);
    } catch (FactoryConfigurationError e) {
      Log.e(TAG, "FactoryConfigurationError in ResultSaxParser()", e);
    } catch (SAXException e) {
      Log.e(TAG, "SAXException in ResultSaxParser()", e);
    }

  }

  public final boolean saxParseStream(InputStream stream) throws SAXException {
    boolean success = false;

    try {
      //Log.i(TAG,getStringForStream(stream));
      isr = new InputStreamReader(stream, "UTF-8");
      is = new InputSource(isr);
      xr.parse(is);
      //xr.parse(new InputSource(stream));
      success = true;
    } catch (IOException e) {
      Log.e(TAG, "Error while running SAX Parser", e);
    } finally {
      if (is != null) {
        is = null;
      }
      if (isr != null) {
        try {
          isr.close();
        } catch (IOException e) {
        }
      }
      if (stream != null) {
        try {
          stream.close();
        } catch (IOException e) {
        }
      }
    }
    return success;
  }

  public static final String getStringForStream(InputStream is) {
    StringBuilder sb = new StringBuilder();
    try {
      long start = System.currentTimeMillis();

      if (is == null) {
        Log.d(TAG, "***** Error, Input Stream is null");
      } else {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        String line = null;

        while ((line = reader.readLine()) != null) {
          sb.append(line);
        }

        reader.close();
        is.close();

        Log.i(TAG, "***** Read Answer in: " + (System.currentTimeMillis() - start));
        start = System.currentTimeMillis();
      }
    } catch (IOException e) {
      Log.e(TAG, "Error while reading answer from Server", e);
    }

    return sb.toString();
  }

}
