package com.odoo.addons.mail;

import java.util.ArrayList;
import java.util.List;

import odoo.controls.OForm.OnViewClickListener;
import odoo.controls.OList;
import odoo.controls.OList.BeforeListRowCreateListener;
import odoo.controls.OList.OnListRowViewClickListener;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.odoo.addons.mail.models.MailMessage;
import com.odoo.orm.OColumn;
import com.odoo.orm.ODataRow;
import com.odoo.orm.OValues;
import com.odoo.support.BaseFragment;
import com.odoo.util.OControls;
import com.odoo.util.drawer.DrawerItem;
import com.openerp.R;

public class MailDetail extends BaseFragment implements OnViewClickListener,
		OnListRowViewClickListener, BeforeListRowCreateListener {
	private View mView = null;
	private Integer mMailId = null;
	private OList mListMessages = null;
	private List<ODataRow> mRecords = new ArrayList<ODataRow>();
	Integer mMessageId = null;
	ODataRow mMessageData = null;
	List<Object> mMessageObjects = new ArrayList<Object>();
	Integer[] mStarredDrawables = new Integer[] { R.drawable.ic_action_starred,
			R.drawable.ic_action_starred };

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		mView = inflater.inflate(R.layout.mail_detail_layout, container, false);
		initArgs();
		return mView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		init();
	}

	private void initArgs() {
		Bundle args = getArguments();
		if (args.containsKey(OColumn.ROW_ID)) {
			mMailId = args.getInt(OColumn.ROW_ID);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void init() {
		mListMessages = (OList) mView.findViewById(R.id.lstMessageDetail);
		mListMessages.setOnListRowViewClickListener(R.id.imgBtnStar, this);
		mListMessages.setOnListRowViewClickListener(R.id.imgBtnReply, this);
		mListMessages.setBeforeListRowCreateListener(this);
		if (mMailId != null) {
			ODataRow parent = db().select(mMailId);
			OControls.setText(mView, R.id.txvDetailSubject, parent.getString("record_name"));
			mRecords.add(0, parent);
			mRecords.addAll(parent.getO2MRecord("child_ids").browseEach());
			mListMessages.initListControl(mRecords);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_message_detail, menu);
	}

	@Override
	public Object databaseHelper(Context context) {
		return new MailMessage(context);
	}

	@Override
	public List<DrawerItem> drawerMenus(Context context) {
		return null;
	}

	@Override
	public void onFormViewClick(View view, ODataRow row) {

	}

	@Override
	public void onRowViewClick(ViewGroup view_group, View view, int position,
			ODataRow row) {
		if (view.getId() == R.id.imgBtnStar) {
			ImageView imgStarred = (ImageView) view;
			boolean is_fav = row.getBoolean("starred");
			imgStarred.setColorFilter((!is_fav) ? Color.parseColor("#FF8800")
					: Color.parseColor("#aaaaaa"));
			OValues values = new OValues();
			values.put("starred", !is_fav);
			db().update(values, row.getInt(OColumn.ROW_ID));
			row.put("starred", !is_fav);
			mRecords.remove(position);
			mRecords.add(position, row);
		} else if (view.getId() == R.id.imgBtnReply) {
			Intent i = new Intent(getActivity(), MailComposeActivity.class);
			i.putExtra("name", "nilesh");
			startActivity(i);
		} else if (view.getId() == R.id.imgVotenb) {
			Toast.makeText(getActivity(), "Voted", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void beforeListRowCreate(int position, ODataRow row, View view) {
		ImageView imgstar = (ImageView) view.findViewById(R.id.imgBtnStar);
		boolean is_favorite = row.getBoolean("starred");
		imgstar.setColorFilter((is_favorite) ? Color.parseColor("#FF8800")
				: Color.parseColor("#aaaaaa"));

	}

}
