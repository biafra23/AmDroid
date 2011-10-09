package com.jaeckel.amdroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * User: biafra
 * Date: 10/9/11
 * Time: 9:25 PM
 */
public class ChooseStatementTypeActivity extends Activity {
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.choose_type);

    TextView person = (TextView) findViewById(R.id.type_person);
    person.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        startActivity(new Intent(ChooseStatementTypeActivity.this, MakeStatementActivity.class));
      }
    });
    TextView place = (TextView) findViewById(R.id.type_place);
    place.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        startActivity(new Intent(ChooseStatementTypeActivity.this, MakeStatementActivity.class));
      }
    });
    TextView thing = (TextView) findViewById(R.id.type_thing);
    thing.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        startActivity(new Intent(ChooseStatementTypeActivity.this, MakeStatementActivity.class));
      }
    });
  }
}