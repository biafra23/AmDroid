package com.jaeckel.amenoid.api;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.AbstractVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;

import javax.net.ssl.SSLException;
import java.io.InputStream;
import java.security.KeyStore;

/**
 * User: biafra
 * Date: 11/13/11
 * Time: 9:47 AM
 */
public class AmenHttpClient extends DefaultHttpClient {

  final InputStream keyStoreStream;
  final String      keyStorePassword;

  public AmenHttpClient(InputStream keyStoreStream, String keyStorePassword) {
    this.keyStoreStream = keyStoreStream;
    this.keyStorePassword = keyStorePassword;

  }

  @Override
  protected ClientConnectionManager createClientConnectionManager() {
    SchemeRegistry registry = new SchemeRegistry();
    registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
    // Register for port 443 our SSLSocketFactory with our keystore
    // to the ConnectionManager
    registry.register(new Scheme("https", newSslSocketFactory(), 443));
    return new ThreadSafeClientConnManager(getParams(), registry);
  }

  private SSLSocketFactory newSslSocketFactory() {
    try {
      // Get an instance of the Bouncy Castle KeyStore format
      KeyStore trusted = KeyStore.getInstance("BKS");
      // Get the raw resource, which contains the keystore with
      // your trusted certificates (root and any intermediate certs)
//      InputStream in = context.getResources().openRawResource(R.raw.amenkeystore);
      try {
        // Initialize the keystore with the provided trusted certificates
        // Also provide the password of the keystore
        trusted.load(keyStoreStream, keyStorePassword.toCharArray());
      } finally {
        keyStoreStream.close();
      }
      // Pass the keystore to the SSLSocketFactory. The factory is responsible
      // for the verification of the server certificate.
      SSLSocketFactory sf = new SSLSocketFactory(trusted);
      // Hostname verification from certificate
      // http://hc.apache.org/httpcomponents-client-ga/tutorial/html/connmgmt.html#d4e506
//      sf.setHostnameVerifier(SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
//      final X509HostnameVerifier delegate = sf.getHostnameVerifier();
//      if (!(delegate instanceof MyVerifier)) {
        sf.setHostnameVerifier(new MyVerifier(SSLSocketFactory.STRICT_HOSTNAME_VERIFIER));
//      }

      return sf;
    } catch (Exception e) {
      throw new AssertionError(e);
    }
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
          }
        }
      }
      if (!ok) throw e;
    }
  }
}
