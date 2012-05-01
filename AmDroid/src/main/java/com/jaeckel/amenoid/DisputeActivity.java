package com.jaeckel.amenoid;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.actionbarsherlock.app.SherlockActivity;
import com.jaeckel.amenoid.api.AmenService;
import com.jaeckel.amenoid.api.model.Amen;
import com.jaeckel.amenoid.api.model.Objekt;
import com.jaeckel.amenoid.api.model.Statement;
import com.jaeckel.amenoid.app.AmenoidApp;
import com.jaeckel.amenoid.statement.MakeStatementActivity;
import com.jaeckel.amenoid.util.AmenLibTask;
import com.jaeckel.amenoid.util.StyleableSpannableStringBuilder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * User: biafra
 * Date: 9/25/11
 * Time: 12:59 PM
 */
public class DisputeActivity extends SherlockActivity implements AdapterView.OnItemClickListener {

  private static final String TAG = "DisputeActivity";
  private AmenService service;
  private Objekt      newObjektName;
  private Amen        currentAmen;
  private Button      disputeButton;
  private boolean hasPhoto = false;

  @Override
  public boolean onSearchRequested() {
    return super.onSearchRequested();

//    Intent intent = new Intent(this, SearchActivity.class);
//    startActivity(intent);
//
//    return false;
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    Log.d(TAG, "onConfigurationChanged(): " + newConfig);
//    setContentView(R.layout.myLayout);
  }

  public void onResume() {
    super.onResume();

    ImageView preview = (ImageView) findViewById(R.id.photo_preview);

    if (hasPhoto) {
      preview.setVisibility(View.VISIBLE);

      // show image!
      Bitmap bMap = BitmapFactory.decodeFile(MakeStatementActivity.TMP_IMAGE_PATH);
      preview.setImageBitmap(bMap);

    } else {
      preview.setVisibility(View.GONE);
    }

  }

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    service = AmenoidApp.getInstance().getService();

    setContentView(R.layout.dispute);

    setTitle("Dispute");

    Intent startingIntent = getIntent();
    currentAmen = startingIntent.getParcelableExtra(Constants.EXTRA_AMEN);
    Log.d(TAG, "Current Amen from intent: " + currentAmen);
    int disputedObjektKindId = currentAmen.getStatement().getObjekt().getKindId();

    final ObjektAutoCompleteTextView textView = (ObjektAutoCompleteTextView) findViewById(R.id.autocomplete_objekt);
    ObjektCompletionAdapter adapter = new ObjektCompletionAdapter(this, R.layout.dispute_list_item_objekt, new ArrayList<Objekt>(), disputedObjektKindId);
    textView.setAdapter(adapter);
    textView.setOnItemClickListener(this);

    TextView disputedStatement = (TextView) findViewById(R.id.disputed_statement);
    disputedStatement.setText(styleDisputedStatementWithColor(currentAmen.getStatement(), this));

