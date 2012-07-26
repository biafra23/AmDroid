package com.jaeckel.amenoid.commands;

import java.io.IOException;

import com.jaeckel.amenoid.api.AmenService;

/**
 * @author biafra
 * @date 7/14/12 11:56 AM
 */
public abstract class AbstractCommand<T> {

  AmenService service;

  public AbstractCommand(AmenService service) {
    this.service = service;
  }

  public abstract T execute() throws IOException;
  public abstract T executeNextPage() throws IOException;

  public AmenService getService() {
    return service;
  }

  public void setService(AmenService service) {
    this.service = service;
  }
}
