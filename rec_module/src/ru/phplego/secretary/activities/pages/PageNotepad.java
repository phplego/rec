package ru.phplego.secretary.activities.pages;

import android.content.Context;
import android.content.Intent;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import ru.phplego.core.db.ActiveQuery;
import ru.phplego.core.pages.ActivityPagerAbstract;
import ru.phplego.secretary.App;
import ru.phplego.rec_module.R;
import ru.phplego.secretary.activities.pages.adapters.AdapterNotepad;
import ru.phplego.secretary.db.ActiveNote;
import ru.phplego.secretary.db.TableNotes;
import ru.phplego.secretary.dialogs.DialogNotepadEditNote;
import static android.view.Menu.NONE;

/**
* Created with IntelliJ IDEA by Oleg Dubrov
* User: Oleg
* Date: 06.06.12
* Time: 19:08
*/

public class PageNotepad extends PageAbstractList implements AdapterView.OnItemClickListener {
	private ActiveNote mCurrentNote;

	private EditText mEditTextNote;

	public PageNotepad(ActivityPagerAbstract activityPager) {
		super(activityPager);
	}

	public String getTitle() {
		return getContext().getString(R.string.notepad).toUpperCase();
	}

	public AdapterNotepad getAdapter() {
		return (AdapterNotepad) super.getAdapter();
	}

	public void onCreate() {
		super.onCreate();

		View headerView = App.inflate(R.layout.page_notepad_header);
		getListView().addHeaderView(headerView);
		getListView().setDivider(null);

		mEditTextNote = (EditText) headerView.findViewById(R.id.editTextNote);
		final Button btnInsertNote    = (Button) headerView.findViewById(R.id.btnInsertNote);
		final Button btnNoteMenu      = (Button) headerView.findViewById(R.id.btnNoteMenu);

		getListView().setOnCreateContextMenuListener(this);
		getListView().setOnItemClickListener(this);
		getContext().registerForContextMenu(btnNoteMenu);

		// Сохраняем заметку на ввод каждого символа
		TextWatcher textWatcher = new TextWatcher() {
				@Override
				public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

				@Override
				public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
						mCurrentNote.set(TableNotes.text, charSequence.toString().trim());
						mCurrentNote.update();
				}

				@Override
				public void afterTextChanged(Editable editable) {}
		};
		mEditTextNote.addTextChangedListener(textWatcher);

