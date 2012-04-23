/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.commons.compress.archivers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

/**
 * <p>
 * Factory to create Archive[In|Out]putStreams from names or the first bytes of the InputStream. In order add
 * other implementations you should extend ArchiveStreamFactory and override the appropriate methods (and call
 * their implementation from super of course).
 * </p>
 * 
 * Compressing a ZIP-File:
 * 
 * <pre>
 * final OutputStream out = new FileOutputStream(output);
 * ArchiveOutputStream os = new ArchiveStreamFactory().createArchiveOutputStream(ArchiveStreamFactory.ZIP, out);
 * 
 * os.putArchiveEntry(new ZipArchiveEntry(&quot;testdata/test1.xml&quot;));
 * IOUtils.copy(new FileInputStream(file1), os);
 * os.closeArchiveEntry();
 * 
 * os.putArchiveEntry(new ZipArchiveEntry(&quot;testdata/test2.xml&quot;));
 * IOUtils.copy(new FileInputStream(file2), os);
 * os.closeArchiveEntry();
 * os.close();
 * </pre>
 * 
 * Decompressing a ZIP-File:
 * 
 * <pre>
 * final InputStream is = new FileInputStream(input);
 * ArchiveInputStream in = new ArchiveStreamFactory().createArchiveInputStream(ArchiveStreamFactory.ZIP, is);
 * ZipArchiveEntry entry = (ZipArchiveEntry) in.getNextEntry();
 * OutputStream out = new FileOutputStream(new File(dir, entry.getName()));
 * IOUtils.copy(in, out);
 * out.close();
 * in.close();
 * </pre>
 * 
 * @Immutable
 */
public class ArchiveStreamFactory {
  /**
   * Constant used to identify the TAR archive format.
   * 
   * @since Commons Compress 1.1
   */
  public static final String TAR = "tar";

  /**
   * Create an archive input stream from an archiver name and an input stream.
   * 
   * @param archiverName
   *          the archive name, i.e. "ar", "zip", "tar", "jar", "dump" or "cpio"
   * @param in
   *          the input stream
   * @return the archive input stream
   * @throws ArchiveException
   *           if the archiver name is not known
   * @throws IllegalArgumentException
   *           if the archiver name or stream is null
   */
  public ArchiveInputStream createArchiveInputStream(final String archiverName, final InputStream in) throws ArchiveException {

    if (archiverName == null) {
      throw new IllegalArgumentException("Archivername must not be null.");
    }

    if (in == null) {
      throw new IllegalArgumentException("InputStream must not be null.");
    }

    if (TAR.equalsIgnoreCase(archiverName)) {
      return new TarArchiveInputStream(in);
    }

    throw new ArchiveException("Archiver: " + archiverName + " not found.");
  }

  /**
   * Create an archive output stream from an archiver name and an input stream.
   * 
   * @param archiverName
   *          the archive name, i.e. "ar", "zip", "tar", "jar" or "cpio"
   * @param out
   *          the output stream
   * @return the archive output stream
   * @throws ArchiveException
   *           if the archiver name is not known
   * @throws IllegalArgumentException
   *           if the archiver name or stream is null
   */
  public ArchiveOutputStream createArchiveOutputStream(final String archiverName, final OutputStream out) throws ArchiveException {
    if (archiverName == null) {
      throw new IllegalArgumentException("Archivername must not be null.");
    }
    if (out == null) {
      throw new IllegalArgumentException("OutputStream must not be null.");
    }
    if (TAR.equalsIgnoreCase(archiverName)) {
      return new TarArchiveOutputStream(out);
    }
    throw new ArchiveException("Archiver: " + archiverName + " not found.");
  }

  /**
   * Create an archive input stream from an input stream, autodetecting the archive type from the first few
   * bytes of the stream. The InputStream must support marks, like BufferedInputStream.
   * 
   * @param in
   *          the input stream
   * @return the archive input stream
   * @throws ArchiveException
   *           if the archiver name is not known
   * @throws IllegalArgumentException
   *           if the stream is null or does not support mark
   */
  public ArchiveInputStream createArchiveInputStream(final InputStream in) throws ArchiveException {
    if (in == null) {
      throw new IllegalArgumentException("Stream must not be null.");
    }

    if (!in.markSupported()) {
      throw new IllegalArgumentException("Mark is not supported.");
    }

    final byte[] signature = new byte[12];
    in.mark(signature.length);
    try {
      int signatureLength = in.read(signature);
      in.reset();

      // Tar needs an even bigger buffer to check the signature; read the first block
      final byte[] tarheader = new byte[512];
      in.mark(tarheader.length);
      signatureLength = in.read(tarheader);
      in.reset();
      if (TarArchiveInputStream.matches(tarheader, signatureLength)) {
        return new TarArchiveInputStream(in);
      }
      // COMPRESS-117 - improve auto-recognition
      try {
        TarArchiveInputStream tais = new TarArchiveInputStream(new ByteArrayInputStream(tarheader));
        tais.getNextEntry();
        return new TarArchiveInputStream(in);
      } catch (Exception e) { // NOPMD
        // can generate IllegalArgumentException as well as IOException
        // autodetection, simply not a TAR
        // ignored
      }
    } catch (IOException e) {
      throw new ArchiveException("Could not use reset and mark operations.", e);
    }

    throw new ArchiveException("No Archiver found for the stream signature");
  }
}
