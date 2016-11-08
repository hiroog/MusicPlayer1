// 2014/11/20 Hiroyuki Ogasawara
// vim:ts=4 sw=4 noet:


package	jp.flatlib.flatlib3.musicplayerw;


import	android.os.IBinder;
import	android.os.Binder;
import	android.content.Intent;
import	android.content.SharedPreferences;
import	android.preference.PreferenceManager;
import	android.app.Service;
import	android.app.IntentService;

import	jp.flatlib.core.GLog;

import	java.util.concurrent.TimeUnit;
import	android.net.Uri;

import	android.media.AudioManager;
import	android.media.MediaPlayer;
import	java.io.IOException;



public class PlayerService extends Service 
		implements MediaPlayer.OnCompletionListener
	{

	private static final int	STATE_STOP= 0;
	private static final int	STATE_PLAY= 1;
	private static final int	STATE_PAUSE= 2;

	private MediaPlayer	iPlayer= null;
	private	MediaList	iFileList= null;
	private	int			State= STATE_STOP;

	public PlayerService()
	{
		super();
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
		GLog.p( "PlayerService: onCreate" );
	}

	@Override
	public void onDestroy()
	{
		GLog.p( "PlayerService: onDestroy" );
		StopRelease();
		super.onDestroy();
	}

	@Override
	public int onStartCommand( Intent intent, int flags, int startid )
	{
		GLog.p( "PlayerService: onStartCommand id=" + startid + " " + intent );
		if( intent == null ){
			return	START_STICKY;
		}
		String	command= intent.getStringExtra( "Command" );
		if( command != null ){
			GLog.p( "Service RecvCommand :" + command );

			if( command.equals( "Play" ) ){
				Play();
			}else if( command.equals( "Stop" ) ){
				Stop();
			}else if( command.equals( "Pause" ) ){
				Pause();
			}else if( command.equals( "Refresh" ) ){
				Refresh();
			}else if( command.equals( "Next" ) ){
				Next();
			}else if( command.equals( "Prev" ) ){
				Prev();
			}
		}
		return	START_STICKY;
	}

	@Override
	public IBinder onBind( Intent intent )
	{
		return	null;
	}

	//-------------------------------------------------------------------------
	//-------------------------------------------------------------------------

	@Override
	public void	onCompletion( MediaPlayer player )
	{
		GLog.p( "MEDIA: Complete" );
		PlayAuto();
	}


	public void Refresh()
	{
		if( iFileList == null ){
			iFileList= new MediaList();
		}
		iFileList.Refresh();
	}

	private void StopRelease()
	{
		if( iPlayer != null ){
			GLog.p( "stop" );
			iPlayer.stop();
			iPlayer.reset();
			iPlayer.release();
			iPlayer= null;
		}
	}

	private void Play( String file )
	{
		StopRelease();
		GLog.p( "MEDIA: play " + file );
		MediaPlayer	player= new MediaPlayer();
		try {
			iPlayer= player;
			player.setOnCompletionListener( this );
			player.setDataSource( file );
			player.prepare();
			player.start();
		}
		catch( IOException e ){
			// not found
			iPlayer= null;
			PlayAuto();
		}
	}

	private void PlayAuto()
	{
		if( State == STATE_PLAY ){
			if( iFileList == null ){
				Refresh();
			}
			String	file= iFileList.getNext();
			if( file != null ){
				Play( file );
			}
		}
	}

	public void Play()
	{
		if( State == STATE_PAUSE ){
			if( iPlayer != null ){
				if( !iPlayer.isPlaying() ){
					iPlayer.start();
					State= STATE_PLAY;
					return;
				}
			}
		}
		if( State != STATE_PLAY ){
			Next();
		}
	}

	public void Next()
	{
		GLog.p( "MEDIA: next" );
		State= STATE_PLAY;
		PlayAuto();
	}

	public void Prev()
	{
		GLog.p( "MEDIA: prev" );
		State= STATE_PLAY;
		if( iFileList == null ){
			Refresh();
		}
		iFileList.setPrev();
		iFileList.setPrev();
		PlayAuto();
	}

	public void Stop()
	{
		GLog.p( "MEDIA: stop" );
		State= STATE_STOP;
		StopRelease();
	}

	public void Pause()
	{
		GLog.p( "MEDIA: pause" );
		if( iPlayer != null ){
			if( iPlayer.isPlaying() ){
				iPlayer.pause();
				State= STATE_PAUSE;
				GLog.p( "pause" );
			}else{
				iPlayer.start();
				State= STATE_PLAY;
				GLog.p( "resume" );
			}
		}
	}

	//-------------------------------------------------------------------------
	//-------------------------------------------------------------------------
}




