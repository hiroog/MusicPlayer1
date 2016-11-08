// vim:ts=4 sw=4 noet:

package	jp.flatlib.flatlib3.wearplayer1;


import	android.app.Activity;
import	android.os.Bundle;
import	android.os.IBinder;
import	android.content.Intent;
import	android.widget.Button;
import	android.view.View;
import	android.media.AudioManager;
import	android.content.Context;
import	android.content.ServiceConnection;
import	android.content.ComponentName;

import	jp.flatlib.core.GLog;


public class MainActivity extends Activity {

	private	AudioManager	iAudioManager= null;

	private boolean			mBound= false;

	//-------------------------------------------------------------------------
	//-------------------------------------------------------------------------

	private PlayerService		mPlayerService= null;
	private ServiceConnection	mConnection= new ServiceConnection() {
		public void onServiceConnected( ComponentName className, IBinder service )
		{
			mPlayerService= ((PlayerService.LocalBinder)service).getService();
			GLog.p( "MainActivity connected CB" );
		}
		public void onServiceDisconnected( ComponentName className )
		{
			GLog.p( "MainActivity disconnected CB" );
			mPlayerService= null;
		}
	};

	//-------------------------------------------------------------------------
	//-------------------------------------------------------------------------

	void startBind()
	{
		if( !mBound ){
			GLog.p( "MainActivity: start bind" );
			bindService( new Intent( this, PlayerService.class ), mConnection, Context.BIND_AUTO_CREATE );
			mBound= true;
		}
	}

	void stopBind()
	{
		if( mBound ){
			GLog.p( "MainActivity: stop bind" );
			unbindService( mConnection );
			mBound= false;
		}
	}


	//-------------------------------------------------------------------------
	//-------------------------------------------------------------------------

	private void	refreshState()
	{
		String	state= "Play";
		if( mPlayerService != null ){
			if( mPlayerService.IsPlay() ){
				state= "Pause";
			}
		}
		((Button)findViewById( R.id.play_button )).setText( state );
	}

	//-------------------------------------------------------------------------
	//-------------------------------------------------------------------------

    @Override
    public void	onCreate( Bundle savedInctancedState )
    {
        super.onCreate( savedInctancedState );
		GLog.p( "MainActivity onCreate" );
        setContentView( R.layout.activity_main );

		iAudioManager= (AudioManager)getSystemService( Context.AUDIO_SERVICE );

		((Button)findViewById( R.id.play_button )).setOnClickListener(
			new View.OnClickListener(){
			@Override
			public void onClick( View button )
			{
				playCommand();
			}
		} );
		((Button)findViewById( R.id.stop_button )).setOnClickListener(
			new View.OnClickListener(){
			@Override
			public void onClick( View button )
			{
				stopCommand();
			}
		} );
		/*
		((Button)findViewById( R.id.pause_button )).setOnClickListener(
			new View.OnClickListener(){
			@Override
			public void onClick( View button )
			{
				pauseCommand();
			}
		} );
		*/
		((Button)findViewById( R.id.next_button )).setOnClickListener(
			new View.OnClickListener(){
			@Override
			public void onClick( View button )
			{
				nextCommand();
			}
		} );
		((Button)findViewById( R.id.prev_button )).setOnClickListener(
			new View.OnClickListener(){
			@Override
			public void onClick( View button )
			{
				prevCommand();
			}
		} );
		((Button)findViewById( R.id.volm_button )).setOnClickListener(
			new View.OnClickListener(){
			@Override
			public void onClick( View button )
			{
				Volume( false );
			}
		} );
		((Button)findViewById( R.id.volp_button )).setOnClickListener(
			new View.OnClickListener(){
			@Override
			public void onClick( View button )
			{
				Volume( true );
			}
		} );

    }

	private void startCommand( String cmd )
	{
		Intent	intent= new Intent( this, PlayerService.class );
		intent.putExtra( "Command", cmd );
		startService( intent );
		refreshState();
	}

	public void playCommand()
	{
		GLog.p( "client Play" );
		if( mPlayerService != null ){
			if( mPlayerService.IsPlay() ){
				mPlayerService.Pause();
			}else{
				mPlayerService.Play();
			}
		}
		startCommand( "Play" );
	}

	public void stopCommand()
	{
		GLog.p( "client Stop" );
		if( mPlayerService != null ){
			mPlayerService.Stop();
		}
		refreshState();
		stopServer();
	}

/*
	public void pauseCommand()
	{
		GLog.p( "client Pause" );
		if( mPlayerService != null ){
			mPlayerService.Pause();
		}
		startCommand( "Pause" );
	}
*/

	public void nextCommand()
	{
		GLog.p( "client Next" );
		if( mPlayerService != null ){
			mPlayerService.Next();
		}
		startCommand( "Next" );
	}

	public void prevCommand()
	{
		GLog.p( "client Prev" );
		if( mPlayerService != null ){
			mPlayerService.Prev();
		}
		startCommand( "Prev" );
	}



	public void stopServer()
	{
		GLog.p( "client Stop Server" );
		Intent	intent= new Intent( this, PlayerService.class );
		stopService( intent );
	}

	public int GetVolume()
	{
		return	iAudioManager.getStreamVolume( AudioManager.STREAM_MUSIC );
	}

	public void Volume( boolean updown )
	{
		GLog.p( "Volume" );
		int	volume= iAudioManager.getStreamVolume( AudioManager.STREAM_MUSIC );
		int	max_volume= iAudioManager.getStreamMaxVolume( AudioManager.STREAM_MUSIC );
		if( updown ){
			volume+= 1;
		}else{
			volume-= 1;
		}
		if( volume > max_volume ){
			volume= max_volume;
		}else if( volume < 0 ){
			volume= 0;
		}
		GLog.p( "cur=" + volume );
		GLog.p( "max=" + max_volume );
		iAudioManager.setStreamVolume( AudioManager.STREAM_MUSIC, volume, 0 );
	}

	//-------------------------------------------------------------------------
	//-------------------------------------------------------------------------

	@Override
	protected void	onStart()
	{
		super.onStart();
		GLog.p( "MainActivity onStart" );
		startBind();
	}

	@Override
	protected void	onStop()
	{
		super.onStop();
		GLog.p( "MainActivity onStop" );
		stopBind();
	}

}
