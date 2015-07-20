package ro.ui.pttdroid;

import edu.mclab1.nccu_story.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;



public class Premain extends Activity  {
	private String[] lunch = {"1", "2", "3", "4"};
	private String[] lunch2 = {"Chinese", "English", "Japanese", "Korean"};
	String[][] Selection = new String[][] {
	{"1Chinese","239.255.255.239"},{"1English","239.255.255.240"},{"1Japanese","239.255.255.241"},{"1Korean","239.255.255.242"}
   ,{"2Chinese","239.255.255.243"},{"2English","239.255.255.244"},{"2Japanese","239.255.255.245"},{"2Korean","239.255.255.246"}
   ,{"3Chinese","239.255.255.247"},{"3English","239.255.255.248"},{"3Japanese","239.255.255.249"},{"3Korean","239.255.255.250"}
   ,{"4Chinese","239.255.255.251"},{"4English","239.255.255.252"},{"4Japanese","239.255.255.253"},{"4Korean","239.255.255.254"}};
	public static String Selected="";
	public static String Number="";
	private Context mContext;
	private Context mContext2;
	private Spinner spinner;   
	private Spinner spinner2;   
	private ArrayAdapter<String> lunchList;  
	private ArrayAdapter<String> lunchList2;  
	private Button button ;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.premain);
		
		
		 
		 /*  mContext = this.getApplicationContext();
		   spinner = (Spinner)findViewById(R.id.mySpinner); 
		   
		   mContext2 = this.getApplicationContext();
		   spinner2 = (Spinner)findViewById(R.id.mySpinner2); 
		   
		   lunchList = new ArrayAdapter<String>(Premain.this,android.R.layout.simple_spinner_item, lunch);
		   spinner.setAdapter(lunchList);
		   
		   lunchList2 = new ArrayAdapter<String>(Premain.this,android.R.layout.simple_spinner_item, lunch2);
		   spinner2.setAdapter(lunchList2);  */
		   Button button = (Button)findViewById(R.id.button1); 
		   
		   button.setOnClickListener(new Button.OnClickListener(){
			   @Override
			   public void onClick(View v) {
			   // TODO Auto-generated method stub 
			   Intent intent = new Intent();
			   intent.setClass(Premain.this, Main.class);
			   startActivity(intent); 
			   Premain.this.finish(); 
			   }
			   });
		

	}
	
	
	@Override
	public void onResume() {
		super.onResume();
		
		
		/*   spinner.setOnItemSelectedListener(new OnItemSelectedListener(){
			   
			   
			    @Override
			    public void onItemSelected(AdapterView<?> arg0, View arg1,int position, long arg3) {
			    	//if_order=true;
			    	Number=lunch[position].toString();
			    	
			       Toast.makeText(mContext, "你選的是"+Number, Toast.LENGTH_SHORT).show();
			                
			    }
			    
			    
			    @Override
			    public void onNothingSelected(AdapterView<?> arg0) {
			       // TODO Auto-generated method stub
			    }
			});
		   
		   spinner2.setOnItemSelectedListener(new OnItemSelectedListener(){

			    @Override
			    public void onItemSelected(AdapterView<?> arg0, View arg1,int position, long arg3) {
				//if(if_order==true){

			    	int i=0;
			    	Selected=Number+lunch2[position].toString();
			    	System.out.println("SelectedSelected"+Selected);

			    	for( ; i<16 ; i++){
			    		if(Selected.equals(Selection[i][0])){
			    			Selected=Selection[i][1];
			    			break;
			    		}
			    	}
			    	
			       
			       Toast.makeText(mContext, "你選的是"+Selected, Toast.LENGTH_SHORT).show();
			     //  if_order=false;        
			    //}
			    }
			    
			    
			    @Override
			    public void onNothingSelected(AdapterView<?> arg0) {
			       // TODO Auto-generated method stub
			    }
			});*/
		   


		
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	switch (item.getItemId()) {
	
	
	/*case R.id.client_to_list:
		Intent intent3 =new Intent(this,Main.class);
		startActivity(intent3);
		return true;*/


	default:
		return super.onOptionsItemSelected(item);
	}
	}
	

}
