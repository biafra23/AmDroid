package com.jaeckel.amenoid.api;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;

import android.util.Log;
import ch.boye.httpclientandroidlib.client.protocol.RequestAcceptEncoding;
import ch.boye.httpclientandroidlib.client.protocol.ResponseContentEncoding;
import ch.boye.httpclientandroidlib.conn.ClientConnectionManager;
import ch.boye.httpclientandroidlib.conn.scheme.PlainSocketFactory;
import ch.boye.httpclientandroidlib.conn.scheme.Scheme;
import ch.boye.httpclientandroidlib.conn.scheme.SchemeRegistry;
import ch.boye.httpclientandroidlib.conn.ssl.AbstractVerifier;
import ch.boye.httpclientandroidlib.conn.ssl.AllowAllHostnameVerifier;
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

  final private InputStream keyStoreStream;
  final private String      keyStorePassword;
  final private String      keyStoreType;

  public AmenHttpClient(InputStream keyStoreStream, String keyStorePassword, String keyStoreType) {
    this.keyStoreStream = keyStoreStream;
    this.keyStorePassword = keyStorePassword;
    this.keyStoreType = keyStoreType;
  }

  public AmenHttpClient() {
    this.keyStoreStream = null;
    this.keyStorePassword = null;
    this.keyStoreType = null;
  }

  @Override
  protected ClientConnectionManager createClientConnectionManager() {
    SchemeRegistry registry = new SchemeRegistry();
    registry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
    // Register for port 443 our SSLSocketFactory with our keystore
    // to the ConnectionManager
    if (keyStoreStream != null) {
      registry.register(new Scheme("https", 443, newSslSocketFactory()));

    } else {
      try {
        registry.register(new Scheme("https", 443, new SSLSocketFactory(SSLContext.getDefault(), new AllowAllHostnameVerifier())));

      } catch (NoSuchAlgorithmException e) {
        e.printStackTrace();
      }

    }

    ThreadSafeClientConnManager connMgr = new ThreadSafeClientConnManager(registry);
    connMgr.setDefaultMaxPerRoute(10);

    this.log.enableTrace(true);
    this.log.enableDebug(true);
    this.log.enableWarn(true);

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

