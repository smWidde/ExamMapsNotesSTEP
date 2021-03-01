package com.example.exammaps;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import java.util.ArrayList;
import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

public class NotesDatabase {
    private int NoteId;
    private int PositionId;
    SQLiteDatabase db;
    public ArrayList<Note> notes;
    public ArrayList<Position> positions;
    public NotesDatabase(Context cw)
    {
        NoteId = 1;
        PositionId = 1;
        db = cw.openOrCreateDatabase("notes.db", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS notes (id INTEGER, Title TEXT, Text TEXT);");
        db.execSQL("CREATE TABLE IF NOT EXISTS positions (id INTEGER, latitude REAL, longtitude REAL);");
        db.execSQL("CREATE TABLE IF NOT EXISTS notes_to_positions (note_id INTEGER, position_id INTEGER);");
        notes = new ArrayList<>();
        getNotes();
        if(notes.size()>0)
            NoteId = notes.get(notes.size()-1).Id + 1;
        positions = new ArrayList<>();
        getPositions();
        if(positions.size()>0)
            PositionId = positions.get(positions.size()-1).Id + 1;

    }
    public void editNote(Integer NoteId, Note note)
    {
        String title = note.Title;
        String text = note.Text;
        db.execSQL("UPDATE notes SET title = '"+title+"', text = '"+text+"' WHERE id = "+NoteId);
        getNotes();
    }
    public void addNote(Note note, int positionId)
    {
        String title = note.Title;
        String text = note.Text;
        db.execSQL("INSERT INTO notes VALUES("+NoteId+", '"+title+"', '"+text+"')");
        db.execSQL("INSERT INTO notes_to_positions VALUES("+NoteId+", "+positionId+")");
        NoteId++;
        getNotes();
    }
    public void addPosition(Position pos)
    {
        Double lat = pos.Latitude;
        Double lan = pos.Longtitude;
        db.execSQL("INSERT INTO notes VALUES("+PositionId+", "+lat+", "+lan+")");
        PositionId++;
        getPositions();
    }
    private void getNotes()
    {
        ArrayList<Note> notes = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM notes", null);
        while (cursor.moveToNext()){
            notes.add(new Note(cursor.getString(1), cursor.getString(2)){{Id=cursor.getInt(0);}});
        }
        cursor.close();
        this.notes.clear();
        this.notes.addAll(notes);
    }
    private void getPositions()
    {
        ArrayList<Position> positions = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM notes", null);
        while (cursor.moveToNext()){
            positions.add(new Position(cursor.getDouble(1), cursor.getDouble(2)){{Id=cursor.getInt(0);}});
        }
        cursor.close();
        this.positions = positions;
    }
    public ArrayList<Note> getNotesByPositionId(int positionId)
    {
        ArrayList<Note> notes = new ArrayList<>();
        ArrayList<Integer> ids = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM notes_to_positions WHERE position_id = "+positionId, null);
        while (cursor.moveToNext()){
            Cursor cursor2 = db.rawQuery("SELECT * FROM notes WHERE id = "+cursor.getInt(0), null);
            while(cursor2.moveToNext())
            {
                Note note = new Note(cursor2.getString(1), cursor2.getString(2));
                note.Id = cursor2.getInt(0);
                notes.add(note);
            }
            cursor2.close();
        }
        cursor.close();
        return notes;
    }
}
