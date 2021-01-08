package ru.phplego.secretary.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.*;
import android.widget.*;
import ru.phplego.core.Utils;
import ru.phplego.core.db.ActiveRecord;
import ru.phplego.core.Cachable;
import ru.phplego.core.EManager;
import ru.phplego.secretary.AService;
import ru.phplego.secretary.App;
import ru.phplego.rec_module.R;
import ru.phplego.secretary.activities.ActivityOneRecord;
import ru.phplego.core.pages.ContextMenuProvider;
import ru.phplego.secretary.db.ActiveCall;
import ru.phplego.secretary.db.TableCalls;
import ru.phplego.secretary.dialogs.DialogOneRecord;
import ru.phplego.secretary.dialogs.Dialogs;
import ru.phplego.secretary.media.MyPlayer;
import ru.phplego.secretary.etc.Humanist;
import java.io.File;
import static android.view.Menu.NONE;

/**
 * Created with IntelliJ IDEA.
 * User: Oleg
 * Date: 21.04.12
 * Time: 22:54
 * To change this template use File | Settings | File Templates.
 */

public class ViewOneRecord extends LinearLayout implements MediaPlayer.OnCompletionListener, MyPlayer.OnProgressListenter, ContextMenuProvider, Cachable {
    private View mLayout;

    private ActiveCall mActiveCall;

    private Context mContext;

    private TextView tvInfo;

    private TextView tvPhone;

    private TextView tvNote;

    private ImageButton ibPlay;

    private ImageButton ibPause;

    private ImageButton ibCall;

    private ImageView ivIncomingIndicator;

    private SeekBar mSeekBar;

    private static final int CONTEXT_ADD_CONTACT =1;

    private static final int CONTEXT_SEND =2;

    private static final int CONTEXT_EDIT =3;

    private static final int CONTEXT_DELETE =4;

    private static final int CONTEXT_FAVOURITE =5;

    private static final int CONTEXT_PLAN =6;

    private static final int CONTEXT_ABONENT_HISTORY =7;

    public static class OnNoteAddOrRemoveEvent extends EManager.Event {
    }
    public boolean isInvalid() {
        return mActiveCall.isInvalid();
    }

    public ViewOneRecord(Context context, ActiveCall ac) {
        super(context);
        mContext = context;
        mActiveCall = ac;
        ListView.LayoutParams lp = new ListView.LayoutParams(-1, -2);
        setLayoutParams(lp);

        LayoutInflater inflater = LayoutInflater.from(context);
        mLayout = inflater.inflate(R.layout.calls_list_item, null, true);
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(-1, -2);
        mLayout.setLayoutParams(lp2);
        addView(mLayout);

        tvInfo              = (TextView)    findViewById(R.id.text_top);
        tvNote              = (TextView)    findViewById(R.id.text_note);
        tvPhone             = (TextView)    findViewById(R.id.text_phone);
        ivIncomingIndicator = (ImageView)   findViewById(R.id.incoming_indicator);
        mSeekBar            = (SeekBar)     findViewById(R.id.playSeekBar);
        ibPlay              = (ImageButton) findViewById(R.id.play_button);
        ibPause             = (ImageButton) findViewById(R.id.pause_button);
        ibCall              = (ImageButton) findViewById(R.id.call_button);

        ibPlay.setFocusable(false); // Для того, чтобы работало контекстное меню и клик по элементу
        ibPause.setFocusable(false);
        ibCall.setFocusable(false);

        fillFromActiveCall(ac);

        setListeners();

        // Слушатель, который обновляет индикатор
        EManager.EventListener dataChangedListener = new EManager.EventListener() {
            @Override
            public void onEvent(EManager.Event e) {
                refreshIndicator();
            }
        };

        // События, на которые мы обновляем индикатор
        App.getEManager().setEventListener(ActiveCall.AddToFavouritesEvent.class, dataChangedListener, null);
        App.getEManager().setEventListener(ActiveCall.RemoveFromFavouritesEvent.class, dataChangedListener, null);
    }

    public void fillFromActiveCall(ActiveCall ac) {
        mActiveCall = ac;

        fillViews();
        this.setVisibility(VISIBLE);
        this.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        this.requestLayout();

        // Обработчик изменения объекта записи разговора откуда-то извне
        mActiveCall.setOnChangeListener(new ActiveRecord.OnChangeListener() {
            @Override
            public void onChange(ActiveRecord activeRecord, String field) {
                if(!field.equals(TableCalls.note.toString())) return;
                tvNote.setText(mActiveCall.getNote());
                if(mActiveCall.getNote().length() > 0){
                    showNote();
                }
                else{
                    hideNote();
                }
            }

            public Context getContext() {
                return ViewOneRecord.this.mContext;
            }
        });
    }