    disputeButton = (Button) findViewById(R.id.dispute_amen);
    disputeButton.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
        if (!TextUtils.isEmpty(textView.getText().toString())) {

          disputeButton.setEnabled(false);

          if (newObjektName == null) {
            newObjektName = new Objekt();
            newObjektName.setName(textView.getText().toString());
            newObjektName.setKindId(currentAmen.getStatement().getObjekt().getKindId());
          }
          new DisputeTask(DisputeActivity.this, hasPhoto).execute(new Amen(currentAmen.getStatement(), newObjektName, currentAmen.getId()));

          finish();
        } else {

          Toast.makeText(DisputeActivity.this, "Empty dispute ignored", Toast.LENGTH_SHORT).show();
          disputeButton.setEnabled(true);
        }
      }
    });


    Button neverMindButton = (Button) findViewById(R.id.dispute_never_mind);
    neverMindButton.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
        finish();
      }
    });

    Button addPhoto = (Button) findViewById(R.id.add_photo);
    addPhoto.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {

//        currentTopic.setBest(currentBest);
//        final Statement statement = new Statement(currentObjekt, currentTopic);

//        new MakeStatementTask(MakeStatementActivity.this).execute(statement);

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.setType("image/*");
        intent.putExtra("crop", "true");

        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // We should always get back an image that is no larger than these dimensions
        intent.putExtra("outputX", 800);
        intent.putExtra("outputY", 800);

        // Scale the image down to 800 x 800
        intent.putExtra("scale", true);

        // Tell the picker to write its output to this URI
        intent.putExtra("output", Uri.fromFile(new File(MakeStatementActivity.TMP_IMAGE_PATH)));
        intent.putExtra("outputFormat", "JPEG");

        startActivityForResult(intent, MakeStatementActivity.REQUEST_CODE_ADD_IMAGE);
      }
    });
  }

  protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    Log.d(TAG, "onActivityResult");
    if (resultCode == RESULT_OK) {
      Log.d(TAG, "onActivityResult | resultCode: RESULT_OK");

      if (requestCode == MakeStatementActivity.REQUEST_CODE_ADD_IMAGE) {

        Log.d(TAG, "REQUEST_CODE_ADD_IMAGE");

        hasPhoto = true;
      }

    } else if (resultCode == RESULT_CANCELED) {
      Log.d(TAG, "onActivityResult | resultCode: RESULT_CANCELED");
    }
  }

  public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
    Log.d(TAG, "onItemClick: adapterView: " + adapterView + " view: " + view + ", i: " + i + " l: " + l);
    Log.d(TAG, "onItemClick: Item: " + adapterView.getAdapter().getItem(i));
    newObjektName = (Objekt) adapterView.getAdapter().getItem(i);
    Log.d(TAG, "-----> newObjektName: " + newObjektName);
  }

  public static CharSequence styleDisputedStatementWithColor(Statement stmt, Context context) {

    StyleableSpannableStringBuilder statementBuilder = new StyleableSpannableStringBuilder(context);

    statementBuilder
      .append("Hell No! ");

    if (stmt.getObjekt().getKindId() == AmenService.OBJEKT_KIND_THING) {
      //Thing

      if (stmt.getTopic().isBest()) {
        statementBuilder.appendOrange("The Best ");
      } else {
        statementBuilder.appendOrange("The Worst ");
      }
      statementBuilder
        .appendOrange(stmt.getTopic().getDescription())
        .appendOrange(" ")
        .appendOrange(stmt.getTopic().getScope());


    } else if (stmt.getObjekt().getKindId() == AmenService.OBJEKT_KIND_PLACE) {
      //Place

      if (stmt.getTopic().isBest()) {
        statementBuilder.appendBlue("The Best Place for ");
      } else {
        statementBuilder.appendBlue("The Worst Place for ");
      }
      statementBuilder
        .appendBlue(stmt.getTopic().getDescription())
        .appendBlue(" ")
        .appendBlue(stmt.getTopic().getScope());

    } else if (stmt.getObjekt().getKindId() == AmenService.OBJEKT_KIND_PERSON) {

      if (stmt.getTopic().isBest()) {
        statementBuilder.appendGreen("The Best ");
      } else {
        statementBuilder.appendGreen("The Worst ");
      }
      statementBuilder
        .appendGreen(stmt.getTopic().getDescription())
        .appendGreen(" ")
        .appendGreen(stmt.getTopic().getScope());
    }

    statementBuilder.appendBold(" is ");


    return statementBuilder;
  }

  class DisputeTask extends AmenLibTask<Amen, Integer, Long> {

    private boolean hasPhoto;

    public DisputeTask(Activity context, boolean hasPhoto) {
      super(context);
      this.hasPhoto = hasPhoto;
    }

    @Override
    protected Long wrappedDoInBackground(Amen... amens) throws IOException {
      for (Amen amen : amens) {
        Long id = service.dispute(amen);
        if (hasPhoto) {
          service.addImageToAmen(id, new File(MakeStatementActivity.TMP_IMAGE_PATH));

        }

      }

      return null;

    }

    @Override
    public void onPreExecute() {

    }

    @Override
    protected void wrappedOnPostExecute(Long result) {
      if (result != null) {
        Toast.makeText(DisputeActivity.this, "Disputed", Toast.LENGTH_SHORT).show();
      }
      disputeButton.setEnabled(true);
    }
  }

}