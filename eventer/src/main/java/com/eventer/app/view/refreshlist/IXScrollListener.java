package com.eventer.app.view.refreshlist;

import android.view.View;

/**
 * you can listen ListView.OnScrollListener or this one. it will invoke
 * onXScrolling when header/footer scroll back.
 */
public interface IXScrollListener {
	 void onXScrolling(View view);
}
