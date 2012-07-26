package com.jaeckel.amenoid.commands;

import java.io.IOException;
import java.util.ArrayList;

import com.jaeckel.amenoid.api.AmenService;
import com.jaeckel.amenoid.api.model.Amen;

/**
 * @author biafra
 * @date 7/14/12 11:56 AM
 */
public class AmenForCategoryCommand extends AbstractCommand<ArrayList<Amen>> {

  private String categoryId;
  private int    page;
  private Double lat;
  private Double lon;


  public AmenForCategoryCommand(AmenService service, String categoryId, int page, Double lat, Double lon) {
    super(service);
    this.categoryId = categoryId;
    this.page = page;
    this.lat = lat;
    this.lon = lon;
  }

  public ArrayList<Amen> execute() throws IOException {

    ArrayList<Amen> result = service.getAmenForCategory(categoryId, page, lat, lon);

    return result;
  }

  public ArrayList<Amen> executeNextPage() throws IOException {

    page = page + 1;

    ArrayList<Amen> result = service.getAmenForCategory(categoryId, page, lat, lon);

    return result;
  }


}
