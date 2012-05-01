package com.jaeckel.amenoid.statement;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.jaeckel.amenoid.Constants;
import com.jaeckel.amenoid.R;
import com.jaeckel.amenoid.api.AmenService;

/**
 * User: biafra
 * Date: 10/9/11
 * Time: 9:25 PM
 */
public class ChooseStatementTypeActivity extends Activity {
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.choose_type);
    setTitle("Choose Statement Type");

    TextView person = (TextView) findViewById(R.id.type_person);
    person.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        final Intent intent = new Intent(ChooseStatementTypeActivity.this, MakeStatementActivity.class);
        intent.putExtra(Constants.EXTRA_OBJEKT_KIND, AmenService.OBJEKT_KIND_PERSON);
        startActivity(intent);
      }
    });
    TextView place = (TextView) findViewById(R.id.type_place);
    place.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        final Intent intent = new Intent(ChooseStatementTypeActivity.this, MakeStatementActivity.class);
        intent.putExtra(Constants.EXTRA_OBJEKT_KIND, AmenService.OBJEKT_KIND_PLACE);
        startActivity(intent);
      }
    });
    TextView thing = (TextView) findViewById(R.id.type_thing);
    thing.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        final Intent intent = new Intent(ChooseStatementTypeActivity.this, MakeStatementActivity.class);
        intent.putExtra(Constants.EXTRA_OBJEKT_KIND, AmenService.OBJEKT_KIND_THING);
        startActivity(intent);
      }
    });
  }
}