package com.jaeckel.amenoid.api;

import java.io.InputStream;
import java.security.KeyStore;

import javax.net.ssl.SSLException;

import android.util.Log;
import ch.boye.httpclientandroidlib.client.protocol.RequestAcceptEncoding;
import ch.boye.httpclientandroidlib.client.protocol.ResponseContentEncoding;
import ch.boye.httpclientandroidlib.conn.ClientConnectionManager;
import ch.boye.httpclientandroidlib.conn.scheme.PlainSocketFactory;
import ch.boye.httpclientandroidlib.conn.scheme.Scheme;
import ch.boye.httpclientandroidlib.conn.scheme.SchemeRegistry;
import ch.boye.httpclientandroidlib.conn.ssl.AbstractVerifier;
import ch.boye.httpclientandroidlib.conn.ssl.SSLSocketFactory;
import ch.boye.httpclientandroidlib.conn.ssl.X509HostnameVerifier;
import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient;
import ch.boye.httpclientandroidlib.impl.conn.tsccm.ThreadSafeClientConnManager;
import ch.boye.httpclientandroidlib.protocol.BasicHttpProcessor;

/**
 * User: biafra
 * Date: 11/13/11
 * Time: 9:47 AM
 */
public class AmenHttpClient extends DefaultHttpClient {

  final   InputStream keyStoreStream;
  final   String      keyStorePassword;
  private String      keyStoreType;

  public AmenHttpClient(InputStream keyStoreStream, String keyStorePassword, String keyStoreType) {
    this.keyStoreStream = keyStoreStream;
    this.keyStorePassword = keyStorePassword;
    this.keyStoreType = keyStoreType;
  }

  @Override
  protected ClientConnectionManager createClientConnectionManager() {
    SchemeRegistry registry = new SchemeRegistry();
    registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
    // Register for port 443 our SSLSocketFactory with our keystore
    // to the ConnectionManager
    registry.register(new Scheme("https", newSslSocketFactory(), 443));

    ThreadSafeClientConnManager connMgr = new ThreadSafeClientConnManager(getParams(), registry);
    connMgr.setDefaultMaxPerRoute(20);
//    connMgr.setDefaultMaxPerRoute(10);

    return connMgr;
  }

  private SSLSocketFactory newSslSocketFactory() {
    try {
      KeyStore trusted = KeyStore.getInstance(keyStoreType);
      try {
        trusted.load(keyStoreStream, keyStorePassword.toCharArray());
      } finally {
        keyStoreStream.close();
      }
      SSLSocketFactory sf = new SSLSocketFactory(trusted);

      sf.setHostnameVerifier(new MyVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER));
//      sf.setHostnameVerifier(new MyVerifier(SSLSocketFactory.STRICT_HOSTNAME_VERIFIER));

      return sf;
    } catch (Exception e) {
      throw new AssertionError(e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected BasicHttpProcessor createHttpProcessor() {
    //for gzip stuff
    BasicHttpProcessor result = super.createHttpProcessor();

    result.addRequestInterceptor(new RequestAcceptEncoding());
    result.addResponseInterceptor(new ResponseContentEncoding());

    return result;
  }

}

class MyVerifier extends AbstractVerifier {

  private final X509HostnameVerifier delegate;

  public MyVerifier(final X509HostnameVerifier delegate) {
    this.delegate = delegate;
  }

  @Override
  public void verify(String host, String[] cns, String[] subjectAlts)
    throws SSLException {
    boolean ok = false;
    try {
      delegate.verify(host, cns, subjectAlts);
    } catch (SSLException e) {
      for (String cn : cns) {
        if (cn.startsWith("*.")) {
          try {
            delegate.verify(host, new String[]{
              cn.substring(2)}, subjectAlts);
            ok = true;
          } catch (Exception e1) {
            e1.printStackTrace();
          }
        }
      }
      if (!ok) {
        Log.d("AmenHttpClient", "verify -> !ok");
        e.printStackTrace();
        throw e;
      }
    }
  }
}

