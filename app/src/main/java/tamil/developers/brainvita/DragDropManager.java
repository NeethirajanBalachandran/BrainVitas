package tamil.developers.brainvita;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SearchEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ImageView;
import android.widget.PopupWindow;

public class DragDropManager
{
    private static DragDropManager instance;
    private Activity mActivity;
    private List<View> dropzones;
    private Map<View, Integer> dropzonestates;
    private Map<View, DropZoneListener> dropzonelisteners;
    private PopupWindow popoup;
    private MotionEvent firstEvent;
    private Rect rect;
    private Object item;

    public static DragDropManager getInstance()
    {
        if (instance == null) instance = new DragDropManager();
        return instance;
    }

    private DragDropManager()
    {
    }

    public void init(Activity a)
    {
        mActivity = a;
        dropzones = new ArrayList<View>();
        dropzonelisteners = new HashMap<View, DropZoneListener>();
        dropzonestates = new HashMap<View, Integer>();
        rect = new Rect();
    }

    public void addDropZone(View zone, DropZoneListener zonelistener)
    {
        dropzones.add(zone);
        dropzonelisteners.put(zone, zonelistener);
        dropzonestates.put(zone, 0);
    }

    public void clearZones()
    {
        dropzones.clear();
        dropzonelisteners.clear();
        dropzonestates.clear();
    }

    public void clearZone(View zone)
    {
        dropzones.remove(zone);
        dropzonelisteners.remove(zone);
        dropzonestates.remove(zone);
    }

    private void checkDropZones(MotionEvent event)
    {
        boolean isOver;
        HashSet<DropZoneListener> listeners = new HashSet<DropZoneListener>(dropzonelisteners.values());

        for (View zone : dropzones)
        {
            int[] location = new int[2];
            zone.getLocationInWindow(location);
            zone.getDrawingRect(rect);
            rect.offset(location[0], location[1]);
            isOver = rect.contains((int) event.getRawX(), (int) event.getRawY());

            switch (dropzonestates.get(zone))
            {
                case 0:
                    if (isOver)
                    {
                        for(DropZoneListener listener:listeners)
                        {
                            listener.OnDragZoneEntered(zone, item); 
                        }
                        dropzonestates.put(zone, 1);
                    }

                    break;
                case 1:
                    if (!isOver)
                    {
                        for(DropZoneListener listener:listeners)
                        {
                            listener.OnDragZoneLeft(zone, item);    
                        }
                        dropzonestates.put(zone, 0);
                    }
                    else if (isOver && event.getAction()==MotionEvent.ACTION_UP)
                    {
                        for(DropZoneListener listener:listeners)
                        {
                            listener.OnDropped(zone, item); 
                        }
                        dropzonestates.put(zone, 0);
                    }
                    break;
            }
        }
    }

    @SuppressWarnings("deprecation")
	public void startDragging(final View dragView, Object item)
    {
        this.item = item;
        // Copy view Bitmap (Clone Object visual)
        ImageView view = new ImageView(mActivity);
        view.measure(dragView.getWidth(), dragView.getHeight());

        Bitmap returnedBitmap = Bitmap.createBitmap(dragView.getWidth(), dragView.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        dragView.draw(canvas);

        view.setBackgroundDrawable(new BitmapDrawable(dragView.getResources(), returnedBitmap));

        // Set up Window
        popoup = new PopupWindow(view, dragView.getWidth(), dragView.getHeight());
        popoup.setWindowLayoutMode(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        // set window at position
        int[] location = new int[2];
        dragView.getLocationInWindow(location);
        popoup.showAtLocation(mActivity.getWindow().getDecorView(), Gravity.NO_GRAVITY, location[0], location[1]);
        // Switch call Backs
        callbackDefault = mActivity.getWindow().getCallback();
        mActivity.getWindow().setCallback(callback);
    }

    private android.view.Window.Callback callbackDefault;

    private android.view.Window.Callback callback = new android.view.Window.Callback()
    {

        @Override
        public boolean dispatchGenericMotionEvent(MotionEvent event)
        {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean dispatchKeyEvent(KeyEvent event)
        {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean dispatchKeyShortcutEvent(KeyEvent event)
        {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event)
        {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent event)
        {
            checkDropZones(event);

            if (event.getAction() == MotionEvent.ACTION_DOWN)
            {
                // popoup.update((int)event.getRawX(), (int)event.getRawY(), -1,
                // -1);
            }

            if (event.getAction() == MotionEvent.ACTION_MOVE)
            {
                if (firstEvent == null) firstEvent = MotionEvent.obtain(event);

                // Log.v("EVENT","X:"+event.getRawX() + " _X:" + location[0] +
                // " __X:" + firstEvent.getRawX());
                // Log.v("EVENT","Y:"+event.getRawY() + " _Y:" + location[1] +
                // " __Y:" + firstEvent.getRawY());

                float pos_x = event.getRawX() + (-popoup.getWidth() / 2);
                float pos_y = event.getRawY() + (-popoup.getHeight() / 2);

                popoup.update((int) pos_x, (int) pos_y, -1, -1);

            }

            if (event.getAction() == MotionEvent.ACTION_UP)
            {
                popoup.dismiss();
                mActivity.getWindow().setCallback(callbackDefault);
            }

            return false;
        }

        @Override
        public boolean dispatchTrackballEvent(MotionEvent event)
        {
            return false;
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public void onActionModeFinished(ActionMode mode)
        {
            // TODO Auto-generated method stub

        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public void onActionModeStarted(ActionMode mode)
        {
            // TODO Auto-generated method stub

        }

        @Override
        public void onAttachedToWindow()
        {
            // TODO Auto-generated method stub

        }

        @Override
        public void onContentChanged()
        {
            // TODO Auto-generated method stub

        }

        @Override
        public boolean onCreatePanelMenu(int featureId, Menu menu)
        {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public View onCreatePanelView(int featureId)
        {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void onDetachedFromWindow()
        {
            // TODO Auto-generated method stub

        }

        @Override
        public boolean onMenuItemSelected(int featureId, MenuItem item)
        {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean onMenuOpened(int featureId, Menu menu)
        {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public void onPanelClosed(int featureId, Menu menu)
        {
            // TODO Auto-generated method stub

        }

        @Override
        public boolean onPreparePanel(int featureId, View view, Menu menu)
        {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean onSearchRequested()
        {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean onSearchRequested(SearchEvent e)
        {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public void onWindowAttributesChanged(android.view.WindowManager.LayoutParams attrs)
        {
            // TODO Auto-generated method stub

        }

        @Override
        public void onWindowFocusChanged(boolean hasFocus)
        {
            // TODO Auto-generated method stub

        }

        @Override
        public ActionMode onWindowStartingActionMode(Callback callback)
        {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public ActionMode onWindowStartingActionMode(Callback callback, int i)
        {
            // TODO Auto-generated method stub
            return null;
        }

    };

    public interface DropZoneListener
    {

        void OnDragZoneEntered(View zone, Object item);

        void OnDragZoneLeft(View zone, Object item);

        void OnDropped(View zone, Object item);

    }
}
