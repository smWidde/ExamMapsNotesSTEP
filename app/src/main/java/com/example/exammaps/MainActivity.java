package com.example.exammaps;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private Marker currentMarker;
    private float cameraZoom;
    private EditText text_X;
    private EditText text_Y;
    private ListView note_list;
    private NotesDatabase nd;
    private HashMap<Marker, Position> positions;
    private ArrayList<Note> notes;
    private ArrayAdapter<Note> notes_adapter;
    private String mode;
    private Integer id;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mode = "a";
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        text_X = findViewById(R.id.editTextTextPersonName);
        text_Y = findViewById(R.id.editTextTextPersonName2);
        nd = new NotesDatabase(getBaseContext());
        note_list = findViewById(R.id.notesList);
        notes = new ArrayList<>();
        notes_adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, notes);
        note_list.setAdapter(notes_adapter);
        note_list.setOnItemClickListener((parent, view, position, id)->{
            showNoteEdit(findViewById(R.id.edit_note));
            Note n = notes.get(position);
            ((EditText)findViewById(R.id.editTextTitle)).setText(n.Title);
            ((EditText)findViewById(R.id.editTextTitle)).setText(n.Text);
            id = n.Id;
            mode = "e";
        });
    }
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(latLng -> {
            currentMarker = mMap.addMarker(new MarkerOptions().position(latLng));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            nd.addPosition(new Position(latLng.latitude, latLng.longitude));
        });
        mMap.setOnMarkerClickListener(marker -> {
            currentMarker = marker;
            getNotes();
            return true;
        });
        LatLng dnipro = new LatLng(48.46317679761322, 35.045639069098456);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(dnipro));
        cameraZoom = 10;
        mMap.moveCamera(CameraUpdateFactory.zoomTo(cameraZoom));
        positions = new HashMap<>();
        for(Position i : nd.positions)
        {
            Marker tmp = mMap.addMarker(new MarkerOptions().position(new LatLng(i.Latitude, i.Longtitude)));
            positions.put(tmp, i);
        }
    }
    public void GoToPoint(View view) {
        float x = Float.parseFloat(text_X.getText().toString());
        float y = Float.parseFloat(text_Y.getText().toString());
        LatLng place = new LatLng(x, y);
        mMap.addMarker(new MarkerOptions().position(new LatLng(x, y)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(place));
        nd.addPosition(new Position(x, y));
    }
    public void AddZoom(View view) {
        cameraZoom += 1;
        mMap.moveCamera(CameraUpdateFactory.zoomTo(cameraZoom));
    }
    public void MinusZoom(View view) {
        if(cameraZoom>0)
        {
            cameraZoom -= 1;
            mMap.moveCamera(CameraUpdateFactory.zoomTo(cameraZoom));
        }
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.editNoteItem)
        {
            showNotes(item.getActionView());
        }
        return super.onOptionsItemSelected(item);
    }
    private void ChangeLayoutWeights(int map, int notes, int edit) {
        ChangeLayout(R.id.map, map);
        ChangeLayout(R.id.notes_show, notes);
        ChangeLayout(R.id.edit_note, edit);
    }
    public void killMenu(View view) {
        ChangeLayoutWeights(5,0,0);
    }
    public void showNoteEdit(View view) {
        ChangeLayoutWeights(3,0,2);
    }
    public void showNotes(View view) {
        ChangeLayoutWeights(3,2,0);
    }
    private void ChangeLayout(int id, int weight) {
        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                weight
        );
        findViewById(id).setLayoutParams(layout);
    }
    private void getNotes()
    {
        Position pos = positions.get(currentMarker);
        notes.clear();
        notes.addAll(nd.getNotesByPositionId(pos.Id));
        notes_adapter.notifyDataSetChanged();
    }
    public void saveNote(View view) {
        if(mode.equals("a"))
        {
            nd.addNote(new Note(((EditText)findViewById(R.id.editTextTitle)).getText().toString(), ((EditText)findViewById(R.id.editTextTitle)).getText().toString()), positions.get(currentMarker).Id);
        }
        else if(mode.equals("e"))
        {
            nd.editNote(id, new Note(((EditText)findViewById(R.id.editTextTitle)).getText().toString(), ((EditText)findViewById(R.id.editTextTitle)).getText().toString()){{Id = id;}});
            id = -1;
            mode = "a";
        }
        getNotes();
        showNotes(view);
    }
}
