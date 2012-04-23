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
package de.neofonie.mobile.unicorn.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;

/**
 * This Thread can be used to extract a tar archive.
 * 
 * @author tim.messerschmidt@neofonie.de
 * 
 */
public class FileExtractor extends Thread {
  private String                filename;
  private String                targetPath;
  private FileExtractorListener listener;

  /**
   * Creates a new instance of the {@link FileExtractor}
   * 
   * @param filename
   *          the tar's filename
   * @param targetPath
   *          the Extractor's target path
   * @param listener
   *          the {@link FileExtractorListener} to be notified on events
   */
  public FileExtractor(String filename, String targetPath, FileExtractorListener listener) {
    this.filename = filename;
    this.targetPath = targetPath;
    this.listener = listener;
  }

  @Override
  public void run() {
    File tarFile = new File(filename);
    File targetDir = new File(targetPath);
    if (!targetDir.exists()) {
      targetDir.mkdirs();
    }
    if (tarFile.exists()) {
      try {
        InputStream is = new FileInputStream(tarFile);
        TarArchiveInputStream tais = (TarArchiveInputStream) new ArchiveStreamFactory().createArchiveInputStream(ArchiveStreamFactory.TAR,
                                                                                                                 is);
        TarArchiveEntry tarEntry = null;
        while ((tarEntry = (TarArchiveEntry) tais.getNextEntry()) != null) {
          OutputStream out = new FileOutputStream(new File(targetDir, tarEntry.getName()));
          IOUtils.copy(tais, out);
        }
        tais.close();
        is.close();
      } catch (FileNotFoundException e) {
        e.printStackTrace();
        listener.error(e);
      } catch (ArchiveException e) {
        e.printStackTrace();
        listener.error(e);
      } catch (IOException e) {
        e.printStackTrace();
        listener.error(e);
      }
    } else {
      listener.error(new IllegalArgumentException());
    }
  }
}