		btnNoteMenu.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
						view.showContextMenu();
				}
		});

		mCurrentNote = ActiveNote.getLast();
		if(mCurrentNote == null){
				// Создаем новую пустую заметку для следующего редактирования
				mCurrentNote = new ActiveNote();
				mCurrentNote.set(TableNotes.created, System.currentTimeMillis());
				mCurrentNote.insert();
		}

		mEditTextNote.setText(mCurrentNote.getText());

		btnInsertNote.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
						if(mEditTextNote.getText().length() == 0) return;
						mCurrentNote.set(TableNotes.text, mEditTextNote.getText().toString().trim());
						mCurrentNote.set(TableNotes.created, System.currentTimeMillis());
						mCurrentNote.set(TableNotes.modified, System.currentTimeMillis());
						mCurrentNote.update();

						// Создаем новую пустую заметку для следующего редактирования
						mCurrentNote = new ActiveNote();
						mCurrentNote.set(TableNotes.created, System.currentTimeMillis());
						mCurrentNote.insert();

						// Перерисовываем список
						getAdapter().notifyDataSetChanged();
						mEditTextNote.setText(""); // Стираем текст в последний момент (важно)
				}
		});

		setAdapter(new AdapterNotepad(getContext()));
	}

	static final int CONTEXT_MENU_DELETE =1;

	static final int CONTEXT_MENU_CLEAR =2;

	static final int CONTEXT_MENU_COPY =3;

	static final int CONTEXT_MENU_SEND =4;

	static final int CONTEXT_MENU_PASTE =5;

	static final int CONTEXT_MENU_MOVE_UP =6;

	static final int CONTEXT_MENU_MOVE_DOWN =7;

	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		if(menuInfo == null) return;
		AdapterView.AdapterContextMenuInfo info1 = (AdapterView.AdapterContextMenuInfo) menuInfo;
		int item_id = (int)info1.id;
		boolean listItem = true;
		if(item_id == -1){
				item_id = (int) ActiveNote.getLast().getId();
				listItem = false;
		}
		ActiveNote activeNote = (ActiveNote) new ActiveNote().getInstance(item_id);
		if(listItem)
				menu.add(CONTEXT_MENU_DELETE, item_id, NONE, R.string.delete);
		else{
				menu.add(CONTEXT_MENU_CLEAR, item_id, NONE, R.string.clear);
		}

		if(activeNote.getText().length() > 0){
				menu.add(CONTEXT_MENU_COPY, item_id, NONE, android.R.string.copy);
				menu.add(CONTEXT_MENU_SEND, item_id, NONE, R.string.send);
		}

		ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
		if(clipboard.hasText()){
				menu.add(CONTEXT_MENU_PASTE, item_id, NONE, android.R.string.paste);
		}

		if(listItem)
				menu.add(CONTEXT_MENU_MOVE_UP, item_id, NONE, "^ "+App.getStr(R.string.move_up));
	}

	public boolean onContextItemSelected(MenuItem menuItem) {
		ActiveNote activeNote = (ActiveNote) new ActiveNote().getInstance(menuItem.getItemId());
		ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
		switch (menuItem.getGroupId()){
				case CONTEXT_MENU_DELETE:
						new ActiveNote().getInstance(menuItem.getItemId()).delete();
						getAdapter().notifyDataSetChanged();
						break;
				case CONTEXT_MENU_CLEAR:
						mEditTextNote.setText("");
						mCurrentNote.set(TableNotes.text, "");
						mCurrentNote.update();
						break;
				case CONTEXT_MENU_COPY:
						clipboard.setText(activeNote.getText());
						break;
				case CONTEXT_MENU_SEND:
						Intent i=new Intent(android.content.Intent.ACTION_SEND);
						i.setType("text/plain");
						i.putExtra(android.content.Intent.EXTRA_TEXT, activeNote.getText());
						getContext().startActivity(i);
						break;
				// Вставить
				case CONTEXT_MENU_PASTE:
						if(clipboard.hasText()){
								activeNote.setText(activeNote.getText()+clipboard.getText());
								activeNote.update();
								getAdapter().notifyDataSetChanged();

								if(mCurrentNote.getId() == activeNote.getId())
										mEditTextNote.setText(activeNote.getText());
						}
						break;
				// Переместить заметку на одну позицию вверх
				case CONTEXT_MENU_MOVE_UP:
						// Меняем местами даты создания у двух записей
						ActiveQuery<ActiveNote> q = new ActiveQuery();                  // Получаем замутку, которая сверху
						q.from(new ActiveNote());
						q.where(TableNotes.created + " > " + activeNote.get(TableNotes.created));
						q.orderby(TableNotes.created.getName());
						q.limit(1);
						if(q.count() < 2) break;                                        // Самая первая запись зарезервирована под текущую заметку
						ActiveNote upperNote = q.object();
						if(upperNote == null) break;                                    // Если некуда двигать выше
						long upperDate = upperNote.getCreatedTime();

						upperNote.set(TableNotes.created, activeNote.getCreatedTime()); // Cохраняем дату
						upperNote.update();
						activeNote.set(TableNotes.created, upperDate);
						activeNote.update();

						// Перерисовываем список
						getAdapter().notifyDataSetChanged();
						break;
		}
		return true;
	}

	public void onItemClick(AdapterView <?> adapterView, View view, int i, long id) {
		ActiveNote activeNote = (ActiveNote) new ActiveNote().getInstance(id);
		Runnable onComplete = new Runnable() {
				@Override
				public void run() {
						getAdapter().notifyDataSetChanged();
				}
		};
		DialogNotepadEditNote.get(getContext(), activeNote, onComplete).show();
	}

}
