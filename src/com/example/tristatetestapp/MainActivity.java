package com.example.tristatetestapp;

import interdroid.swan.ExpressionManager;
import interdroid.swan.SwanException;
import interdroid.swan.TriStateExpressionListener;
import interdroid.swan.swansong.ExpressionFactory;
import interdroid.swan.swansong.ExpressionParseException;
import interdroid.swan.swansong.TriState;
import interdroid.swan.swansong.TriStateExpression;
import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

	//String battery_low = "self@battery:level<200";	
	//String battery_plugged = "self@battery:plugged == 0";
	
	/* Expression to check if it is morning. 12 value indicates the hour 12 pm*/
	private static String is_morning = "self@time:hour_of_day < 12";
	
	/* Expression to check if it is raining. We assume the value less than 0.1 mm/hr means it is not raining */
	private static String not_raining = "self@rain:expected_mm?longitude='4'&latitude='52'<0.1";
	
	/* Expression to check if the smartphone has been moved in the past hour */
	private static String active = "self@movement:total{MAX,3600000} > 15.0";
	
	/* Final expression */
	private static String expression = not_raining+" && "+is_morning+ " && "+ active;
	
	/* Unique code used to register with SWAN */
	private static int REQUEST_CODE = 9987;
	
	TextView tv = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tv = (TextView) findViewById(R.id.textView1);
		registerSWANSensor(expression);
	}
	
	/* Unregister expression from SWAN */
	private void unregisterSWANSensor(){
		
		ExpressionManager.unregisterExpression(this, String.valueOf(REQUEST_CODE));
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		unregisterSWANSensor();
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterSWANSensor();

	}
	
	/* Register expression to SWAN */ 
	private void registerSWANSensor(String myExpression){
		
			try {
				/* Registering tri state expression */
				ExpressionManager.registerTriStateExpression(this, String.valueOf(REQUEST_CODE),
				(TriStateExpression) ExpressionFactory.parse(myExpression),
				new TriStateExpressionListener() {
					
					@Override
					public void onNewState(String arg0, long arg1, TriState arg2) {
												
						tv.setText("arg0 = "+arg0+" arg1="+arg1+" arg2="+arg2);
							
						if(arg2==TriState.TRUE){
							/* Notification to the User */
							AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
				            builder1.setMessage("Good Morning!. Looks like a nice weather. How about biking today?");
				            builder1.setCancelable(true);
				            builder1.setPositiveButton("Yes",
				                    new DialogInterface.OnClickListener() {
				                public void onClick(DialogInterface dialog, int id) {
				                    dialog.cancel();
				                }
				            });
				            builder1.setNegativeButton("No",
				                    new DialogInterface.OnClickListener() {
				                public void onClick(DialogInterface dialog, int id) {
				                    dialog.cancel();
				                }
				            });
				            
				            AlertDialog alert11 = builder1.create();
				            alert11.show();
						}	
						
					}
				}); 
			} catch (SwanException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExpressionParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	

	}

	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
