package com.jaeckel.amenoid.api;

import java.io.InputStream;
import java.security.KeyStore;

import javax.net.ssl.SSLException;

import org.apache.http.client.protocol.RequestAcceptEncoding;
import org.apache.http.client.protocol.ResponseContentEncoding;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.AbstractVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.protocol.BasicHttpProcessor;

import android.util.Log;

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

      sf.setHostnameVerifier(new MyVerifier(SSLSocketFactory.STRICT_HOSTNAME_VERIFIER));

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

