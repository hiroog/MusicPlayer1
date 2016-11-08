// 2014/11/20 Hiroyuki Ogasawara
// vim:ts=4 sw=4 noet:

// WearPlayer   DAPP


package	jp.flatlib.flatlib3.musicplayerw;

import	java.io.File;
import	java.io.FilenameFilter;
import	java.util.Random;
import	java.lang.System;
import	java.util.Set;
import	java.util.Map;
import	android.os.SystemClock;

import	jp.flatlib.core.GLog;


import	com.google.android.gms.wearable.DataEventBuffer;
import	com.google.android.gms.wearable.DataEvent;
import	com.google.android.gms.wearable.DataMap;
import	com.google.android.gms.wearable.DataMapItem;

import	com.google.android.gms.common.api.GoogleApiClient;
import	com.google.android.gms.wearable.PutDataMapRequest;
import	com.google.android.gms.wearable.PutDataRequest;
import	com.google.android.gms.wearable.Wearable;
import	com.google.android.gms.wearable.DataApi;
import	com.google.android.gms.wearable.Asset;
import	com.google.android.gms.wearable.MessageApi;
import	com.google.android.gms.wearable.NodeApi;
import	com.google.android.gms.wearable.Node;
import	com.google.android.gms.wearable.DataItem;
import	com.google.android.gms.wearable.DataItemBuffer;
import	com.google.android.gms.wearable.DataItemAsset;
import	com.google.android.gms.common.api.ResultCallback;




public class MediaList2 extends MediaList {


	//-------------------------------------------------------------------------
	//-------------------------------------------------------------------------

	private String[]	FileList= null;
	private int			Index= 0;

	//-------------------------------------------------------------------------
	//-------------------------------------------------------------------------

	public static class CallEvent {
		public void	Run( MediaList2 list )
		{
		}
	}

	//-------------------------------------------------------------------------
	//-------------------------------------------------------------------------

	public MediaList2()
	{
	}

	public int	getSize()
	{
		return	FileList.length;
	}

	public String	getName( int index )
	{
		return	FileList[index];
	}


	private void	refreshListInternal( GoogleApiClient mApiClient, DataItem item, CallEvent event )
	{
		Map<String,DataItemAsset>	asset_map= item.getAssets();
		Set<String>	set= asset_map.keySet();
		int			size= set.size();
		FileList= new String[size];
		int			index= 0;
		for( String key : set ){
			FileList[index++]= key;
		}
		for( int i= 0 ; i< size ; i++ ){
			GLog.p( "  Asset(" + i + ") " + FileList[i] );
		}
		if( event != null ){
			event.Run( this );
		}
	}

	public void	RefreshList( GoogleApiClient mApiClient, CallEvent event )
	{
		final CallEvent			event_= event;
		final GoogleApiClient	mApiClient_= mApiClient;
		Index= 0;
		Wearable.DataApi.getDataItems( mApiClient )
			.setResultCallback( new ResultCallback<DataItemBuffer>() {
				@Override
				public void onResult( DataItemBuffer result ) {
					if( result.getStatus().isSuccess() ){
						for( DataItem item : result ){
							if( item.getUri().getPath().equals( Command.STORAGE_PATH ) ){
								refreshListInternal( mApiClient_, item, event_ );
							}
						}
					}
				}
			} );

	}

}