    private void fillViews() {
        long duration = mActiveCall.getDuration();

        //Если файла нет, то на кнопке Play крестик
        if(!mActiveCall.getFile().exists() || mActiveCall.getFile().length() == 0)
            ibPlay.setImageResource(android.R.drawable.ic_delete);
        else
            ibPlay.setImageResource(android.R.drawable.ic_media_play);

        // Строка с датой звонка и продолжительностью
        String text = Humanist.date("E, dd MMMMMMMMMM HH:mm", mActiveCall.getCreatedDate());
        if(duration > 0) text += " | " + Humanist.getDuration(duration);
        tvInfo.setText(text);

        // Строка с номером телефона
        tvPhone.setText(mActiveCall.getContactDisplayName(App.getContext()));

        refreshIndicator();

        // Тексовая заметка к разговору
        tvNote.setText(mActiveCall.getNote());
        hideNote();
        if(mActiveCall.getNote().length() == 0)
            hideNote();
        else
            showNote();

        setTag(mActiveCall.getId());
        setId((int) mActiveCall.getId());

        if(App.getPlayer().isCurrent(mActiveCall)){
            if(App.getPlayer().isPlaying()){
                ibPlay.setVisibility(View.GONE);
                ibPause.setVisibility(View.VISIBLE);
            }
            mSeekBar.setVisibility(View.VISIBLE);
        }
        else {
            ibPlay.setVisibility(View.VISIBLE);
            ibPause.setVisibility(View.GONE);
            mSeekBar.setVisibility(View.GONE);
        }
    }

    private void refreshIndicator() {
        // Индикатор входящего/исходящего вызова
        ivIncomingIndicator.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.MULTIPLY);
        ivIncomingIndicator.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
        if(mActiveCall.isIncoming())
            ivIncomingIndicator.setImageDrawable(getResources().getDrawable(R.drawable.sym_call_incoming));
        else
            ivIncomingIndicator.setImageDrawable(getResources().getDrawable(R.drawable.sym_call_outgoing));

