package test.churuata.rap;

import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.ui.controller.EditEvent;
import org.condast.commons.ui.controller.IEditListener;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.application.AbstractEntryPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import test.churuata.rap.ui.MapBrowser;


public class BasicEntryPoint extends AbstractEntryPoint {
    private static final long serialVersionUID = 1L;

	public static final String S_CHURUATA = "Churuata-Digital";

	public static final String S_CHURUATA_PAGE = "/churuata";

	public static final int DEFAULT_SCHEDULE = 1000; //milli seconds

	private MapBrowser mapComposite;
	private Label timerLabel;
	private Label slowestLabel;

	private ScheduledExecutorService timer;
	private int interval;
	private long counter, slowest;
	
	private IEditListener<LatLng> listener = e->onEditEvent( e );

	@Override
    protected void createContents(Composite parent) {
        parent.setLayout(new GridLayout(1, false ));
        mapComposite = new MapBrowser( parent, SWT.NONE);
 		mapComposite.setData( RWT.CUSTOM_VARIANT, S_CHURUATA );
 		mapComposite.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ));
 		mapComposite.addEditListener(listener);
 		this.interval = 3000;
 
		Group group = new Group( parent, SWT.NONE );
		group.setText("Show Progress");
		group.setLayout( new GridLayout(5, false ));
 		group.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, false ));
		
		Label label = new Label( group, SWT.None );
		label.setText("Interval (" + interval + "): ");
		label.setLayoutData( new GridData( SWT.FILL, SWT.FILL, false, true ));
		this.timerLabel = new Label( group, SWT.None );
		this.timerLabel.setText( "0");
		this.timerLabel.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ));

		this.slowestLabel = new Label( group, SWT.None );
		this.slowestLabel.setText( "0");
		this.slowestLabel.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ));

		timer = Executors.newScheduledThreadPool(10);
		timer.scheduleAtFixedRate(()->handleTimer(), 1000, interval, TimeUnit.MILLISECONDS);
 		this.counter = Calendar.getInstance().getTimeInMillis();
 		this.slowest = 0;
    }

	private void onEditEvent(EditEvent<LatLng> e) {
		switch( e.getType()) {
		case COMPLETE:
			Calendar calendar = Calendar.getInstance();
			long elapsed = calendar.getTimeInMillis() -counter;
			if( elapsed > slowest ) {
				slowest = elapsed;
				this.slowestLabel.setText( String.valueOf(slowest));
			}
			this.timerLabel.setText( String.valueOf(elapsed));
			this.counter = calendar.getTimeInMillis();
			break;
		default:
			break;
		}
	}

	private void handleTimer() {
		try {
			mapComposite.locate();
			mapComposite.refresh();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
