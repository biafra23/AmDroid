package com.jaeckel.amenoid.commands;

import java.io.IOException;
import java.util.List;

import com.jaeckel.amenoid.api.AmenService;
import com.jaeckel.amenoid.api.model.Amen;

/**
 * @author biafra
 * @date 7/14/12 12:16 PM
 */
public class AmenFromFollowersCommand extends AbstractCommand<List<Amen>> {

  private int  type;
  private Long lastId;
  private int  pageSize;


  public AmenFromFollowersCommand(AmenService service, int type, Long lastId, int pageSize) {

    super(service);
    this.lastId = lastId;
    this.type = type;
    this.pageSize = pageSize;

  }

  public List<Amen> execute() throws IOException {

    List<Amen> result = service.getFeed(type);
    lastId = result.get(result.size()).getId();

    return result;
  }

  public List<Amen> executeNextPage() throws IOException {

    List<Amen> result = service.getFeed(lastId, pageSize, type);
    lastId = result.get(result.size()).getId();
    return result;
  }


}