        // Этот же индикатор используем для отображения звезды (избранное)
        if(mActiveCall.isFavourite()){
            ivIncomingIndicator.setColorFilter(Color.parseColor("#FFDD00"), PorterDuff.Mode.MULTIPLY);
            ivIncomingIndicator.setImageDrawable(getResources().getDrawable(android.R.drawable.btn_star_big_on));
            ivIncomingIndicator.getLayoutParams().height = App.dip(25);
        }
    }

    private void setListeners() {
        // Кнопка "Воспроизвести"
        ibPlay.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Начинаем воспроизведение
                if(App.getPlayer().play(mActiveCall)){
                    mSeekBar.setVisibility(View.VISIBLE); // Показываем полосу воспроизведения
                    ibPlay.setVisibility(View.GONE);
                    ibPause.setVisibility(View.VISIBLE);
                };
            }
        });

        // Кнопка "Пауза"
        ibPause.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                App.getPlayer().pause();
                ibPlay.setVisibility(View.VISIBLE);
                ibPause.setVisibility(View.GONE);
            }
        });

        // Кнопка "Перезвонить"
        ibCall.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + mActiveCall.getPhone()));
                mContext.startActivity(intent);
            }
        });

        // Перемотка записи
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
                if(fromUser) App.getPlayer().seekTo(App.getPlayer().getDuration() * i / 100);
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Обработчик прикосновения к телу (подсвечиваем в лучших традициях)
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        setBackgroundColor(Color.parseColor("#55AAAAAA"));
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_OUTSIDE:
                        setBackgroundColor(Color.parseColor("#00000000"));
                        break;
                }
                return false;
            }
        });

        // Обработчик клика по телу всего ListItem
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogOneRecord.get((Activity)mContext, mActiveCall).show();
            }
        });


        ((Activity) mContext).registerForContextMenu(this);
    }

    public void hideNote() {
        tvNote.setVisibility(View.GONE);
    }

    public void showNote() {
        tvNote.setVisibility(View.VISIBLE);
    }

    public ActiveCall getActiveCall() {
        return mActiveCall;
    }

    public void onCompletion(MediaPlayer mediaPlayer) {
        mSeekBar.setProgress(0);
        mSeekBar.setVisibility(View.GONE);
        ibPause.setVisibility(View.GONE);
        ibPlay.setVisibility(View.VISIBLE);
    }

    public void onProgress(final int progress) {
        if (!App.getPlayer().isCurrent(mActiveCall)) return;
        mSeekBar.setProgress(progress / 10);
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;
        if(mActiveCall.getContactDisplayName(App.getContext()) == mActiveCall.getPhone()){
            menu.add(CONTEXT_ADD_CONTACT, (int)info.id, NONE, R.string.save_number);
        }
        menu.add(CONTEXT_SEND, (int)info.id, NONE, R.string.send_record);
        menu.add(CONTEXT_ABONENT_HISTORY, (int)info.id, NONE, R.string.abonent_history);
        menu.add(CONTEXT_EDIT, (int)info.id, NONE, R.string.edit_file);
        menu.add(CONTEXT_FAVOURITE, (int)info.id, NONE, mActiveCall.isFavourite() ? R.string.remove_from_fav : R.string.add_to_fav);
        menu.add(CONTEXT_PLAN, (int)info.id, NONE, mActiveCall.isPlanned() ? R.string.remove_from_plan : R.string.add_to_plan);
        menu.add(CONTEXT_DELETE, (int)info.id, NONE, R.string.delete);
    }

    public boolean onContextItemSelected(MenuItem menuItem, View view) {
        switch(menuItem.getGroupId()){
            // Удаление записи
            case CONTEXT_DELETE:
                mActiveCall.delete();
                // СОБЫТИЕ УДАЛЕНИЯ !!!!!!!!
                break;
            case CONTEXT_ABONENT_HISTORY:{
                Intent intent = new Intent(mContext, ActivityOneRecord.class);
                intent.putExtra(AService.EXTRA_RECORD_ID, (long) menuItem.getItemId());
                mContext.startActivity(intent);
                break;
            }
            // Редактирование записи
            case CONTEXT_EDIT:{
                ActiveCall ac = (ActiveCall) new ActiveCall().getInstance(menuItem.getItemId());
                File f = new File(ac.getFilename());
                if(!f.exists()){
                    App.toast("File not found");
                    return true;
                }

                Intent intent = new Intent(Intent.ACTION_EDIT);
                Uri uri = Uri.parse("file:/" + ac.getFilenameSmart());
                //intent.setData(uri);
                intent.setDataAndType(uri, ac.getFileMimeType());
                mContext.startActivity(intent);
                break;
            }
            case CONTEXT_SEND:
                ActiveCall ac = (ActiveCall) new ActiveCall().getInstance(menuItem.getItemId());
                File f = new File(ac.getFilename());
                if(!f.exists()){
                    App.toast("File not found");
                    return true;
                }

                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.setType("video/mp4");
                sendIntent.putExtra(Intent.EXTRA_TITLE, R.string.app_name);
                sendIntent.putExtra(Intent.EXTRA_TEXT, R.string.app_name);
                //Если имя файла спорчено, подчеркиванием в конце
                if(ac.getFilename().endsWith("_")){
                    Utils.copyfile(ac.getFilename(), ac.getFilenameSmart());
                    sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:/" + ac.getFilenameSmart()));
                }
                else{
                    sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:/" + ac.getFilename()));
                }

                mContext.startActivity(Intent.createChooser(sendIntent, mContext.getString(R.string.send_record)));
                break;
            case CONTEXT_ADD_CONTACT:
                ActiveCall ac1 = (ActiveCall) new ActiveCall().getInstance(menuItem.getItemId());
                Intent addContactIntent = new Intent(Intent.ACTION_INSERT);
                addContactIntent.setType(ContactsContract.Contacts.CONTENT_TYPE);
                addContactIntent.putExtra(ContactsContract.Intents.Insert.PHONE, ac1.getPhone());
                mContext.startActivity(addContactIntent);
                break;
            case CONTEXT_FAVOURITE:
                if(mActiveCall.isFavourite())
                    mActiveCall.removeFromFavourites();
                else
                    mActiveCall.addToFavourites();
                break;
            case CONTEXT_PLAN:
                if(mActiveCall.isPlanned())
                    mActiveCall.removeFromPlan();
                else
                    Dialogs.toPlanDialog(mContext, mActiveCall).show();

                break;
        }
        return true;
    }

    protected void onAttachedToWindow() {
        App.getPlayer().setOnCompletionListener(this);
        App.getPlayer().setOnProgressListener(this);
    }

    protected void onDetachedFromWindow() {
        App.getPlayer().removeOnCompletionListener(this);
        App.getPlayer().removeOnProgressListener(this);
    }

}
